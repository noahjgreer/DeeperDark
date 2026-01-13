/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.debug;

import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.debug.DebugSubscriptionType;
import net.minecraft.world.debug.DebugTrackable;

class SubscriptionTracker.1
implements DebugTrackable.Tracker {
    final /* synthetic */ WorldChunk field_62892;

    SubscriptionTracker.1() {
        this.field_62892 = worldChunk;
    }

    @Override
    public <T> void track(DebugSubscriptionType<T> type, DebugTrackable.DebugDataSupplier<T> dataSupplier) {
        SubscriptionTracker.this.get(type).trackChunk(this.field_62892.getPos(), dataSupplier);
    }
}
