/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.data;

import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import org.jspecify.annotations.Nullable;

public static class DataTracker.Builder {
    private final DataTracked entity;
    private final @Nullable DataTracker.Entry<?>[] entries;

    public DataTracker.Builder(DataTracked entity) {
        this.entity = entity;
        this.entries = new DataTracker.Entry[CLASS_TO_LAST_ID.getNext(entity.getClass())];
    }

    public <T> DataTracker.Builder add(TrackedData<T> data, T value) {
        int i = data.id();
        if (i > this.entries.length) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + this.entries.length + ")");
        }
        if (this.entries[i] != null) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        }
        if (TrackedDataHandlerRegistry.getId(data.dataType()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + String.valueOf(data.dataType()) + " for " + i + "!");
        }
        this.entries[data.id()] = new DataTracker.Entry<T>(data, value);
        return this;
    }

    public DataTracker build() {
        for (int i = 0; i < this.entries.length; ++i) {
            if (this.entries[i] != null) continue;
            throw new IllegalStateException("Entity " + String.valueOf(this.entity.getClass()) + " has not defined synched data value " + i);
        }
        return new DataTracker(this.entity, this.entries);
    }
}
