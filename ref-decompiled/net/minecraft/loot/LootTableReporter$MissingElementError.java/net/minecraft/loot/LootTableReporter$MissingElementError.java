/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;

public record LootTableReporter.MissingElementError(RegistryKey<?> referenced) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Missing element " + String.valueOf(this.referenced.getValue()) + " of type " + String.valueOf(this.referenced.getRegistry());
    }
}
