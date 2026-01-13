/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.logging;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import net.minecraft.util.logging.LogFileCompressor;
import org.jspecify.annotations.Nullable;

public static interface LogFileCompressor.LogFile {
    public Path path();

    public LogFileCompressor.LogId id();

    public @Nullable Reader getReader() throws IOException;

    public LogFileCompressor.Compressed compress() throws IOException;
}
