/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen
 *  net.minecraft.client.gui.screen.ingame.FurnaceScreen
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget$Tab
 *  net.minecraft.client.recipebook.RecipeBookType
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.Items
 *  net.minecraft.recipe.book.RecipeBookCategories
 *  net.minecraft.screen.AbstractFurnaceScreenHandler
 *  net.minecraft.screen.FurnaceScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class FurnaceScreen
extends AbstractFurnaceScreen<FurnaceScreenHandler> {
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.ofVanilla((String)"container/furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.ofVanilla((String)"container/furnace/burn_progress");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/furnace.png");
    private static final Text TOGGLE_SMELTABLE_TEXT = Text.translatable((String)"gui.recipebook.toggleRecipes.smeltable");
    private static final List<RecipeBookWidget.Tab> TABS = List.of(new RecipeBookWidget.Tab(RecipeBookType.FURNACE), new RecipeBookWidget.Tab(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD), new RecipeBookWidget.Tab(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS), new RecipeBookWidget.Tab(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC));

    public FurnaceScreen(FurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super((AbstractFurnaceScreenHandler)handler, inventory, title, TOGGLE_SMELTABLE_TEXT, TEXTURE, LIT_PROGRESS_TEXTURE, BURN_PROGRESS_TEXTURE, TABS);
    }
}

