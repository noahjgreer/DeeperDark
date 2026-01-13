/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import net.minecraft.nbt.NbtType;
import net.minecraft.util.ErrorReporter;

public record NbtReadView.ExpectedTypeAtIndexError(String name, int index, NbtType<?> expected, NbtType<?> actual) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Expected list '" + this.name + "' to contain at index " + this.index + " value of type " + this.expected.getCrashReportName() + ", but got " + this.actual.getCrashReportName();
    }
}
