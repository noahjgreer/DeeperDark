package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class FatalErrorScreen extends Screen {
   private final Text message;

   public FatalErrorScreen(Text title, Text message) {
      super(title);
      this.message = message;
   }

   protected void init() {
      super.init();
      this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.client.setScreen((Screen)null);
      }).dimensions(this.width / 2 - 100, 140, 200, 20).build());
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.title, this.width / 2, 90, -1);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.message, this.width / 2, 110, -1);
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      context.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
