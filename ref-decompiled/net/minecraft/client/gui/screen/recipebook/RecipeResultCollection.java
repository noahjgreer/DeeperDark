package net.minecraft.client.gui.screen.recipebook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;

@Environment(EnvType.CLIENT)
public class RecipeResultCollection {
   public static final RecipeResultCollection EMPTY = new RecipeResultCollection(List.of());
   private final List entries;
   private final Set craftableRecipes = new HashSet();
   private final Set displayableRecipes = new HashSet();

   public RecipeResultCollection(List entries) {
      this.entries = entries;
   }

   public void populateRecipes(RecipeFinder finder, Predicate displayablePredicate) {
      Iterator var3 = this.entries.iterator();

      while(true) {
         while(var3.hasNext()) {
            RecipeDisplayEntry recipeDisplayEntry = (RecipeDisplayEntry)var3.next();
            boolean bl = displayablePredicate.test(recipeDisplayEntry.display());
            if (bl) {
               this.displayableRecipes.add(recipeDisplayEntry.id());
            } else {
               this.displayableRecipes.remove(recipeDisplayEntry.id());
            }

            if (bl && recipeDisplayEntry.isCraftable(finder)) {
               this.craftableRecipes.add(recipeDisplayEntry.id());
            } else {
               this.craftableRecipes.remove(recipeDisplayEntry.id());
            }
         }

         return;
      }
   }

   public boolean isCraftable(NetworkRecipeId recipeId) {
      return this.craftableRecipes.contains(recipeId);
   }

   public boolean hasCraftableRecipes() {
      return !this.craftableRecipes.isEmpty();
   }

   public boolean hasDisplayableRecipes() {
      return !this.displayableRecipes.isEmpty();
   }

   public List getAllRecipes() {
      return this.entries;
   }

   public List filter(RecipeFilterMode filterMode) {
      Predicate var10000;
      Set var6;
      switch (filterMode.ordinal()) {
         case 0:
            var6 = this.displayableRecipes;
            Objects.requireNonNull(var6);
            var10000 = var6::contains;
            break;
         case 1:
            var6 = this.craftableRecipes;
            Objects.requireNonNull(var6);
            var10000 = var6::contains;
            break;
         case 2:
            var10000 = (recipeId) -> {
               return this.displayableRecipes.contains(recipeId) && !this.craftableRecipes.contains(recipeId);
            };
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Predicate predicate = var10000;
      List list = new ArrayList();
      Iterator var4 = this.entries.iterator();

      while(var4.hasNext()) {
         RecipeDisplayEntry recipeDisplayEntry = (RecipeDisplayEntry)var4.next();
         if (predicate.test(recipeDisplayEntry.id())) {
            list.add(recipeDisplayEntry);
         }
      }

      return list;
   }

   @Environment(EnvType.CLIENT)
   public static enum RecipeFilterMode {
      ANY,
      CRAFTABLE,
      NOT_CRAFTABLE;

      // $FF: synthetic method
      private static RecipeFilterMode[] method_62052() {
         return new RecipeFilterMode[]{ANY, CRAFTABLE, NOT_CRAFTABLE};
      }
   }
}
