/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.RecordBuilder$AbstractUniversalBuilder
 */
package net.minecraft.util.dynamic;

import com.google.common.hash.HashCode;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.dynamic.HashCodeOps;

final class HashCodeOps.Builder
extends RecordBuilder.AbstractUniversalBuilder<HashCode, List<Pair<HashCode, HashCode>>> {
    public HashCodeOps.Builder() {
        super((DynamicOps)HashCodeOps.this);
    }

    protected List<Pair<HashCode, HashCode>> initBuilder() {
        return new ArrayList<Pair<HashCode, HashCode>>();
    }

    protected List<Pair<HashCode, HashCode>> append(HashCode hashCode, HashCode hashCode2, List<Pair<HashCode, HashCode>> list) {
        list.add((Pair<HashCode, HashCode>)Pair.of((Object)hashCode, (Object)hashCode2));
        return list;
    }

    protected DataResult<HashCode> build(List<Pair<HashCode, HashCode>> list, HashCode hashCode) {
        assert (HashCodeOps.this.isEmpty(hashCode));
        return DataResult.success((Object)HashCodeOps.hash(HashCodeOps.this.function.newHasher(), list.stream()).hash());
    }

    protected /* synthetic */ Object append(Object object, Object object2, Object object3) {
        return this.append((HashCode)object, (HashCode)object2, (List)object3);
    }

    protected /* synthetic */ DataResult build(Object object, Object object2) {
        return this.build((List)object, (HashCode)object2);
    }

    protected /* synthetic */ Object initBuilder() {
        return this.initBuilder();
    }
}
