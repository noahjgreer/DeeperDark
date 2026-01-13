/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hasher
 *  com.mojang.serialization.DataResult
 */
package net.minecraft.util.dynamic;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.mojang.serialization.DataResult;
import net.minecraft.util.dynamic.AbstractListBuilder;

class HashCodeOps.ListBuilder
extends AbstractListBuilder<HashCode, Hasher> {
    public HashCodeOps.ListBuilder() {
        super(HashCodeOps.this);
    }

    @Override
    protected Hasher initBuilder() {
        return HashCodeOps.this.function.newHasher().putByte((byte)4);
    }

    @Override
    protected Hasher add(Hasher hasher, HashCode hashCode) {
        return hasher.putBytes(hashCode.asBytes());
    }

    @Override
    protected DataResult<HashCode> build(Hasher hasher, HashCode hashCode) {
        assert (hashCode.equals((Object)HashCodeOps.this.empty));
        hasher.putByte((byte)5);
        return DataResult.success((Object)hasher.hash());
    }

    @Override
    protected /* synthetic */ Object initBuilder() {
        return this.initBuilder();
    }
}
