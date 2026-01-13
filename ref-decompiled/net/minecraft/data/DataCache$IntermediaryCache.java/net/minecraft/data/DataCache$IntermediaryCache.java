/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.hash.HashCode
 */
package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.data.DataCache;

record DataCache.IntermediaryCache(String version, ConcurrentMap<Path, HashCode> data) {
    DataCache.IntermediaryCache(String version) {
        this(version, new ConcurrentHashMap<Path, HashCode>());
    }

    public void put(Path path, HashCode hashCode) {
        this.data.put(path, hashCode);
    }

    public DataCache.CachedData toCachedData() {
        return new DataCache.CachedData(this.version, (ImmutableMap<Path, HashCode>)ImmutableMap.copyOf(this.data));
    }
}
