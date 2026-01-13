/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;

public record LootTableReporter.ReferenceNotAllowedError(RegistryKey<?> referenced) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Reference to " + String.valueOf(this.referenced.getValue()) + " of type " + String.valueOf(this.referenced.getRegistry()) + " was used, but references are not allowed";
    }
}
