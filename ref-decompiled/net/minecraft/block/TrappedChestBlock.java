/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.TrappedChestBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.TrappedChestBlockEntity
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stat
 *  net.minecraft.stat.Stats
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.BlockView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

public class TrappedChestBlock
extends ChestBlock {
    public static final MapCodec<TrappedChestBlock> CODEC = TrappedChestBlock.createCodec(TrappedChestBlock::new);

    public MapCodec<TrappedChestBlock> getCodec() {
        return CODEC;
    }

    public TrappedChestBlock(AbstractBlock.Settings settings) {
        super(() -> BlockEntityType.TRAPPED_CHEST, SoundEvents.BLOCK_CHEST_OPEN, SoundEvents.BLOCK_CHEST_CLOSE, settings);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrappedChestBlockEntity(pos, state);
    }

    protected Stat<Identifier> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat((Object)Stats.TRIGGER_TRAPPED_CHEST);
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return MathHelper.clamp((int)ChestBlockEntity.getPlayersLookingInChestCount((BlockView)world, (BlockPos)pos), (int)0, (int)15);
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (direction == Direction.UP) {
            return state.getWeakRedstonePower(world, pos, direction);
        }
        return 0;
    }
}

