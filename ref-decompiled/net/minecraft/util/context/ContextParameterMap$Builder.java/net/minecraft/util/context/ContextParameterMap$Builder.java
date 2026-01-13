/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.context;

import com.google.common.collect.Sets;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import org.jspecify.annotations.Nullable;

public static class ContextParameterMap.Builder {
    private final Map<ContextParameter<?>, Object> map = new IdentityHashMap();

    public <T> ContextParameterMap.Builder add(ContextParameter<T> parameter, T value) {
        this.map.put(parameter, value);
        return this;
    }

    public <T> ContextParameterMap.Builder addNullable(ContextParameter<T> parameter, @Nullable T value) {
        if (value == null) {
            this.map.remove(parameter);
        } else {
            this.map.put(parameter, value);
        }
        return this;
    }

    public <T> T getOrThrow(ContextParameter<T> parameter) {
        Object object = this.map.get(parameter);
        if (object == null) {
            throw new NoSuchElementException(parameter.getId().toString());
        }
        return (T)object;
    }

    public <T> @Nullable T getNullable(ContextParameter<T> parameter) {
        return (T)this.map.get(parameter);
    }

    public ContextParameterMap build(ContextType type) {
        Sets.SetView set = Sets.difference(this.map.keySet(), type.getAllowed());
        if (!set.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + String.valueOf(set));
        }
        Sets.SetView set2 = Sets.difference(type.getRequired(), this.map.keySet());
        if (!set2.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameters: " + String.valueOf(set2));
        }
        return new ContextParameterMap(this.map);
    }
}
