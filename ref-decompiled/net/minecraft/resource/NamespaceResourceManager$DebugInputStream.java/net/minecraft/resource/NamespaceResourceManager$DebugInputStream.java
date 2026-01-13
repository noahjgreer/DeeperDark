/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;
import net.minecraft.util.Identifier;

static class NamespaceResourceManager.DebugInputStream
extends FilterInputStream {
    private final Supplier<String> leakMessage;
    private boolean closed;

    public NamespaceResourceManager.DebugInputStream(InputStream parent, Identifier id, String packId) {
        super(parent);
        Exception exception = new Exception("Stacktrace");
        this.leakMessage = () -> {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            return "Leaked resource: '" + String.valueOf(id) + "' loaded from pack: '" + packId + "'\n" + String.valueOf(stringWriter);
        };
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.closed = true;
    }

    protected void finalize() throws Throwable {
        if (!this.closed) {
            LOGGER.warn("{}", (Object)this.leakMessage.get());
        }
        super.finalize();
    }
}
