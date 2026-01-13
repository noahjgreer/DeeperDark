/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public static class TemptGoal.HappyGhastTemptGoal
extends TemptGoal {
    public TemptGoal.HappyGhastTemptGoal(MobEntity mobEntity, double d, Predicate<ItemStack> predicate, boolean bl, double e) {
        super(mobEntity, d, predicate, bl, e);
    }

    @Override
    protected void stopMoving() {
        this.mob.getMoveControl().setWaiting();
    }

    @Override
    protected void startMovingTo(PlayerEntity player) {
        Vec3d vec3d = player.getEyePos().subtract(this.mob.getEntityPos()).multiply(this.mob.getRandom().nextDouble()).add(this.mob.getEntityPos());
        this.mob.getMoveControl().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
    }
}
