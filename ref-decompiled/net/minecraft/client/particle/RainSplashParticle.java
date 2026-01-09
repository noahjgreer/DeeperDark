package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class RainSplashParticle extends SpriteBillboardParticle {
   protected RainSplashParticle(ClientWorld clientWorld, double d, double e, double f) {
      super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
      this.velocityX *= 0.30000001192092896;
      this.velocityY = Math.random() * 0.20000000298023224 + 0.10000000149011612;
      this.velocityZ *= 0.30000001192092896;
      this.setBoundingBoxSpacing(0.01F, 0.01F);
      this.gravityStrength = 0.06F;
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2));
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.lastX = this.x;
      this.lastY = this.y;
      this.lastZ = this.z;
      if (this.maxAge-- <= 0) {
         this.markDead();
      } else {
         this.velocityY -= (double)this.gravityStrength;
         this.move(this.velocityX, this.velocityY, this.velocityZ);
         this.velocityX *= 0.9800000190734863;
         this.velocityY *= 0.9800000190734863;
         this.velocityZ *= 0.9800000190734863;
         if (this.onGround) {
            if (Math.random() < 0.5) {
               this.markDead();
            }

            this.velocityX *= 0.699999988079071;
            this.velocityZ *= 0.699999988079071;
         }

         BlockPos blockPos = BlockPos.ofFloored(this.x, this.y, this.z);
         double d = Math.max(this.world.getBlockState(blockPos).getCollisionShape(this.world, blockPos).getEndingCoord(Direction.Axis.Y, this.x - (double)blockPos.getX(), this.z - (double)blockPos.getZ()), (double)this.world.getFluidState(blockPos).getHeight(this.world, blockPos));
         if (d > 0.0 && this.y < (double)blockPos.getY() + d) {
            this.markDead();
         }

      }
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      private final SpriteProvider spriteProvider;

      public Factory(SpriteProvider spriteProvider) {
         this.spriteProvider = spriteProvider;
      }

      public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         RainSplashParticle rainSplashParticle = new RainSplashParticle(clientWorld, d, e, f);
         rainSplashParticle.setSprite(this.spriteProvider);
         return rainSplashParticle;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }
}
