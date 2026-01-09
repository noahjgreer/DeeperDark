package net.minecraft.util.math;

import org.joml.Vector3f;

public class ColorHelper {
   public static int getAlpha(int argb) {
      return argb >>> 24;
   }

   public static int getRed(int argb) {
      return argb >> 16 & 255;
   }

   public static int getGreen(int argb) {
      return argb >> 8 & 255;
   }

   public static int getBlue(int argb) {
      return argb & 255;
   }

   public static int getArgb(int alpha, int red, int green, int blue) {
      return alpha << 24 | red << 16 | green << 8 | blue;
   }

   public static int getArgb(int red, int green, int blue) {
      return getArgb(255, red, green, blue);
   }

   public static int getArgb(Vec3d rgb) {
      return getArgb(channelFromFloat((float)rgb.getX()), channelFromFloat((float)rgb.getY()), channelFromFloat((float)rgb.getZ()));
   }

   public static int mix(int first, int second) {
      if (first == -1) {
         return second;
      } else {
         return second == -1 ? first : getArgb(getAlpha(first) * getAlpha(second) / 255, getRed(first) * getRed(second) / 255, getGreen(first) * getGreen(second) / 255, getBlue(first) * getBlue(second) / 255);
      }
   }

   public static int scaleRgb(int argb, float scale) {
      return scaleRgb(argb, scale, scale, scale);
   }

   public static int scaleRgb(int argb, float redScale, float greenScale, float blueScale) {
      return getArgb(getAlpha(argb), Math.clamp((long)((int)((float)getRed(argb) * redScale)), 0, 255), Math.clamp((long)((int)((float)getGreen(argb) * greenScale)), 0, 255), Math.clamp((long)((int)((float)getBlue(argb) * blueScale)), 0, 255));
   }

   public static int scaleRgb(int argb, int scale) {
      return getArgb(getAlpha(argb), Math.clamp((long)getRed(argb) * (long)scale / 255L, 0, 255), Math.clamp((long)getGreen(argb) * (long)scale / 255L, 0, 255), Math.clamp((long)getBlue(argb) * (long)scale / 255L, 0, 255));
   }

   public static int grayscale(int argb) {
      int i = (int)((float)getRed(argb) * 0.3F + (float)getGreen(argb) * 0.59F + (float)getBlue(argb) * 0.11F);
      return getArgb(i, i, i);
   }

   public static int lerp(float delta, int start, int end) {
      int i = MathHelper.lerp(delta, getAlpha(start), getAlpha(end));
      int j = MathHelper.lerp(delta, getRed(start), getRed(end));
      int k = MathHelper.lerp(delta, getGreen(start), getGreen(end));
      int l = MathHelper.lerp(delta, getBlue(start), getBlue(end));
      return getArgb(i, j, k, l);
   }

   public static int fullAlpha(int argb) {
      return argb | -16777216;
   }

   public static int zeroAlpha(int argb) {
      return argb & 16777215;
   }

   public static int withAlpha(int alpha, int rgb) {
      return alpha << 24 | rgb & 16777215;
   }

   public static int withAlpha(float alpha, int color) {
      return channelFromFloat(alpha) << 24 | color & 16777215;
   }

   public static int getWhite(float alpha) {
      return channelFromFloat(alpha) << 24 | 16777215;
   }

   public static int fromFloats(float alpha, float red, float green, float blue) {
      return getArgb(channelFromFloat(alpha), channelFromFloat(red), channelFromFloat(green), channelFromFloat(blue));
   }

   public static Vector3f toVector(int rgb) {
      float f = (float)getRed(rgb) / 255.0F;
      float g = (float)getGreen(rgb) / 255.0F;
      float h = (float)getBlue(rgb) / 255.0F;
      return new Vector3f(f, g, h);
   }

   public static int average(int first, int second) {
      return getArgb((getAlpha(first) + getAlpha(second)) / 2, (getRed(first) + getRed(second)) / 2, (getGreen(first) + getGreen(second)) / 2, (getBlue(first) + getBlue(second)) / 2);
   }

   public static int channelFromFloat(float value) {
      return MathHelper.floor(value * 255.0F);
   }

   public static float getAlphaFloat(int argb) {
      return floatFromChannel(getAlpha(argb));
   }

   public static float getRedFloat(int argb) {
      return floatFromChannel(getRed(argb));
   }

   public static float getGreenFloat(int argb) {
      return floatFromChannel(getGreen(argb));
   }

   public static float getBlueFloat(int argb) {
      return floatFromChannel(getBlue(argb));
   }

   private static float floatFromChannel(int channel) {
      return (float)channel / 255.0F;
   }

   public static int toAbgr(int argb) {
      return argb & -16711936 | (argb & 16711680) >> 16 | (argb & 255) << 16;
   }

   public static int fromAbgr(int abgr) {
      return toAbgr(abgr);
   }

   public static int withBrightness(int argb, float brightness) {
      int i = getRed(argb);
      int j = getGreen(argb);
      int k = getBlue(argb);
      int l = getAlpha(argb);
      int m = Math.max(Math.max(i, j), k);
      int n = Math.min(Math.min(i, j), k);
      float f = (float)(m - n);
      float g;
      if (m != 0) {
         g = f / (float)m;
      } else {
         g = 0.0F;
      }

      float h;
      float o;
      float p;
      float q;
      if (g == 0.0F) {
         h = 0.0F;
      } else {
         o = (float)(m - i) / f;
         p = (float)(m - j) / f;
         q = (float)(m - k) / f;
         if (i == m) {
            h = q - p;
         } else if (j == m) {
            h = 2.0F + o - q;
         } else {
            h = 4.0F + p - o;
         }

         h /= 6.0F;
         if (h < 0.0F) {
            ++h;
         }
      }

      if (g == 0.0F) {
         i = j = k = Math.round(brightness * 255.0F);
         return getArgb(l, i, j, k);
      } else {
         o = (h - (float)Math.floor((double)h)) * 6.0F;
         p = o - (float)Math.floor((double)o);
         q = brightness * (1.0F - g);
         float r = brightness * (1.0F - g * p);
         float s = brightness * (1.0F - g * (1.0F - p));
         switch ((int)o) {
            case 0:
               i = Math.round(brightness * 255.0F);
               j = Math.round(s * 255.0F);
               k = Math.round(q * 255.0F);
               break;
            case 1:
               i = Math.round(r * 255.0F);
               j = Math.round(brightness * 255.0F);
               k = Math.round(q * 255.0F);
               break;
            case 2:
               i = Math.round(q * 255.0F);
               j = Math.round(brightness * 255.0F);
               k = Math.round(s * 255.0F);
               break;
            case 3:
               i = Math.round(q * 255.0F);
               j = Math.round(r * 255.0F);
               k = Math.round(brightness * 255.0F);
               break;
            case 4:
               i = Math.round(s * 255.0F);
               j = Math.round(q * 255.0F);
               k = Math.round(brightness * 255.0F);
               break;
            case 5:
               i = Math.round(brightness * 255.0F);
               j = Math.round(q * 255.0F);
               k = Math.round(r * 255.0F);
         }

         return getArgb(l, i, j, k);
      }
   }
}
