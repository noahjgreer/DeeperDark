/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import java.nio.file.Path;

static class SnbtProvider.CompressionException
extends RuntimeException {
    public SnbtProvider.CompressionException(Path path, Throwable cause) {
        super(path.toAbsolutePath().toString(), cause);
    }
}
