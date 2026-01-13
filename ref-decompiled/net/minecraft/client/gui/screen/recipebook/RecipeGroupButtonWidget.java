/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget$Tab
 *  net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection$RecipeFilterMode
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.TexturedButtonWidget
 *  net.minecraft.client.recipebook.ClientRecipeBook
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.RecipeDisplayEntry
 *  net.minecraft.recipe.book.RecipeBookGroup
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RecipeGroupButtonWidget
extends TexturedButtonWidget {
    private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"recipe_book/tab"), Identifier.ofVanilla((String)"recipe_book/tab_selected"));
    public static final int field_64554 = 35;
    public static final int field_64555 = 27;
    private final RecipeBookWidget.Tab tab;
    private static final float field_32412 = 15.0f;
    private float bounce;
    private boolean groupFocused = false;

    public RecipeGroupButtonWidget(int x, int y, RecipeBookWidget.Tab tab, ButtonWidget.PressAction onPress) {
        super(x, y, 35, 27, TEXTURES, onPress);
        this.tab = tab;
    }

    public void checkForNewRecipes(ClientRecipeBook recipeBook, boolean filteringCraftable) {
        RecipeResultCollection.RecipeFilterMode recipeFilterMode = filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY;
        List list = recipeBook.getResultsForCategory(this.tab.category());
        for (RecipeResultCollection recipeResultCollection : list) {
            for (RecipeDisplayEntry recipeDisplayEntry : recipeResultCollection.filter(recipeFilterMode)) {
                if (!recipeBook.isHighlighted(recipeDisplayEntry.id())) continue;
                this.bounce = 15.0f;
                return;
            }
        }
    }

    public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (this.bounce > 0.0f) {
            float f = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
            context.getMatrices().scale(1.0f, f);
            context.getMatrices().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
        }
        Identifier identifier = this.textures.get(true, this.groupFocused);
        int i = this.getX();
        if (this.groupFocused) {
            i -= 2;
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i, this.getY(), this.width, this.height);
        this.renderIcons(context);
        if (this.bounce > 0.0f) {
            context.getMatrices().popMatrix();
            this.bounce -= deltaTicks;
        }
    }

    protected void setCursor(DrawContext context) {
        if (!this.groupFocused) {
            super.setCursor(context);
        }
    }

    private void renderIcons(DrawContext context) {
        int i;
        int n = i = this.groupFocused ? -2 : 0;
        if (this.tab.secondaryIcon().isPresent()) {
            context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 3 + i, this.getY() + 5);
            context.drawItemWithoutEntity((ItemStack)this.tab.secondaryIcon().get(), this.getX() + 14 + i, this.getY() + 5);
        } else {
            context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 9 + i, this.getY() + 5);
        }
    }

    public RecipeBookGroup getCategory() {
        return this.tab.category();
    }

    public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
        List list = recipeBook.getResultsForCategory(this.tab.category());
        this.visible = false;
        for (RecipeResultCollection recipeResultCollection : list) {
            if (!recipeResultCollection.hasDisplayableRecipes()) continue;
            this.visible = true;
            break;
        }
        return this.visible;
    }

    public void focus() {
        this.groupFocused = true;
    }

    public void unfocus() {
        this.groupFocused = false;
    }
}

