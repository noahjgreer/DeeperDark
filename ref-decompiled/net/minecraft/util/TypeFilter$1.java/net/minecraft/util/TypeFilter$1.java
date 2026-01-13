/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import net.minecraft.util.TypeFilter;
import org.jspecify.annotations.Nullable;

static class TypeFilter.1
implements TypeFilter<B, T> {
    final /* synthetic */ Class field_27257;

    TypeFilter.1(Class class_) {
        this.field_27257 = class_;
    }

    @Override
    public @Nullable T downcast(B obj) {
        return this.field_27257.isInstance(obj) ? obj : null;
    }

    @Override
    public Class<? extends B> getBaseClass() {
        return this.field_27257;
    }
}
