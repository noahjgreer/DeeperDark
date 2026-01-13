/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class SimpleMultipartModelSelector.Term
extends Record {
    final String value;
    final boolean negated;
    private static final String NEGATED_PREFIX = "!";

    public SimpleMultipartModelSelector.Term(String value, boolean negated) {
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Empty term");
        }
        this.value = value;
        this.negated = negated;
    }

    public static SimpleMultipartModelSelector.Term parse(String value) {
        if (value.startsWith(NEGATED_PREFIX)) {
            return new SimpleMultipartModelSelector.Term(value.substring(1), true);
        }
        return new SimpleMultipartModelSelector.Term(value, false);
    }

    @Override
    public String toString() {
        return this.negated ? NEGATED_PREFIX + this.value : this.value;
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SimpleMultipartModelSelector.Term.class, "value;negated", "value", "negated"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SimpleMultipartModelSelector.Term.class, "value;negated", "value", "negated"}, this, object);
    }

    public String value() {
        return this.value;
    }

    public boolean negated() {
        return this.negated;
    }
}
