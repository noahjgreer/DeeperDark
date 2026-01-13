/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

static class ShulkerEntity.TargetOtherTeamGoal
extends ActiveTargetGoal<LivingEntity> {
    public ShulkerEntity.TargetOtherTeamGoal(ShulkerEntity shulker) {
        super(shulker, LivingEntity.class, 10, true, false, (entity, world) -> entity instanceof Monster);
    }

    @Override
    public boolean canStart() {
        if (this.mob.getScoreboardTeam() == null) {
            return false;
        }
        return super.canStart();
    }

    @Override
    protected Box getSearchBox(double distance) {
        Direction direction = ((ShulkerEntity)this.mob).getAttachedFace();
        if (direction.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().expand(4.0, distance, distance);
        }
        if (direction.getAxis() == Direction.Axis.Z) {
            return this.mob.getBoundingBox().expand(distance, distance, 4.0);
        }
        return this.mob.getBoundingBox().expand(distance, 4.0, distance);
    }
}
