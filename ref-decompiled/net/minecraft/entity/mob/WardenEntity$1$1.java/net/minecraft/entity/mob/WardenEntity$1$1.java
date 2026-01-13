/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.WardenEntity;

class WardenEntity.1
extends PathNodeNavigator {
    WardenEntity.1(WardenEntity.1 arg, PathNodeMaker pathNodeMaker, int i) {
        super(pathNodeMaker, i);
    }

    @Override
    protected float getDistance(PathNode a, PathNode b) {
        return a.getHorizontalDistance(b);
    }
}
