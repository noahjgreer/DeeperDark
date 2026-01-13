/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

static interface StatePredicate.ValueMatcher {
    public static final Codec<StatePredicate.ValueMatcher> CODEC = Codec.either(StatePredicate.ExactValueMatcher.CODEC, StatePredicate.RangedValueMatcher.CODEC).xmap(Either::unwrap, valueMatcher -> {
        if (valueMatcher instanceof StatePredicate.ExactValueMatcher) {
            StatePredicate.ExactValueMatcher exactValueMatcher = (StatePredicate.ExactValueMatcher)valueMatcher;
            return Either.left((Object)exactValueMatcher);
        }
        if (valueMatcher instanceof StatePredicate.RangedValueMatcher) {
            StatePredicate.RangedValueMatcher rangedValueMatcher = (StatePredicate.RangedValueMatcher)valueMatcher;
            return Either.right((Object)rangedValueMatcher);
        }
        throw new UnsupportedOperationException();
    });
    public static final PacketCodec<ByteBuf, StatePredicate.ValueMatcher> PACKET_CODEC = PacketCodecs.either(StatePredicate.ExactValueMatcher.PACKET_CODEC, StatePredicate.RangedValueMatcher.PACKET_CODEC).xmap(Either::unwrap, valueMatcher -> {
        if (valueMatcher instanceof StatePredicate.ExactValueMatcher) {
            StatePredicate.ExactValueMatcher exactValueMatcher = (StatePredicate.ExactValueMatcher)valueMatcher;
            return Either.left((Object)exactValueMatcher);
        }
        if (valueMatcher instanceof StatePredicate.RangedValueMatcher) {
            StatePredicate.RangedValueMatcher rangedValueMatcher = (StatePredicate.RangedValueMatcher)valueMatcher;
            return Either.right((Object)rangedValueMatcher);
        }
        throw new UnsupportedOperationException();
    });

    public <T extends Comparable<T>> boolean test(State<?, ?> var1, Property<T> var2);
}
