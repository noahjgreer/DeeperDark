/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import org.jspecify.annotations.Nullable;

class ComponentMap.3
implements ComponentMap {
    final /* synthetic */ Predicate field_51461;

    ComponentMap.3() {
        this.field_51461 = predicate;
    }

    @Override
    public <T> @Nullable T get(ComponentType<? extends T> type) {
        return this.field_51461.test(type) ? (T)ComponentMap.this.get(type) : null;
    }

    @Override
    public Set<ComponentType<?>> getTypes() {
        return Sets.filter(ComponentMap.this.getTypes(), this.field_51461::test);
    }
}
