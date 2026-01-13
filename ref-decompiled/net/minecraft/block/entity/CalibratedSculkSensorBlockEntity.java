/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CalibratedSculkSensorBlockEntity
 *  net.minecraft.block.entity.CalibratedSculkSensorBlockEntity$Callback
 *  net.minecraft.block.entity.SculkSensorBlockEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.event.Vibrations$Callback
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.Vibrations;

public class CalibratedSculkSensorBlockEntity
extends SculkSensorBlockEntity {
    public CalibratedSculkSensorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.CALIBRATED_SCULK_SENSOR, blockPos, blockState);
    }

    public Vibrations.Callback createCallback() {
        return new Callback(this, this.getPos());
    }
}

