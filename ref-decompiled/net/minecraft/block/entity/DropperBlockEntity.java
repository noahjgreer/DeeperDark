/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.DispenserBlockEntity
 *  net.minecraft.block.entity.DropperBlockEntity
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class DropperBlockEntity
extends DispenserBlockEntity {
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.dropper");

    public DropperBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.DROPPER, blockPos, blockState);
    }

    protected Text getContainerName() {
        return CONTAINER_NAME_TEXT;
    }
}

