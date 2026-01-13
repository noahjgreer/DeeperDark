/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.context;

import net.minecraft.loot.LootDataType;

public record LootContext.Entry<T>(LootDataType<T> type, T value) {
}
