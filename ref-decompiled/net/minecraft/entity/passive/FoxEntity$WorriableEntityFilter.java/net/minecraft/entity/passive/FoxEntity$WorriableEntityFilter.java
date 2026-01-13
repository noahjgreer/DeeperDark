/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FoxEntity.WorriableEntityFilter
implements TargetPredicate.EntityPredicate {
    @Override
    public boolean test(LivingEntity livingEntity, ServerWorld serverWorld) {
        PlayerEntity playerEntity;
        if (livingEntity instanceof FoxEntity) {
            return false;
        }
        if (livingEntity instanceof ChickenEntity || livingEntity instanceof RabbitEntity || livingEntity instanceof HostileEntity) {
            return true;
        }
        if (livingEntity instanceof TameableEntity) {
            return !((TameableEntity)livingEntity).isTamed();
        }
        if (livingEntity instanceof PlayerEntity && ((playerEntity = (PlayerEntity)livingEntity).isSpectator() || playerEntity.isCreative())) {
            return false;
        }
        if (FoxEntity.this.canTrust(livingEntity)) {
            return false;
        }
        return !livingEntity.isSleeping() && !livingEntity.isSneaky();
    }
}
