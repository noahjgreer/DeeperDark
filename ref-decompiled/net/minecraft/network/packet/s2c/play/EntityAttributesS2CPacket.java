package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class EntityAttributesS2CPacket implements Packet {
   public static final PacketCodec CODEC;
   private final int entityId;
   private final List entries;

   public EntityAttributesS2CPacket(int entityId, Collection attributes) {
      this.entityId = entityId;
      this.entries = Lists.newArrayList();
      Iterator var3 = attributes.iterator();

      while(var3.hasNext()) {
         EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)var3.next();
         this.entries.add(new Entry(entityAttributeInstance.getAttribute(), entityAttributeInstance.getBaseValue(), entityAttributeInstance.getModifiers()));
      }

   }

   private EntityAttributesS2CPacket(int entityId, List attributes) {
      this.entityId = entityId;
      this.entries = attributes;
   }

   public PacketType getPacketType() {
      return PlayPackets.UPDATE_ATTRIBUTES;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onEntityAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List getEntries() {
      return this.entries;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, EntityAttributesS2CPacket::getEntityId, EntityAttributesS2CPacket.Entry.CODEC.collect(PacketCodecs.toList()), EntityAttributesS2CPacket::getEntries, EntityAttributesS2CPacket::new);
   }

   public static record Entry(RegistryEntry attribute, double base, Collection modifiers) {
      public static final PacketCodec MODIFIER_CODEC;
      public static final PacketCodec CODEC;

      public Entry(RegistryEntry registryEntry, double baseValue, Collection modifiers) {
         this.attribute = registryEntry;
         this.base = baseValue;
         this.modifiers = modifiers;
      }

      public RegistryEntry attribute() {
         return this.attribute;
      }

      public double base() {
         return this.base;
      }

      public Collection modifiers() {
         return this.modifiers;
      }

      static {
         MODIFIER_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, EntityAttributeModifier::id, PacketCodecs.DOUBLE, EntityAttributeModifier::value, EntityAttributeModifier.Operation.PACKET_CODEC, EntityAttributeModifier::operation, EntityAttributeModifier::new);
         CODEC = PacketCodec.tuple(EntityAttribute.PACKET_CODEC, Entry::attribute, PacketCodecs.DOUBLE, Entry::base, MODIFIER_CODEC.collect(PacketCodecs.toCollection(ArrayList::new)), Entry::modifiers, Entry::new);
      }
   }
}
