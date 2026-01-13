/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.util.context;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;

public static class ContextType.Builder {
    private final Set<ContextParameter<?>> required = Sets.newIdentityHashSet();
    private final Set<ContextParameter<?>> allowed = Sets.newIdentityHashSet();

    public ContextType.Builder require(ContextParameter<?> parameter) {
        if (this.allowed.contains(parameter)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(parameter.getId()) + " is already optional");
        }
        this.required.add(parameter);
        return this;
    }

    public ContextType.Builder allow(ContextParameter<?> parameter) {
        if (this.required.contains(parameter)) {
            throw new IllegalArgumentException("Parameter " + String.valueOf(parameter.getId()) + " is already required");
        }
        this.allowed.add(parameter);
        return this;
    }

    public ContextType build() {
        return new ContextType(this.required, this.allowed);
    }
}
