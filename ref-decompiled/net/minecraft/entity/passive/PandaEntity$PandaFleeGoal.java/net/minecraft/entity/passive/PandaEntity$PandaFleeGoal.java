/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.predicate.entity.EntityPredicates;

static class PandaEntity.PandaFleeGoal<T extends LivingEntity>
extends FleeEntityGoal<T> {
    private final PandaEntity panda;

    public PandaEntity.PandaFleeGoal(PandaEntity panda, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
        super(panda, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_SPECTATOR);
        this.panda = panda;
    }

    @Override
    public boolean canStart() {
        return this.panda.isWorried() && this.panda.isIdle() && super.canStart();
    }
}
