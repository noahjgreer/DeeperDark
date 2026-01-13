/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.operator;

import net.minecraft.loot.context.LootContext;

@FunctionalInterface
static interface BoundedIntUnaryOperator.Tester {
    public boolean test(LootContext var1, int var2);
}
