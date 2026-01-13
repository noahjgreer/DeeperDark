/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.util.function.Predicate;

@FunctionalInterface
public static interface VariantSelectorProvider.SelectorCondition<C>
extends Predicate<C> {
    public static <C> VariantSelectorProvider.SelectorCondition<C> alwaysTrue() {
        return context -> true;
    }
}
