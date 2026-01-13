/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.ErrorReporter;

static final class ErrorReporter.Impl.ErrorEntry
extends Record {
    final ErrorReporter.Impl source;
    final ErrorReporter.Error error;

    ErrorReporter.Impl.ErrorEntry(ErrorReporter.Impl source, ErrorReporter.Error error) {
        this.source = source;
        this.error = error;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorReporter.Impl.ErrorEntry.class, "source;problem", "source", "error"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorReporter.Impl.ErrorEntry.class, "source;problem", "source", "error"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorReporter.Impl.ErrorEntry.class, "source;problem", "source", "error"}, this, object);
    }

    public ErrorReporter.Impl source() {
        return this.source;
    }

    public ErrorReporter.Error error() {
        return this.error;
    }
}
