/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.EndRodBlock
 *  net.minecraft.block.RodBlock
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RodBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EndRodBlock
extends RodBlock {
    public static final MapCodec<EndRodBlock> CODEC = EndRodBlock.createCodec(EndRodBlock::new);

    public MapCodec<EndRodBlock> getCodec() {
        return CODEC;
    }

    public EndRodBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.UP));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction.getOpposite()));
        if (blockState.isOf((Block)this) && blockState.get((Property)FACING) == direction) {
            return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)direction.getOpposite());
        }
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)direction);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction direction = (Direction)state.get((Property)FACING);
        double d = (double)pos.getX() + 0.55 - (double)(random.nextFloat() * 0.1f);
        double e = (double)pos.getY() + 0.55 - (double)(random.nextFloat() * 0.1f);
        double f = (double)pos.getZ() + 0.55 - (double)(random.nextFloat() * 0.1f);
        double g = 0.4f - (random.nextFloat() + random.nextFloat()) * 0.4f;
        if (random.nextInt(5) == 0) {
            world.addParticleClient((ParticleEffect)ParticleTypes.END_ROD, d + (double)direction.getOffsetX() * g, e + (double)direction.getOffsetY() * g, f + (double)direction.getOffsetZ() * g, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }
}

