package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public record ExplosionS2CPacket(Vec3d center, Optional playerKnockback, ParticleEffect explosionParticle, RegistryEntry explosionSound) implements Packet {
   public static final PacketCodec CODEC;

   public ExplosionS2CPacket(Vec3d vec3d, Optional optional, ParticleEffect particleEffect, RegistryEntry registryEntry) {
      this.center = vec3d;
      this.playerKnockback = optional;
      this.explosionParticle = particleEffect;
      this.explosionSound = registryEntry;
   }

   public PacketType getPacketType() {
      return PlayPackets.EXPLODE;
   }

   public void apply(ClientPlayPacketListener clientPlayPacketListener) {
      clientPlayPacketListener.onExplosion(this);
   }

   public Vec3d center() {
      return this.center;
   }

   public Optional playerKnockback() {
      return this.playerKnockback;
   }

   public ParticleEffect explosionParticle() {
      return this.explosionParticle;
   }

   public RegistryEntry explosionSound() {
      return this.explosionSound;
   }

   static {
      CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, ExplosionS2CPacket::center, Vec3d.PACKET_CODEC.collect(PacketCodecs::optional), ExplosionS2CPacket::playerKnockback, ParticleTypes.PACKET_CODEC, ExplosionS2CPacket::explosionParticle, SoundEvent.ENTRY_PACKET_CODEC, ExplosionS2CPacket::explosionSound, ExplosionS2CPacket::new);
   }
}
