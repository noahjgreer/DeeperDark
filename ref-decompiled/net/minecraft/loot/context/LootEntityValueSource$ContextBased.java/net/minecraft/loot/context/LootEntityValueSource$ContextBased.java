/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.loot.context;

import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootEntityValueSource;
import net.minecraft.util.context.ContextParameter;
import org.jspecify.annotations.Nullable;

public static interface LootEntityValueSource.ContextBased<T>
extends LootEntityValueSource<T> {
    @Override
    public ContextParameter<? extends T> contextParam();

    @Override
    default public @Nullable T get(LootContext context) {
        return context.get(this.contextParam());
    }
}
