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
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import org.jspecify.annotations.Nullable;

static class ComponentMap.2
implements ComponentMap {
    final /* synthetic */ ComponentMap field_51459;
    final /* synthetic */ ComponentMap field_51460;

    ComponentMap.2() {
        this.field_51459 = componentMap;
        this.field_51460 = componentMap2;
    }

    @Override
    public <T> @Nullable T get(ComponentType<? extends T> type) {
        T object = this.field_51459.get(type);
        if (object != null) {
            return object;
        }
        return this.field_51460.get(type);
    }

    @Override
    public Set<ComponentType<?>> getTypes() {
        return Sets.union(this.field_51460.getTypes(), this.field_51459.getTypes());
    }
}
