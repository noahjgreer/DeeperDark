/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import org.jspecify.annotations.Nullable;

static final class SnbtParsing.NumberSuffix
extends Record {
    final  @Nullable SnbtParsing.Signedness signed;
    final  @Nullable SnbtParsing.NumericType type;
    public static final SnbtParsing.NumberSuffix DEFAULT = new SnbtParsing.NumberSuffix(null, null);

    SnbtParsing.NumberSuffix( @Nullable SnbtParsing.Signedness signed,  @Nullable SnbtParsing.NumericType type) {
        this.signed = signed;
        this.type = type;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SnbtParsing.NumberSuffix.class, "signed;type", "signed", "type"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SnbtParsing.NumberSuffix.class, "signed;type", "signed", "type"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SnbtParsing.NumberSuffix.class, "signed;type", "signed", "type"}, this, object);
    }

    public  @Nullable SnbtParsing.Signedness signed() {
        return this.signed;
    }

    public  @Nullable SnbtParsing.NumericType type() {
        return this.type;
    }
}
