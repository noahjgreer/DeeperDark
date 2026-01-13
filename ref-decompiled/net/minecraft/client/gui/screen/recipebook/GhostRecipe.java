/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider
 *  net.minecraft.client.gui.screen.recipebook.GhostRecipe
 *  net.minecraft.client.gui.screen.recipebook.GhostRecipe$CyclingItem
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.display.SlotDisplay
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.context.ContextParameterMap
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.recipebook;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.recipebook.CurrentIndexProvider;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GhostRecipe {
    private final Reference2ObjectMap<Slot, CyclingItem> items = new Reference2ObjectArrayMap();
    private final CurrentIndexProvider currentIndexProvider;

    public GhostRecipe(CurrentIndexProvider currentIndexProvider) {
        this.currentIndexProvider = currentIndexProvider;
    }

    public void clear() {
        this.items.clear();
    }

    private void addItems(Slot slot, ContextParameterMap context, SlotDisplay display, boolean resultSlot) {
        List list = display.getStacks(context);
        if (!list.isEmpty()) {
            this.items.put((Object)slot, (Object)new CyclingItem(list, resultSlot));
        }
    }

    protected void addInputs(Slot slot, ContextParameterMap context, SlotDisplay display) {
        this.addItems(slot, context, display, false);
    }

    protected void addResults(Slot slot, ContextParameterMap context, SlotDisplay display) {
        this.addItems(slot, context, display, true);
    }

    public void draw(DrawContext context, MinecraftClient client, boolean resultHasPadding) {
        this.items.forEach((slot, item) -> {
            int i = slot.x;
            int j = slot.y;
            if (item.isResultSlot && resultHasPadding) {
                context.fill(i - 4, j - 4, i + 20, j + 20, 0x30FF0000);
            } else {
                context.fill(i, j, i + 16, j + 16, 0x30FF0000);
            }
            ItemStack itemStack = item.get(this.currentIndexProvider.currentIndex());
            context.drawItemWithoutEntity(itemStack, i, j);
            context.fill(i, j, i + 16, j + 16, 0x30FFFFFF);
            if (item.isResultSlot) {
                context.drawStackOverlay(minecraftClient.textRenderer, itemStack, i, j);
            }
        });
    }

    public void drawTooltip(DrawContext context, MinecraftClient client, int x, int y, @Nullable Slot slot) {
        if (slot == null) {
            return;
        }
        CyclingItem cyclingItem = (CyclingItem)this.items.get((Object)slot);
        if (cyclingItem != null) {
            ItemStack itemStack = cyclingItem.get(this.currentIndexProvider.currentIndex());
            context.drawTooltip(client.textRenderer, Screen.getTooltipFromItem((MinecraftClient)client, (ItemStack)itemStack), x, y, (Identifier)itemStack.get(DataComponentTypes.TOOLTIP_STYLE));
        }
    }
}

