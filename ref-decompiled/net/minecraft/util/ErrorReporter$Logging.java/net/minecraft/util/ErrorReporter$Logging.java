/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import net.minecraft.util.ErrorReporter;
import org.slf4j.Logger;

public static class ErrorReporter.Logging
extends ErrorReporter.Impl
implements AutoCloseable {
    private final Logger logger;

    public ErrorReporter.Logging(Logger logger) {
        this.logger = logger;
    }

    public ErrorReporter.Logging(ErrorReporter.Context context, Logger logger) {
        super(context);
        this.logger = logger;
    }

    @Override
    public void close() {
        if (!this.isEmpty()) {
            this.logger.warn("[{}] Serialization errors:\n{}", (Object)this.logger.getName(), (Object)this.getErrorsAsLongString());
        }
    }
}
