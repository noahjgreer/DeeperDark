/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBannerBlock
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.entity.BannerBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public abstract class AbstractBannerBlock
extends BlockWithEntity {
    private final DyeColor color;

    protected AbstractBannerBlock(DyeColor color, AbstractBlock.Settings settings) {
        super(settings);
        this.color = color;
    }

    protected abstract MapCodec<? extends AbstractBannerBlock> getCodec();

    public boolean canMobSpawnInside(BlockState state) {
        return true;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BannerBlockEntity(pos, state, this.color);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BannerBlockEntity) {
            BannerBlockEntity bannerBlockEntity = (BannerBlockEntity)blockEntity;
            return bannerBlockEntity.getPickStack();
        }
        return super.getPickStack(world, pos, state, includeData);
    }

    public DyeColor getColor() {
        return this.color;
    }
}

