/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.screen.recipebook.AnimatedResultButton
 *  net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider
 *  net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookResults
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.TexturedButtonWidget
 *  net.minecraft.client.recipebook.ClientRecipeBook
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.NetworkRecipeId
 *  net.minecraft.recipe.display.SlotDisplayContexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.context.ContextParameterMap
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeAlternativesWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RecipeBookResults {
    public static final int field_32411 = 20;
    private static final ButtonTextures PAGE_FORWARD_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"recipe_book/page_forward"), Identifier.ofVanilla((String)"recipe_book/page_forward_highlighted"));
    private static final ButtonTextures PAGE_BACKWARD_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"recipe_book/page_backward"), Identifier.ofVanilla((String)"recipe_book/page_backward_highlighted"));
    private static final Text NEXT_PAGE_TOOLTIP = Text.translatable((String)"gui.recipebook.next_page");
    private static final Text PREVIOUS_PAGE_TOOLTIP = Text.translatable((String)"gui.recipebook.previous_page");
    private static final int field_64552 = 12;
    private static final int field_64553 = 17;
    private final List<AnimatedResultButton> resultButtons = Lists.newArrayListWithCapacity((int)20);
    private @Nullable AnimatedResultButton hoveredResultButton;
    private final RecipeAlternativesWidget alternatesWidget;
    private MinecraftClient client;
    private final RecipeBookWidget<?> recipeBookWidget;
    private List<RecipeResultCollection> resultCollections = ImmutableList.of();
    private @Nullable TexturedButtonWidget nextPageButton;
    private @Nullable TexturedButtonWidget prevPageButton;
    private int pageCount;
    private int currentPage;
    private ClientRecipeBook recipeBook;
    private @Nullable NetworkRecipeId lastClickedRecipe;
    private @Nullable RecipeResultCollection resultCollection;
    private boolean filteringCraftable;

    public RecipeBookResults(RecipeBookWidget<?> recipeBookWidget, CurrentIndexProvider currentIndexProvider, boolean furnace) {
        this.recipeBookWidget = recipeBookWidget;
        this.alternatesWidget = new RecipeAlternativesWidget(currentIndexProvider, furnace);
        for (int i = 0; i < 20; ++i) {
            this.resultButtons.add(new AnimatedResultButton(currentIndexProvider));
        }
    }

    public void initialize(MinecraftClient client, int parentLeft, int parentTop) {
        this.client = client;
        this.recipeBook = client.player.getRecipeBook();
        for (int i = 0; i < this.resultButtons.size(); ++i) {
            ((AnimatedResultButton)this.resultButtons.get(i)).setPosition(parentLeft + 11 + 25 * (i % 5), parentTop + 31 + 25 * (i / 5));
        }
        this.nextPageButton = new TexturedButtonWidget(parentLeft + 93, parentTop + 137, 12, 17, PAGE_FORWARD_TEXTURES, buttonWidget -> this.hideShowPageButtons(), NEXT_PAGE_TOOLTIP);
        this.nextPageButton.setTooltip(Tooltip.of((Text)NEXT_PAGE_TOOLTIP));
        this.prevPageButton = new TexturedButtonWidget(parentLeft + 38, parentTop + 137, 12, 17, PAGE_BACKWARD_TEXTURES, buttonWidget -> this.hideShowPageButtons(), PREVIOUS_PAGE_TOOLTIP);
        this.prevPageButton.setTooltip(Tooltip.of((Text)PREVIOUS_PAGE_TOOLTIP));
    }

    public void setResults(List<RecipeResultCollection> resultCollections, boolean resetCurrentPage, boolean filteringCraftable) {
        this.resultCollections = resultCollections;
        this.filteringCraftable = filteringCraftable;
        this.pageCount = (int)Math.ceil((double)resultCollections.size() / 20.0);
        if (this.pageCount <= this.currentPage || resetCurrentPage) {
            this.currentPage = 0;
        }
        this.refreshResultButtons();
    }

    private void refreshResultButtons() {
        int i = 20 * this.currentPage;
        ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters((World)this.client.world);
        for (int j = 0; j < this.resultButtons.size(); ++j) {
            AnimatedResultButton animatedResultButton = (AnimatedResultButton)this.resultButtons.get(j);
            if (i + j < this.resultCollections.size()) {
                RecipeResultCollection recipeResultCollection = (RecipeResultCollection)this.resultCollections.get(i + j);
                animatedResultButton.showResultCollection(recipeResultCollection, this.filteringCraftable, this, contextParameterMap);
                animatedResultButton.visible = true;
                continue;
            }
            animatedResultButton.visible = false;
        }
        this.hideShowPageButtons();
    }

    private void hideShowPageButtons() {
        if (this.nextPageButton != null) {
            boolean bl = this.nextPageButton.visible = this.pageCount > 1 && this.currentPage < this.pageCount - 1;
        }
        if (this.prevPageButton != null) {
            this.prevPageButton.visible = this.pageCount > 1 && this.currentPage > 0;
        }
    }

    public void draw(DrawContext context, int x, int y, int mouseX, int mouseY, float deltaTicks) {
        if (this.pageCount > 1) {
            MutableText text = Text.translatable((String)"gui.recipebook.page", (Object[])new Object[]{this.currentPage + 1, this.pageCount});
            int i = this.client.textRenderer.getWidth((StringVisitable)text);
            context.drawTextWithShadow(this.client.textRenderer, (Text)text, x - i / 2 + 73, y + 141, -1);
        }
        this.hoveredResultButton = null;
        for (AnimatedResultButton animatedResultButton : this.resultButtons) {
            animatedResultButton.render(context, mouseX, mouseY, deltaTicks);
            if (!animatedResultButton.visible || !animatedResultButton.isSelected()) continue;
            this.hoveredResultButton = animatedResultButton;
        }
        if (this.nextPageButton != null) {
            this.nextPageButton.render(context, mouseX, mouseY, deltaTicks);
        }
        if (this.prevPageButton != null) {
            this.prevPageButton.render(context, mouseX, mouseY, deltaTicks);
        }
        context.createNewRootLayer();
        this.alternatesWidget.render(context, mouseX, mouseY, deltaTicks);
    }

    public void drawTooltip(DrawContext context, int x, int y) {
        if (this.client.currentScreen != null && this.hoveredResultButton != null && !this.alternatesWidget.isVisible()) {
            ItemStack itemStack = this.hoveredResultButton.getDisplayStack();
            Identifier identifier = (Identifier)itemStack.get(DataComponentTypes.TOOLTIP_STYLE);
            context.drawTooltip(this.client.textRenderer, this.hoveredResultButton.getTooltip(itemStack), x, y, identifier);
        }
    }

    public @Nullable NetworkRecipeId getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    public @Nullable RecipeResultCollection getLastClickedResults() {
        return this.resultCollection;
    }

    public void hideAlternates() {
        this.alternatesWidget.setVisible(false);
    }

    public boolean mouseClicked(Click click, int left, int top, int width, int height, boolean doubled) {
        this.lastClickedRecipe = null;
        this.resultCollection = null;
        if (this.alternatesWidget.isVisible()) {
            if (this.alternatesWidget.mouseClicked(click, doubled)) {
                this.lastClickedRecipe = this.alternatesWidget.getLastClickedRecipe();
                this.resultCollection = this.alternatesWidget.getResults();
            } else {
                this.alternatesWidget.setVisible(false);
            }
            return true;
        }
        if (this.nextPageButton.mouseClicked(click, doubled)) {
            ++this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        if (this.prevPageButton.mouseClicked(click, doubled)) {
            --this.currentPage;
            this.refreshResultButtons();
            return true;
        }
        ContextParameterMap contextParameterMap = SlotDisplayContexts.createParameters((World)this.client.world);
        for (AnimatedResultButton animatedResultButton : this.resultButtons) {
            if (!animatedResultButton.mouseClicked(click, doubled)) continue;
            if (click.button() == 0) {
                this.lastClickedRecipe = animatedResultButton.getCurrentId();
                this.resultCollection = animatedResultButton.getResultCollection();
            } else if (click.button() == 1 && !this.alternatesWidget.isVisible() && !animatedResultButton.hasSingleResult()) {
                this.alternatesWidget.showAlternativesForResult(animatedResultButton.getResultCollection(), contextParameterMap, this.filteringCraftable, animatedResultButton.getX(), animatedResultButton.getY(), left + width / 2, top + 13 + height / 2, (float)animatedResultButton.getWidth());
            }
            return true;
        }
        return false;
    }

    public void onRecipeDisplayed(NetworkRecipeId recipeId) {
        this.recipeBookWidget.onRecipeDisplayed(recipeId);
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    protected void forEachButton(Consumer<ClickableWidget> action) {
        this.resultButtons.forEach(action);
    }
}

