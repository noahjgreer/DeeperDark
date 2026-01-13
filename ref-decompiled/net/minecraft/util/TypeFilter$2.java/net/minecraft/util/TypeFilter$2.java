/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import net.minecraft.util.TypeFilter;
import org.jspecify.annotations.Nullable;

static class TypeFilter.2
implements TypeFilter<B, T> {
    final /* synthetic */ Class field_47524;

    TypeFilter.2(Class class_) {
        this.field_47524 = class_;
    }

    @Override
    public @Nullable T downcast(B obj) {
        return this.field_47524.equals(obj.getClass()) ? obj : null;
    }

    @Override
    public Class<? extends B> getBaseClass() {
        return this.field_47524;
    }
}
