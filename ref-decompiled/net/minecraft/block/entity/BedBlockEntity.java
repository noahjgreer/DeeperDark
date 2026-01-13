/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BedBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BedBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.block.entity;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;

public class BedBlockEntity
extends BlockEntity {
    private final DyeColor color;

    public BedBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, ((BedBlock)state.getBlock()).getColor());
    }

    public BedBlockEntity(BlockPos pos, BlockState state, DyeColor color) {
        super(BlockEntityType.BED, pos, state);
        this.color = color;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

