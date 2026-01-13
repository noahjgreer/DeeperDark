/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.effect;

import java.util.ArrayList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
protected static interface OozingStatusEffect.SlimeCounter {
    public int count(int var1);

    public static OozingStatusEffect.SlimeCounter around(LivingEntity entity) {
        return limit -> {
            ArrayList list = new ArrayList();
            entity.getEntityWorld().collectEntitiesByType(EntityType.SLIME, entity.getBoundingBox().expand(2.0), slime -> slime != entity, list, limit);
            return list.size();
        };
    }
}
