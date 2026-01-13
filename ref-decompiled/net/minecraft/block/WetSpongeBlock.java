/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.WetSpongeBlock
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.EnvironmentAttributes
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class WetSpongeBlock
extends Block {
    public static final MapCodec<WetSpongeBlock> CODEC = WetSpongeBlock.createCodec(WetSpongeBlock::new);

    public MapCodec<WetSpongeBlock> getCodec() {
        return CODEC;
    }

    public WetSpongeBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (((Boolean)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.WATER_EVAPORATES_GAMEPLAY, pos)).booleanValue()) {
            world.setBlockState(pos, Blocks.SPONGE.getDefaultState(), 3);
            world.syncWorldEvent(2009, pos, 0);
            world.playSound(null, pos, SoundEvents.BLOCK_WET_SPONGE_DRIES, SoundCategory.BLOCKS, 1.0f, (1.0f + world.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Direction direction = Direction.random((Random)random);
        if (direction == Direction.UP) {
            return;
        }
        BlockPos blockPos = pos.offset(direction);
        BlockState blockState = world.getBlockState(blockPos);
        if (state.isOpaque() && blockState.isSideSolidFullSquare((BlockView)world, blockPos, direction.getOpposite())) {
            return;
        }
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        if (direction == Direction.DOWN) {
            e -= 0.05;
            d += random.nextDouble();
            f += random.nextDouble();
        } else {
            e += random.nextDouble() * 0.8;
            if (direction.getAxis() == Direction.Axis.X) {
                f += random.nextDouble();
                d = direction == Direction.EAST ? (d += 1.1) : (d += 0.05);
            } else {
                d += random.nextDouble();
                f = direction == Direction.SOUTH ? (f += 1.1) : (f += 0.05);
            }
        }
        world.addParticleClient((ParticleEffect)ParticleTypes.DRIPPING_WATER, d, e, f, 0.0, 0.0, 0.0);
    }
}

