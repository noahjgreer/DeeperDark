/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.StemBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

class BeeEntity.GrowCropsGoal
extends BeeEntity.NotAngryGoal {
    static final int field_30299 = 30;

    BeeEntity.GrowCropsGoal() {
        super(BeeEntity.this);
    }

    @Override
    public boolean canBeeStart() {
        if (BeeEntity.this.getCropsGrownSincePollination() >= 10) {
            return false;
        }
        if (BeeEntity.this.random.nextFloat() < 0.3f) {
            return false;
        }
        return BeeEntity.this.hasNectar() && BeeEntity.this.hasValidHive();
    }

    @Override
    public boolean canBeeContinue() {
        return this.canBeeStart();
    }

    @Override
    public void tick() {
        if (BeeEntity.this.random.nextInt(this.getTickCount(30)) != 0) {
            return;
        }
        for (int i = 1; i <= 2; ++i) {
            Fertilizable fertilizable;
            BlockPos blockPos = BeeEntity.this.getBlockPos().down(i);
            BlockState blockState = BeeEntity.this.getEntityWorld().getBlockState(blockPos);
            Block block = blockState.getBlock();
            BlockState blockState2 = null;
            if (!blockState.isIn(BlockTags.BEE_GROWABLES)) continue;
            if (block instanceof CropBlock) {
                CropBlock cropBlock = (CropBlock)block;
                if (!cropBlock.isMature(blockState)) {
                    blockState2 = cropBlock.withAge(cropBlock.getAge(blockState) + 1);
                }
            } else if (block instanceof StemBlock) {
                int j = blockState.get(StemBlock.AGE);
                if (j < 7) {
                    blockState2 = (BlockState)blockState.with(StemBlock.AGE, j + 1);
                }
            } else if (blockState.isOf(Blocks.SWEET_BERRY_BUSH)) {
                int j = blockState.get(SweetBerryBushBlock.AGE);
                if (j < 3) {
                    blockState2 = (BlockState)blockState.with(SweetBerryBushBlock.AGE, j + 1);
                }
            } else if ((blockState.isOf(Blocks.CAVE_VINES) || blockState.isOf(Blocks.CAVE_VINES_PLANT)) && (fertilizable = (Fertilizable)((Object)blockState.getBlock())).isFertilizable(BeeEntity.this.getEntityWorld(), blockPos, blockState)) {
                fertilizable.grow((ServerWorld)BeeEntity.this.getEntityWorld(), BeeEntity.this.random, blockPos, blockState);
                blockState2 = BeeEntity.this.getEntityWorld().getBlockState(blockPos);
            }
            if (blockState2 == null) continue;
            BeeEntity.this.getEntityWorld().syncWorldEvent(2011, blockPos, 15);
            BeeEntity.this.getEntityWorld().setBlockState(blockPos, blockState2);
            BeeEntity.this.addCropCounter();
        }
    }
}
