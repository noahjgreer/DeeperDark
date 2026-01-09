package net.minecraft.client.font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface MultilineText {
   MultilineText EMPTY = new MultilineText() {
      public void drawCenterWithShadow(DrawContext context, int x, int y) {
      }

      public void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
      }

      public void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
      }

      public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
         return y;
      }

      @Nullable
      public Style getStyleAtCentered(int x, int y, int i, double mouseX, double mouseY) {
         return null;
      }

      @Nullable
      public Style getStyleAtLeftAligned(int x, int y, int i, double mouseX, double mouseY) {
         return null;
      }

      public int count() {
         return 0;
      }

      public int getMaxWidth() {
         return 0;
      }
   };

   static MultilineText create(TextRenderer renderer, Text... texts) {
      return create(renderer, Integer.MAX_VALUE, Integer.MAX_VALUE, texts);
   }

   static MultilineText create(TextRenderer renderer, int maxWidth, Text... texts) {
      return create(renderer, maxWidth, Integer.MAX_VALUE, texts);
   }

   static MultilineText create(TextRenderer renderer, Text text, int maxWidth) {
      return create(renderer, maxWidth, Integer.MAX_VALUE, text);
   }

   static MultilineText create(final TextRenderer renderer, final int maxWidth, final int maxLines, final Text... texts) {
      return texts.length == 0 ? EMPTY : new MultilineText() {
         @Nullable
         private List lines;
         @Nullable
         private Language language;

         public void drawCenterWithShadow(DrawContext context, int x, int y) {
            Objects.requireNonNull(renderer);
            this.drawCenterWithShadow(context, x, y, 9, -1);
         }

         public void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
            int i = y;

            for(Iterator var7 = this.getLines().iterator(); var7.hasNext(); i += lineHeight) {
               Line line = (Line)var7.next();
               context.drawTextWithShadow(renderer, line.text, x - line.width / 2, i, color);
            }

         }

         public void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color) {
            int i = y;

            for(Iterator var7 = this.getLines().iterator(); var7.hasNext(); i += lineHeight) {
               Line line = (Line)var7.next();
               context.drawTextWithShadow(renderer, line.text, x, i, color);
            }

         }

         public int draw(DrawContext context, int x, int y, int lineHeight, int color) {
            int i = y;

            for(Iterator var7 = this.getLines().iterator(); var7.hasNext(); i += lineHeight) {
               Line line = (Line)var7.next();
               context.drawText(renderer, line.text, x, i, color, false);
            }

            return i;
         }

         @Nullable
         public Style getStyleAtCentered(int x, int y, int i, double mouseX, double mouseY) {
            List list = this.getLines();
            int j = MathHelper.floor((mouseY - (double)y) / (double)i);
            if (j >= 0 && j < list.size()) {
               Line line = (Line)list.get(j);
               int k = x - line.width / 2;
               if (mouseX < (double)k) {
                  return null;
               } else {
                  int l = MathHelper.floor(mouseX - (double)k);
                  return renderer.getTextHandler().getStyleAt(line.text, l);
               }
            } else {
               return null;
            }
         }

         @Nullable
         public Style getStyleAtLeftAligned(int x, int y, int i, double mouseX, double mouseY) {
            if (mouseX < (double)x) {
               return null;
            } else {
               List list = this.getLines();
               int j = MathHelper.floor((mouseY - (double)y) / (double)i);
               if (j >= 0 && j < list.size()) {
                  Line line = (Line)list.get(j);
                  int k = MathHelper.floor(mouseX - (double)x);
                  return renderer.getTextHandler().getStyleAt(line.text, k);
               } else {
                  return null;
               }
            }
         }

         private List getLines() {
            Language language = Language.getInstance();
            if (this.lines != null && language == this.language) {
               return this.lines;
            } else {
               this.language = language;
               List list = new ArrayList();
               Text[] var3 = texts;
               int var4 = var3.length;

               int j;
               for(j = 0; j < var4; ++j) {
                  Text text = var3[j];
                  list.addAll(renderer.wrapLinesWithoutLanguage(text, maxWidth));
               }

               this.lines = new ArrayList();
               int i = Math.min(list.size(), maxLines);
               List list2 = list.subList(0, i);

               for(j = 0; j < list2.size(); ++j) {
                  StringVisitable stringVisitable = (StringVisitable)list2.get(j);
                  OrderedText orderedText = Language.getInstance().reorder(stringVisitable);
                  if (j == list2.size() - 1 && i == maxLines && i != list.size()) {
                     StringVisitable stringVisitable2 = renderer.trimToWidth(stringVisitable, renderer.getWidth(stringVisitable) - renderer.getWidth((StringVisitable)ScreenTexts.ELLIPSIS));
                     StringVisitable stringVisitable3 = StringVisitable.concat(stringVisitable2, ScreenTexts.ELLIPSIS);
                     this.lines.add(new Line(Language.getInstance().reorder(stringVisitable3), renderer.getWidth(stringVisitable3)));
                  } else {
                     this.lines.add(new Line(orderedText, renderer.getWidth(orderedText)));
                  }
               }

               return this.lines;
            }
         }

         public int count() {
            return this.getLines().size();
         }

         public int getMaxWidth() {
            return Math.min(maxWidth, this.getLines().stream().mapToInt(Line::width).max().orElse(0));
         }
      };
   }

   void drawCenterWithShadow(DrawContext context, int x, int y);

   void drawCenterWithShadow(DrawContext context, int x, int y, int lineHeight, int color);

   void drawWithShadow(DrawContext context, int x, int y, int lineHeight, int color);

   int draw(DrawContext context, int x, int y, int lineHeight, int color);

   @Nullable
   Style getStyleAtCentered(int x, int y, int i, double mouseX, double mouseY);

   @Nullable
   Style getStyleAtLeftAligned(int x, int y, int i, double mouseX, double mouseY);

   int count();

   int getMaxWidth();

   @Environment(EnvType.CLIENT)
   public static record Line(OrderedText text, int width) {
      final OrderedText text;
      final int width;

      public Line(OrderedText text, int width) {
         this.text = text;
         this.width = width;
      }

      public OrderedText text() {
         return this.text;
      }

      public int width() {
         return this.width;
      }
   }
}
