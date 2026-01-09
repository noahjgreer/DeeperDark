package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public abstract class StandardFogModifier extends FogModifier {
   public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
      float f = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(skyDarkness) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
      BiomeAccess biomeAccess = world.getBiomeAccess();
      Vec3d vec3d = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
      Vec3d vec3d2 = world.getDimensionEffects().adjustFogColor(CubicSampler.sampleColor(vec3d, (biomeX, biomeY, biomeZ) -> {
         return Vec3d.unpackRgb(((Biome)biomeAccess.getBiomeForNoiseGen(biomeX, biomeY, biomeZ).value()).getFogColor());
      }), f);
      float g = (float)vec3d2.getX();
      float h = (float)vec3d2.getY();
      float i = (float)vec3d2.getZ();
      float k;
      if (viewDistance >= 4) {
         float j = MathHelper.sin(world.getSkyAngleRadians(skyDarkness)) > 0.0F ? -1.0F : 1.0F;
         Vector3f vector3f = new Vector3f(j, 0.0F, 0.0F);
         k = camera.getHorizontalPlane().dot(vector3f);
         if (k > 0.0F && world.getDimensionEffects().isSunRisingOrSetting(world.getSkyAngle(skyDarkness))) {
            int l = world.getDimensionEffects().getSkyColor(world.getSkyAngle(skyDarkness));
            k *= ColorHelper.getAlphaFloat(l);
            g = MathHelper.lerp(k, g, ColorHelper.getRedFloat(l));
            h = MathHelper.lerp(k, h, ColorHelper.getGreenFloat(l));
            i = MathHelper.lerp(k, i, ColorHelper.getBlueFloat(l));
         }
      }

      int m = world.getSkyColor(camera.getPos(), skyDarkness);
      float n = ColorHelper.getRedFloat(m);
      k = ColorHelper.getGreenFloat(m);
      float o = ColorHelper.getBlueFloat(m);
      float p = 0.25F + 0.75F * (float)viewDistance / 32.0F;
      p = 1.0F - (float)Math.pow((double)p, 0.25);
      g += (n - g) * p;
      h += (k - h) * p;
      i += (o - i) * p;
      float q = world.getRainGradient(skyDarkness);
      float r;
      float s;
      if (q > 0.0F) {
         r = 1.0F - q * 0.5F;
         s = 1.0F - q * 0.4F;
         g *= r;
         h *= r;
         i *= s;
      }

      r = world.getThunderGradient(skyDarkness);
      if (r > 0.0F) {
         s = 1.0F - r * 0.5F;
         g *= s;
         h *= s;
         i *= s;
      }

      return ColorHelper.fromFloats(1.0F, g, h, i);
   }
}
