/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec$ResultFunction
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import org.apache.commons.lang3.mutable.MutableObject;

static class Codecs.1
implements Codec.ResultFunction<A> {
    final /* synthetic */ Object field_35173;

    Codecs.1(Object object) {
        this.field_35173 = object;
    }

    public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> ops, T input, DataResult<Pair<A, T>> result) {
        MutableObject mutableObject = new MutableObject();
        Optional optional = result.resultOrPartial(arg_0 -> ((MutableObject)mutableObject).setValue(arg_0));
        if (optional.isPresent()) {
            return result;
        }
        return DataResult.error(() -> "(" + (String)mutableObject.get() + " -> using default)", (Object)Pair.of((Object)this.field_35173, input));
    }

    public <T> DataResult<T> coApply(DynamicOps<T> ops, A input, DataResult<T> result) {
        return result;
    }

    public String toString() {
        return "OrElsePartial[" + String.valueOf(this.field_35173) + "]";
    }
}
