/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.predicate;

import io.netty.buffer.ByteBuf;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

record StatePredicate.Condition(String key, StatePredicate.ValueMatcher valueMatcher) {
    public static final PacketCodec<ByteBuf, StatePredicate.Condition> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, StatePredicate.Condition::key, StatePredicate.ValueMatcher.PACKET_CODEC, StatePredicate.Condition::valueMatcher, StatePredicate.Condition::new);

    public <S extends State<?, S>> boolean test(StateManager<?, S> stateManager, S state) {
        Property<?> property = stateManager.getProperty(this.key);
        return property != null && this.valueMatcher.test(state, property);
    }

    public Optional<String> reportMissing(StateManager<?, ?> factory) {
        Property<?> property = factory.getProperty(this.key);
        return property != null ? Optional.empty() : Optional.of(this.key);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{StatePredicate.Condition.class, "name;valueMatcher", "key", "valueMatcher"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{StatePredicate.Condition.class, "name;valueMatcher", "key", "valueMatcher"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{StatePredicate.Condition.class, "name;valueMatcher", "key", "valueMatcher"}, this, object);
    }
}
