/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractFireBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.SoulFireBlock
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class SoulFireBlock
extends AbstractFireBlock {
    public static final MapCodec<SoulFireBlock> CODEC = SoulFireBlock.createCodec(SoulFireBlock::new);

    public MapCodec<SoulFireBlock> getCodec() {
        return CODEC;
    }

    public SoulFireBlock(AbstractBlock.Settings settings) {
        super(settings, 2.0f);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (this.canPlaceAt(state, world, pos)) {
            return this.getDefaultState();
        }
        return Blocks.AIR.getDefaultState();
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return SoulFireBlock.isSoulBase((BlockState)world.getBlockState(pos.down()));
    }

    public static boolean isSoulBase(BlockState state) {
        return state.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS);
    }

    protected boolean isFlammable(BlockState state) {
        return true;
    }
}

