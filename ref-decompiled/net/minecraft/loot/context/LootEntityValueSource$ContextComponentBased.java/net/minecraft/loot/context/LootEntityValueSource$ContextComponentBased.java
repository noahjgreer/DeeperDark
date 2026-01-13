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

public static interface LootEntityValueSource.ContextComponentBased<T, R>
extends LootEntityValueSource<R> {
    public @Nullable R get(T var1);

    @Override
    public ContextParameter<? extends T> contextParam();

    @Override
    default public @Nullable R get(LootContext context) {
        T object = context.get(this.contextParam());
        return object != null ? (R)this.get(object) : null;
    }
}
