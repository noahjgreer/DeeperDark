/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.DaylightDetectorBlockEntity
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class DaylightDetectorBlockEntity
extends BlockEntity {
    public DaylightDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.DAYLIGHT_DETECTOR, pos, state);
    }
}

