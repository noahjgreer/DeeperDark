/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SporeBlossomBlock
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class SporeBlossomBlock
extends Block {
    public static final MapCodec<SporeBlossomBlock> CODEC = SporeBlossomBlock.createCodec(SporeBlossomBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)12.0, (double)13.0, (double)16.0);
    private static final int field_31252 = 14;
    private static final int field_31253 = 10;
    private static final int field_31254 = 10;

    public MapCodec<SporeBlossomBlock> getCodec() {
        return CODEC;
    }

    public SporeBlossomBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare((WorldView)world, (BlockPos)pos.up(), (Direction)Direction.DOWN) && !world.isWater(pos);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.UP && !this.canPlaceAt(state, world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        double d = (double)i + random.nextDouble();
        double e = (double)j + 0.7;
        double f = (double)k + random.nextDouble();
        world.addParticleClient((ParticleEffect)ParticleTypes.FALLING_SPORE_BLOSSOM, d, e, f, 0.0, 0.0, 0.0);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int l = 0; l < 14; ++l) {
            mutable.set(i + MathHelper.nextInt((Random)random, (int)-10, (int)10), j - random.nextInt(10), k + MathHelper.nextInt((Random)random, (int)-10, (int)10));
            BlockState blockState = world.getBlockState((BlockPos)mutable);
            if (blockState.isFullCube((BlockView)world, (BlockPos)mutable)) continue;
            world.addParticleClient((ParticleEffect)ParticleTypes.SPORE_BLOSSOM_AIR, (double)mutable.getX() + random.nextDouble(), (double)mutable.getY() + random.nextDouble(), (double)mutable.getZ() + random.nextDouble(), 0.0, 0.0, 0.0);
        }
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}

