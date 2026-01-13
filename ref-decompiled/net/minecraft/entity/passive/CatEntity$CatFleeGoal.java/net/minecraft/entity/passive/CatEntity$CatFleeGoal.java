/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.predicate.entity.EntityPredicates;

static class CatEntity.CatFleeGoal<T extends LivingEntity>
extends FleeEntityGoal<T> {
    private final CatEntity cat;

    public CatEntity.CatFleeGoal(CatEntity cat, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
        super(cat, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
        this.cat = cat;
    }

    @Override
    public boolean canStart() {
        return !this.cat.isTamed() && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        return !this.cat.isTamed() && super.shouldContinue();
    }
}
