/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookGroup;

@Environment(value=EnvType.CLIENT)
public record RecipeBookWidget.Tab(ItemStack primaryIcon, Optional<ItemStack> secondaryIcon, RecipeBookGroup category) {
    public RecipeBookWidget.Tab(RecipeBookType type) {
        this(new ItemStack(Items.COMPASS), Optional.empty(), type);
    }

    public RecipeBookWidget.Tab(Item primaryIcon, RecipeBookCategory category) {
        this(new ItemStack(primaryIcon), Optional.empty(), (RecipeBookGroup)category);
    }

    public RecipeBookWidget.Tab(Item primaryIcon, Item secondaryIcon, RecipeBookCategory category) {
        this(new ItemStack(primaryIcon), Optional.of(new ItemStack(secondaryIcon)), (RecipeBookGroup)category);
    }
}
