/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.cursor.StandardCursors
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.MerchantScreen
 *  net.minecraft.client.gui.screen.ingame.MerchantScreen$WidgetButtonPage
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket
 *  net.minecraft.screen.MerchantScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.village.TradeOffer
 *  net.minecraft.village.TradeOfferList
 *  net.minecraft.village.VillagerData
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;

@Environment(value=EnvType.CLIENT)
public class MerchantScreen
extends HandledScreen<MerchantScreenHandler> {
    private static final Identifier OUT_OF_STOCK_TEXTURE = Identifier.ofVanilla((String)"container/villager/out_of_stock");
    private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla((String)"container/villager/experience_bar_background");
    private static final Identifier EXPERIENCE_BAR_CURRENT_TEXTURE = Identifier.ofVanilla((String)"container/villager/experience_bar_current");
    private static final Identifier EXPERIENCE_BAR_RESULT_TEXTURE = Identifier.ofVanilla((String)"container/villager/experience_bar_result");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla((String)"container/villager/scroller");
    private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla((String)"container/villager/scroller_disabled");
    private static final Identifier TRADE_ARROW_OUT_OF_STOCK_TEXTURE = Identifier.ofVanilla((String)"container/villager/trade_arrow_out_of_stock");
    private static final Identifier TRADE_ARROW_TEXTURE = Identifier.ofVanilla((String)"container/villager/trade_arrow");
    private static final Identifier DISCOUNT_STRIKETHROUGH_TEXTURE = Identifier.ofVanilla((String)"container/villager/discount_strikethrough");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/villager.png");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int field_32356 = 99;
    private static final int EXPERIENCE_BAR_X_OFFSET = 136;
    private static final int TRADE_LIST_AREA_Y_OFFSET = 16;
    private static final int FIRST_BUY_ITEM_X_OFFSET = 5;
    private static final int SECOND_BUY_ITEM_X_OFFSET = 35;
    private static final int SOLD_ITEM_X_OFFSET = 68;
    private static final int field_32362 = 6;
    private static final int MAX_TRADE_OFFERS = 7;
    private static final int field_32364 = 5;
    private static final int TRADE_OFFER_BUTTON_HEIGHT = 20;
    private static final int TRADE_OFFER_BUTTON_WIDTH = 88;
    private static final int SCROLLBAR_HEIGHT = 27;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_AREA_HEIGHT = 139;
    private static final int SCROLLBAR_OFFSET_Y = 18;
    private static final int SCROLLBAR_OFFSET_X = 94;
    private static final Text TRADES_TEXT = Text.translatable((String)"merchant.trades");
    private static final Text DEPRECATED_TEXT = Text.translatable((String)"merchant.deprecated");
    private int selectedIndex;
    private final WidgetButtonPage[] offers = new WidgetButtonPage[7];
    int indexStartOffset;
    private boolean scrolling;

    public MerchantScreen(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super((ScreenHandler)handler, inventory, title);
        this.backgroundWidth = 276;
        this.playerInventoryTitleX = 107;
    }

    private void syncRecipeIndex() {
        ((MerchantScreenHandler)this.handler).setRecipeIndex(this.selectedIndex);
        ((MerchantScreenHandler)this.handler).switchTo(this.selectedIndex);
        this.client.getNetworkHandler().sendPacket((Packet)new SelectMerchantTradeC2SPacket(this.selectedIndex));
    }

    protected void init() {
        super.init();
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        int k = j + 16 + 2;
        for (int l = 0; l < 7; ++l) {
            this.offers[l] = (WidgetButtonPage)this.addDrawableChild((Element)new WidgetButtonPage(this, i + 5, k, l, button -> {
                if (button instanceof WidgetButtonPage) {
                    this.selectedIndex = ((WidgetButtonPage)button).getIndex() + this.indexStartOffset;
                    this.syncRecipeIndex();
                }
            }));
            k += 20;
        }
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int i = ((MerchantScreenHandler)this.handler).getLevelProgress();
        if (i > 0 && i <= 5 && ((MerchantScreenHandler)this.handler).isLeveled()) {
            MutableText text = Text.translatable((String)"merchant.title", (Object[])new Object[]{this.title, Text.translatable((String)("merchant.level." + i))});
            int j = this.textRenderer.getWidth((StringVisitable)text);
            int k = 49 + this.backgroundWidth / 2 - j / 2;
            context.drawText(this.textRenderer, (Text)text, k, 6, -12566464, false);
        } else {
            context.drawText(this.textRenderer, this.title, 49 + this.backgroundWidth / 2 - this.textRenderer.getWidth((StringVisitable)this.title) / 2, 6, -12566464, false);
        }
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, -12566464, false);
        int l = this.textRenderer.getWidth((StringVisitable)TRADES_TEXT);
        context.drawText(this.textRenderer, TRADES_TEXT, 5 - l / 2 + 48, 6, -12566464, false);
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 512, 256);
        TradeOfferList tradeOfferList = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!tradeOfferList.isEmpty()) {
            int k = this.selectedIndex;
            if (k < 0 || k >= tradeOfferList.size()) {
                return;
            }
            TradeOffer tradeOffer = (TradeOffer)tradeOfferList.get(k);
            if (tradeOffer.isDisabled()) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, OUT_OF_STOCK_TEXTURE, this.x + 83 + 99, this.y + 35, 28, 21);
            }
        }
    }

    private void drawLevelInfo(DrawContext context, int x, int y, TradeOffer tradeOffer) {
        int i = ((MerchantScreenHandler)this.handler).getLevelProgress();
        int j = ((MerchantScreenHandler)this.handler).getExperience();
        if (i >= 5) {
            return;
        }
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_BACKGROUND_TEXTURE, x + 136, y + 16, 102, 5);
        int k = VillagerData.getLowerLevelExperience((int)i);
        if (j < k || !VillagerData.canLevelUp((int)i)) {
            return;
        }
        int l = 102;
        float f = 102.0f / (float)(VillagerData.getUpperLevelExperience((int)i) - k);
        int m = Math.min(MathHelper.floor((float)(f * (float)(j - k))), 102);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_CURRENT_TEXTURE, 102, 5, 0, 0, x + 136, y + 16, m, 5);
        int n = ((MerchantScreenHandler)this.handler).getMerchantRewardedExperience();
        if (n > 0) {
            int o = Math.min(MathHelper.floor((float)((float)n * f)), 102 - m);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_RESULT_TEXTURE, 102, 5, m, 0, x + 136 + m, y + 16, o, 5);
        }
    }

    private void renderScrollbar(DrawContext context, int x, int y, int mouseX, int mouseY, TradeOfferList tradeOffers) {
        int i = tradeOffers.size() + 1 - 7;
        if (i > 1) {
            int j = 139 - (27 + (i - 1) * 139 / i);
            int k = 1 + j / i + 139 / i;
            int l = 113;
            int m = Math.min(113, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 113;
            }
            int n = x + 94;
            int o = y + 18 + m;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, n, o, 6, 27);
            if (mouseX >= n && mouseX < x + 94 + 6 && mouseY >= o && mouseY <= o + 27) {
                context.setCursor(this.scrolling ? StandardCursors.RESIZE_NS : StandardCursors.POINTING_HAND);
            }
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SCROLLER_DISABLED_TEXTURE, x + 94, y + 18, 6, 27);
        }
    }

    public void renderMain(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderMain(context, mouseX, mouseY, deltaTicks);
        TradeOfferList tradeOfferList = ((MerchantScreenHandler)this.handler).getRecipes();
        if (!tradeOfferList.isEmpty()) {
            TradeOffer tradeOffer2;
            int i = (this.width - this.backgroundWidth) / 2;
            int j = (this.height - this.backgroundHeight) / 2;
            int k = j + 16 + 1;
            int l = i + 5 + 5;
            this.renderScrollbar(context, i, j, mouseX, mouseY, tradeOfferList);
            int m = 0;
            for (TradeOffer tradeOffer2 : tradeOfferList) {
                if (this.canScroll(tradeOfferList.size()) && (m < this.indexStartOffset || m >= 7 + this.indexStartOffset)) {
                    ++m;
                    continue;
                }
                ItemStack itemStack = tradeOffer2.getOriginalFirstBuyItem();
                ItemStack itemStack2 = tradeOffer2.getDisplayedFirstBuyItem();
                ItemStack itemStack3 = tradeOffer2.getDisplayedSecondBuyItem();
                ItemStack itemStack4 = tradeOffer2.getSellItem();
                int n = k + 2;
                this.renderFirstBuyItem(context, itemStack2, itemStack, l, n);
                if (!itemStack3.isEmpty()) {
                    context.drawItemWithoutEntity(itemStack3, i + 5 + 35, n);
                    context.drawStackOverlay(this.textRenderer, itemStack3, i + 5 + 35, n);
                }
                this.renderArrow(context, tradeOffer2, i, n);
                context.drawItemWithoutEntity(itemStack4, i + 5 + 68, n);
                context.drawStackOverlay(this.textRenderer, itemStack4, i + 5 + 68, n);
                k += 20;
                ++m;
            }
            int o = this.selectedIndex;
            tradeOffer2 = (TradeOffer)tradeOfferList.get(o);
            if (((MerchantScreenHandler)this.handler).isLeveled()) {
                this.drawLevelInfo(context, i, j, tradeOffer2);
            }
            if (tradeOffer2.isDisabled() && this.isPointWithinBounds(186, 35, 22, 21, (double)mouseX, (double)mouseY) && ((MerchantScreenHandler)this.handler).canRefreshTrades()) {
                context.drawTooltip(this.textRenderer, DEPRECATED_TEXT, mouseX, mouseY);
            }
            for (WidgetButtonPage widgetButtonPage : this.offers) {
                if (widgetButtonPage.isSelected()) {
                    widgetButtonPage.renderTooltip(context, mouseX, mouseY);
                }
                widgetButtonPage.visible = widgetButtonPage.index < ((MerchantScreenHandler)this.handler).getRecipes().size();
            }
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void renderArrow(DrawContext context, TradeOffer tradeOffer, int x, int y) {
        if (tradeOffer.isDisabled()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_OUT_OF_STOCK_TEXTURE, x + 5 + 35 + 20, y + 3, 10, 9);
        } else {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TRADE_ARROW_TEXTURE, x + 5 + 35 + 20, y + 3, 10, 9);
        }
    }

    private void renderFirstBuyItem(DrawContext context, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, int x, int y) {
        context.drawItemWithoutEntity(adjustedFirstBuyItem, x, y);
        if (originalFirstBuyItem.getCount() == adjustedFirstBuyItem.getCount()) {
            context.drawStackOverlay(this.textRenderer, adjustedFirstBuyItem, x, y);
        } else {
            context.drawStackOverlay(this.textRenderer, originalFirstBuyItem, x, y, originalFirstBuyItem.getCount() == 1 ? "1" : null);
            context.drawStackOverlay(this.textRenderer, adjustedFirstBuyItem, x + 14, y, adjustedFirstBuyItem.getCount() == 1 ? "1" : null);
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DISCOUNT_STRIKETHROUGH_TEXTURE, x + 7, y + 12, 9, 2);
        }
    }

    private boolean canScroll(int listSize) {
        return listSize > 7;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        int i = ((MerchantScreenHandler)this.handler).getRecipes().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.indexStartOffset = MathHelper.clamp((int)((int)((double)this.indexStartOffset - verticalAmount)), (int)0, (int)j);
        }
        return true;
    }

    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        int i = ((MerchantScreenHandler)this.handler).getRecipes().size();
        if (this.scrolling) {
            int j = this.y + 18;
            int k = j + 139;
            int l = i - 7;
            float f = ((float)click.y() - (float)j - 13.5f) / ((float)(k - j) - 27.0f);
            f = f * (float)l + 0.5f;
            this.indexStartOffset = MathHelper.clamp((int)((int)f), (int)0, (int)l);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        if (this.canScroll(((MerchantScreenHandler)this.handler).getRecipes().size()) && click.x() > (double)(i + 94) && click.x() < (double)(i + 94 + 6) && click.y() > (double)(j + 18) && click.y() <= (double)(j + 18 + 139 + 1)) {
            this.scrolling = true;
        }
        return super.mouseClicked(click, doubled);
    }

    public boolean mouseReleased(Click click) {
        this.scrolling = false;
        return super.mouseReleased(click);
    }

    static /* synthetic */ TextRenderer method_20227(MerchantScreen merchantScreen) {
        return merchantScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_20225(MerchantScreen merchantScreen) {
        return merchantScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_20226(MerchantScreen merchantScreen) {
        return merchantScreen.textRenderer;
    }
}

