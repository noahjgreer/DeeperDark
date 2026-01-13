/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.List;
import net.minecraft.recipe.RecipeMatcher;
import org.jspecify.annotations.Nullable;

class RecipeMatcher.Matcher {
    private final List<? extends RecipeMatcher.RawIngredient<T>> ingredients;
    private final int totalIngredients;
    private final List<T> requiredItems;
    private final int totalRequiredItems;
    private final BitSet bits;
    private final IntList ingredientItemLookup = new IntArrayList();

    public RecipeMatcher.Matcher(List<? extends RecipeMatcher.RawIngredient<T>> ingredients) {
        this.ingredients = ingredients;
        this.totalIngredients = ingredients.size();
        this.requiredItems = RecipeMatcher.this.createItemRequirementList(ingredients);
        this.totalRequiredItems = this.requiredItems.size();
        this.bits = new BitSet(this.getVisitedIngredientIndexCount() + this.getVisitedItemIndexCount() + this.getRequirementIndexCount() + this.getItemMatchIndexCount() + this.getMissingIndexCount());
        this.initItemMatch();
    }

    private void initItemMatch() {
        for (int i = 0; i < this.totalIngredients; ++i) {
            RecipeMatcher.RawIngredient rawIngredient = this.ingredients.get(i);
            for (int j = 0; j < this.totalRequiredItems; ++j) {
                if (!rawIngredient.acceptsItem(this.requiredItems.get(j))) continue;
                this.setMatch(j, i);
            }
        }
    }

    public boolean match(int quantity,  @Nullable RecipeMatcher.ItemCallback<T> itemCallback) {
        int l;
        int k;
        IntList intList;
        if (quantity <= 0) {
            return true;
        }
        int i = 0;
        while ((intList = this.tryFindIngredientItemLookup(quantity)) != null) {
            int j = intList.getInt(0);
            RecipeMatcher.this.consume(this.requiredItems.get(j), quantity);
            k = intList.size() - 1;
            this.unfulfillRequirement(intList.getInt(k));
            ++i;
            for (l = 0; l < intList.size() - 1; ++l) {
                int n;
                int m;
                if (RecipeMatcher.Matcher.isItem(l)) {
                    m = intList.getInt(l);
                    n = intList.getInt(l + 1);
                    this.markMissing(m, n);
                    continue;
                }
                m = intList.getInt(l + 1);
                n = intList.getInt(l);
                this.markNotMissing(m, n);
            }
        }
        boolean bl = i == this.totalIngredients;
        boolean bl2 = bl && itemCallback != null;
        this.clearVisited();
        this.clearRequirements();
        block2: for (k = 0; k < this.totalIngredients; ++k) {
            for (l = 0; l < this.totalRequiredItems; ++l) {
                if (!this.isMissing(l, k)) continue;
                this.markNotMissing(l, k);
                RecipeMatcher.this.addInput(this.requiredItems.get(l), quantity);
                if (!bl2) continue block2;
                itemCallback.accept(this.requiredItems.get(l));
                continue block2;
            }
        }
        assert (this.bits.get(this.getMissingIndexOffset(), this.getMissingIndexOffset() + this.getMissingIndexCount()).isEmpty());
        return bl;
    }

    private static boolean isItem(int index) {
        return (index & 1) == 0;
    }

    private @Nullable IntList tryFindIngredientItemLookup(int min) {
        this.clearVisited();
        for (int i = 0; i < this.totalRequiredItems; ++i) {
            IntList intList;
            if (!RecipeMatcher.this.hasAtLeast(this.requiredItems.get(i), min) || (intList = this.findIngredientItemLookup(i)) == null) continue;
            return intList;
        }
        return null;
    }

