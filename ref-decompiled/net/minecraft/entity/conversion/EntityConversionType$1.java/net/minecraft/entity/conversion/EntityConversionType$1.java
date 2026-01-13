/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.conversion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.conversion.EntityConversionType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;

final class EntityConversionType.1
extends EntityConversionType {
    EntityConversionType.1(boolean bl) {
    }

    @Override
    void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
        Entity entity3;
        Entity entity = oldEntity.getFirstPassenger();
        newEntity.copyPositionAndRotation(oldEntity);
        newEntity.setVelocity(oldEntity.getVelocity());
        if (entity != null) {
            entity.stopRiding();
            entity.ridingCooldown = 0;
            for (Entity entity2 : newEntity.getPassengerList()) {
                entity2.stopRiding();
                entity2.remove(Entity.RemovalReason.DISCARDED);
            }
            entity.startRiding(newEntity);
        }
        if ((entity3 = oldEntity.getVehicle()) != null) {
            oldEntity.stopRiding();
            newEntity.startRiding(entity3, false, false);
        }
        if (context.keepEquipment()) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
                ItemStack itemStack = oldEntity.getEquippedStack(equipmentSlot);
                if (itemStack.isEmpty()) continue;
                newEntity.equipStack(equipmentSlot, itemStack.copyAndEmpty());
                newEntity.setEquipmentDropChance(equipmentSlot, oldEntity.getEquipmentDropChances().get(equipmentSlot));
            }
        }
        newEntity.fallDistance = oldEntity.fallDistance;
        newEntity.setFlag(7, oldEntity.isGliding());
        newEntity.playerHitTimer = oldEntity.playerHitTimer;
        newEntity.hurtTime = oldEntity.hurtTime;
        newEntity.bodyYaw = oldEntity.bodyYaw;
        newEntity.setOnGround(oldEntity.isOnGround());
        oldEntity.getSleepingPosition().ifPresent(newEntity::setSleepingPosition);
        Entity entity4 = oldEntity.getLeashHolder();
        if (entity4 != null) {
            newEntity.attachLeash(entity4, true);
        }
        this.copyData(oldEntity, newEntity, context);
    }
}
