/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;

protected static class TraderLlamaEntity.DefendTraderGoal
extends TrackTargetGoal {
    private final LlamaEntity llama;
    private LivingEntity offender;
    private int traderLastAttackedTime;

    public TraderLlamaEntity.DefendTraderGoal(LlamaEntity llama) {
        super(llama, false);
        this.llama = llama;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (!this.llama.isLeashed()) {
            return false;
        }
        Entity entity = this.llama.getLeashHolder();
        if (!(entity instanceof WanderingTraderEntity)) {
            return false;
        }
        WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)entity;
        this.offender = wanderingTraderEntity.getAttacker();
        int i = wanderingTraderEntity.getLastAttackedTime();
        return i != this.traderLastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.offender);
        Entity entity = this.llama.getLeashHolder();
        if (entity instanceof WanderingTraderEntity) {
            this.traderLastAttackedTime = ((WanderingTraderEntity)entity).getLastAttackedTime();
        }
        super.start();
    }
}
