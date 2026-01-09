package net.minecraft.client.font;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class TextRenderer {
   private static final float Z_INDEX = 0.01F;
   private static final float field_60693 = 0.01F;
   private static final float field_60694 = -0.01F;
   public static final float FORWARD_SHIFT = 0.03F;
   public static final int field_55090 = 0;
   public final int fontHeight = 9;
   public final Random random = Random.create();
   private final Function fontStorageAccessor;
   final boolean validateAdvance;
   private final TextHandler handler;

   public TextRenderer(Function fontStorageAccessor, boolean validateAdvance) {
      this.fontStorageAccessor = fontStorageAccessor;
      this.validateAdvance = validateAdvance;
      this.handler = new TextHandler((codePoint, style) -> {
         return this.getFontStorage(style.getFont()).getGlyph(codePoint, this.validateAdvance).getAdvance(style.isBold());
      });
   }

   FontStorage getFontStorage(Identifier id) {
      return (FontStorage)this.fontStorageAccessor.apply(id);
   }

   public String mirror(String text) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return text;
      }
   }

   public void draw(String string, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
      GlyphDrawable glyphDrawable = this.prepare(string, x, y, color, shadow, backgroundColor);
      glyphDrawable.draw(TextRenderer.GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
   }

   public void draw(Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
      GlyphDrawable glyphDrawable = this.prepare(text.asOrderedText(), x, y, color, shadow, backgroundColor);
      glyphDrawable.draw(TextRenderer.GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
   }

   public void draw(OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextLayerType layerType, int backgroundColor, int light) {
      GlyphDrawable glyphDrawable = this.prepare(text, x, y, color, shadow, backgroundColor);
      glyphDrawable.draw(TextRenderer.GlyphDrawer.drawing(vertexConsumers, matrix, layerType, light));
   }

   public void drawWithOutline(OrderedText text, float x, float y, int color, int outlineColor, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
      Drawer drawer = new Drawer(0.0F, 0.0F, outlineColor, false);

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            if (i != 0 || j != 0) {
               float[] fs = new float[]{x};
               text.accept((index, style, codePoint) -> {
                  boolean bl = style.isBold();
                  FontStorage fontStorage = this.getFontStorage(style.getFont());
                  Glyph glyph = fontStorage.getGlyph(codePoint, this.validateAdvance);
                  drawer.x = fs[0] + (float)i * glyph.getShadowOffset();
                  drawer.y = y + (float)j * glyph.getShadowOffset();
                  fs[0] += glyph.getAdvance(bl);
                  return drawer.accept(index, style.withColor(outlineColor), codePoint);
               });
            }
         }
      }

      GlyphDrawer glyphDrawer = TextRenderer.GlyphDrawer.drawing(vertexConsumers, matrix, TextRenderer.TextLayerType.NORMAL, light);
      Iterator var16 = drawer.drawnGlyphs.iterator();

      while(var16.hasNext()) {
         BakedGlyph.DrawnGlyph drawnGlyph = (BakedGlyph.DrawnGlyph)var16.next();
         glyphDrawer.drawGlyph(drawnGlyph);
      }

      Drawer drawer2 = new Drawer(x, y, color, false);
      text.accept(drawer2);
      drawer2.draw(TextRenderer.GlyphDrawer.drawing(vertexConsumers, matrix, TextRenderer.TextLayerType.POLYGON_OFFSET, light));
   }

   public GlyphDrawable prepare(String string, float x, float y, int color, boolean shadow, int backgroundColor) {
      if (this.isRightToLeft()) {
         string = this.mirror(string);
      }

      Drawer drawer = new Drawer(x, y, color, backgroundColor, shadow);
      TextVisitFactory.visitFormatted((String)string, Style.EMPTY, drawer);
      return drawer;
   }

   public GlyphDrawable prepare(OrderedText text, float x, float y, int color, boolean shadow, int backgroundColor) {
      Drawer drawer = new Drawer(x, y, color, backgroundColor, shadow);
      text.accept(drawer);
      return drawer;
   }

   public int getWidth(String text) {
      return MathHelper.ceil(this.handler.getWidth(text));
   }

   public int getWidth(StringVisitable text) {
      return MathHelper.ceil(this.handler.getWidth(text));
   }

   public int getWidth(OrderedText text) {
      return MathHelper.ceil(this.handler.getWidth(text));
   }

   public String trimToWidth(String text, int maxWidth, boolean backwards) {
      return backwards ? this.handler.trimToWidthBackwards(text, maxWidth, Style.EMPTY) : this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
   }

   public String trimToWidth(String text, int maxWidth) {
      return this.handler.trimToWidth(text, maxWidth, Style.EMPTY);
   }

   public StringVisitable trimToWidth(StringVisitable text, int width) {
      return this.handler.trimToWidth(text, width, Style.EMPTY);
   }

   public int getWrappedLinesHeight(String text, int maxWidth) {
      return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
   }

   public int getWrappedLinesHeight(StringVisitable text, int maxWidth) {
      return 9 * this.handler.wrapLines(text, maxWidth, Style.EMPTY).size();
   }

   public List wrapLines(StringVisitable text, int width) {
      return Language.getInstance().reorder(this.handler.wrapLines(text, width, Style.EMPTY));
   }

   public List wrapLinesWithoutLanguage(StringVisitable text, int width) {
      return this.handler.wrapLines(text, width, Style.EMPTY);
   }

   public boolean isRightToLeft() {
      return Language.getInstance().isRightToLeft();
   }

   public TextHandler getTextHandler() {
      return this.handler;
   }

   @Environment(EnvType.CLIENT)
   public interface GlyphDrawable {
      void draw(GlyphDrawer glyphDrawer);

      @Nullable
      ScreenRect getScreenRect();
   }

   @Environment(EnvType.CLIENT)
   public interface GlyphDrawer {
      static GlyphDrawer drawing(final VertexConsumerProvider vertexConsumers, final Matrix4f matrix, final TextLayerType layerType, final int light) {
         return new GlyphDrawer() {
            public void drawGlyph(BakedGlyph.DrawnGlyph glyph) {
               BakedGlyph bakedGlyph = glyph.glyph();
               VertexConsumer vertexConsumer = vertexConsumers.getBuffer(bakedGlyph.getLayer(layerType));
               bakedGlyph.draw(glyph, matrix, vertexConsumer, light, false);
            }

            public void drawRectangle(BakedGlyph bakedGlyph, BakedGlyph.Rectangle rect) {
               VertexConsumer vertexConsumer = vertexConsumers.getBuffer(bakedGlyph.getLayer(layerType));
               bakedGlyph.drawRectangle(rect, matrix, vertexConsumer, light, false);
            }
         };
      }

      void drawGlyph(BakedGlyph.DrawnGlyph glyph);

      void drawRectangle(BakedGlyph bakedGlyph, BakedGlyph.Rectangle rect);
   }

   @Environment(EnvType.CLIENT)
   public static enum TextLayerType {
      NORMAL,
      SEE_THROUGH,
      POLYGON_OFFSET;

      // $FF: synthetic method
      private static TextLayerType[] method_37344() {
         return new TextLayerType[]{NORMAL, SEE_THROUGH, POLYGON_OFFSET};
      }
   }

   @Environment(EnvType.CLIENT)
   class Drawer implements CharacterVisitor, GlyphDrawable {
      private final boolean shadow;
      private final int color;
      private final int backgroundColor;
      float x;
      float y;
      private float minX;
      private float minY;
      private float maxX;
      private float maxY;
      private float minBackgroundX;
      private float minBackgroundY;
      private float maxBackgroundX;
      private float maxBackgroundY;
      final List drawnGlyphs;
      @Nullable
      private List rectangles;

      public Drawer(final float x, final float y, final int color, final boolean shadow) {
         this(x, y, color, 0, shadow);
      }

      public Drawer(final float x, final float y, final int color, final int backgroundColor, final boolean shadow) {
         this.minX = Float.MAX_VALUE;
         this.minY = Float.MAX_VALUE;
         this.maxX = -3.4028235E38F;
         this.maxY = -3.4028235E38F;
         this.minBackgroundX = Float.MAX_VALUE;
         this.minBackgroundY = Float.MAX_VALUE;
         this.maxBackgroundX = -3.4028235E38F;
         this.maxBackgroundY = -3.4028235E38F;
         this.drawnGlyphs = new ArrayList();
         this.x = x;
         this.y = y;
         this.shadow = shadow;
         this.color = color;
         this.backgroundColor = backgroundColor;
         this.updateBackgroundBounds(x, y, 0.0F);
      }

      private void updateTextBounds(float minX, float minY, float maxX, float maxY) {
         this.minX = Math.min(this.minX, minX);
         this.minY = Math.min(this.minY, minY);
         this.maxX = Math.max(this.maxX, maxX);
         this.maxY = Math.max(this.maxY, maxY);
      }

      private void updateBackgroundBounds(float x, float y, float width) {
         if (ColorHelper.getAlpha(this.backgroundColor) != 0) {
            this.minBackgroundX = Math.min(this.minBackgroundX, x - 1.0F);
            this.minBackgroundY = Math.min(this.minBackgroundY, y - 1.0F);
            this.maxBackgroundX = Math.max(this.maxBackgroundX, x + width);
            this.maxBackgroundY = Math.max(this.maxBackgroundY, y + 9.0F);
            this.updateTextBounds(this.minBackgroundX, this.minBackgroundY, this.maxBackgroundX, this.maxBackgroundY);
         }
      }

      private void addGlyph(BakedGlyph.DrawnGlyph glyph) {
         this.drawnGlyphs.add(glyph);
         this.updateTextBounds(glyph.getEffectiveMinX(), glyph.getEffectiveMinY(), glyph.getEffectiveMaxX(), glyph.getEffectiveMaxY());
      }

      private void addRectangle(BakedGlyph.Rectangle rectangle) {
         if (this.rectangles == null) {
            this.rectangles = new ArrayList();
         }

         this.rectangles.add(rectangle);
         this.updateTextBounds(rectangle.getEffectiveMinX(), rectangle.getEffectiveMinY(), rectangle.getEffectiveMaxX(), rectangle.getEffectiveMaxY());
      }

      public boolean accept(int i, Style style, int j) {
         FontStorage fontStorage = TextRenderer.this.getFontStorage(style.getFont());
         Glyph glyph = fontStorage.getGlyph(j, TextRenderer.this.validateAdvance);
         BakedGlyph bakedGlyph = style.isObfuscated() && j != 32 ? fontStorage.getObfuscatedBakedGlyph(glyph) : fontStorage.getBaked(j);
         boolean bl = style.isBold();
         TextColor textColor = style.getColor();
         int k = this.getRenderColor(textColor);
         int l = this.getShadowColor(style, k);
         float f = glyph.getAdvance(bl);
         float g = i == 0 ? this.x - 1.0F : this.x;
         float h = glyph.getShadowOffset();
         if (!(bakedGlyph instanceof EmptyBakedGlyph)) {
            float m = bl ? glyph.getBoldOffset() : 0.0F;
            this.addGlyph(new BakedGlyph.DrawnGlyph(this.x, this.y, k, l, bakedGlyph, style, m, h));
         }

         this.updateBackgroundBounds(this.x, this.y, f);
         if (style.isStrikethrough()) {
            this.addRectangle(new BakedGlyph.Rectangle(g, this.y + 4.5F - 1.0F, this.x + f, this.y + 4.5F, 0.01F, k, l, h));
         }

         if (style.isUnderlined()) {
            this.addRectangle(new BakedGlyph.Rectangle(g, this.y + 9.0F - 1.0F, this.x + f, this.y + 9.0F, 0.01F, k, l, h));
         }

         this.x += f;
         return true;
      }

      public void draw(GlyphDrawer glyphDrawer) {
         BakedGlyph bakedGlyph = null;
         if (ColorHelper.getAlpha(this.backgroundColor) != 0) {
            BakedGlyph.Rectangle rectangle = new BakedGlyph.Rectangle(this.minBackgroundX, this.minBackgroundY, this.maxBackgroundX, this.maxBackgroundY, -0.01F, this.backgroundColor);
            bakedGlyph = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
            glyphDrawer.drawRectangle(bakedGlyph, rectangle);
         }

         Iterator var5 = this.drawnGlyphs.iterator();

         while(var5.hasNext()) {
            BakedGlyph.DrawnGlyph drawnGlyph = (BakedGlyph.DrawnGlyph)var5.next();
            glyphDrawer.drawGlyph(drawnGlyph);
         }

         if (this.rectangles != null) {
            if (bakedGlyph == null) {
               bakedGlyph = TextRenderer.this.getFontStorage(Style.DEFAULT_FONT_ID).getRectangleBakedGlyph();
            }

            var5 = this.rectangles.iterator();

            while(var5.hasNext()) {
               BakedGlyph.Rectangle rectangle2 = (BakedGlyph.Rectangle)var5.next();
               glyphDrawer.drawRectangle(bakedGlyph, rectangle2);
            }
         }

      }

      private int getRenderColor(@Nullable TextColor override) {
         if (override != null) {
            int i = ColorHelper.getAlpha(this.color);
            int j = override.getRgb();
            return ColorHelper.withAlpha(i, j);
         } else {
            return this.color;
         }
      }

      private int getShadowColor(Style style, int textColor) {
         Integer integer = style.getShadowColor();
         if (integer != null) {
            float f = ColorHelper.getAlphaFloat(textColor);
            float g = ColorHelper.getAlphaFloat(integer);
            return f != 1.0F ? ColorHelper.withAlpha(ColorHelper.channelFromFloat(f * g), integer) : integer;
         } else {
            return this.shadow ? ColorHelper.scaleRgb(textColor, 0.25F) : 0;
         }
      }

      @Nullable
      public ScreenRect getScreenRect() {
         if (!(this.minX >= this.maxX) && !(this.minY >= this.maxY)) {
            int i = MathHelper.floor(this.minX);
            int j = MathHelper.floor(this.minY);
            int k = MathHelper.ceil(this.maxX);
            int l = MathHelper.ceil(this.maxY);
            return new ScreenRect(i, j, k - i, l - j);
         } else {
            return null;
         }
      }
   }
}