    private @Nullable IntList findIngredientItemLookup(int itemIndex) {
        this.ingredientItemLookup.clear();
        this.markItemVisited(itemIndex);
        this.ingredientItemLookup.add(itemIndex);
        while (!this.ingredientItemLookup.isEmpty()) {
            int j;
            int i = this.ingredientItemLookup.size();
            if (RecipeMatcher.Matcher.isItem(i - 1)) {
                j = this.ingredientItemLookup.getInt(i - 1);
                for (k = 0; k < this.totalIngredients; ++k) {
                    if (this.hasVisitedIngredient(k) || !this.matches(j, k) || this.isMissing(j, k)) continue;
                    this.markIngredientVisited(k);
                    this.ingredientItemLookup.add(k);
                    break;
                }
            } else {
                j = this.ingredientItemLookup.getInt(i - 1);
                if (!this.getRequirement(j)) {
                    return this.ingredientItemLookup;
                }
                for (k = 0; k < this.totalRequiredItems; ++k) {
                    if (this.isRequirementUnfulfilled(k) || !this.isMissing(k, j)) continue;
                    assert (this.matches(k, j));
                    this.markItemVisited(k);
                    this.ingredientItemLookup.add(k);
                    break;
                }
            }
            if ((j = this.ingredientItemLookup.size()) != i) continue;
            this.ingredientItemLookup.removeInt(j - 1);
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
        assert (itemId >= 0 && itemId < this.totalIngredients);
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
        assert (itemIndex >= 0 && itemIndex < this.totalRequiredItems);
        assert (ingredientIndex >= 0 && ingredientIndex < this.totalIngredients);
        return this.getItemMatchIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
    }

    private boolean isMissing(int itemIndex, int ingredientIndex) {
        return this.bits.get(this.getMissingIndex(itemIndex, ingredientIndex));
    }

    private void markMissing(int itemIndex, int ingredientIndex) {
        int i = this.getMissingIndex(itemIndex, ingredientIndex);
        assert (!this.bits.get(i));
        this.bits.set(i);
    }

    private void markNotMissing(int itemIndex, int ingredientIndex) {
        int i = this.getMissingIndex(itemIndex, ingredientIndex);
        assert (this.bits.get(i));
        this.bits.clear(i);
    }

    private int getMissingIndex(int itemIndex, int ingredientIndex) {
        assert (itemIndex >= 0 && itemIndex < this.totalRequiredItems);
        assert (ingredientIndex >= 0 && ingredientIndex < this.totalIngredients);
        return this.getMissingIndexOffset() + itemIndex * this.totalIngredients + ingredientIndex;
    }

    private void markIngredientVisited(int index) {
        this.bits.set(this.getVisitedIngredientIndex(index));
    }

    private boolean hasVisitedIngredient(int index) {
        return this.bits.get(this.getVisitedIngredientIndex(index));
    }

    private int getVisitedIngredientIndex(int index) {
        assert (index >= 0 && index < this.totalIngredients);
        return this.getVisitedIngredientIndexOffset() + index;
    }

    private void markItemVisited(int index) {
        this.bits.set(this.getVisitedItemIndex(index));
    }

    private boolean isRequirementUnfulfilled(int index) {
        return this.bits.get(this.getVisitedItemIndex(index));
    }

    private int getVisitedItemIndex(int index) {
        assert (index >= 0 && index < this.totalRequiredItems);
        return this.getVisitedItemIndexOffset() + index;
    }

    private void clearVisited() {
        this.clear(this.getVisitedIngredientIndexOffset(), this.getVisitedIngredientIndexCount());
        this.clear(this.getVisitedItemIndexOffset(), this.getVisitedItemIndexCount());
    }

    private void clear(int start, int offset) {
        this.bits.clear(start, start + offset);
    }

    public int countCrafts(int max,  @Nullable RecipeMatcher.ItemCallback<T> itemCallback) {
        int k;
        int i = 0;
        int j = Math.min(max, RecipeMatcher.this.getMaximumCrafts(this.ingredients)) + 1;
        while (true) {
            if (this.match(k = (i + j) / 2, null)) {
                if (j - i <= 1) break;
                i = k;
                continue;
            }
            j = k;
        }
        if (k > 0) {
            this.match(k, itemCallback);
        }
        return k;
    }
}
