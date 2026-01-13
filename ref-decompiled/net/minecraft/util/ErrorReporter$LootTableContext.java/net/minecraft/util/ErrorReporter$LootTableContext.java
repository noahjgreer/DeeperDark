/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;

public record ErrorReporter.LootTableContext(RegistryKey<?> id) implements ErrorReporter.Context
{
    @Override
    public String getName() {
        return "{" + String.valueOf(this.id.getValue()) + "@" + String.valueOf(this.id.getRegistry()) + "}";
    }
}
