/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.WolfEntity;

static class LlamaEntity.ChaseWolvesGoal
extends ActiveTargetGoal<WolfEntity> {
    public LlamaEntity.ChaseWolvesGoal(LlamaEntity llama) {
        super(llama, WolfEntity.class, 16, false, true, (wolf, world) -> !((WolfEntity)wolf).isTamed());
    }

    @Override
    protected double getFollowRange() {
        return super.getFollowRange() * 0.25;
    }
}
