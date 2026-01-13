/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.logging;

import java.io.IOException;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraft.util.logging.LogFileCompressor;
import org.jspecify.annotations.Nullable;

public record LogFileCompressor.Uncompressed(Path path, LogFileCompressor.LogId id) implements LogFileCompressor.LogFile
{
    public FileChannel open() throws IOException {
        return FileChannel.open(this.path, StandardOpenOption.WRITE, StandardOpenOption.READ);
    }

    @Override
    public @Nullable Reader getReader() throws IOException {
        return Files.exists(this.path, new LinkOption[0]) ? Files.newBufferedReader(this.path) : null;
    }

    @Override
    public LogFileCompressor.Compressed compress() throws IOException {
        Path path = this.path.resolveSibling(this.path.getFileName().toString() + LogFileCompressor.GZ_EXTENSION);
        LogFileCompressor.compress(this.path, path);
        return new LogFileCompressor.Compressed(path, this.id);
    }
}
