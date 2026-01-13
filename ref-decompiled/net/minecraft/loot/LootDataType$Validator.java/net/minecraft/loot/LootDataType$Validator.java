/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import net.minecraft.loot.LootTableReporter;
import net.minecraft.registry.RegistryKey;

@FunctionalInterface
public static interface LootDataType.Validator<T> {
    public void run(LootTableReporter var1, RegistryKey<T> var2, T var3);
}
