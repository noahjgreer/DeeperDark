/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public record EntityAttributesS2CPacket.Entry(RegistryEntry<EntityAttribute> attribute, double base, Collection<EntityAttributeModifier> modifiers) {
    public static final PacketCodec<ByteBuf, EntityAttributeModifier> MODIFIER_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, EntityAttributeModifier::id, PacketCodecs.DOUBLE, EntityAttributeModifier::value, EntityAttributeModifier.Operation.PACKET_CODEC, EntityAttributeModifier::operation, EntityAttributeModifier::new);
    public static final PacketCodec<RegistryByteBuf, EntityAttributesS2CPacket.Entry> CODEC = PacketCodec.tuple(EntityAttribute.PACKET_CODEC, EntityAttributesS2CPacket.Entry::attribute, PacketCodecs.DOUBLE, EntityAttributesS2CPacket.Entry::base, MODIFIER_CODEC.collect(PacketCodecs.toCollection(ArrayList::new)), EntityAttributesS2CPacket.Entry::modifiers, EntityAttributesS2CPacket.Entry::new);
}
