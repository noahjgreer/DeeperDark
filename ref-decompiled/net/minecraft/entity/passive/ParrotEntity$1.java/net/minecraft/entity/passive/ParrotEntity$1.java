/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.passive;

import java.util.function.Predicate;
import net.minecraft.entity.mob.MobEntity;
import org.jspecify.annotations.Nullable;

class ParrotEntity.1
implements Predicate<MobEntity> {
    ParrotEntity.1() {
    }

    @Override
    public boolean test(@Nullable MobEntity mobEntity) {
        return mobEntity != null && MOB_SOUNDS.containsKey(mobEntity.getType());
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object entity) {
        return this.test((MobEntity)entity);
    }
}
