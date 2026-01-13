/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.widget.ItemStackWidget;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class StatsScreen.ItemStatsListWidget.StatEntry
extends StatsScreen.ItemStatsListWidget.Entry {
    private final Item item;
    private final ItemStackInSlotWidget button;

    StatsScreen.ItemStatsListWidget.StatEntry(Item item) {
        this.item = item;
        this.button = new ItemStackInSlotWidget(item.getDefaultStack());
    }

    protected Item getItem() {
        return this.item;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int j;
        this.button.setPosition(this.getContentX(), this.getContentY());
        this.button.render(context, mouseX, mouseY, deltaTicks);
        StatsScreen.ItemStatsListWidget itemStatsListWidget = ItemStatsListWidget.this;
        int i = itemStatsListWidget.children().indexOf(this);
        for (j = 0; j < itemStatsListWidget.blockStatTypes.size(); ++j) {
            Stat<Block> stat;
            Item item = this.item;
            if (item instanceof BlockItem) {
                BlockItem blockItem = (BlockItem)item;
                stat = itemStatsListWidget.blockStatTypes.get(j).getOrCreateStat(blockItem.getBlock());
            } else {
                stat = null;
            }
            this.render(context, stat, this.getContentX() + ItemStatsListWidget.this.getIconX(j), this.getContentMiddleY() - ((StatsScreen)ItemStatsListWidget.this.field_18752).textRenderer.fontHeight / 2, i % 2 == 0);
        }
        for (j = 0; j < itemStatsListWidget.itemStatTypes.size(); ++j) {
            this.render(context, itemStatsListWidget.itemStatTypes.get(j).getOrCreateStat(this.item), this.getContentX() + ItemStatsListWidget.this.getIconX(j + itemStatsListWidget.blockStatTypes.size()), this.getContentMiddleY() - ((StatsScreen)ItemStatsListWidget.this.field_18752).textRenderer.fontHeight / 2, i % 2 == 0);
        }
    }

    protected void render(DrawContext context, @Nullable Stat<?> stat, int x, int y, boolean white) {
        Text text = stat == null ? NONE_TEXT : Text.literal(stat.format(ItemStatsListWidget.this.field_18752.statHandler.getStat(stat)));
        context.drawTextWithShadow(ItemStatsListWidget.this.field_18752.textRenderer, text, x - ItemStatsListWidget.this.field_18752.textRenderer.getWidth(text), y, white ? -1 : -4539718);
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return List.of(this.button);
    }

    @Override
    public List<? extends Element> children() {
        return List.of(this.button);
    }

    @Environment(value=EnvType.CLIENT)
    class ItemStackInSlotWidget
    extends ItemStackWidget {
        ItemStackInSlotWidget(ItemStack stack) {
            super(ItemStatsListWidget.this.client, 1, 1, 18, 18, stack.getName(), stack, false, true);
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, StatEntry.this.getContentX(), StatEntry.this.getContentY(), 18, 18);
            super.renderWidget(context, mouseX, mouseY, deltaTicks);
        }

        @Override
        protected void renderTooltip(DrawContext context, int mouseX, int mouseY) {
            super.renderTooltip(context, StatEntry.this.getContentX() + 18, StatEntry.this.getContentY() + 18);
        }
    }
}
