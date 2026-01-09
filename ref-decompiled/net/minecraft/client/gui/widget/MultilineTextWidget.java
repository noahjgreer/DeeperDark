package net.minecraft.client.gui.widget;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MultilineTextWidget extends AbstractTextWidget {
   private OptionalInt maxWidth;
   private OptionalInt maxRows;
   private final CachedMapper cacheKeyToText;
   private boolean centered;
   private boolean allowHoverEvents;
   @Nullable
   private Consumer onClick;

   public MultilineTextWidget(Text message, TextRenderer textRenderer) {
      this(0, 0, message, textRenderer);
   }

   public MultilineTextWidget(int x, int y, Text message, TextRenderer textRenderer) {
      super(x, y, 0, 0, message, textRenderer);
      this.maxWidth = OptionalInt.empty();
      this.maxRows = OptionalInt.empty();
      this.centered = false;
      this.allowHoverEvents = false;
      this.onClick = null;
      this.cacheKeyToText = Util.cachedMapper((cacheKey) -> {
         return cacheKey.maxRows.isPresent() ? MultilineText.create(textRenderer, cacheKey.maxWidth, cacheKey.maxRows.getAsInt(), cacheKey.message) : MultilineText.create(textRenderer, cacheKey.message, cacheKey.maxWidth);
      });
      this.active = false;
   }

   public MultilineTextWidget setTextColor(int i) {
      super.setTextColor(i);
      return this;
   }

   public MultilineTextWidget setMaxWidth(int maxWidth) {
      this.maxWidth = OptionalInt.of(maxWidth);
      return this;
   }

   public MultilineTextWidget setMaxRows(int maxRows) {
      this.maxRows = OptionalInt.of(maxRows);
      return this;
   }

   public MultilineTextWidget setCentered(boolean centered) {
      this.centered = centered;
      return this;
   }

   public MultilineTextWidget setStyleConfig(boolean allowHoverEvents, @Nullable Consumer onClick) {
      this.allowHoverEvents = allowHoverEvents;
      this.onClick = onClick;
      return this;
   }

   public int getWidth() {
      return ((MultilineText)this.cacheKeyToText.map(this.getCacheKey())).getMaxWidth();
   }

   public int getHeight() {
      int var10000 = ((MultilineText)this.cacheKeyToText.map(this.getCacheKey())).count();
      Objects.requireNonNull(this.getTextRenderer());
      return var10000 * 9;
   }

   public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      MultilineText multilineText = (MultilineText)this.cacheKeyToText.map(this.getCacheKey());
      int i = this.getX();
      int j = this.getY();
      Objects.requireNonNull(this.getTextRenderer());
      int k = 9;
      int l = this.getTextColor();
      if (this.centered) {
         multilineText.drawCenterWithShadow(context, i + this.getWidth() / 2, j, k, l);
      } else {
         multilineText.drawWithShadow(context, i, j, k, l);
      }

      if (this.allowHoverEvents) {
         Style style = this.getStyleAt((double)mouseX, (double)mouseY);
         if (this.isHovered()) {
            context.drawHoverEvent(this.getTextRenderer(), style, mouseX, mouseY);
         }
      }

   }

   @Nullable
   private Style getStyleAt(double mouseX, double mouseY) {
      MultilineText multilineText = (MultilineText)this.cacheKeyToText.map(this.getCacheKey());
      int i = this.getX();
      int j = this.getY();
      Objects.requireNonNull(this.getTextRenderer());
      int k = 9;
      return this.centered ? multilineText.getStyleAtCentered(i + this.getWidth() / 2, j, k, mouseX, mouseY) : multilineText.getStyleAtLeftAligned(i, j, k, mouseX, mouseY);
   }

   public void onClick(double mouseX, double mouseY) {
      if (this.onClick != null) {
         Style style = this.getStyleAt(mouseX, mouseY);
         if (style != null) {
            this.onClick.accept(style);
            return;
         }
      }

      super.onClick(mouseX, mouseY);
   }

   private CacheKey getCacheKey() {
      return new CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
   }

   // $FF: synthetic method
   public AbstractTextWidget setTextColor(final int textColor) {
      return this.setTextColor(textColor);
   }

   @Environment(EnvType.CLIENT)
   private static record CacheKey(Text message, int maxWidth, OptionalInt maxRows) {
      final Text message;
      final int maxWidth;
      final OptionalInt maxRows;

      CacheKey(Text text, int i, OptionalInt optionalInt) {
         this.message = text;
         this.maxWidth = i;
         this.maxRows = optionalInt;
      }

      public Text message() {
         return this.message;
      }

      public int maxWidth() {
         return this.maxWidth;
      }

      public OptionalInt maxRows() {
         return this.maxRows;
      }
   }
}
