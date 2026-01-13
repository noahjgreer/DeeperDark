/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot;

import java.util.Set;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.context.ContextParameter;

public record LootTableReporter.ParametersNotProvidedError(Set<ContextParameter<?>> notProvided) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Parameters " + String.valueOf(this.notProvided) + " are not provided in this context";
    }
}
