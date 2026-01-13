/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.passive.HappyGhastEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

static class HappyGhastEntity.GhastlingNavigation
extends BirdNavigation {
    public HappyGhastEntity.GhastlingNavigation(HappyGhastEntity entity, World world) {
        super(entity, world);
        this.setCanOpenDoors(false);
        this.setCanSwim(true);
        this.setMaxFollowRange(48.0f);
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target) {
        return HappyGhastEntity.GhastlingNavigation.doesNotCollide(this.entity, origin, target, false);
    }
}
