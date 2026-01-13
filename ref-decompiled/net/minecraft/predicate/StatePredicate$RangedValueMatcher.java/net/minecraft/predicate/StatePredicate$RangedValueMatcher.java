/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

record StatePredicate.RangedValueMatcher(Optional<String> min, Optional<String> max) implements StatePredicate.ValueMatcher
{
    public static final Codec<StatePredicate.RangedValueMatcher> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.optionalFieldOf("min").forGetter(StatePredicate.RangedValueMatcher::min), (App)Codec.STRING.optionalFieldOf("max").forGetter(StatePredicate.RangedValueMatcher::max)).apply((Applicative)instance, StatePredicate.RangedValueMatcher::new));
    public static final PacketCodec<ByteBuf, StatePredicate.RangedValueMatcher> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(PacketCodecs.STRING), StatePredicate.RangedValueMatcher::min, PacketCodecs.optional(PacketCodecs.STRING), StatePredicate.RangedValueMatcher::max, StatePredicate.RangedValueMatcher::new);

    @Override
    public <T extends Comparable<T>> boolean test(State<?, ?> state, Property<T> property) {
        Optional<T> optional;
        Comparable comparable = state.get(property);
        if (this.min.isPresent() && ((optional = property.parse(this.min.get())).isEmpty() || comparable.compareTo((Comparable)((Comparable)optional.get())) < 0)) {
            return false;
        }
        return !this.max.isPresent() || !(optional = property.parse(this.max.get())).isEmpty() && comparable.compareTo((Comparable)((Comparable)optional.get())) <= 0;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{StatePredicate.RangedValueMatcher.class, "minValue;maxValue", "min", "max"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StatePredicate.RangedValueMatcher.class, "minValue;maxValue", "min", "max"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StatePredicate.RangedValueMatcher.class, "minValue;maxValue", "min", "max"}, this, object);
    }
}
