/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

class RespawnAnchorBlock.1
extends ExplosionBehavior {
    final /* synthetic */ BlockPos field_25404;
    final /* synthetic */ boolean field_25405;

    RespawnAnchorBlock.1(RespawnAnchorBlock respawnAnchorBlock, BlockPos blockPos, boolean bl) {
        this.field_25404 = blockPos;
        this.field_25405 = bl;
    }

    @Override
    public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (pos.equals(this.field_25404) && this.field_25405) {
            return Optional.of(Float.valueOf(Blocks.WATER.getBlastResistance()));
        }
        return super.getBlastResistance(explosion, world, pos, blockState, fluidState);
    }
}
