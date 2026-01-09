package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;

@Environment(EnvType.CLIENT)
public abstract class AbstractSlowingParticle extends SpriteBillboardParticle {
   protected AbstractSlowingParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
      super(clientWorld, d, e, f, g, h, i);
      this.velocityMultiplier = 0.96F;
      this.velocityX = this.velocityX * 0.009999999776482582 + g;
      this.velocityY = this.velocityY * 0.009999999776482582 + h;
      this.velocityZ = this.velocityZ * 0.009999999776482582 + i;
      this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.maxAge = (int)(8.0 / (Math.random() * 0.8 + 0.2)) + 4;
   }
}
