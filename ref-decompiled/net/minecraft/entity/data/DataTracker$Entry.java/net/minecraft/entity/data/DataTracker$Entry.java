/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.data;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;

public static class DataTracker.Entry<T> {
    final TrackedData<T> data;
    T value;
    private final T initialValue;
    private boolean dirty;

    public DataTracker.Entry(TrackedData<T> data, T value) {
        this.data = data;
        this.initialValue = value;
        this.value = value;
    }

    public TrackedData<T> getData() {
        return this.data;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isUnchanged() {
        return this.initialValue.equals(this.value);
    }

    public DataTracker.SerializedEntry<T> toSerialized() {
        return DataTracker.SerializedEntry.of(this.data, this.value);
    }
}
