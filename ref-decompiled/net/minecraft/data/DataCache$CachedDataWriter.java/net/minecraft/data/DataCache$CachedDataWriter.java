/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 */
package net.minecraft.data;

import com.google.common.hash.HashCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataWriter;

static class DataCache.CachedDataWriter
implements DataWriter {
    private final String providerName;
    private final DataCache.CachedData oldCache;
    private final DataCache.IntermediaryCache newCache;
    private final AtomicInteger cacheMissCount = new AtomicInteger();
    private volatile boolean closed;

    DataCache.CachedDataWriter(String providerName, String version, DataCache.CachedData oldCache) {
        this.providerName = providerName;
        this.oldCache = oldCache;
        this.newCache = new DataCache.IntermediaryCache(version);
    }

    private boolean isCacheInvalid(Path path, HashCode hashCode) {
        return !Objects.equals(this.oldCache.get(path), hashCode) || !Files.exists(path, new LinkOption[0]);
    }

    @Override
    public void write(Path path, byte[] data, HashCode hashCode) throws IOException {
        if (this.closed) {
            throw new IllegalStateException("Cannot write to cache as it has already been closed");
        }
        if (this.isCacheInvalid(path, hashCode)) {
            this.cacheMissCount.incrementAndGet();
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Files.write(path, data, new OpenOption[0]);
        }
        this.newCache.put(path, hashCode);
    }

    public DataCache.RunResult finish() {
        this.closed = true;
        return new DataCache.RunResult(this.providerName, this.newCache.toCachedData(), this.cacheMissCount.get());
    }
}
