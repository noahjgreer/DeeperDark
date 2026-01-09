package net.minecraft.network.packet.s2c.play;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record RemoveEntityStatusEffectS2CPacket(int entityId, RegistryEntry effect) implements Packet {
   public static final PacketCodec CODEC;

   public RemoveEntityStatusEffectS2CPacket(int entityId, RegistryEntry registryEntry) {
      this.entityId = entityId;
      this.effect = registryEntry;
   }

   public PacketType getPacketType() {
      return PlayPackets.REMOVE_MOB_EFFECT;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onRemoveEntityStatusEffect(this);
   }

   @Nullable
   public Entity getEntity(World world) {
      return world.getEntityById(this.entityId);
   }

   public int entityId() {
      return this.entityId;
   }

   public RegistryEntry effect() {
      return this.effect;
   }

   static {
      CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, RemoveEntityStatusEffectS2CPacket::entityId, StatusEffect.ENTRY_PACKET_CODEC, RemoveEntityStatusEffectS2CPacket::effect, RemoveEntityStatusEffectS2CPacket::new);
   }
}
