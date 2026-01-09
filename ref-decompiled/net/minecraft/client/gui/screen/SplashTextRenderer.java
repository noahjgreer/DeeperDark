package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SplashTextRenderer {
   public static final SplashTextRenderer MERRY_X_MAS_ = new SplashTextRenderer("Merry X-mas!");
   public static final SplashTextRenderer HAPPY_NEW_YEAR_ = new SplashTextRenderer("Happy new year!");
   public static final SplashTextRenderer OOOOO_O_O_OOOOO__SPOOKY_ = new SplashTextRenderer("OOoooOOOoooo! Spooky!");
   private static final int TEXT_X = 123;
   private static final int TEXT_Y = 69;
   private final String text;

   public SplashTextRenderer(String text) {
      this.text = text;
   }

   public void render(DrawContext context, int screenWidth, TextRenderer textRenderer, float alpha) {
      context.getMatrices().pushMatrix();
      context.getMatrices().translate((float)screenWidth / 2.0F + 123.0F, 69.0F);
      context.getMatrices().rotate(-0.34906584F);
      float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
      f = f * 100.0F / (float)(textRenderer.getWidth(this.text) + 32);
      context.getMatrices().scale(f, f);
      context.drawCenteredTextWithShadow(textRenderer, (String)this.text, 0, -8, ColorHelper.withAlpha(alpha, -256));
      context.getMatrices().popMatrix();
   }
}
