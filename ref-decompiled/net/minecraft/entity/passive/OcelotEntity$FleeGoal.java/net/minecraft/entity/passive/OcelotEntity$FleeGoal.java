/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.predicate.entity.EntityPredicates;

static class OcelotEntity.FleeGoal<T extends LivingEntity>
extends FleeEntityGoal<T> {
    private final OcelotEntity ocelot;

    public OcelotEntity.FleeGoal(OcelotEntity ocelot, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
        super(ocelot, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
        this.ocelot = ocelot;
    }

    @Override
    public boolean canStart() {
        return !this.ocelot.isTrusting() && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return !this.ocelot.isTrusting() && super.shouldContinue();
    }
}
