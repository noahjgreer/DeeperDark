/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.StatsScreen
 *  net.minecraft.client.gui.screen.StatsScreen$EntityStatsListWidget
 *  net.minecraft.client.gui.screen.StatsScreen$GeneralStatsListWidget
 *  net.minecraft.client.gui.screen.StatsScreen$ItemStatsListWidget
 *  net.minecraft.client.gui.screen.StatsScreen$StatsTab
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.tab.LoadingTab
 *  net.minecraft.client.gui.tab.Tab
 *  net.minecraft.client.gui.tab.TabManager
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.EntryListWidget
 *  net.minecraft.client.gui.widget.TabNavigationWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket$Mode
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.stat.Stat
 *  net.minecraft.stat.StatHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.StatsScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.LoadingTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class StatsScreen
extends Screen {
    private static final Text TITLE_TEXT = Text.translatable((String)"gui.stats");
    static final Identifier SLOT_TEXTURE = Identifier.ofVanilla((String)"container/slot");
    static final Identifier HEADER_TEXTURE = Identifier.ofVanilla((String)"statistics/header");
    static final Identifier SORT_UP_TEXTURE = Identifier.ofVanilla((String)"statistics/sort_up");
    static final Identifier SORT_DOWN_TEXTURE = Identifier.ofVanilla((String)"statistics/sort_down");
    private static final Text DOWNLOADING_STATS_TEXT = Text.translatable((String)"multiplayer.downloadingStats");
    static final Text NONE_TEXT = Text.translatable((String)"stats.none");
    private static final Text GENERAL_BUTTON_TEXT = Text.translatable((String)"stat.generalButton");
    private static final Text ITEM_BUTTON_TEXT = Text.translatable((String)"stat.itemsButton");
    private static final Text MOBS_BUTTON_TEXT = Text.translatable((String)"stat.mobsButton");
    protected final Screen parent;
    private static final int field_49520 = 280;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private final TabManager tabManager = new TabManager(child -> {
        ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
    }, child -> this.remove(child));
    private @Nullable TabNavigationWidget tabNavigationWidget;
    final StatHandler statHandler;
    private boolean downloadingStats = true;

    public StatsScreen(Screen parent, StatHandler statHandler) {
        super(TITLE_TEXT);
        this.parent = parent;
        this.statHandler = statHandler;
    }

    protected void init() {
        Text text = DOWNLOADING_STATS_TEXT;
        this.tabNavigationWidget = TabNavigationWidget.builder((TabManager)this.tabManager, (int)this.width).tabs(new Tab[]{new LoadingTab(this.getTextRenderer(), GENERAL_BUTTON_TEXT, text), new LoadingTab(this.getTextRenderer(), ITEM_BUTTON_TEXT, text), new LoadingTab(this.getTextRenderer(), MOBS_BUTTON_TEXT, text)}).build();
        this.addDrawableChild((Element)this.tabNavigationWidget);
        this.layout.addFooter((Widget)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.close()).width(200).build());
        this.tabNavigationWidget.setTabActive(0, true);
        this.tabNavigationWidget.setTabActive(1, false);
        this.tabNavigationWidget.setTabActive(2, false);
        this.layout.forEachChild(child -> {
            child.setNavigationOrder(1);
            this.addDrawableChild((Element)child);
        });
        this.tabNavigationWidget.selectTab(0, false);
        this.refreshWidgetPositions();
        this.client.getNetworkHandler().sendPacket((Packet)new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
    }

    public void onStatsReady() {
        if (this.downloadingStats) {
            if (this.tabNavigationWidget != null) {
                this.remove((Element)this.tabNavigationWidget);
            }
            this.tabNavigationWidget = TabNavigationWidget.builder((TabManager)this.tabManager, (int)this.width).tabs(new Tab[]{new StatsTab(this, GENERAL_BUTTON_TEXT, (EntryListWidget)new GeneralStatsListWidget(this, this.client)), new StatsTab(this, ITEM_BUTTON_TEXT, (EntryListWidget)new ItemStatsListWidget(this, this.client)), new StatsTab(this, MOBS_BUTTON_TEXT, (EntryListWidget)new EntityStatsListWidget(this, this.client))}).build();
            this.setFocused((Element)this.tabNavigationWidget);
            this.addDrawableChild((Element)this.tabNavigationWidget);
            this.refreshTab(1);
            this.refreshTab(2);
            this.tabNavigationWidget.selectTab(0, false);
            this.refreshWidgetPositions();
            this.downloadingStats = false;
        }
    }

    /*
     * Unable to fully structure code
     */
    private void refreshTab(int tab) {
        if (this.tabNavigationWidget == null) {
            return;
        }
        var4_2 = this.tabNavigationWidget.getTabs().get(tab);
        if (!(var4_2 instanceof StatsTab)) ** GOTO lbl-1000
        statsTab = (StatsTab)var4_2;
        if (!statsTab.widget.children().isEmpty()) {
            v0 = true;
        } else lbl-1000:
        // 2 sources

        {
            v0 = false;
        }
        bl = v0;
        this.tabNavigationWidget.setTabActive(tab, bl);
        if (bl) {
            this.tabNavigationWidget.setTabTooltip(tab, null);
        } else {
            this.tabNavigationWidget.setTabTooltip(tab, Tooltip.of((Text)Text.translatable((String)"gui.stats.none_found")));
        }
    }

    protected void refreshWidgetPositions() {
        if (this.tabNavigationWidget == null) {
            return;
        }
        this.tabNavigationWidget.setWidth(this.width);
        this.tabNavigationWidget.init();
        int i = this.tabNavigationWidget.getNavigationFocus().getBottom();
        ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - this.layout.getFooterHeight() - i);
        this.tabNavigationWidget.getTabs().forEach(tab -> tab.forEachChild(child -> child.setHeight(screenRect.height())));
        this.tabManager.setTabArea(screenRect);
        this.layout.setHeaderHeight(i);
        this.layout.refreshPositions();
    }

    public boolean keyPressed(KeyInput input) {
        if (this.tabNavigationWidget != null && this.tabNavigationWidget.keyPressed(input)) {
            return true;
        }
        return super.keyPressed(input);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR_TEXTURE, 0, this.height - this.layout.getFooterHeight(), 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    protected void renderDarkening(DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, CreateWorldScreen.TAB_HEADER_BACKGROUND_TEXTURE, 0, 0, 0.0f, 0.0f, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderDarkening(context, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    static String getStatTranslationKey(Stat<Identifier> stat) {
        return "stat." + ((Identifier)stat.getValue()).toString().replace(':', '.');
    }

    static /* synthetic */ TextRenderer method_36880(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_36881(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19391(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_36882(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19392(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19393(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19396(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19398(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19401(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19394(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19395(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_19402(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_57740(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_57741(StatsScreen statsScreen) {
        return statsScreen.textRenderer;
    }
}

