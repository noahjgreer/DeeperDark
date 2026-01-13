/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.operator;

import net.minecraft.loot.context.LootContext;

@FunctionalInterface
static interface BoundedIntUnaryOperator.Applier {
    public int apply(LootContext var1, int var2);
}
