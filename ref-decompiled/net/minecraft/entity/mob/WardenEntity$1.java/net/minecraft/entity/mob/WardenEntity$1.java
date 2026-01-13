/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.world.World;

class WardenEntity.1
extends MobNavigation {
    WardenEntity.1(WardenEntity wardenEntity, MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new LandPathNodeMaker();
        return new PathNodeNavigator(this, this.nodeMaker, range){

            @Override
            protected float getDistance(PathNode a, PathNode b) {
                return a.getHorizontalDistance(b);
            }
        };
    }
}
