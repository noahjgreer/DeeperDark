/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.predicate.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.component.ComponentPredicate;

public static abstract class ComponentPredicate.TypeImpl<T extends ComponentPredicate>
implements ComponentPredicate.Type<T> {
    private final Codec<T> predicateCodec;
    private final MapCodec<ComponentPredicate.Typed<T>> typedCodec;
    private final PacketCodec<RegistryByteBuf, ComponentPredicate.Typed<T>> packetCodec;

    public ComponentPredicate.TypeImpl(Codec<T> predicateCodec) {
        this.predicateCodec = predicateCodec;
        this.typedCodec = ComponentPredicate.Typed.getCodec(this, predicateCodec);
        this.packetCodec = PacketCodecs.registryCodec(predicateCodec).xmap(predicate -> new ComponentPredicate.Typed<ComponentPredicate>(this, (ComponentPredicate)predicate), ComponentPredicate.Typed::predicate);
    }

    @Override
    public Codec<T> getPredicateCodec() {
        return this.predicateCodec;
    }

    @Override
    public MapCodec<ComponentPredicate.Typed<T>> getTypedCodec() {
        return this.typedCodec;
    }

    @Override
    public PacketCodec<RegistryByteBuf, ComponentPredicate.Typed<T>> getTypedPacketCodec() {
        return this.packetCodec;
    }
}
