/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package net.minecraft.text;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

static class TextCodecs.1
implements Codec<Text> {
    final /* synthetic */ int field_55909;

    TextCodecs.1(int i) {
        this.field_55909 = i;
    }

    public <T> DataResult<Pair<Text, T>> decode(DynamicOps<T> ops, T value) {
        return CODEC.decode(ops, value).flatMap(pair -> {
            if (this.isTooLarge(ops, (Text)pair.getFirst())) {
                return DataResult.error(() -> "Component was too large: greater than max size " + this.field_55909);
            }
            return DataResult.success((Object)pair);
        });
    }

    public <T> DataResult<T> encode(Text text, DynamicOps<T> dynamicOps, T object) {
        return CODEC.encodeStart(dynamicOps, (Object)text);
    }

    private <T> boolean isTooLarge(DynamicOps<T> ops, Text text) {
        DataResult dataResult = CODEC.encodeStart(TextCodecs.1.toJsonOps(ops), (Object)text);
        return dataResult.isSuccess() && JsonHelper.isTooLarge((JsonElement)dataResult.getOrThrow(), this.field_55909);
    }

    private static <T> DynamicOps<JsonElement> toJsonOps(DynamicOps<T> ops) {
        if (ops instanceof RegistryOps) {
            RegistryOps registryOps = (RegistryOps)ops;
            return registryOps.withDelegate(JsonOps.INSTANCE);
        }
        return JsonOps.INSTANCE;
    }

    public /* synthetic */ DataResult encode(Object input, DynamicOps ops, Object prefix) {
        return this.encode((Text)input, ops, prefix);
    }
}
