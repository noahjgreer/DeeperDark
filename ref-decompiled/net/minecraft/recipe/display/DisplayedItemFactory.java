package net.minecraft.recipe.display;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public interface DisplayedItemFactory {
   public interface FromRemainder extends DisplayedItemFactory {
      Object toDisplayed(Object input, List remainders);
   }

   public interface FromStack extends DisplayedItemFactory {
      default Object toDisplayed(RegistryEntry item) {
         return this.toDisplayed(new ItemStack(item));
      }

      default Object toDisplayed(Item item) {
         return this.toDisplayed(new ItemStack(item));
      }

      Object toDisplayed(ItemStack stack);
   }
}
