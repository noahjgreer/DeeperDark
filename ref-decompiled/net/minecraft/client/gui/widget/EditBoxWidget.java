package net.minecraft.client.gui.widget;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class EditBoxWidget extends ScrollableTextFieldWidget {
   private static final int CURSOR_PADDING = 1;
   private static final int CURSOR_COLOR = -3092272;
   private static final String UNDERSCORE = "_";
   private static final int FOCUSED_BOX_TEXT_COLOR = -2039584;
   private static final int UNFOCUSED_BOX_TEXT_COLOR = -857677600;
   private static final int CURSOR_BLINK_INTERVAL = 300;
   private final TextRenderer textRenderer;
   private final Text placeholder;
   private final EditBox editBox;
   private final int textColor;
   private final boolean textShadow;
   private final int cursorColor;
   private long lastSwitchFocusTime = Util.getMeasuringTimeMs();

   EditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, Text message, int textColor, boolean textShadow, int cursorColor, boolean hasBackground, boolean hasOverlay) {
      super(x, y, width, height, message, hasBackground, hasOverlay);
      this.textRenderer = textRenderer;
      this.textShadow = textShadow;
      this.textColor = textColor;
      this.cursorColor = cursorColor;
      this.placeholder = placeholder;
      this.editBox = new EditBox(textRenderer, width - this.getPadding());
      this.editBox.setCursorChangeListener(this::onCursorChange);
   }

   public void setMaxLength(int maxLength) {
      this.editBox.setMaxLength(maxLength);
   }

   public void setMaxLines(int maxLines) {
      this.editBox.setMaxLines(maxLines);
   }

   public void setChangeListener(Consumer changeListener) {
      this.editBox.setChangeListener(changeListener);
   }

   public void setText(String text) {
      this.setText(text, false);
   }

   public void setText(String text, boolean allowOverflow) {
      this.editBox.setText(text, allowOverflow);
   }

   public String getText() {
      return this.editBox.getText();
   }

   public void appendClickableNarrations(NarrationMessageBuilder builder) {
      builder.put(NarrationPart.TITLE, (Text)Text.translatable("gui.narrate.editBox", this.getMessage(), this.getText()));
   }

   public void onClick(double mouseX, double mouseY) {
      this.editBox.setSelecting(Screen.hasShiftDown());
      this.moveCursor(mouseX, mouseY);
   }

   protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
      this.editBox.setSelecting(true);
      this.moveCursor(mouseX, mouseY);
      this.editBox.setSelecting(Screen.hasShiftDown());
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return this.editBox.handleSpecialKey(keyCode);
   }

   public boolean charTyped(char chr, int modifiers) {
      if (this.visible && this.isFocused() && StringHelper.isValidChar(chr)) {
         this.editBox.replaceSelection(Character.toString(chr));
         return true;
      } else {
         return false;
      }
   }

   protected void renderContents(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      String string = this.editBox.getText();
      if (string.isEmpty() && !this.isFocused()) {
         context.drawWrappedTextWithShadow(this.textRenderer, this.placeholder, this.getTextX(), this.getTextY(), this.width - this.getPadding(), -857677600);
      } else {
         int i = this.editBox.getCursor();
         boolean bl = this.isFocused() && (Util.getMeasuringTimeMs() - this.lastSwitchFocusTime) / 300L % 2L == 0L;
         boolean bl2 = i < string.length();
         int j = 0;
         int k = 0;
         int l = this.getTextY();

         int var10003;
         for(Iterator var12 = this.editBox.getLines().iterator(); var12.hasNext(); l += 9) {
            EditBox.Substring substring = (EditBox.Substring)var12.next();
            Objects.requireNonNull(this.textRenderer);
            boolean bl3 = this.isVisible(l, l + 9);
            int m = this.getTextX();
            String string2;
            if (bl && bl2 && i >= substring.beginIndex() && i < substring.endIndex()) {
               if (bl3) {
                  string2 = string.substring(substring.beginIndex(), i);
                  context.drawText(this.textRenderer, string2, m, l, this.textColor, this.textShadow);
                  j = m + this.textRenderer.getWidth(string2);
                  int var10002 = l - 1;
                  var10003 = j + 1;
                  int var10004 = l + 1;
                  Objects.requireNonNull(this.textRenderer);
                  context.fill(j, var10002, var10003, var10004 + 9, this.cursorColor);
                  context.drawText(this.textRenderer, string.substring(i, substring.endIndex()), j, l, this.textColor, this.textShadow);
               }
            } else {
               if (bl3) {
                  string2 = string.substring(substring.beginIndex(), substring.endIndex());
                  context.drawText(this.textRenderer, string2, m, l, this.textColor, this.textShadow);
                  j = m + this.textRenderer.getWidth(string2) - 1;
               }

               k = l;
            }

            Objects.requireNonNull(this.textRenderer);
         }

         if (bl && !bl2) {
            Objects.requireNonNull(this.textRenderer);
            if (this.isVisible(k, k + 9)) {
               context.drawText(this.textRenderer, "_", j, k, this.cursorColor, this.textShadow);
            }
         }

         if (this.editBox.hasSelection()) {
            EditBox.Substring substring2 = this.editBox.getSelection();
            int n = this.getTextX();
            l = this.getTextY();
            Iterator var20 = this.editBox.getLines().iterator();

            while(var20.hasNext()) {
               EditBox.Substring substring3 = (EditBox.Substring)var20.next();
               if (substring2.beginIndex() > substring3.endIndex()) {
                  Objects.requireNonNull(this.textRenderer);
                  l += 9;
               } else {
                  if (substring3.beginIndex() > substring2.endIndex()) {
                     break;
                  }

                  Objects.requireNonNull(this.textRenderer);
                  if (this.isVisible(l, l + 9)) {
                     int o = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
                     int p;
                     if (substring2.endIndex() > substring3.endIndex()) {
                        p = this.width - this.getTextMargin();
                     } else {
                        p = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
                     }

                     int var10001 = n + o;
                     var10003 = n + p;
                     Objects.requireNonNull(this.textRenderer);
                     context.drawSelection(var10001, l, var10003, l + 9);
                  }

                  Objects.requireNonNull(this.textRenderer);
                  l += 9;
               }
            }
         }

      }
   }

   protected void renderOverlay(DrawContext context) {
      super.renderOverlay(context);
      if (this.editBox.hasMaxLength()) {
         int i = this.editBox.getMaxLength();
         Text text = Text.translatable("gui.multiLineEditBox.character_limit", this.editBox.getText().length(), i);
         context.drawTextWithShadow(this.textRenderer, (Text)text, this.getX() + this.width - this.textRenderer.getWidth((StringVisitable)text), this.getY() + this.height + 4, -6250336);
      }

   }

   public int getContentsHeight() {
      Objects.requireNonNull(this.textRenderer);
      return 9 * this.editBox.getLineCount();
   }

   protected double getDeltaYPerScroll() {
      Objects.requireNonNull(this.textRenderer);
      return 9.0 / 2.0;
   }

   private void onCursorChange() {
      double d = this.getScrollY();
      EditBox var10000 = this.editBox;
      Objects.requireNonNull(this.textRenderer);
      EditBox.Substring substring = var10000.getLine((int)(d / 9.0));
      int var5;
      if (this.editBox.getCursor() <= substring.beginIndex()) {
         var5 = this.editBox.getCurrentLineIndex();
         Objects.requireNonNull(this.textRenderer);
         d = (double)(var5 * 9);
      } else {
         var10000 = this.editBox;
         double var10001 = d + (double)this.height;
         Objects.requireNonNull(this.textRenderer);
         EditBox.Substring substring2 = var10000.getLine((int)(var10001 / 9.0) - 1);
         if (this.editBox.getCursor() > substring2.endIndex()) {
            var5 = this.editBox.getCurrentLineIndex();
            Objects.requireNonNull(this.textRenderer);
            var5 = var5 * 9 - this.height;
            Objects.requireNonNull(this.textRenderer);
            d = (double)(var5 + 9 + this.getPadding());
         }
      }

      this.setScrollY(d);
   }

   private void moveCursor(double mouseX, double mouseY) {
      double d = mouseX - (double)this.getX() - (double)this.getTextMargin();
      double e = mouseY - (double)this.getY() - (double)this.getTextMargin() + this.getScrollY();
      this.editBox.moveCursor(d, e);
   }

   public void setFocused(boolean focused) {
      super.setFocused(focused);
      if (focused) {
         this.lastSwitchFocusTime = Util.getMeasuringTimeMs();
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private int x;
      private int y;
      private Text placeholder;
      private int textColor;
      private boolean textShadow;
      private int cursorColor;
      private boolean hasBackground;
      private boolean hasOverlay;

      public Builder() {
         this.placeholder = ScreenTexts.EMPTY;
         this.textColor = -2039584;
         this.textShadow = true;
         this.cursorColor = -3092272;
         this.hasBackground = true;
         this.hasOverlay = true;
      }

      public Builder x(int x) {
         this.x = x;
         return this;
      }

      public Builder y(int y) {
         this.y = y;
         return this;
      }

      public Builder placeholder(Text placeholder) {
         this.placeholder = placeholder;
         return this;
      }

      public Builder textColor(int textColor) {
         this.textColor = textColor;
         return this;
      }

      public Builder textShadow(boolean textShadow) {
         this.textShadow = textShadow;
         return this;
      }

      public Builder cursorColor(int cursorColor) {
         this.cursorColor = cursorColor;
         return this;
      }

      public Builder hasBackground(boolean hasBackground) {
         this.hasBackground = hasBackground;
         return this;
      }

      public Builder hasOverlay(boolean hasOverlay) {
         this.hasOverlay = hasOverlay;
         return this;
      }

      public EditBoxWidget build(TextRenderer textRenderer, int width, int height, Text message) {
         return new EditBoxWidget(textRenderer, this.x, this.y, width, height, this.placeholder, message, this.textColor, this.textShadow, this.cursorColor, this.hasBackground, this.hasOverlay);
      }
   }
}
