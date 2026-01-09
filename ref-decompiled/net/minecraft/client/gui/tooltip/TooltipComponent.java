package net.minecraft.client.gui.tooltip;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.tooltip.BundleTooltipData;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;

@Environment(EnvType.CLIENT)
public interface TooltipComponent {
   static TooltipComponent of(OrderedText text) {
      return new OrderedTextTooltipComponent(text);
   }

   static TooltipComponent of(TooltipData tooltipData) {
      Objects.requireNonNull(tooltipData);
      byte var2 = 0;
      Object var10000;
      switch (tooltipData.typeSwitch<invokedynamic>(tooltipData, var2)) {
         case 0:
            BundleTooltipData bundleTooltipData = (BundleTooltipData)tooltipData;
            var10000 = new BundleTooltipComponent(bundleTooltipData.contents());
            break;
         case 1:
            ProfilesTooltipComponent.ProfilesData profilesData = (ProfilesTooltipComponent.ProfilesData)tooltipData;
            var10000 = new ProfilesTooltipComponent(profilesData);
            break;
         default:
            throw new IllegalArgumentException("Unknown TooltipComponent");
      }

      return (TooltipComponent)var10000;
   }

   int getHeight(TextRenderer textRenderer);

   int getWidth(TextRenderer textRenderer);

   default boolean isSticky() {
      return false;
   }

   default void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {
   }

   default void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
   }
}
