/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.listener.GameEventListener;

class AllayEntity.JukeboxEventListener
implements GameEventListener {
    private final PositionSource positionSource;
    private final int range;

    public AllayEntity.JukeboxEventListener(PositionSource positionSource, int range) {
        this.positionSource = positionSource;
        this.range = range;
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public int getRange() {
        return this.range;
    }

    @Override
    public boolean listen(ServerWorld world, RegistryEntry<GameEvent> event, GameEvent.Emitter emitter, Vec3d emitterPos) {
        if (event.matches(GameEvent.JUKEBOX_PLAY)) {
            AllayEntity.this.updateJukeboxPos(BlockPos.ofFloored(emitterPos), true);
            return true;
        }
        if (event.matches(GameEvent.JUKEBOX_STOP_PLAY)) {
            AllayEntity.this.updateJukeboxPos(BlockPos.ofFloored(emitterPos), false);
            return true;
        }
        return false;
    }
}
