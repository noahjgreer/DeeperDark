/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.WolfEntity;

class WolfEntity.AvoidLlamaGoal<T extends LivingEntity>
extends FleeEntityGoal<T> {
    private final WolfEntity wolf;

    public WolfEntity.AvoidLlamaGoal(WolfEntity wolf, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
        super(wolf, fleeFromType, distance, slowSpeed, fastSpeed);
        this.wolf = wolf;
    }

    @Override
    public boolean canStart() {
        if (super.canStart() && this.targetEntity instanceof LlamaEntity) {
            return !this.wolf.isTamed() && this.isScaredOf((LlamaEntity)this.targetEntity);
        }
        return false;
    }

    private boolean isScaredOf(LlamaEntity llama) {
        return llama.getStrength() >= WolfEntity.this.random.nextInt(5);
    }

    @Override
    public void start() {
        WolfEntity.this.setTarget(null);
        super.start();
    }

    @Override
    public void tick() {
        WolfEntity.this.setTarget(null);
        super.tick();
    }
}
