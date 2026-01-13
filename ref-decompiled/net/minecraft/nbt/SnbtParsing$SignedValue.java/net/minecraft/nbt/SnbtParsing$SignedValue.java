/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.nbt.SnbtParsing;

static final class SnbtParsing.SignedValue<T>
extends Record {
    private final SnbtParsing.Sign sign;
    final T value;

    SnbtParsing.SignedValue(SnbtParsing.Sign sign, T value) {
        this.sign = sign;
        this.value = value;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SnbtParsing.SignedValue.class, "sign;value", "sign", "value"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SnbtParsing.SignedValue.class, "sign;value", "sign", "value"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SnbtParsing.SignedValue.class, "sign;value", "sign", "value"}, this, object);
    }

    public SnbtParsing.Sign sign() {
        return this.sign;
    }

    public T value() {
        return this.value;
    }
}
