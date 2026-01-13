/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.recipebook;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
abstract class RecipeAlternativesWidget.AlternativeButtonWidget
extends ClickableWidget {
    final NetworkRecipeId recipeId;
    private final boolean craftable;
    private final List<InputSlot> inputSlots;

    public RecipeAlternativesWidget.AlternativeButtonWidget(int x, int y, NetworkRecipeId recipeId, boolean craftable, List<InputSlot> inputSlots) {
        super(x, y, 24, 24, ScreenTexts.EMPTY);
        this.inputSlots = inputSlots;
        this.recipeId = recipeId;
        this.craftable = craftable;
    }

    protected static InputSlot slot(int x, int y, List<ItemStack> stacks) {
        return new InputSlot(3 + x * 7, 3 + y * 7, stacks);
    }

    protected abstract Identifier getOverlayTexture(boolean var1);

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getOverlayTexture(this.craftable), this.getX(), this.getY(), this.width, this.height);
        float f = this.getX() + 2;
        float g = this.getY() + 2;
        for (InputSlot inputSlot : this.inputSlots) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(f + (float)inputSlot.y, g + (float)inputSlot.x);
            context.getMatrices().scale(0.375f, 0.375f);
            context.getMatrices().translate(-8.0f, -8.0f);
            context.drawItem(inputSlot.get(RecipeAlternativesWidget.this.currentIndexProvider.currentIndex()), 0, 0);
            context.getMatrices().popMatrix();
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static final class InputSlot
    extends Record {
        final int y;
        final int x;
        private final List<ItemStack> stacks;

        public InputSlot(int y, int y2, List<ItemStack> stacks) {
            if (stacks.isEmpty()) {
                throw new IllegalArgumentException("Ingredient list must be non-empty");
            }
            this.y = y;
            this.x = y2;
            this.stacks = stacks;
        }

        public ItemStack get(int index) {
            return this.stacks.get(index % this.stacks.size());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{InputSlot.class, "x;y;ingredients", "y", "x", "stacks"}, this, object);
        }

        public int y() {
            return this.y;
        }

        public int x() {
            return this.x;
        }

        public List<ItemStack> stacks() {
            return this.stacks;
        }
    }
}
