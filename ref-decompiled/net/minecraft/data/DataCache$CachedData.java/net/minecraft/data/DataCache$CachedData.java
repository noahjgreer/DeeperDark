/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.hash.HashCode
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.data.DataCache;
import org.jspecify.annotations.Nullable;

static final class DataCache.CachedData
extends Record {
    final String version;
    private final ImmutableMap<Path, HashCode> data;

    DataCache.CachedData(String version, ImmutableMap<Path, HashCode> data) {
        this.version = version;
        this.data = data;
    }

    public @Nullable HashCode get(Path path) {
        return (HashCode)this.data.get((Object)path);
    }

    public int size() {
        return this.data.size();
    }

    public static DataCache.CachedData parseCache(Path root, Path dataProviderPath) throws IOException {
        try (BufferedReader bufferedReader = Files.newBufferedReader(dataProviderPath, StandardCharsets.UTF_8);){
            String string = bufferedReader.readLine();
            if (!string.startsWith(DataCache.HEADER)) {
                throw new IllegalStateException("Missing cache file header");
            }
            String[] strings = string.substring(DataCache.HEADER.length()).split("\t", 2);
            String string2 = strings[0];
            ImmutableMap.Builder builder = ImmutableMap.builder();
            bufferedReader.lines().forEach(line -> {
                int i = line.indexOf(32);
                builder.put((Object)root.resolve(line.substring(i + 1)), (Object)HashCode.fromString((String)line.substring(0, i)));
            });
            DataCache.CachedData cachedData = new DataCache.CachedData(string2, (ImmutableMap<Path, HashCode>)builder.build());
            return cachedData;
        }
    }

    public void write(Path root, Path dataProviderPath, String description) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(dataProviderPath, StandardCharsets.UTF_8, new OpenOption[0]);){
            bufferedWriter.write(DataCache.HEADER);
            bufferedWriter.write(this.version);
            bufferedWriter.write(9);
            bufferedWriter.write(description);
            bufferedWriter.newLine();
            for (Map.Entry entry : this.data.entrySet()) {
                bufferedWriter.write(((HashCode)entry.getValue()).toString());
                bufferedWriter.write(32);
                bufferedWriter.write(root.relativize((Path)entry.getKey()).toString());
                bufferedWriter.newLine();
            }
        }
        catch (IOException iOException) {
            LOGGER.warn("Unable write cachefile {}: {}", (Object)dataProviderPath, (Object)iOException);
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{DataCache.CachedData.class, "version;data", "version", "data"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{DataCache.CachedData.class, "version;data", "version", "data"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{DataCache.CachedData.class, "version;data", "version", "data"}, this, object);
    }

    public String version() {
        return this.version;
    }

    public ImmutableMap<Path, HashCode> data() {
        return this.data;
    }
}
