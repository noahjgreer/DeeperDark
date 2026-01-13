/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.loottable;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;

public record LootTableProvider.MissingTableError(RegistryKey<LootTable> id) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Missing built-in table: " + String.valueOf(this.id.getValue());
    }
}
