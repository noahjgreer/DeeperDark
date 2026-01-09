package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SculkChargeParticleEffect;

@Environment(EnvType.CLIENT)
public class SculkChargeParticle extends SpriteBillboardParticle {
   private final SpriteProvider spriteProvider;

   SculkChargeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
      super(world, x, y, z, velocityX, velocityY, velocityZ);
      this.velocityMultiplier = 0.96F;
      this.spriteProvider = spriteProvider;
      this.scale(1.5F);
      this.collidesWithWorld = false;
      this.setSpriteForAge(spriteProvider);
   }

   public int getBrightness(float tint) {
      return 240;
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteForAge(this.spriteProvider);
   }

   @Environment(EnvType.CLIENT)
   public static record Factory(SpriteProvider spriteProvider) implements ParticleFactory {
      public Factory(SpriteProvider spriteProvider) {
         this.spriteProvider = spriteProvider;
      }

      public Particle createParticle(SculkChargeParticleEffect sculkChargeParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         SculkChargeParticle sculkChargeParticle = new SculkChargeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
         sculkChargeParticle.setAlpha(1.0F);
         sculkChargeParticle.setVelocity(g, h, i);
         sculkChargeParticle.lastAngle = sculkChargeParticleEffect.roll();
         sculkChargeParticle.angle = sculkChargeParticleEffect.roll();
         sculkChargeParticle.setMaxAge(clientWorld.random.nextInt(12) + 8);
         return sculkChargeParticle;
      }

      public SpriteProvider spriteProvider() {
         return this.spriteProvider;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SculkChargeParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }
}
