/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import org.jspecify.annotations.Nullable;

protected class SculkSensorBlockEntity.VibrationCallback
implements Vibrations.Callback {
    public static final int RANGE = 8;
    protected final BlockPos pos;
    private final PositionSource positionSource;

    public SculkSensorBlockEntity.VibrationCallback(BlockPos pos) {
        this.pos = pos;
        this.positionSource = new BlockPositionSource(pos);
    }

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public PositionSource getPositionSource() {
        return this.positionSource;
    }

    @Override
    public boolean triggersAvoidCriterion() {
        return true;
    }

    @Override
    public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable GameEvent.Emitter emitter) {
        if (pos.equals(this.pos) && (event.matches(GameEvent.BLOCK_DESTROY) || event.matches(GameEvent.BLOCK_PLACE))) {
            return false;
        }
        if (Vibrations.getFrequency(event) == 0) {
            return false;
        }
        return SculkSensorBlock.isInactive(SculkSensorBlockEntity.this.getCachedState());
    }

    @Override
    public void accept(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
        BlockState blockState = SculkSensorBlockEntity.this.getCachedState();
        if (SculkSensorBlock.isInactive(blockState)) {
            int i = Vibrations.getFrequency(event);
            SculkSensorBlockEntity.this.setLastVibrationFrequency(i);
            int j = Vibrations.getSignalStrength(distance, this.getRange());
            Block block = blockState.getBlock();
            if (block instanceof SculkSensorBlock) {
                SculkSensorBlock sculkSensorBlock = (SculkSensorBlock)block;
                sculkSensorBlock.setActive(sourceEntity, world, this.pos, blockState, j, i);
            }
        }
    }

    @Override
    public void onListen() {
        SculkSensorBlockEntity.this.markDirty();
    }

    @Override
    public boolean requiresTickingChunksAround() {
        return true;
    }
}
