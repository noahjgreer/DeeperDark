package net.minecraft.client.gui.screen;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MessageScreen extends Screen {
   @Nullable
   private NarratedMultilineTextWidget textWidget;

   public MessageScreen(Text text) {
      super(text);
   }

   protected void init() {
      this.textWidget = (NarratedMultilineTextWidget)this.addDrawableChild(new NarratedMultilineTextWidget(this.width, this.title, this.textRenderer, 12));
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      if (this.textWidget != null) {
         this.textWidget.initMaxWidth(this.width);
         NarratedMultilineTextWidget var10000 = this.textWidget;
         int var10001 = this.width / 2 - this.textWidget.getWidth() / 2;
         int var10002 = this.height / 2;
         Objects.requireNonNull(this.textRenderer);
         var10000.setPosition(var10001, var10002 - 9 / 2);
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected boolean hasUsageText() {
      return false;
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.renderPanoramaBackground(context, deltaTicks);
      this.applyBlur(context);
      this.renderDarkening(context);
   }
}
