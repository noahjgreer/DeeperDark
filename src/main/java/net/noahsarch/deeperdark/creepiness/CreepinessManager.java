package net.noahsarch.deeperdark.creepiness;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.creature.CreatureManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Rolls one haunt per player every few minutes and drives the effects already in progress. */
public final class CreepinessManager {
    private static final int RETRY_TICKS = 200;

    private static final Map<UUID, HauntState> STATES = new HashMap<>();

    private CreepinessManager() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CreepinessManager::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> STATES.remove(handler.getPlayer().getUUID()));
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayer serverPlayer) || state.hasBlockEntity() || !state.canOcclude()) return;
            HauntState haunts = state(serverPlayer);
            haunts.minedLevel = serverPlayer.level();
            haunts.minedPos = pos.immutable();
            haunts.minedState = state;
            haunts.minedTick = serverPlayer.level().getServer().getTickCount();
        });
    }

    private static void tick(MinecraftServer server) {
        CreepinessConfig config = DeeperDarkConfig.get().creepiness;
        int now = server.getTickCount();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            HauntState state = state(player);
            // A creature chase silences the world, so a haunt would break that silence
            boolean quiet = player.isSpectator() || CreatureManager.isPlayerSoundSuppressed(player.getUUID());
            if (quiet) state.silence();
            state.tick(player, now);
            if (quiet || !config.enabled || --state.ticksUntilHaunt > 0) continue;

            boolean played = player.level().dimension() != Level.NETHER && playRandom(player, state, now, config);
            state.ticksUntilHaunt = played ? rollDelay(player.getRandom()) : RETRY_TICKS;
        }
    }

    private static boolean playRandom(ServerPlayer player, HauntState state, int now, CreepinessConfig config) {
        List<Haunt> haunts = new ArrayList<>(Arrays.asList(Haunt.values()));
        haunts.removeIf(h -> !h.enabled.test(config));
        Util.shuffle(haunts, player.getRandom());
        for (Haunt haunt : haunts) {
            if (haunt.play(player, state, now)) return true;
        }
        return false;
    }

    /** Plays one haunt now, ignoring the timer and the per-haunt toggles. */
    static boolean trigger(ServerPlayer player, Haunt haunt) {
        return haunt.play(player, state(player), player.level().getServer().getTickCount());
    }

    static int ticksUntilHaunt(ServerPlayer player) {
        return state(player).ticksUntilHaunt;
    }

    private static HauntState state(ServerPlayer player) {
        return STATES.computeIfAbsent(player.getUUID(), id -> new HauntState(rollDelay(player.getRandom())));
    }

    private static int rollDelay(RandomSource random) {
        CreepinessConfig config = DeeperDarkConfig.get().creepiness;
        return random.nextIntBetweenInclusive(config.minDelayTicks, Math.max(config.minDelayTicks, config.maxDelayTicks));
    }
}
