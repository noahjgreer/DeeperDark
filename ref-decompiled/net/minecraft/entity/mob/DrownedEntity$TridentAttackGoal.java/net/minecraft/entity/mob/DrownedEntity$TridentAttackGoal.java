/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

static class DrownedEntity.TridentAttackGoal
extends ProjectileAttackGoal {
    private final DrownedEntity drowned;

    public DrownedEntity.TridentAttackGoal(RangedAttackMob rangedAttackMob, double d, int i, float f) {
        super(rangedAttackMob, d, i, f);
        this.drowned = (DrownedEntity)rangedAttackMob;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.drowned.getMainHandStack().isOf(Items.TRIDENT);
    }

    @Override
    public void start() {
        super.start();
        this.drowned.setAttacking(true);
        this.drowned.setCurrentHand(Hand.MAIN_HAND);
    }

    @Override
    public void stop() {
        super.stop();
        this.drowned.clearActiveItem();
        this.drowned.setAttacking(false);
    }
}
