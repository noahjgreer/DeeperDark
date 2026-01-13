/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.util.collection;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.collection.WeightedList;

static class WeightedList.Entry.1
implements Codec<WeightedList.Entry<E>> {
    final /* synthetic */ Codec field_30174;

    WeightedList.Entry.1(Codec codec) {
        this.field_30174 = codec;
    }

    public <T> DataResult<Pair<WeightedList.Entry<E>, T>> decode(DynamicOps<T> ops, T data2) {
        Dynamic dynamic = new Dynamic(ops, data2);
        return dynamic.get("data").flatMap(arg_0 -> ((Codec)this.field_30174).parse(arg_0)).map(data -> new WeightedList.Entry<Object>(data, dynamic.get("weight").asInt(1))).map(entry -> Pair.of((Object)entry, (Object)ops.empty()));
    }

    public <T> DataResult<T> encode(WeightedList.Entry<E> entry, DynamicOps<T> dynamicOps, T object) {
        return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(entry.weight)).add("data", this.field_30174.encodeStart(dynamicOps, entry.data)).build(object);
    }

    public /* synthetic */ DataResult encode(Object entries, DynamicOps ops, Object data) {
        return this.encode((WeightedList.Entry)entries, ops, data);
    }
}
