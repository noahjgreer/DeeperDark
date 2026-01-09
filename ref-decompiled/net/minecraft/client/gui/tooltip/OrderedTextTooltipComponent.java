package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;

@Environment(EnvType.CLIENT)
public class OrderedTextTooltipComponent implements TooltipComponent {
   private final OrderedText text;

   public OrderedTextTooltipComponent(OrderedText text) {
      this.text = text;
   }

   public int getWidth(TextRenderer textRenderer) {
      return textRenderer.getWidth(this.text);
   }

   public int getHeight(TextRenderer textRenderer) {
      return 10;
   }

   public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {
      context.drawText(textRenderer, (OrderedText)this.text, x, y, -1, true);
   }
}
