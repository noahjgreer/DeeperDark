package net.noahsarch.deeperdark.creepiness;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** One player's haunt timer and the effects currently playing out for them. */
final class HauntState {
    private static final int FOLLOWER_LAG_TICKS = 10;
    // Vanilla steps once per (int) moveDist + 1, and moveDist grows at 0.6× horizontal distance
    private static final double STRIDE = 1.0 / 0.6;

    private record QueuedSound(int dueTick, Holder<SoundEvent> sound, SoundSource source, Vec3 pos, float volume, float pitch) {}

    int ticksUntilHaunt;
    private final List<QueuedSound> queued = new ArrayList<>();

    private ServerLevel lastLevel;
    private int followerTicksLeft;
    private int followerStepsLeft;
    private final ArrayDeque<Vec3> trail = new ArrayDeque<>();
    private double followerStride;

    ServerLevel minedLevel;
    BlockPos minedPos;
    BlockState minedState;
    int minedTick;

    private ServerLevel ghostLevel;
    private BlockPos ghostPos;
    private int ghostRevertTick;

    HauntState(int ticksUntilHaunt) {
        this.ticksUntilHaunt = ticksUntilHaunt;
    }

    /** Sends a sound to this player only, so nobody nearby can confirm it. */
    static void send(ServerPlayer player, Holder<SoundEvent> sound, SoundSource source, Vec3 pos, float volume, float pitch) {
        player.connection.send(new ClientboundSoundPacket(sound, source, pos.x, pos.y, pos.z, volume, pitch, player.getRandom().nextLong()));
    }

    void queue(int dueTick, Holder<SoundEvent> sound, SoundSource source, Vec3 pos, float volume, float pitch) {
        queued.add(new QueuedSound(dueTick, sound, source, pos, volume, pitch));
    }

    boolean followerActive() {
        return followerTicksLeft > 0;
    }

    void startFollower(int steps, int timeoutTicks) {
        followerStepsLeft = steps;
        followerTicksLeft = timeoutTicks;
        followerStride = 0;
        trail.clear();
    }

    void showGhost(ServerPlayer player, int now, int revertAfter) {
        revertGhost(player);
        player.connection.send(new ClientboundBlockUpdatePacket(minedPos, minedState));
        ghostLevel = minedLevel;
        ghostPos = minedPos;
        ghostRevertTick = now + revertAfter;
        minedPos = null;
    }

    /** Cuts off queued sounds and the follower, for a chase or after the player changes level. */
    void silence() {
        queued.clear();
        followerTicksLeft = 0;
    }

    void tick(ServerPlayer player, int now) {
        if (player.level() != lastLevel) {
            silence();
            lastLevel = player.level();
        }
        queued.removeIf(q -> {
            if (q.dueTick > now) return false;
            send(player, q.sound, q.source, q.pos, q.volume, q.pitch);
            return true;
        });

        if (now >= ghostRevertTick) revertGhost(player);
        if (followerTicksLeft > 0) tickFollower(player);
    }

    // Resends the real state; mining or bumping the ghost earlier gets corrected by vanilla anyway
    private void revertGhost(ServerPlayer player) {
        if (ghostPos == null) return;
        if (player.level() == ghostLevel) player.connection.send(new ClientboundBlockUpdatePacket(ghostLevel, ghostPos));
        ghostPos = null;
    }

    // Replays the player's own path a few blocks behind them, so it stops one step after they do
    private void tickFollower(ServerPlayer player) {
        if (--followerTicksLeft <= 0) return;
        trail.addLast(player.position());
        if (trail.size() <= FOLLOWER_LAG_TICKS) return;

        Vec3 from = trail.removeFirst();
        Vec3 to = trail.peekFirst();
        double moved = Math.hypot(to.x - from.x, to.z - from.z);
        // A teleport isn't walking; restart the stride instead of stepping at the far end
        followerStride = moved > 4 ? 0 : followerStride + moved;
        if (followerStride < STRIDE) return;
        followerStride = 0;

        BlockState ground = player.level().getBlockState(BlockPos.containing(to.x, to.y - 0.2, to.z));
        if (ground.isAir() || player.isPassenger() || player.isFallFlying()) return;
        SoundType type = ground.getSoundType();
        send(player, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(type.getStepSound()), SoundSource.PLAYERS, to, type.getVolume() * 0.25f, type.getPitch() * 0.9f);
        if (--followerStepsLeft <= 0) followerTicksLeft = 0;
    }
}
