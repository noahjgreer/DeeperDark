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

public static class WeightedList.Entry<T> {
    final T data;
    final int weight;
    private double shuffledOrder;

    WeightedList.Entry(T data, int weight) {
        this.weight = weight;
        this.data = data;
    }

    private double getShuffledOrder() {
        return this.shuffledOrder;
    }

    void setShuffledOrder(float random) {
        this.shuffledOrder = -Math.pow(random, 1.0f / (float)this.weight);
    }

    public T getElement() {
        return this.data;
    }

    public int getWeight() {
        return this.weight;
    }

    public String toString() {
        return this.weight + ":" + String.valueOf(this.data);
    }

    public static <E> Codec<WeightedList.Entry<E>> createCodec(final Codec<E> codec) {
        return new Codec<WeightedList.Entry<E>>(){

            public <T> DataResult<Pair<WeightedList.Entry<E>, T>> decode(DynamicOps<T> ops, T data2) {
                Dynamic dynamic = new Dynamic(ops, data2);
                return dynamic.get("data").flatMap(arg_0 -> ((Codec)codec).parse(arg_0)).map(data -> new WeightedList.Entry<Object>(data, dynamic.get("weight").asInt(1))).map(entry -> Pair.of((Object)entry, (Object)ops.empty()));
            }

            public <T> DataResult<T> encode(WeightedList.Entry<E> entry, DynamicOps<T> dynamicOps, T object) {
                return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(entry.weight)).add("data", codec.encodeStart(dynamicOps, entry.data)).build(object);
            }

            public /* synthetic */ DataResult encode(Object entries, DynamicOps ops, Object data) {
                return this.encode((WeightedList.Entry)entries, ops, data);
            }
        };
    }
}
