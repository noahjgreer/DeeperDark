/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.recipebook.RecipeBookType
 *  net.minecraft.recipe.book.RecipeBookCategories
 *  net.minecraft.recipe.book.RecipeBookCategory
 *  net.minecraft.recipe.book.RecipeBookGroup
 */
package net.minecraft.client.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class RecipeBookType
extends Enum<RecipeBookType>
implements RecipeBookGroup {
    public static final /* enum */ RecipeBookType CRAFTING = new RecipeBookType("CRAFTING", 0, new RecipeBookCategory[]{RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE});
    public static final /* enum */ RecipeBookType FURNACE = new RecipeBookType("FURNACE", 1, new RecipeBookCategory[]{RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC});
    public static final /* enum */ RecipeBookType BLAST_FURNACE = new RecipeBookType("BLAST_FURNACE", 2, new RecipeBookCategory[]{RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC});
    public static final /* enum */ RecipeBookType SMOKER = new RecipeBookType("SMOKER", 3, new RecipeBookCategory[]{RecipeBookCategories.SMOKER_FOOD});
    private final List<RecipeBookCategory> categories;
    private static final /* synthetic */ RecipeBookType[] field_54842;

    public static RecipeBookType[] values() {
        return (RecipeBookType[])field_54842.clone();
    }

    public static RecipeBookType valueOf(String string) {
        return Enum.valueOf(RecipeBookType.class, string);
    }

    private RecipeBookType(RecipeBookCategory ... categories) {
        this.categories = List.of(categories);
    }

    public List<RecipeBookCategory> getCategories() {
        return this.categories;
    }

    private static /* synthetic */ RecipeBookType[] method_64889() {
        return new RecipeBookType[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
    }

    static {
        field_54842 = RecipeBookType.method_64889();
    }
}

