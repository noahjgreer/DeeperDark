/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.util.function.Supplier;

static class Util.5
implements Supplier<T> {
    final /* synthetic */ Supplier field_62782;
    final /* synthetic */ String field_62783;

    Util.5(Supplier supplier, String string) {
        this.field_62782 = supplier;
        this.field_62783 = string;
    }

    @Override
    public T get() {
        return this.field_62782.get();
    }

    public String toString() {
        return this.field_62783;
    }
}
