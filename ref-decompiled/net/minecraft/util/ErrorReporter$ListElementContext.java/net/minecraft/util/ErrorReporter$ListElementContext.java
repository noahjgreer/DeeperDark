/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.ErrorReporter;

public record ErrorReporter.ListElementContext(int index) implements ErrorReporter.Context
{
    @Override
    public String getName() {
        return "[" + this.index + "]";
    }
}
