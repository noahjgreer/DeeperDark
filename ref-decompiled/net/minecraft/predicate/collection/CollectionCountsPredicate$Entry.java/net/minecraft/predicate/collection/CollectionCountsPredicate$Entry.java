/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.predicate.collection;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Predicate;
import net.minecraft.predicate.NumberRange;

public record CollectionCountsPredicate.Entry<T, P extends Predicate<T>>(P test, NumberRange.IntRange count) {
    public static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate.Entry<T, P>> createCodec(Codec<P> predicateCodec) {
        return RecordCodecBuilder.create(instance -> instance.group((App)predicateCodec.fieldOf("test").forGetter(CollectionCountsPredicate.Entry::test), (App)NumberRange.IntRange.CODEC.fieldOf("count").forGetter(CollectionCountsPredicate.Entry::count)).apply((Applicative)instance, CollectionCountsPredicate.Entry::new));
    }

    public boolean test(Iterable<T> collection) {
        int i = 0;
        for (T object : collection) {
            if (!this.test.test(object)) continue;
            ++i;
        }
        return this.count.test(i);
    }
}
