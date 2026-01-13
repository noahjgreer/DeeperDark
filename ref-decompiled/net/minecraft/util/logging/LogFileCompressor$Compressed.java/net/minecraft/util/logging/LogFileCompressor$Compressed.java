/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import net.minecraft.util.logging.LogFileCompressor;
import org.jspecify.annotations.Nullable;

public record LogFileCompressor.Compressed(Path path, LogFileCompressor.LogId id) implements LogFileCompressor.LogFile
{
    @Override
    public @Nullable Reader getReader() throws IOException {
        if (!Files.exists(this.path, new LinkOption[0])) {
            return null;
        }
        return new BufferedReader(new InputStreamReader((InputStream)new GZIPInputStream(Files.newInputStream(this.path, new OpenOption[0])), StandardCharsets.UTF_8));
    }

    @Override
    public LogFileCompressor.Compressed compress() {
        return this;
    }
}
