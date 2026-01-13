/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.CalibratedSculkSensorBlock;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import org.jspecify.annotations.Nullable;

protected class CalibratedSculkSensorBlockEntity.Callback
extends SculkSensorBlockEntity.VibrationCallback {
    public CalibratedSculkSensorBlockEntity.Callback(BlockPos pos) {
        super(CalibratedSculkSensorBlockEntity.this, pos);
    }

    @Override
    public int getRange() {
        return 16;
    }

    @Override
    public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event, @Nullable GameEvent.Emitter emitter) {
        int i = this.getCalibrationFrequency(world, this.pos, CalibratedSculkSensorBlockEntity.this.getCachedState());
        if (i != 0 && Vibrations.getFrequency(event) != i) {
            return false;
        }
        return super.accepts(world, pos, event, emitter);
    }

    private int getCalibrationFrequency(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(CalibratedSculkSensorBlock.FACING).getOpposite();
        return world.getEmittedRedstonePower(pos.offset(direction), direction);
    }
}
