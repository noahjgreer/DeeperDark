/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.debug;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.debug.DebugSubscriptionType;
import net.minecraft.world.debug.DebugTrackable;

class SubscriptionTracker.2
implements DebugTrackable.Tracker {
    final /* synthetic */ BlockEntity field_62894;

    SubscriptionTracker.2() {
        this.field_62894 = blockEntity;
    }

    @Override
    public <T> void track(DebugSubscriptionType<T> type, DebugTrackable.DebugDataSupplier<T> dataSupplier) {
        SubscriptionTracker.this.get(type).trackBlockEntity(this.field_62894.getPos(), dataSupplier);
    }
}
