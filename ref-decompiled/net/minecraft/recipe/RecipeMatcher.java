package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class RecipeMatcher {
   public final Reference2IntOpenHashMap available = new Reference2IntOpenHashMap();

   boolean hasAtLeast(Object input, int minimum) {
      return this.available.getInt(input) >= minimum;
   }

   void consume(Object input, int count) {
      int i = this.available.addTo(input, -count);
      if (i < count) {
         throw new IllegalStateException("Took " + count + " items, but only had " + i);
      }
   }

   void addInput(Object input, int count) {
      this.available.addTo(input, count);
   }

   public boolean match(List ingredients, int quantity, @Nullable ItemCallback itemCallback) {
      return (new Matcher(ingredients)).match(quantity, itemCallback);
   }

   public int countCrafts(List ingredients, int max, @Nullable ItemCallback itemCallback) {
      return (new Matcher(ingredients)).countCrafts(max, itemCallback);
   }

   public void clear() {
      this.available.clear();
   }

   public void add(Object input, int count) {
      this.addInput(input, count);
   }

   List createItemRequirementList(Iterable ingredients) {
      List list = new ArrayList();
      ObjectIterator var3 = Reference2IntMaps.fastIterable(this.available).iterator();

      while(var3.hasNext()) {
         Reference2IntMap.Entry entry = (Reference2IntMap.Entry)var3.next();
         if (entry.getIntValue() > 0 && anyAccept(ingredients, entry.getKey())) {
            list.add(entry.getKey());
         }
      }

      return list;
   }

   private static boolean anyAccept(Iterable ingredients, Object item) {
      Iterator var2 = ingredients.iterator();

      RawIngredient rawIngredient;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         rawIngredient = (RawIngredient)var2.next();
      } while(!rawIngredient.acceptsItem(item));

      return true;
   }

   @VisibleForTesting
   public int getMaximumCrafts(List ingredients) {
      int i = Integer.MAX_VALUE;
      ObjectIterable objectIterable = Reference2IntMaps.fastIterable(this.available);
      Iterator var4 = ingredients.iterator();

      label31:
      while(var4.hasNext()) {
         RawIngredient rawIngredient = (RawIngredient)var4.next();
         int j = 0;
         ObjectIterator var7 = objectIterable.iterator();

         while(var7.hasNext()) {
            Reference2IntMap.Entry entry = (Reference2IntMap.Entry)var7.next();
            int k = entry.getIntValue();
            if (k > j) {
               if (rawIngredient.acceptsItem(entry.getKey())) {
                  j = k;
               }

               if (j >= i) {
                  continue label31;
               }
            }
         }

         i = j;
         if (j == 0) {
            break;
         }
      }

      return i;
   }

   private class Matcher {
      private final List ingredients;
      private final int totalIngredients;
      private final List requiredItems;
      private final int totalRequiredItems;
      private final BitSet bits;
      private final IntList ingredientItemLookup = new IntArrayList();

      public Matcher(final List ingredients) {
         this.ingredients = ingredients;
         this.totalIngredients = ingredients.size();
         this.requiredItems = RecipeMatcher.this.createItemRequirementList(ingredients);
         this.totalRequiredItems = this.requiredItems.size();
         this.bits = new BitSet(this.getVisitedIngredientIndexCount() + this.getVisitedItemIndexCount() + this.getRequirementIndexCount() + this.getItemMatchIndexCount() + this.getMissingIndexCount());
         this.initItemMatch();
      }

      private void initItemMatch() {
         for(int i = 0; i < this.totalIngredients; ++i) {
            RawIngredient rawIngredient = (RawIngredient)this.ingredients.get(i);

            for(int j = 0; j < this.totalRequiredItems; ++j) {
               if (rawIngredient.acceptsItem(this.requiredItems.get(j))) {
                  this.setMatch(j, i);
               }
            }
         }

      }

      public boolean match(int quantity, @Nullable ItemCallback itemCallback) {
         if (quantity <= 0) {
            return true;
         } else {
            int i = 0;

            while(true) {
               IntList intList = this.tryFindIngredientItemLookup(quantity);
               int k;
               int l;
               if (intList == null) {
                  boolean bl = i == this.totalIngredients;
                  boolean bl2 = bl && itemCallback != null;
                  this.clearVisited();
                  this.clearRequirements();

                  for(k = 0; k < this.totalIngredients; ++k) {
                     for(l = 0; l < this.totalRequiredItems; ++l) {
                        if (this.isMissing(l, k)) {
                           this.markNotMissing(l, k);
                           RecipeMatcher.this.addInput(this.requiredItems.get(l), quantity);
                           if (bl2) {
                              itemCallback.accept(this.requiredItems.get(l));
                           }
                           break;
                        }
                     }
                  }

                  assert this.bits.get(this.getMissingIndexOffset(), this.getMissingIndexOffset() + this.getMissingIndexCount()).isEmpty();

                  return bl;
               }

               int j = intList.getInt(0);
               RecipeMatcher.this.consume(this.requiredItems.get(j), quantity);
               k = intList.size() - 1;
               this.unfulfillRequirement(intList.getInt(k));
               ++i;

               for(l = 0; l < intList.size() - 1; ++l) {
                  int m;
                  int n;
                  if (isItem(l)) {
                     m = intList.getInt(l);
                     n = intList.getInt(l + 1);
                     this.markMissing(m, n);
                  } else {
                     m = intList.getInt(l + 1);
                     n = intList.getInt(l);
                     this.markNotMissing(m, n);
                  }
               }
            }
         }
      }

      private static boolean isItem(int index) {
         return (index & 1) == 0;
      }

      @Nullable
      private IntList tryFindIngredientItemLookup(int min) {
         this.clearVisited();

         for(int i = 0; i < this.totalRequiredItems; ++i) {
            if (RecipeMatcher.this.hasAtLeast(this.requiredItems.get(i), min)) {
               IntList intList = this.findIngredientItemLookup(i);
               if (intList != null) {
                  return intList;
               }
            }
         }

         return null;
      }

      @Nullable
      private IntList findIngredientItemLookup(int itemIndex) {
         this.ingredientItemLookup.clear();
         this.markItemVisited(itemIndex);
         this.ingredientItemLookup.add(itemIndex);

         while(!this.ingredientItemLookup.isEmpty()) {
            int i = this.ingredientItemLookup.size();
            int j;
            int k;
            if (isItem(i - 1)) {
               j = this.ingredientItemLookup.getInt(i - 1);

               for(k = 0; k < this.totalIngredients; ++k) {
                  if (!this.hasVisitedIngredient(k) && this.matches(j, k) && !this.isMissing(j, k)) {
                     this.markIngredientVisited(k);
                     this.ingredientItemLookup.add(k);
                     break;
                  }
               }
            } else {
               j = this.ingredientItemLookup.getInt(i - 1);
               if (!this.getRequirement(j)) {
                  return this.ingredientItemLookup;
               }

               for(k = 0; k < this.totalRequiredItems; ++k) {
                  if (!this.isRequirementUnfulfilled(k) && this.isMissing(k, j)) {
                     assert this.matches(k, j);

                     this.markItemVisited(k);
                     this.ingredientItemLookup.add(k);
                     break;
                  }
               }
            }

            j = this.ingredientItemLookup.size();
            if (j == i) {
               this.ingredientItemLookup.removeInt(j - 1);
            }
         }

         return null;
      }

      private int getVisitedIngredientIndexOffset() {
         return 0;
      }

      private int getVisitedIngredientIndexCount() {
         return this.totalIngredients;
      }

      private int getVisitedItemIndexOffset() {
         return this.getVisitedIngredientIndexOffset() + this.getVisitedIngredientIndexCount();
      }

      private int getVisitedItemIndexCount() {
         return this.totalRequiredItems;
      }

      private int getRequirementIndexOffset() {
         return this.getVisitedItemIndexOffset() + this.getVisitedItemIndexCount();
      }

      private int getRequirementIndexCount() {
         return this.totalIngredients;
      }

      private int getItemMatchIndexOffset() {
         return this.getRequirementIndexOffset() + this.getRequirementIndexCount();
      }

      private int getItemMatchIndexCount() {
         return this.totalIngredients * this.totalRequiredItems;
      }

      private int getMissingIndexOffset() {
         return this.getItemMatchIndexOffset() + this.getItemMatchIndexCount();
      }

      private int getMissingIndexCount() {
         return this.totalIngredients * this.totalRequiredItems;
      }

      private boolean getRequirement(int itemId) {
         return this.bits.get(this.getRequirementIndex(itemId));
      }

      private void unfulfillRequirement(int itemId) {
         this.bits.set(this.getRequirementIndex(itemId));
      }

      private int getRequirementIndex(int itemId) {
         assert itemId >= 0 && itemId < this.totalIngredients;

         return this.getRequirementIndexOffset() + itemId;
      }

      private void clearRequirements() {
         this.clear(this.getRequirementIndexOffset(), this.getRequirementIndexCount());
      }

      private void setMatch(int itemIndex, int ingredientIndex) {
         this.bits.set(this.getMatchIndex(itemIndex, ingredientIndex));
      }

      private boolean matches(int itemIndex, int ingredientIndex) {
         return this.bits.get(this.getMatchIndex(itemIndex, ingredientIndex));
      }

      private int getMatchIndex(int itemIndex, int ingredientIndex) {
         assert itemIndex >= 0 && itemIndex < this.totalRequiredItems;

         assert ingredientIndex >= 0 && ingredientIndex < this.totalIngredients;

         return this.getItemMatchIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
      }

      private boolean isMissing(int itemIndex, int ingredientIndex) {
         return this.bits.get(this.getMissingIndex(itemIndex, ingredientIndex));
      }

      private void markMissing(int itemIndex, int ingredientIndex) {
         int i = this.getMissingIndex(itemIndex, ingredientIndex);

         assert !this.bits.get(i);

         this.bits.set(i);
      }

      private void markNotMissing(int itemIndex, int ingredientIndex) {
         int i = this.getMissingIndex(itemIndex, ingredientIndex);

         assert this.bits.get(i);

         this.bits.clear(i);
      }

      private int getMissingIndex(int itemIndex, int ingredientIndex) {
         assert itemIndex >= 0 && itemIndex < this.totalRequiredItems;

         assert ingredientIndex >= 0 && ingredientIndex < this.totalIngredients;

         return this.getMissingIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
      }

      private void markIngredientVisited(int index) {
         this.bits.set(this.getVisitedIngredientIndex(index));
      }

      private boolean hasVisitedIngredient(int index) {
         return this.bits.get(this.getVisitedIngredientIndex(index));
      }

      private int getVisitedIngredientIndex(int index) {
         assert index >= 0 && index < this.totalIngredients;

         return this.getVisitedIngredientIndexOffset() + index;
      }

      private void markItemVisited(int index) {
         this.bits.set(this.getVisitedItemIndex(index));
      }

      private boolean isRequirementUnfulfilled(int index) {
         return this.bits.get(this.getVisitedItemIndex(index));
      }

      private int getVisitedItemIndex(int index) {
         assert index >= 0 && index < this.totalRequiredItems;

         return this.getVisitedItemIndexOffset() + index;
      }

      private void clearVisited() {
         this.clear(this.getVisitedIngredientIndexOffset(), this.getVisitedIngredientIndexCount());
         this.clear(this.getVisitedItemIndexOffset(), this.getVisitedItemIndexCount());
      }

      private void clear(int start, int offset) {
         this.bits.clear(start, start + offset);
      }

      public int countCrafts(int max, @Nullable ItemCallback itemCallback) {
         int i = 0;
         int j = Math.min(max, RecipeMatcher.this.getMaximumCrafts(this.ingredients)) + 1;

         while(true) {
            while(true) {
               int k = (i + j) / 2;
               if (this.match(k, (ItemCallback)null)) {
                  if (j - i <= 1) {
                     if (k > 0) {
                        this.match(k, itemCallback);
                     }

                     return k;
                  }

                  i = k;
               } else {
                  j = k;
               }
            }
         }
      }
   }

   @FunctionalInterface
   public interface ItemCallback {
      void accept(Object item);
   }

   @FunctionalInterface
   public interface RawIngredient {
      boolean acceptsItem(Object entry);
   }
}
