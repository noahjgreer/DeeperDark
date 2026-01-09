package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;

public class TintedParticleEffect implements ParticleEffect {
   private final ParticleType type;
   private final int color;

   public static MapCodec createCodec(ParticleType type) {
      return Codecs.ARGB.xmap((color) -> {
         return new TintedParticleEffect(type, color);
      }, (effect) -> {
         return effect.color;
      }).fieldOf("color");
   }

   public static PacketCodec createPacketCodec(ParticleType type) {
      return PacketCodecs.INTEGER.xmap((color) -> {
         return new TintedParticleEffect(type, color);
      }, (particleEffect) -> {
         return particleEffect.color;
      });
   }

   private TintedParticleEffect(ParticleType type, int color) {
      this.type = type;
      this.color = color;
   }

   public ParticleType getType() {
      return this.type;
   }

   public float getRed() {
      return (float)ColorHelper.getRed(this.color) / 255.0F;
   }

   public float getGreen() {
      return (float)ColorHelper.getGreen(this.color) / 255.0F;
   }

   public float getBlue() {
      return (float)ColorHelper.getBlue(this.color) / 255.0F;
   }

   public float getAlpha() {
      return (float)ColorHelper.getAlpha(this.color) / 255.0F;
   }

   public static TintedParticleEffect create(ParticleType type, int color) {
      return new TintedParticleEffect(type, color);
   }

   public static TintedParticleEffect create(ParticleType type, float r, float g, float b) {
      return create(type, ColorHelper.fromFloats(1.0F, r, g, b));
   }
}
