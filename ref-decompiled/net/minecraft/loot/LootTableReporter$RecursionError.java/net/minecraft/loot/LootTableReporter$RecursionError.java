/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;

public record LootTableReporter.RecursionError(RegistryKey<?> referenced) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return String.valueOf(this.referenced.getValue()) + " of type " + String.valueOf(this.referenced.getRegistry()) + " is recursively called";
    }
}
