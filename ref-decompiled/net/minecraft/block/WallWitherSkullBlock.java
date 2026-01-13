/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.block.WallSkullBlock
 *  net.minecraft.block.WallWitherSkullBlock
 *  net.minecraft.block.WitherSkullBlock
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class WallWitherSkullBlock
extends WallSkullBlock {
    public static final MapCodec<WallWitherSkullBlock> CODEC = WallWitherSkullBlock.createCodec(WallWitherSkullBlock::new);

    public MapCodec<WallWitherSkullBlock> getCodec() {
        return CODEC;
    }

    public WallWitherSkullBlock(AbstractBlock.Settings settings) {
        super((SkullBlock.SkullType)SkullBlock.Type.WITHER_SKELETON, settings);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        WitherSkullBlock.onPlaced((World)world, (BlockPos)pos);
    }
}

