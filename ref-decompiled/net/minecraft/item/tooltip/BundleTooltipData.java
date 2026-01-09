package net.minecraft.item.tooltip;

import net.minecraft.component.type.BundleContentsComponent;

public record BundleTooltipData(BundleContentsComponent contents) implements TooltipData {
   public BundleTooltipData(BundleContentsComponent bundleContentsComponent) {
      this.contents = bundleContentsComponent;
   }

   public BundleContentsComponent contents() {
      return this.contents;
   }
}
