package net.minecraft.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

public interface SuspiciousStewIngredient {
   SuspiciousStewEffectsComponent getStewEffects();

   static List getAll() {
      return (List)Registries.ITEM.stream().map(SuspiciousStewIngredient::of).filter(Objects::nonNull).collect(Collectors.toList());
   }

   @Nullable
   static SuspiciousStewIngredient of(ItemConvertible item) {
      Item var3 = item.asItem();
      if (var3 instanceof BlockItem blockItem) {
         Block var6 = blockItem.getBlock();
         if (var6 instanceof SuspiciousStewIngredient suspiciousStewIngredient) {
            return suspiciousStewIngredient;
         }
      }

      Item var2 = item.asItem();
      if (var2 instanceof SuspiciousStewIngredient suspiciousStewIngredient2) {
         return suspiciousStewIngredient2;
      } else {
         return null;
      }
   }
}
