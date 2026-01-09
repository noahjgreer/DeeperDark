package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class ShriekParticleEffect implements ParticleEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.INT.fieldOf("delay").forGetter((particleEffect) -> {
         return particleEffect.delay;
      })).apply(instance, ShriekParticleEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;
   private final int delay;

   public ShriekParticleEffect(int delay) {
      this.delay = delay;
   }

   public ParticleType getType() {
      return ParticleTypes.SHRIEK;
   }

   public int getDelay() {
      return this.delay;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, (effect) -> {
         return effect.delay;
      }, ShriekParticleEffect::new);
   }
}
