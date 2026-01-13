/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import org.jspecify.annotations.Nullable;

class ComponentMap.1
implements ComponentMap {
    ComponentMap.1() {
    }

    @Override
    public <T> @Nullable T get(ComponentType<? extends T> type) {
        return null;
    }

    @Override
    public Set<ComponentType<?>> getTypes() {
        return Set.of();
    }

    @Override
    public Iterator<Component<?>> iterator() {
        return Collections.emptyIterator();
    }
}
