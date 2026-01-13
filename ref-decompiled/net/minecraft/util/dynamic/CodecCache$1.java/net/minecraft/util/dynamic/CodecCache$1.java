/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.util.dynamic;

import com.google.common.cache.CacheLoader;
import com.mojang.serialization.DataResult;
import net.minecraft.util.dynamic.CodecCache;

class CodecCache.1
extends CacheLoader<CodecCache.Key<?, ?>, DataResult<?>> {
    CodecCache.1(CodecCache codecCache) {
    }

    public DataResult<?> load(CodecCache.Key<?, ?> key) {
        return key.encode();
    }

    public /* synthetic */ Object load(Object key) throws Exception {
        return this.load((CodecCache.Key)key);
    }
}
