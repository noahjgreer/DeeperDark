/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.world.World;

class CreakingEntity.CreakingNavigation
extends MobNavigation {
    CreakingEntity.CreakingNavigation(CreakingEntity creaking, World world) {
        super(creaking, world);
    }

    @Override
    public void tick() {
        if (CreakingEntity.this.isUnrooted()) {
            super.tick();
        }
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new CreakingEntity.CreakingLandPathNodeMaker(CreakingEntity.this);
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, range);
    }
}
