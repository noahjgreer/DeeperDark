/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.logging;

import java.io.IOException;
import net.minecraft.util.logging.LogReader;
import org.jspecify.annotations.Nullable;

class LogWriter.1
implements LogReader<T> {
    private volatile long pos;
    final /* synthetic */ LogReader field_41299;

    LogWriter.1(LogReader logReader) {
        this.field_41299 = logReader;
    }

    @Override
    public @Nullable T read() throws IOException {
        try {
            LogWriter.this.channel.position(this.pos);
            Object t = this.field_41299.read();
            return t;
        }
        finally {
            this.pos = LogWriter.this.channel.position();
        }
    }

    @Override
    public void close() throws IOException {
        LogWriter.this.closeIfNotReferenced();
    }
}
