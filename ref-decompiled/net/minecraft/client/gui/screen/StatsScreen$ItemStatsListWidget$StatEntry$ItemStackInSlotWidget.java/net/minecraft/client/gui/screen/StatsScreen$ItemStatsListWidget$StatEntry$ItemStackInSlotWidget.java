/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ItemStackWidget;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
class StatsScreen.ItemStatsListWidget.StatEntry.ItemStackInSlotWidget
extends ItemStackWidget {
    StatsScreen.ItemStatsListWidget.StatEntry.ItemStackInSlotWidget(ItemStack stack) {
        super(StatEntry.this.field_18751.client, 1, 1, 18, 18, stack.getName(), stack, false, true);
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
