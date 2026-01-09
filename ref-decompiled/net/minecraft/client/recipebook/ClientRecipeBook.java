package net.minecraft.client.recipebook;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(EnvType.CLIENT)
public class ClientRecipeBook extends RecipeBook {
   private final Map recipes = new HashMap();
   private final Set highlightedRecipes = new HashSet();
   private Map resultsByCategory = Map.of();
   private List orderedResults = List.of();

   public void add(RecipeDisplayEntry entry) {
      this.recipes.put(entry.id(), entry);
   }

   public void remove(NetworkRecipeId recipeId) {
      this.recipes.remove(recipeId);
      this.highlightedRecipes.remove(recipeId);
   }

   public void clear() {
      this.recipes.clear();
      this.highlightedRecipes.clear();
   }

   public boolean isHighlighted(NetworkRecipeId recipeId) {
      return this.highlightedRecipes.contains(recipeId);
   }

   public void unmarkHighlighted(NetworkRecipeId recipeId) {
      this.highlightedRecipes.remove(recipeId);
   }

   public void markHighlighted(NetworkRecipeId recipeId) {
      this.highlightedRecipes.add(recipeId);
   }

   public void refresh() {
      Map map = toGroupedMap(this.recipes.values());
      Map map2 = new HashMap();
      ImmutableList.Builder builder = ImmutableList.builder();
      map.forEach((group, resultCollections) -> {
         Stream var10002 = resultCollections.stream().map(RecipeResultCollection::new);
         Objects.requireNonNull(builder);
         map2.put(group, (List)var10002.peek(builder::add).collect(ImmutableList.toImmutableList()));
      });
      RecipeBookType[] var4 = RecipeBookType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         RecipeBookType recipeBookType = var4[var6];
         map2.put(recipeBookType, (List)recipeBookType.getCategories().stream().flatMap((group) -> {
            return ((List)map2.getOrDefault(group, List.of())).stream();
         }).collect(ImmutableList.toImmutableList()));
      }

      this.resultsByCategory = Map.copyOf(map2);
      this.orderedResults = builder.build();
   }

   private static Map toGroupedMap(Iterable recipes) {
      Map map = new HashMap();
      Table table = HashBasedTable.create();
      Iterator var3 = recipes.iterator();

      while(var3.hasNext()) {
         RecipeDisplayEntry recipeDisplayEntry = (RecipeDisplayEntry)var3.next();
         RecipeBookCategory recipeBookCategory = recipeDisplayEntry.category();
         OptionalInt optionalInt = recipeDisplayEntry.group();
         if (optionalInt.isEmpty()) {
            ((List)map.computeIfAbsent(recipeBookCategory, (group) -> {
               return new ArrayList();
            })).add(List.of(recipeDisplayEntry));
         } else {
            List list = (List)table.get(recipeBookCategory, optionalInt.getAsInt());
            if (list == null) {
               list = new ArrayList();
               table.put(recipeBookCategory, optionalInt.getAsInt(), list);
               ((List)map.computeIfAbsent(recipeBookCategory, (group) -> {
                  return new ArrayList();
               })).add(list);
            }

            ((List)list).add(recipeDisplayEntry);
         }
      }

      return map;
   }

   public List getOrderedResults() {
      return this.orderedResults;
   }

   public List getResultsForCategory(RecipeBookGroup category) {
      return (List)this.resultsByCategory.getOrDefault(category, Collections.emptyList());
   }
}
