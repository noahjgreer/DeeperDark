/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider
 *  net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget$AlternativeButtonWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget$CraftingAlternativeButtonWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget$FurnaceAlternativeButtonWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection$RecipeFilterMode
 *  net.minecraft.recipe.NetworkRecipeId
 *  net.minecraft.recipe.RecipeDisplayEntry
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.context.ContextParameterMap
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RecipeAlternativesWidget
implements Drawable,
Element {
    private static final Identifier OVERLAY_RECIPE_TEXTURE = Identifier.ofVanilla((String)"recipe_book/overlay_recipe");
    private static final int field_32406 = 4;
    private static final int field_32407 = 5;
    private static final float field_33739 = 0.375f;
    public static final int field_42162 = 25;
    private final List<AlternativeButtonWidget> alternativeButtons = Lists.newArrayList();
    private boolean visible;
    private int buttonX;
    private int buttonY;
    private RecipeResultCollection resultCollection = RecipeResultCollection.EMPTY;
    private @Nullable NetworkRecipeId lastClickedRecipe;
    final CurrentIndexProvider currentIndexProvider;
    private final boolean furnace;

    public RecipeAlternativesWidget(CurrentIndexProvider currentIndexProvider, boolean furnace) {
        this.currentIndexProvider = currentIndexProvider;
        this.furnace = furnace;
    }

    public void showAlternativesForResult(RecipeResultCollection resultCollection, ContextParameterMap context, boolean filteringCraftable, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta) {
        float o;
        float n;
        float m;
        float h;
        float g;
        this.resultCollection = resultCollection;
        List list = resultCollection.filter(RecipeResultCollection.RecipeFilterMode.CRAFTABLE);
        List list2 = filteringCraftable ? Collections.emptyList() : resultCollection.filter(RecipeResultCollection.RecipeFilterMode.NOT_CRAFTABLE);
        int i = list.size();
        int j = i + list2.size();
        int k = j <= 16 ? 4 : 5;
        int l = (int)Math.ceil((float)j / (float)k);
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        float f = this.buttonX + Math.min(j, k) * 25;
        if (f > (g = (float)(areaCenterX + 50))) {
            this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((f - g) / delta)));
        }
        if ((h = (float)(this.buttonY + l * 25)) > (m = (float)(areaCenterY + 50))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((float)((h - m) / delta)));
        }
        if ((n = (float)this.buttonY) < (o = (float)(areaCenterY - 100))) {
            this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((float)((n - o) / delta)));
        }
        this.visible = true;
        this.alternativeButtons.clear();
        for (int p = 0; p < j; ++p) {
            boolean bl = p < i;
            RecipeDisplayEntry recipeDisplayEntry = bl ? (RecipeDisplayEntry)list.get(p) : (RecipeDisplayEntry)list2.get(p - i);
            int q = this.buttonX + 4 + 25 * (p % k);
            int r = this.buttonY + 5 + 25 * (p / k);
            if (this.furnace) {
                this.alternativeButtons.add(new FurnaceAlternativeButtonWidget(this, q, r, recipeDisplayEntry.id(), recipeDisplayEntry.display(), context, bl));
                continue;
            }
            this.alternativeButtons.add(new CraftingAlternativeButtonWidget(this, q, r, recipeDisplayEntry.id(), recipeDisplayEntry.display(), context, bl));
        }
        this.lastClickedRecipe = null;
    }

    public RecipeResultCollection getResults() {
        return this.resultCollection;
    }

    public @Nullable NetworkRecipeId getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) {
            return false;
        }
        for (AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
            if (!alternativeButtonWidget.mouseClicked(click, doubled)) continue;
            this.lastClickedRecipe = alternativeButtonWidget.recipeId;
            return true;
        }
        return false;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!this.visible) {
            return;
        }
        int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
        int j = Math.min(this.alternativeButtons.size(), i);
        int k = MathHelper.ceil((float)((float)this.alternativeButtons.size() / (float)i));
        int l = 4;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, OVERLAY_RECIPE_TEXTURE, this.buttonX, this.buttonY, j * 25 + 8, k * 25 + 8);
        for (AlternativeButtonWidget alternativeButtonWidget : this.alternativeButtons) {
            alternativeButtonWidget.render(context, mouseX, mouseY, deltaTicks);
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setFocused(boolean focused) {
    }

    public boolean isFocused() {
        return false;
    }
}

