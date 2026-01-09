package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;

@Environment(EnvType.CLIENT)
public class BlockMarkerParticle extends SpriteBillboardParticle {
   BlockMarkerParticle(ClientWorld world, double x, double y, double z, BlockState state) {
      super(world, x, y, z);
      this.setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state));
      this.gravityStrength = 0.0F;
      this.maxAge = 80;
      this.collidesWithWorld = false;
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.TERRAIN_SHEET;
   }

   public float getSize(float tickProgress) {
      return 0.5F;
   }

   @Environment(EnvType.CLIENT)
   public static class Factory implements ParticleFactory {
      public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         return new BlockMarkerParticle(clientWorld, d, e, f, blockStateParticleEffect.getBlockState());
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((BlockStateParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }
}
