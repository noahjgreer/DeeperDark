/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.crash;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

static final class SuppressedExceptionsTracker.Key
extends Record {
    final String location;
    final Class<? extends Throwable> cls;

    SuppressedExceptionsTracker.Key(String location, Class<? extends Throwable> cls) {
        this.location = location;
        this.cls = cls;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SuppressedExceptionsTracker.Key.class, "location;cls", "location", "cls"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SuppressedExceptionsTracker.Key.class, "location;cls", "location", "cls"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SuppressedExceptionsTracker.Key.class, "location;cls", "location", "cls"}, this, object);
    }

    public String location() {
        return this.location;
    }

    public Class<? extends Throwable> cls() {
        return this.cls;
    }
}
