/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import net.minecraft.nbt.NbtType;
import net.minecraft.util.ErrorReporter;

public record NbtReadView.ExpectedNumberError(String name, NbtType<?> actual) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Expected field '" + this.name + "' to contain number, but got " + this.actual.getCrashReportName();
    }
}
