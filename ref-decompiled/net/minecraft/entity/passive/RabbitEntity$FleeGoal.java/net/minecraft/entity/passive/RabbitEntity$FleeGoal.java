/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.RabbitEntity;

static class RabbitEntity.FleeGoal<T extends LivingEntity>
extends FleeEntityGoal<T> {
    private final RabbitEntity rabbit;

    public RabbitEntity.FleeGoal(RabbitEntity rabbit, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
        super(rabbit, fleeFromType, distance, slowSpeed, fastSpeed);
        this.rabbit = rabbit;
    }

    @Override
    public boolean canStart() {
        return this.rabbit.getVariant() != RabbitEntity.Variant.EVIL && super.canStart();
    }
}
