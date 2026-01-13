/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.world.World;

static class FrogEntity.FrogSwimNavigation
extends AmphibiousSwimNavigation {
    FrogEntity.FrogSwimNavigation(FrogEntity frog, World world) {
        super(frog, world);
    }

    @Override
    public boolean canJumpToNext(PathNodeType nodeType) {
        return nodeType != PathNodeType.WATER_BORDER && super.canJumpToNext(nodeType);
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new FrogEntity.FrogSwimPathNodeMaker(true);
        return new PathNodeNavigator(this.nodeMaker, range);
    }
}
