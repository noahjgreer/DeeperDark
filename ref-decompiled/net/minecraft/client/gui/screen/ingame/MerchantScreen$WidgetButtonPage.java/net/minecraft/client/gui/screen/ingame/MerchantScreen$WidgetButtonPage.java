/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.village.TradeOffer;

@Environment(value=EnvType.CLIENT)
class MerchantScreen.WidgetButtonPage
extends ButtonWidget.Text {
    final int index;

    public MerchantScreen.WidgetButtonPage(int x, int y, int index, ButtonWidget.PressAction onPress) {
        super(x, y, 88, 20, ScreenTexts.EMPTY, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.index = index;
        this.visible = false;
    }

    public int getIndex() {
        return this.index;
    }

    public void renderTooltip(DrawContext context, int x, int y) {
        if (this.hovered && ((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().size() > this.index + MerchantScreen.this.indexStartOffset) {
            if (x < this.getX() + 20) {
                ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getDisplayedFirstBuyItem();
                context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
            } else if (x < this.getX() + 50 && x > this.getX() + 30) {
                ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getDisplayedSecondBuyItem();
                if (!itemStack.isEmpty()) {
                    context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
                }
            } else if (x > this.getX() + 65) {
                ItemStack itemStack = ((TradeOffer)((MerchantScreenHandler)MerchantScreen.this.handler).getRecipes().get(this.index + MerchantScreen.this.indexStartOffset)).getSellItem();
                context.drawItemTooltip(MerchantScreen.this.textRenderer, itemStack, x, y);
            }
        }
    }
}
