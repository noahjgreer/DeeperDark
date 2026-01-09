package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface ParticleFactory {
   @Nullable
   Particle createParticle(ParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

   @Environment(EnvType.CLIENT)
   public interface BlockLeakParticleFactory {
      @Nullable
      SpriteBillboardParticle createParticle(ParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
   }
}
