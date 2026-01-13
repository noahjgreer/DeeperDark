/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.conversion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.conversion.EntityConversionType;
import net.minecraft.entity.mob.MobEntity;

final class EntityConversionType.2
extends EntityConversionType {
    EntityConversionType.2(boolean bl) {
    }

    @Override
    void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
        Entity entity2;
        Entity entity = oldEntity.getFirstPassenger();
        if (entity != null) {
            entity.stopRiding();
        }
        if ((entity2 = oldEntity.getLeashHolder()) != null) {
            oldEntity.detachLeash();
        }
        this.copyData(oldEntity, newEntity, context);
    }
}
