/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

static class TurtleEntity.TurtleSwimNavigation
extends AmphibiousSwimNavigation {
    TurtleEntity.TurtleSwimNavigation(TurtleEntity owner, World world) {
        super(owner, world);
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        MobEntity mobEntity = this.entity;
        if (mobEntity instanceof TurtleEntity) {
            TurtleEntity turtleEntity = (TurtleEntity)mobEntity;
            if (turtleEntity.travelPos != null) {
                return this.world.getBlockState(pos).isOf(Blocks.WATER);
            }
        }
        return !this.world.getBlockState(pos.down()).isAir();
    }
}
