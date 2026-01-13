/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;

class ShulkerEntity.TargetPlayerGoal
extends ActiveTargetGoal<PlayerEntity> {
    public ShulkerEntity.TargetPlayerGoal(ShulkerEntity shulker) {
        super((MobEntity)shulker, PlayerEntity.class, true);
    }

    @Override
    public boolean canStart() {
        if (ShulkerEntity.this.getEntityWorld().getDifficulty() == Difficulty.PEACEFUL) {
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
