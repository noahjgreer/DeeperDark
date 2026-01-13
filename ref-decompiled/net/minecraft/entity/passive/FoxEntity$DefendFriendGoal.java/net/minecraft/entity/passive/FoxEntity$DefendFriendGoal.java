/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import org.jspecify.annotations.Nullable;

class FoxEntity.DefendFriendGoal
extends ActiveTargetGoal<LivingEntity> {
    private @Nullable LivingEntity offender;
    private @Nullable LivingEntity friend;
    private int lastAttackedTime;

    public FoxEntity.DefendFriendGoal(Class<LivingEntity> targetEntityClass, boolean checkVisibility, @Nullable boolean checkCanNavigate, TargetPredicate.EntityPredicate targetPredicate) {
        super(FoxEntity.this, targetEntityClass, 10, checkVisibility, checkCanNavigate, targetPredicate);
    }

    @Override
    public boolean canStart() {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        }
        ServerWorld serverWorld = FoxEntity.DefendFriendGoal.castToServerWorld(FoxEntity.this.getEntityWorld());
        for (LazyEntityReference<LivingEntity> lazyEntityReference : FoxEntity.this.getTrustedEntities().toList()) {
            LivingEntity livingEntity = lazyEntityReference.getEntityByClass(serverWorld, LivingEntity.class);
            if (livingEntity == null) continue;
            this.friend = livingEntity;
            this.offender = livingEntity.getAttacker();
            int i = livingEntity.getLastAttackedTime();
            return i != this.lastAttackedTime && this.canTrack(this.offender, this.targetPredicate);
        }
        return false;
    }

    @Override
    public void start() {
        this.setTargetEntity(this.offender);
        this.targetEntity = this.offender;
        if (this.friend != null) {
            this.lastAttackedTime = this.friend.getLastAttackedTime();
        }
        FoxEntity.this.playSound(SoundEvents.ENTITY_FOX_AGGRO, 1.0f, 1.0f);
        FoxEntity.this.setAggressive(true);
        FoxEntity.this.stopSleeping();
        super.start();
    }
}
