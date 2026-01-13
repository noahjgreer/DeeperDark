/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.context;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public static interface LootWorldContext.DynamicDrop {
    public void add(Consumer<ItemStack> var1);
}
