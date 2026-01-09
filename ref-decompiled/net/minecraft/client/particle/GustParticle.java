package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class GustParticle extends SpriteBillboardParticle {
   private final SpriteProvider spriteProvider;

   protected GustParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
      super(world, x, y, z);
      this.spriteProvider = spriteProvider;
      this.setSpriteForAge(spriteProvider);
      this.maxAge = 12 + this.random.nextInt(4);
      this.scale = 1.0F;
      this.setBoundingBoxSpacing(1.0F, 1.0F);
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
   }

   public int getBrightness(float tint) {
      return 15728880;
   }

   public void tick() {
      if (this.age++ >= this.maxAge) {
         this.markDead();
      } else {
         this.setSpriteForAge(this.spriteProvider);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class SmallGustFactory implements ParticleFactory {
      private final SpriteProvider field_50230;

      public SmallGustFactory(SpriteProvider spriteProvider) {
         this.field_50230 = spriteProvider;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         Particle particle = new GustParticle(clientWorld, d, e, f, this.field_50230);
         particle.scale(0.15F);
         return particle;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      private final SpriteProvider spriteProvider;

      public Factory(SpriteProvider spriteProvider) {
         this.spriteProvider = spriteProvider;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         return new GustParticle(clientWorld, d, e, f, this.spriteProvider);
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }
}
