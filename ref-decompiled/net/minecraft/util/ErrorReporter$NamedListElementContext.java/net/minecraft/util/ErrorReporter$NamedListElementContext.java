/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.ErrorReporter;

public record ErrorReporter.NamedListElementContext(String key, int index) implements ErrorReporter.Context
{
    @Override
    public String getName() {
        return "." + this.key + "[" + this.index + "]";
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorReporter.NamedListElementContext.class, "name;index", "key", "index"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorReporter.NamedListElementContext.class, "name;index", "key", "index"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorReporter.NamedListElementContext.class, "name;index", "key", "index"}, this, object);
    }
}
