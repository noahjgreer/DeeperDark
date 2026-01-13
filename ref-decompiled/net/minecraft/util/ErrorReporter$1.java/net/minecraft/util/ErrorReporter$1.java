/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import net.minecraft.util.ErrorReporter;

class ErrorReporter.1
implements ErrorReporter {
    ErrorReporter.1() {
    }

    @Override
    public ErrorReporter makeChild(ErrorReporter.Context context) {
        return this;
    }

    @Override
    public void report(ErrorReporter.Error error) {
    }
}
