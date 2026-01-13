/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.screen.recipebook.AnimatedResultButton
 *  net.minecraft.client.gui.screen.recipebook.AnimatedResultButton$Result
 *  net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookResults
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection
 *  net.minecraft.client.gui.screen.recipebook.RecipeResultCollection$RecipeFilterMode
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.input.MouseInput
 *  net.minecraft.client.recipebook.ClientRecipeBook
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.NetworkRecipeId
 *  net.minecraft.recipe.RecipeDisplayEntry
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.context.ContextParameterMap
 */
package net.minecraft.client.gui.screen.recipebook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class AnimatedResultButton
extends ClickableWidget {
    private static final Identifier SLOT_MANY_CRAFTABLE_TEXTURE = Identifier.ofVanilla((String)"recipe_book/slot_many_craftable");
    private static final Identifier SLOT_CRAFTABLE_TEXTURE = Identifier.ofVanilla((String)"recipe_book/slot_craftable");
    private static final Identifier SLOT_MANY_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla((String)"recipe_book/slot_many_uncraftable");
    private static final Identifier SLOT_UNCRAFTABLE_TEXTURE = Identifier.ofVanilla((String)"recipe_book/slot_uncraftable");
    private static final float field_32414 = 15.0f;
    private static final int field_32415 = 25;
    private static final Text MORE_RECIPES_TEXT = Text.translatable((String)"gui.recipebook.moreRecipes");
    private RecipeResultCollection resultCollection = RecipeResultCollection.EMPTY;
    private List<Result> results = List.of();
    private boolean allResultsEqual;
    private final CurrentIndexProvider currentIndexProvider;
    private float bounce;

    public AnimatedResultButton(CurrentIndexProvider currentIndexProvider) {
        super(0, 0, 25, 25, ScreenTexts.EMPTY);
        this.currentIndexProvider = currentIndexProvider;
    }

    public void showResultCollection(RecipeResultCollection resultCollection, boolean filteringCraftable, RecipeBookResults results, ContextParameterMap context) {
        this.resultCollection = resultCollection;
        List list = resultCollection.filter(filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY);
        this.results = list.stream().map(entry -> new Result(entry.id(), entry.getStacks(context))).toList();
        this.allResultsEqual = AnimatedResultButton.areAllResultsEqual((List)this.results);
        List<NetworkRecipeId> list2 = list.stream().map(RecipeDisplayEntry::id).filter(arg_0 -> ((ClientRecipeBook)results.getRecipeBook()).isHighlighted(arg_0)).toList();
        if (!list2.isEmpty()) {
            list2.forEach(arg_0 -> ((RecipeBookResults)results).onRecipeDisplayed(arg_0));
            this.bounce = 15.0f;
        }
    }

    private static boolean areAllResultsEqual(List<Result> results) {
        Iterator iterator = results.stream().flatMap(result -> result.displayItems().stream()).iterator();
        if (!iterator.hasNext()) {
            return true;
        }
        ItemStack itemStack = (ItemStack)iterator.next();
        while (iterator.hasNext()) {
            ItemStack itemStack2 = (ItemStack)iterator.next();
            if (ItemStack.areItemsAndComponentsEqual((ItemStack)itemStack, (ItemStack)itemStack2)) continue;
            return false;
        }
        return true;
    }

    public RecipeResultCollection getResultCollection() {
        return this.resultCollection;
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        boolean bl;
        Identifier identifier = this.resultCollection.hasCraftableRecipes() ? (this.hasMultipleResults() ? SLOT_MANY_CRAFTABLE_TEXTURE : SLOT_CRAFTABLE_TEXTURE) : (this.hasMultipleResults() ? SLOT_MANY_UNCRAFTABLE_TEXTURE : SLOT_UNCRAFTABLE_TEXTURE);
        boolean bl2 = bl = this.bounce > 0.0f;
        if (bl) {
            float f = 1.0f + 0.1f * (float)Math.sin(this.bounce / 15.0f * (float)Math.PI);
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
            context.getMatrices().scale(f, f);
            context.getMatrices().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
            this.bounce -= deltaTicks;
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
        ItemStack itemStack = this.getDisplayStack();
        int i = 4;
        if (this.hasMultipleResults() && this.allResultsEqual) {
            context.drawItem(itemStack, this.getX() + i + 1, this.getY() + i + 1, 0);
            --i;
        }
        context.drawItemWithoutEntity(itemStack, this.getX() + i, this.getY() + i);
        if (bl) {
            context.getMatrices().popMatrix();
        }
    }

    private boolean hasMultipleResults() {
        return this.results.size() > 1;
    }

    public boolean hasSingleResult() {
        return this.results.size() == 1;
    }

    public NetworkRecipeId getCurrentId() {
        int i = this.currentIndexProvider.currentIndex() % this.results.size();
        return ((Result)this.results.get((int)i)).id;
    }

    public ItemStack getDisplayStack() {
        int i = this.currentIndexProvider.currentIndex();
        int j = this.results.size();
        int k = i / j;
        int l = i - j * k;
        return ((Result)this.results.get(l)).getDisplayStack(k);
    }

    public List<Text> getTooltip(ItemStack stack) {
        ArrayList<Text> list = new ArrayList<Text>(Screen.getTooltipFromItem((MinecraftClient)MinecraftClient.getInstance(), (ItemStack)stack));
        if (this.hasMultipleResults()) {
            list.add(MORE_RECIPES_TEXT);
        }
        return list;
    }

    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, (Text)Text.translatable((String)"narration.recipe", (Object[])new Object[]{this.getDisplayStack().getName()}));
        if (this.hasMultipleResults()) {
            builder.put(NarrationPart.USAGE, new Text[]{Text.translatable((String)"narration.button.usage.hovered"), Text.translatable((String)"narration.recipe.usage.more")});
        } else {
            builder.put(NarrationPart.USAGE, (Text)Text.translatable((String)"narration.button.usage.hovered"));
        }
    }

    public int getWidth() {
        return 25;
    }

    protected boolean isValidClickButton(MouseInput input) {
        return input.button() == 0 || input.button() == 1;
    }
}

