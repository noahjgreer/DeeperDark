package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

@Environment(EnvType.CLIENT)
public abstract class DimensionEffects {
   private static final Object2ObjectMap BY_IDENTIFIER = (Object2ObjectMap)Util.make(new Object2ObjectArrayMap(), (map) -> {
      Overworld overworld = new Overworld();
      map.defaultReturnValue(overworld);
      map.put(DimensionTypes.OVERWORLD_ID, overworld);
      map.put(DimensionTypes.THE_NETHER_ID, new Nether());
      map.put(DimensionTypes.THE_END_ID, new End());
   });
   private final SkyType skyType;
   private final boolean brightenLighting;
   private final boolean darkened;

   public DimensionEffects(SkyType skyType, boolean alternateSkyColor, boolean darkened) {
      this.skyType = skyType;
      this.brightenLighting = alternateSkyColor;
      this.darkened = darkened;
   }

   public static DimensionEffects byDimensionType(DimensionType dimensionType) {
      return (DimensionEffects)BY_IDENTIFIER.get(dimensionType.effects());
   }

   public boolean isSunRisingOrSetting(float skyAngle) {
      return false;
   }

   public int getSkyColor(float skyAngle) {
      return 0;
   }

   public abstract Vec3d adjustFogColor(Vec3d color, float sunHeight);

   public abstract boolean useThickFog(int camX, int camY);

   public SkyType getSkyType() {
      return this.skyType;
   }

   public boolean shouldBrightenLighting() {
      return this.brightenLighting;
   }

   public boolean isDarkened() {
      return this.darkened;
   }

   @Environment(EnvType.CLIENT)
   public static enum SkyType {
      NONE,
      NORMAL,
      END;

      // $FF: synthetic method
      private static SkyType[] method_36912() {
         return new SkyType[]{NONE, NORMAL, END};
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Overworld extends DimensionEffects {
      private static final float SUN_RISE_SET_THRESHOLD = 0.4F;

      public Overworld() {
         super(DimensionEffects.SkyType.NORMAL, false, false);
      }

      public boolean isSunRisingOrSetting(float skyAngle) {
         float f = MathHelper.cos(skyAngle * 6.2831855F);
         return f >= -0.4F && f <= 0.4F;
      }

      public int getSkyColor(float skyAngle) {
         float f = MathHelper.cos(skyAngle * 6.2831855F);
         float g = f / 0.4F * 0.5F + 0.5F;
         float h = MathHelper.square(1.0F - (1.0F - MathHelper.sin(g * 3.1415927F)) * 0.99F);
         return ColorHelper.fromFloats(h, g * 0.3F + 0.7F, g * g * 0.7F + 0.2F, 0.2F);
      }

      public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
         return color.multiply((double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.94F + 0.06F), (double)(sunHeight * 0.91F + 0.09F));
      }

      public boolean useThickFog(int camX, int camY) {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Nether extends DimensionEffects {
      public Nether() {
         super(DimensionEffects.SkyType.NONE, false, true);
      }

      public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
         return color;
      }

      public boolean useThickFog(int camX, int camY) {
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class End extends DimensionEffects {
      public End() {
         super(DimensionEffects.SkyType.END, true, false);
      }

      public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
         return color.multiply(0.15000000596046448);
      }

      public boolean useThickFog(int camX, int camY) {
         return false;
      }
   }
}
