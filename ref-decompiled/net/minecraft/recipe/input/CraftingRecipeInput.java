package net.minecraft.recipe.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;

public class CraftingRecipeInput implements RecipeInput {
   public static final CraftingRecipeInput EMPTY = new CraftingRecipeInput(0, 0, List.of());
   private final int width;
   private final int height;
   private final List stacks;
   private final RecipeFinder matcher = new RecipeFinder();
   private final int stackCount;

   private CraftingRecipeInput(int width, int height, List stacks) {
      this.width = width;
      this.height = height;
      this.stacks = stacks;
      int i = 0;
      Iterator var5 = stacks.iterator();

      while(var5.hasNext()) {
         ItemStack itemStack = (ItemStack)var5.next();
         if (!itemStack.isEmpty()) {
            ++i;
            this.matcher.addInput(itemStack, 1);
         }
      }

      this.stackCount = i;
   }

   public static CraftingRecipeInput create(int width, int height, List stacks) {
      return createPositioned(width, height, stacks).input();
   }

   public static Positioned createPositioned(int width, int height, List stacks) {
      if (width != 0 && height != 0) {
         int i = width - 1;
         int j = 0;
         int k = height - 1;
         int l = 0;

         int m;
         for(m = 0; m < height; ++m) {
            boolean bl = true;

            for(int n = 0; n < width; ++n) {
               ItemStack itemStack = (ItemStack)stacks.get(n + m * width);
               if (!itemStack.isEmpty()) {
                  i = Math.min(i, n);
                  j = Math.max(j, n);
                  bl = false;
               }
            }

            if (!bl) {
               k = Math.min(k, m);
               l = Math.max(l, m);
            }
         }

         m = j - i + 1;
         int o = l - k + 1;
         if (m > 0 && o > 0) {
            if (m == width && o == height) {
               return new Positioned(new CraftingRecipeInput(width, height, stacks), i, k);
            } else {
               List list = new ArrayList(m * o);

               for(int p = 0; p < o; ++p) {
                  for(int q = 0; q < m; ++q) {
                     int r = q + i + (p + k) * width;
                     list.add((ItemStack)stacks.get(r));
                  }
               }

               return new Positioned(new CraftingRecipeInput(m, o, list), i, k);
            }
         } else {
            return CraftingRecipeInput.Positioned.EMPTY;
         }
      } else {
         return CraftingRecipeInput.Positioned.EMPTY;
      }
   }

   public ItemStack getStackInSlot(int slot) {
      return (ItemStack)this.stacks.get(slot);
   }

   public ItemStack getStackInSlot(int x, int y) {
      return (ItemStack)this.stacks.get(x + y * this.width);
   }

   public int size() {
      return this.stacks.size();
   }

   public boolean isEmpty() {
      return this.stackCount == 0;
   }

   public RecipeFinder getRecipeMatcher() {
      return this.matcher;
   }

   public List getStacks() {
      return this.stacks;
   }

   public int getStackCount() {
      return this.stackCount;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CraftingRecipeInput)) {
         return false;
      } else {
         CraftingRecipeInput craftingRecipeInput = (CraftingRecipeInput)o;
         return this.width == craftingRecipeInput.width && this.height == craftingRecipeInput.height && this.stackCount == craftingRecipeInput.stackCount && ItemStack.stacksEqual(this.stacks, craftingRecipeInput.stacks);
      }
   }

   public int hashCode() {
      int i = ItemStack.listHashCode(this.stacks);
      i = 31 * i + this.width;
      i = 31 * i + this.height;
      return i;
   }

   public static record Positioned(CraftingRecipeInput input, int left, int top) {
      public static final Positioned EMPTY;

      public Positioned(CraftingRecipeInput craftingRecipeInput, int i, int j) {
         this.input = craftingRecipeInput;
         this.left = i;
         this.top = j;
      }

      public CraftingRecipeInput input() {
         return this.input;
      }

      public int left() {
         return this.left;
      }

      public int top() {
         return this.top;
      }

      static {
         EMPTY = new Positioned(CraftingRecipeInput.EMPTY, 0, 0);
      }
   }
}
