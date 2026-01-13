/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.datafixer.DataFixTypes;

class DataFixTypes.1
implements Codec<A> {
    final /* synthetic */ Codec field_46087;
    final /* synthetic */ int field_46088;
    final /* synthetic */ DataFixer field_46089;

    DataFixTypes.1(Codec codec, int i, DataFixer dataFixer) {
        this.field_46087 = codec;
        this.field_46088 = i;
        this.field_46089 = dataFixer;
    }

    public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
        return this.field_46087.encode(input, ops, prefix).flatMap(encoded -> ops.mergeToMap(encoded, ops.createString("DataVersion"), ops.createInt(DataFixTypes.getSaveVersionId())));
    }

    public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
        int i = ops.get(input, "DataVersion").flatMap(arg_0 -> ops.getNumberValue(arg_0)).map(Number::intValue).result().orElse(this.field_46088);
        Dynamic dynamic = new Dynamic(ops, ops.remove(input, "DataVersion"));
        Dynamic dynamic2 = DataFixTypes.this.update(this.field_46089, dynamic, i);
        return this.field_46087.decode(dynamic2);
    }
}
