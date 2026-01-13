/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import net.minecraft.server.SaveLoading;

@FunctionalInterface
public static interface SaveLoading.LoadContextSupplier<D> {
    public SaveLoading.LoadContext<D> get(SaveLoading.LoadContextSupplierContext var1);
}
