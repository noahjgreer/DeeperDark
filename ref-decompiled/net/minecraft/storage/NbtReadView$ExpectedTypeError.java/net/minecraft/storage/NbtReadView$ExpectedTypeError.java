/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.storage;

import net.minecraft.nbt.NbtType;
import net.minecraft.util.ErrorReporter;

public record NbtReadView.ExpectedTypeError(String name, NbtType<?> expected, NbtType<?> actual) implements ErrorReporter.Error
{
    @Override
    public String getMessage() {
        return "Expected field '" + this.name + "' to contain value of type " + this.expected.getCrashReportName() + ", but got " + this.actual.getCrashReportName();
    }
}
