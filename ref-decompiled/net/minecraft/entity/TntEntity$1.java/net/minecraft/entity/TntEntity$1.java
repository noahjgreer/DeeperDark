/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

class TntEntity.1
extends ExplosionBehavior {
    TntEntity.1() {
    }

    @Override
    public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
        if (state.isOf(Blocks.NETHER_PORTAL)) {
            return false;
        }
        return super.canDestroyBlock(explosion, world, pos, state, power);
    }

    @Override
    public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (blockState.isOf(Blocks.NETHER_PORTAL)) {
            return Optional.empty();
        }
        return super.getBlastResistance(explosion, world, pos, blockState, fluidState);
    }
}
