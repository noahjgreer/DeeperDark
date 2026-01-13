/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.LlamaEntity;

static class LlamaEntity.SpitRevengeGoal
extends RevengeGoal {
    public LlamaEntity.SpitRevengeGoal(LlamaEntity llama) {
        super(llama, new Class[0]);
    }

    @Override
    public boolean shouldContinue() {
        MobEntity mobEntity = this.mob;
        if (mobEntity instanceof LlamaEntity) {
            LlamaEntity llamaEntity = (LlamaEntity)mobEntity;
            if (llamaEntity.spit) {
                llamaEntity.setSpit(false);
                return false;
            }
        }
        return super.shouldContinue();
    }
}
