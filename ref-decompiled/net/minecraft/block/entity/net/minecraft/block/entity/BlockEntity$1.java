/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.Set;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import org.jspecify.annotations.Nullable;

class BlockEntity.1
implements ComponentsAccess {
    final /* synthetic */ Set field_50173;
    final /* synthetic */ ComponentMap field_50174;

    BlockEntity.1() {
        this.field_50173 = set;
        this.field_50174 = componentMap;
    }

    @Override
    public <T> @Nullable T get(ComponentType<? extends T> type) {
        this.field_50173.add(type);
        return this.field_50174.get(type);
    }

    @Override
    public <T> T getOrDefault(ComponentType<? extends T> type, T fallback) {
        this.field_50173.add(type);
        return this.field_50174.getOrDefault(type, fallback);
    }
}
