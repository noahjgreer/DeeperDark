/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

record StatePredicate.ExactValueMatcher(String value) implements StatePredicate.ValueMatcher
{
    public static final Codec<StatePredicate.ExactValueMatcher> CODEC = Codec.STRING.xmap(StatePredicate.ExactValueMatcher::new, StatePredicate.ExactValueMatcher::value);
    public static final PacketCodec<ByteBuf, StatePredicate.ExactValueMatcher> PACKET_CODEC = PacketCodecs.STRING.xmap(StatePredicate.ExactValueMatcher::new, StatePredicate.ExactValueMatcher::value);

    @Override
    public <T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property) {
        Comparable comparable = state.get(property);
        Optional<T> optional = property.parse(this.value);
        return optional.isPresent() && comparable.compareTo((Comparable)((Comparable)optional.get())) == 0;
    }
}
