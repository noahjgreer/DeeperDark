package net.minecraft.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class ShieldItem extends Item {
   public ShieldItem(Item.Settings settings) {
      super(settings);
   }

   public Text getName(ItemStack stack) {
      DyeColor dyeColor = (DyeColor)stack.get(DataComponentTypes.BASE_COLOR);
      if (dyeColor != null) {
         String var10000 = this.translationKey;
         return Text.translatable(var10000 + "." + dyeColor.getId());
      } else {
         return super.getName(stack);
      }
   }
}
