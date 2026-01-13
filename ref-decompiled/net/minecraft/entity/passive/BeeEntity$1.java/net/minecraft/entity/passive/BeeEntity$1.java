/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class BeeEntity.1
extends BirdNavigation {
    BeeEntity.1(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        return !this.world.getBlockState(pos.down()).isAir();
    }

    @Override
    public void tick() {
        if (BeeEntity.this.pollinateGoal.isRunning()) {
            return;
        }
        super.tick();
    }
}
