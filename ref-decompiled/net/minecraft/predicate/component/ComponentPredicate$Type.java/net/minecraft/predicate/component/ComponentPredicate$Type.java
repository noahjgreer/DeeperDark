/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.predicate.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.component.ComponentPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;

public static interface ComponentPredicate.Type<T extends ComponentPredicate> {
    public static final Codec<ComponentPredicate.Type<?>> CODEC = Codec.either(Registries.DATA_COMPONENT_PREDICATE_TYPE.getCodec(), Registries.DATA_COMPONENT_TYPE.getCodec()).xmap(ComponentPredicate.Type::toType, ComponentPredicate.Type::fromType);
    public static final PacketCodec<RegistryByteBuf, ComponentPredicate.Type<?>> PACKET_CODEC = PacketCodecs.either(PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE), PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_TYPE)).xmap(ComponentPredicate.Type::toType, ComponentPredicate.Type::fromType);

    private static <T extends ComponentPredicate.Type<?>> Either<T, ComponentType<?>> fromType(T type) {
        Either either;
        if (type instanceof ComponentPredicate.OfExistence) {
            ComponentPredicate.OfExistence ofExistence = (ComponentPredicate.OfExistence)type;
            either = Either.right(ofExistence.getComponentType());
        } else {
            either = Either.left(type);
        }
        return either;
    }

    private static ComponentPredicate.Type<?> toType(Either<ComponentPredicate.Type<?>, ComponentType<?>> either) {
        return (ComponentPredicate.Type)either.map(type -> type, ComponentPredicate.OfExistence::toPredicateType);
    }

    public Codec<T> getPredicateCodec();

    public MapCodec<ComponentPredicate.Typed<T>> getTypedCodec();

    public PacketCodec<RegistryByteBuf, ComponentPredicate.Typed<T>> getTypedPacketCodec();
}
