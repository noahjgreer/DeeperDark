/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class StatsScreen.ItemStatsListWidget.Header
extends StatsScreen.ItemStatsListWidget.Entry {
    private static final Identifier BLOCK_MINED_TEXTURE = Identifier.ofVanilla("statistics/block_mined");
    private static final Identifier ITEM_BROKEN_TEXTURE = Identifier.ofVanilla("statistics/item_broken");
    private static final Identifier ITEM_CRAFTED_TEXTURE = Identifier.ofVanilla("statistics/item_crafted");
    private static final Identifier ITEM_USED_TEXTURE = Identifier.ofVanilla("statistics/item_used");
    private static final Identifier ITEM_PICKED_UP_TEXTURE = Identifier.ofVanilla("statistics/item_picked_up");
    private static final Identifier ITEM_DROPPED_TEXTURE = Identifier.ofVanilla("statistics/item_dropped");
    private final HeaderButton blockMinedButton;
    private final HeaderButton itemBrokenButton;
    private final HeaderButton itemCraftedButton;
    private final HeaderButton itemUsedButton;
    private final HeaderButton itemPickedUpButton;
    private final HeaderButton itemDroppedButton;
    private final List<ClickableWidget> buttons = new ArrayList<ClickableWidget>();

    StatsScreen.ItemStatsListWidget.Header() {
        this.blockMinedButton = new HeaderButton(this, 0, BLOCK_MINED_TEXTURE);
        this.itemBrokenButton = new HeaderButton(this, 1, ITEM_BROKEN_TEXTURE);
        this.itemCraftedButton = new HeaderButton(this, 2, ITEM_CRAFTED_TEXTURE);
        this.itemUsedButton = new HeaderButton(this, 3, ITEM_USED_TEXTURE);
        this.itemPickedUpButton = new HeaderButton(this, 4, ITEM_PICKED_UP_TEXTURE);
        this.itemDroppedButton = new HeaderButton(this, 5, ITEM_DROPPED_TEXTURE);
        this.buttons.addAll(List.of(this.blockMinedButton, this.itemBrokenButton, this.itemCraftedButton, this.itemUsedButton, this.itemPickedUpButton, this.itemDroppedButton));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.blockMinedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(0) - 18, this.getContentY() + 1);
        this.blockMinedButton.render(context, mouseX, mouseY, deltaTicks);
        this.itemBrokenButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(1) - 18, this.getContentY() + 1);
        this.itemBrokenButton.render(context, mouseX, mouseY, deltaTicks);
        this.itemCraftedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(2) - 18, this.getContentY() + 1);
        this.itemCraftedButton.render(context, mouseX, mouseY, deltaTicks);
        this.itemUsedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(3) - 18, this.getContentY() + 1);
        this.itemUsedButton.render(context, mouseX, mouseY, deltaTicks);
        this.itemPickedUpButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(4) - 18, this.getContentY() + 1);
        this.itemPickedUpButton.render(context, mouseX, mouseY, deltaTicks);
        this.itemDroppedButton.setPosition(this.getContentX() + ItemStatsListWidget.this.getIconX(5) - 18, this.getContentY() + 1);
        this.itemDroppedButton.render(context, mouseX, mouseY, deltaTicks);
        if (ItemStatsListWidget.this.selectedStatType != null) {
            int i = ItemStatsListWidget.this.getIconX(ItemStatsListWidget.this.getHeaderIndex(ItemStatsListWidget.this.selectedStatType)) - 36;
            Identifier identifier = ItemStatsListWidget.this.listOrder == 1 ? SORT_UP_TEXTURE : SORT_DOWN_TEXTURE;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getContentX() + i, this.getContentY() + 1, 18, 18);
        }
    }

    @Override
    public List<? extends Element> children() {
        return this.buttons;
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return this.buttons;
    }

    @Environment(value=EnvType.CLIENT)
    class HeaderButton
    extends TexturedButtonWidget {
        private final Identifier texture;

        HeaderButton(StatsScreen.ItemStatsListWidget.Header header, int index, Identifier texture) {
            super(18, 18, new ButtonTextures(HEADER_TEXTURE, SLOT_TEXTURE), button -> header.ItemStatsListWidget.this.selectStatType(header.ItemStatsListWidget.this.getStatType(index)), header.ItemStatsListWidget.this.getStatType(index).getName());
            this.texture = texture;
            this.setTooltip(Tooltip.of(this.getMessage()));
        }

        @Override
        public void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            Identifier identifier = this.textures.get(this.isInteractable(), this.isSelected());
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.getX(), this.getY(), this.width, this.height);
        }
    }
}
