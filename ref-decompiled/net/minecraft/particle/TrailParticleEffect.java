package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;

public record TrailParticleEffect(Vec3d target, int color, int duration) implements ParticleEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Vec3d.CODEC.fieldOf("target").forGetter(TrailParticleEffect::target), Codecs.RGB.fieldOf("color").forGetter(TrailParticleEffect::color), Codecs.POSITIVE_INT.fieldOf("duration").forGetter(TrailParticleEffect::duration)).apply(instance, TrailParticleEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public TrailParticleEffect(Vec3d vec3d, int i, int j) {
      this.target = vec3d;
      this.color = i;
      this.duration = j;
   }

   public ParticleType getType() {
      return ParticleTypes.TRAIL;
   }

   public Vec3d target() {
      return this.target;
   }

   public int color() {
      return this.color;
   }

   public int duration() {
      return this.duration;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, TrailParticleEffect::target, PacketCodecs.INTEGER, TrailParticleEffect::color, PacketCodecs.VAR_INT, TrailParticleEffect::duration, TrailParticleEffect::new);
   }
}
