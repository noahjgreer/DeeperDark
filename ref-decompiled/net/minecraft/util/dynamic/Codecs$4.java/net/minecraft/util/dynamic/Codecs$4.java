/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec$ResultFunction
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.function.Function;

static class Codecs.4
implements Codec.ResultFunction<E> {
    final /* synthetic */ Function field_35664;
    final /* synthetic */ Function field_35665;

    Codecs.4(Function function, Function function2) {
        this.field_35664 = function;
        this.field_35665 = function2;
    }

    public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<E, T>> result) {
        return result.result().map(pair -> result.setLifecycle((Lifecycle)this.field_35664.apply(pair.getFirst()))).orElse(result);
    }

    public <T> DataResult<T> coApply(DynamicOps<T> ops, E input, DataResult<T> result) {
        return result.setLifecycle((Lifecycle)this.field_35665.apply(input));
    }

    public String toString() {
        return "WithLifecycle[" + String.valueOf(this.field_35664) + " " + String.valueOf(this.field_35665) + "]";
    }
}
