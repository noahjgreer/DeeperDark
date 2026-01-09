package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SculkChargeParticleEffect(float roll) implements ParticleEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.FLOAT.fieldOf("roll").forGetter((particleEffect) -> {
         return particleEffect.roll;
      })).apply(instance, SculkChargeParticleEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public SculkChargeParticleEffect(float f) {
      this.roll = f;
   }

   public ParticleType getType() {
      return ParticleTypes.SCULK_CHARGE;
   }

   public float roll() {
      return this.roll;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, (effect) -> {
         return effect.roll;
      }, SculkChargeParticleEffect::new);
   }
}
