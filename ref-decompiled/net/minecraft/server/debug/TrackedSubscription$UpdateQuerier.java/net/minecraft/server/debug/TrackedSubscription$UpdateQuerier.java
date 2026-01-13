/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.debug;

import java.util.Objects;
import net.minecraft.world.debug.DebugSubscriptionType;
import net.minecraft.world.debug.DebugTrackable;
import org.jspecify.annotations.Nullable;

static class TrackedSubscription.UpdateQuerier<T> {
    private final DebugTrackable.DebugDataSupplier<T> dataSupplier;
    @Nullable T lastData;

    TrackedSubscription.UpdateQuerier(DebugTrackable.DebugDataSupplier<T> dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    public @Nullable DebugSubscriptionType.OptionalValue<T> queryUpdate(DebugSubscriptionType<T> type) {
        T object = this.dataSupplier.get();
        if (!Objects.equals(object, this.lastData)) {
            this.lastData = object;
            return type.optionalValueFor(object);
        }
        return null;
    }
}
