package net.minecraft.recipe;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class RecipeCache {
   private final CachedRecipe[] cache;
   private WeakReference recipeManagerRef = new WeakReference((Object)null);

   public RecipeCache(int size) {
      this.cache = new CachedRecipe[size];
   }

   public Optional getRecipe(ServerWorld world, CraftingRecipeInput input) {
      if (input.isEmpty()) {
         return Optional.empty();
      } else {
         this.validateRecipeManager(world);

         for(int i = 0; i < this.cache.length; ++i) {
            CachedRecipe cachedRecipe = this.cache[i];
            if (cachedRecipe != null && cachedRecipe.matches(input)) {
               this.sendToFront(i);
               return Optional.ofNullable(cachedRecipe.value());
            }
         }

         return this.getAndCacheRecipe(input, world);
      }
   }

   private void validateRecipeManager(ServerWorld world) {
      ServerRecipeManager serverRecipeManager = world.getRecipeManager();
      if (serverRecipeManager != this.recipeManagerRef.get()) {
         this.recipeManagerRef = new WeakReference(serverRecipeManager);
         Arrays.fill(this.cache, (Object)null);
      }

   }

   private Optional getAndCacheRecipe(CraftingRecipeInput input, ServerWorld world) {
      Optional optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, input, world);
      this.cache(input, (RecipeEntry)optional.orElse((Object)null));
      return optional;
   }

   private void sendToFront(int index) {
      if (index > 0) {
         CachedRecipe cachedRecipe = this.cache[index];
         System.arraycopy(this.cache, 0, this.cache, 1, index);
         this.cache[0] = cachedRecipe;
      }

   }

   private void cache(CraftingRecipeInput input, @Nullable RecipeEntry recipe) {
      DefaultedList defaultedList = DefaultedList.ofSize(input.size(), ItemStack.EMPTY);

      for(int i = 0; i < input.size(); ++i) {
         defaultedList.set(i, input.getStackInSlot(i).copyWithCount(1));
      }

      System.arraycopy(this.cache, 0, this.cache, 1, this.cache.length - 1);
      this.cache[0] = new CachedRecipe(defaultedList, input.getWidth(), input.getHeight(), recipe);
   }

   private static record CachedRecipe(DefaultedList key, int width, int height, @Nullable RecipeEntry value) {
      CachedRecipe(DefaultedList defaultedList, int i, int j, @Nullable RecipeEntry recipeEntry) {
         this.key = defaultedList;
         this.width = i;
         this.height = j;
         this.value = recipeEntry;
      }

      public boolean matches(CraftingRecipeInput input) {
         if (this.width == input.getWidth() && this.height == input.getHeight()) {
            for(int i = 0; i < this.key.size(); ++i) {
               if (!ItemStack.areItemsAndComponentsEqual((ItemStack)this.key.get(i), input.getStackInSlot(i))) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      public DefaultedList key() {
         return this.key;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      @Nullable
      public RecipeEntry value() {
         return this.value;
      }
   }
}
