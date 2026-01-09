package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class IngredientPlacement {
   public static final int field_55495 = -1;
   public static final IngredientPlacement NONE = new IngredientPlacement(List.of(), IntList.of());
   private final List ingredients;
   private final IntList placementSlots;

   private IngredientPlacement(List ingredients, IntList placementSlots) {
      this.ingredients = ingredients;
      this.placementSlots = placementSlots;
   }

   public static IngredientPlacement forSingleSlot(Ingredient ingredient) {
      return ingredient.isEmpty() ? NONE : new IngredientPlacement(List.of(ingredient), IntList.of(0));
   }

   public static IngredientPlacement forMultipleSlots(List ingredients) {
      int i = ingredients.size();
      List list = new ArrayList(i);
      IntList intList = new IntArrayList(i);
      int j = 0;
      Iterator var5 = ingredients.iterator();

      while(var5.hasNext()) {
         Optional optional = (Optional)var5.next();
         if (optional.isPresent()) {
            Ingredient ingredient = (Ingredient)optional.get();
            if (ingredient.isEmpty()) {
               return NONE;
            }

            list.add(ingredient);
            intList.add(j++);
         } else {
            intList.add(-1);
         }
      }

      return new IngredientPlacement(list, intList);
   }

   public static IngredientPlacement forShapeless(List ingredients) {
      int i = ingredients.size();
      IntList intList = new IntArrayList(i);

      for(int j = 0; j < i; ++j) {
         Ingredient ingredient = (Ingredient)ingredients.get(j);
         if (ingredient.isEmpty()) {
            return NONE;
         }

         intList.add(j);
      }

      return new IngredientPlacement(ingredients, intList);
   }

   public IntList getPlacementSlots() {
      return this.placementSlots;
   }

   public List getIngredients() {
      return this.ingredients;
   }

   public boolean hasNoPlacement() {
      return this.placementSlots.isEmpty();
   }
}
