/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.util.collection;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.collection.ListOperation;

public record ListOperation.Values<T>(List<T> value, ListOperation operation) {
    public static <T> Codec<ListOperation.Values<T>> createCodec(Codec<T> codec, int maxSize) {
        return RecordCodecBuilder.create(instance -> instance.group((App)codec.sizeLimitedListOf(maxSize).fieldOf("values").forGetter(values -> values.value), (App)ListOperation.createCodec(maxSize).forGetter(values -> values.operation)).apply((Applicative)instance, ListOperation.Values::new));
    }

    public List<T> apply(List<T> current) {
        return this.operation.apply(current, this.value);
    }
}
