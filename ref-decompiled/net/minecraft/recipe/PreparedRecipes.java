package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PreparedRecipes {
   public static final PreparedRecipes EMPTY = new PreparedRecipes(ImmutableMultimap.of(), Map.of());
   private final Multimap byType;
   private final Map byKey;

   private PreparedRecipes(Multimap byType, Map byKey) {
      this.byType = byType;
      this.byKey = byKey;
   }

   public static PreparedRecipes of(Iterable recipes) {
      ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
      ImmutableMap.Builder builder2 = ImmutableMap.builder();
      Iterator var3 = recipes.iterator();

      while(var3.hasNext()) {
         RecipeEntry recipeEntry = (RecipeEntry)var3.next();
         builder.put(recipeEntry.value().getType(), recipeEntry);
         builder2.put(recipeEntry.id(), recipeEntry);
      }

      return new PreparedRecipes(builder.build(), builder2.build());
   }

   public Collection getAll(RecipeType type) {
      return this.byType.get(type);
   }

   public Collection recipes() {
      return this.byKey.values();
   }

   @Nullable
   public RecipeEntry get(RegistryKey key) {
      return (RecipeEntry)this.byKey.get(key);
   }

   public Stream find(RecipeType type, RecipeInput input, World world) {
      return input.isEmpty() ? Stream.empty() : this.getAll(type).stream().filter((entry) -> {
         return entry.value().matches(input, world);
      });
   }
}
