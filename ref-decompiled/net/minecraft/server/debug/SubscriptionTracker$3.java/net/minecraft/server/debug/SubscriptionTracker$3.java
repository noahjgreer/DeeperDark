/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.debug;

import net.minecraft.entity.Entity;
import net.minecraft.world.debug.DebugSubscriptionType;
import net.minecraft.world.debug.DebugTrackable;

class SubscriptionTracker.3
implements DebugTrackable.Tracker {
    final /* synthetic */ Entity field_62896;

    SubscriptionTracker.3() {
        this.field_62896 = entity;
    }

    @Override
    public <T> void track(DebugSubscriptionType<T> type, DebugTrackable.DebugDataSupplier<T> dataSupplier) {
        SubscriptionTracker.this.get(type).trackEntity(this.field_62896.getUuid(), dataSupplier);
    }
}
