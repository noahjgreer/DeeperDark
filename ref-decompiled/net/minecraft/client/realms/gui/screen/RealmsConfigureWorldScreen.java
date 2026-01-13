/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.world.CreateWorldScreen
 *  net.minecraft.client.gui.tab.LoadingTab
 *  net.minecraft.client.gui.tab.Tab
 *  net.minecraft.client.gui.tab.TabManager
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.TabNavigationWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.RealmsError
 *  net.minecraft.client.realms.RealmsError$SimpleHttpError
 *  net.minecraft.client.realms.ServiceQuality
 *  net.minecraft.client.realms.dto.RealmsRegion
 *  net.minecraft.client.realms.dto.RealmsRegionDataList
 *  net.minecraft.client.realms.dto.RealmsRegionSelectionPreference
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$State
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.dto.RegionData
 *  net.minecraft.client.realms.dto.RegionSelectionMethod
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsSubscriptionInfoTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab
 *  net.minecraft.client.realms.gui.screen.tab.RealmsWorldsTab
 *  net.minecraft.client.realms.task.CloseServerTask
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.OpenServerTask
 *  net.minecraft.client.realms.util.RealmsUtil
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.StringHelper
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.mojang.logging.LogUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.LoadingTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.ServiceQuality;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RealmsRegionDataList;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.RegionData;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.gui.screen.tab.RealmsPlayerTab;
import net.minecraft.client.realms.gui.screen.tab.RealmsSettingsTab;
import net.minecraft.client.realms.gui.screen.tab.RealmsSubscriptionInfoTab;
import net.minecraft.client.realms.gui.screen.tab.RealmsUpdatableTab;
import net.minecraft.client.realms.gui.screen.tab.RealmsWorldsTab;
import net.minecraft.client.realms.task.CloseServerTask;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.OpenServerTask;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsConfigureWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Text PLAY_BUTTON_TEXT = Text.translatable((String)"mco.selectServer.play");
    private final RealmsMainScreen parent;
    private @Nullable RealmsServer server;
    private @Nullable RealmsRegionDataList regionDataList;
    private final Map<RealmsRegion, ServiceQuality> regions = new LinkedHashMap();
    private final long serverId;
    private boolean stateChanged;
    private final TabManager tabManager = new TabManager(loadedWidget -> {
        ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(loadedWidget);
    }, unloadedWidget -> this.remove(unloadedWidget), arg_0 -> this.onTabLoaded(arg_0), arg_0 -> this.onTabUnloaded(arg_0));
    private @Nullable ButtonWidget playButton;
    private @Nullable TabNavigationWidget tabNavigation;
    final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);

    public RealmsConfigureWorldScreen(RealmsMainScreen parent, long serverId, @Nullable RealmsServer server, @Nullable RealmsRegionDataList regionDataList) {
        super((Text)Text.empty());
        this.parent = parent;
        this.serverId = serverId;
        this.server = server;
        this.regionDataList = regionDataList;
    }

    public RealmsConfigureWorldScreen(RealmsMainScreen parent, long serverId) {
        this(parent, serverId, null, null);
    }

    public void init() {
        if (this.server == null) {
            this.fetchServerData(this.serverId);
        }
        if (this.regionDataList == null) {
            this.fetchRegionDataList();
        }
        MutableText text = Text.translatable((String)"mco.configure.world.loading");
        this.tabNavigation = TabNavigationWidget.builder((TabManager)this.tabManager, (int)this.width).tabs(new Tab[]{new LoadingTab(this.getTextRenderer(), RealmsWorldsTab.TITLE_TEXT, (Text)text), new LoadingTab(this.getTextRenderer(), RealmsPlayerTab.TITLE, (Text)text), new LoadingTab(this.getTextRenderer(), RealmsSubscriptionInfoTab.SUBSCRIPTION_TITLE, (Text)text), new LoadingTab(this.getTextRenderer(), RealmsSettingsTab.TITLE_TEXT, (Text)text)}).build();
        this.tabNavigation.setTabActive(3, false);
        this.addDrawableChild((Element)this.tabNavigation);
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        this.playButton = (ButtonWidget)directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)PLAY_BUTTON_TEXT, button -> {
            this.close();
            RealmsMainScreen.play((RealmsServer)this.server, (Screen)this);
        }).width(150).build());
        this.playButton.active = false;
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).build());
        this.layout.forEachChild(child -> {
            child.setNavigationOrder(1);
            this.addDrawableChild((Element)child);
        });
        this.tabNavigation.selectTab(0, false);
        this.refreshWidgetPositions();
        if (this.server != null && this.regionDataList != null) {
            this.refresh();
        }
    }

    private void onTabLoaded(Tab tab) {
        if (this.server != null && tab instanceof RealmsUpdatableTab) {
            RealmsUpdatableTab realmsUpdatableTab = (RealmsUpdatableTab)tab;
            realmsUpdatableTab.onLoaded(this.server);
        }
    }

    private void onTabUnloaded(Tab tab) {
        if (this.server != null && tab instanceof RealmsUpdatableTab) {
            RealmsUpdatableTab realmsUpdatableTab = (RealmsUpdatableTab)tab;
            realmsUpdatableTab.onUnloaded(this.server);
        }
    }

    public int getContentHeight() {
        return this.layout.getContentHeight();
    }

    public int getHeaderHeight() {
        return this.layout.getHeaderHeight();
    }

    public Screen getParent() {
        return this.parent;
    }

    public Screen createErrorScreen(RealmsServiceException error) {
        return new RealmsGenericErrorScreen(error, (Screen)this.parent);
    }

    public void refreshWidgetPositions() {
        if (this.tabNavigation == null) {
            return;
        }
        this.tabNavigation.setWidth(this.width);
        this.tabNavigation.init();
        int i = this.tabNavigation.getNavigationFocus().getBottom();
        ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - this.layout.getFooterHeight() - i);
        this.tabManager.setTabArea(screenRect);
        this.layout.setHeaderHeight(i);
        this.layout.refreshPositions();
    }

    private void updatePlayButton() {
        if (this.server != null && this.playButton != null) {
            this.playButton.active = this.server.shouldAllowPlay();
            if (!this.playButton.active && this.server.state == RealmsServer.State.CLOSED) {
                this.playButton.setTooltip(Tooltip.of((Text)RealmsServer.REALM_CLOSED_TEXT));
            }
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR_TEXTURE, 0, this.height - this.layout.getFooterHeight() - 2, 0.0f, 0.0f, this.width, 2, 32, 2);
    }

    public boolean keyPressed(KeyInput input) {
        if (this.tabNavigation.keyPressed(input)) {
            return true;
        }
        return super.keyPressed(input);
    }

    protected void renderDarkening(DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, CreateWorldScreen.TAB_HEADER_BACKGROUND_TEXTURE, 0, 0, 0.0f, 0.0f, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderDarkening(context, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    public void close() {
        Tab tab;
        if (this.server != null && (tab = this.tabManager.getCurrentTab()) instanceof RealmsUpdatableTab) {
            RealmsUpdatableTab realmsUpdatableTab = (RealmsUpdatableTab)tab;
            realmsUpdatableTab.onUnloaded(this.server);
        }
        this.client.setScreen((Screen)this.parent);
        if (this.stateChanged) {
            this.parent.removeSelection();
        }
    }

    public void fetchRegionDataList() {
        RealmsUtil.runAsync(RealmsClient::getRegionDataList, (Consumer)RealmsUtil.openingScreenAndLogging(arg_0 -> this.createErrorScreen(arg_0), (String)"Couldn't get realms region data")).thenAcceptAsync(regionDataList -> {
            this.regionDataList = regionDataList;
            this.refresh();
        }, (Executor)this.client);
    }

    public void fetchServerData(long worldId) {
        RealmsUtil.runAsync(client -> client.getOwnWorld(worldId), (Consumer)RealmsUtil.openingScreenAndLogging(arg_0 -> this.createErrorScreen(arg_0), (String)"Couldn't get own world")).thenAcceptAsync(server -> {
            this.server = server;
            this.refresh();
        }, (Executor)this.client);
    }

    private void refresh() {
        if (this.server == null || this.regionDataList == null) {
            return;
        }
        this.regions.clear();
        for (RegionData regionData : this.regionDataList.regionData()) {
            if (regionData.region() == RealmsRegion.INVALID_REGION) continue;
            this.regions.put(regionData.region(), regionData.serviceQuality());
        }
        int i = -1;
        if (this.tabNavigation != null) {
            i = this.tabNavigation.getTabs().indexOf(this.tabManager.getCurrentTab());
        }
        if (this.tabNavigation != null) {
            this.remove((Element)this.tabNavigation);
        }
        this.tabNavigation = (TabNavigationWidget)this.addDrawableChild((Element)TabNavigationWidget.builder((TabManager)this.tabManager, (int)this.width).tabs(new Tab[]{new RealmsWorldsTab(this, Objects.requireNonNull(this.client), this.server), new RealmsPlayerTab(this, this.client, this.server), new RealmsSubscriptionInfoTab(this, this.client, this.server), new RealmsSettingsTab(this, this.client, this.server, this.regions)}).build());
        this.setFocused((Element)this.tabNavigation);
        if (i != -1) {
            this.tabNavigation.selectTab(i, false);
        }
        this.tabNavigation.setTabActive(3, !this.server.expired);
        if (this.server.expired) {
            this.tabNavigation.setTabTooltip(3, Tooltip.of((Text)Text.translatable((String)"mco.configure.world.settings.expired")));
        } else {
            this.tabNavigation.setTabTooltip(3, null);
        }
        this.updatePlayButton();
        this.refreshWidgetPositions();
    }

    public void saveSlotSettings(RealmsSlot slot) {
        RealmsSlot realmsSlot = (RealmsSlot)this.server.slots.get(this.server.activeSlot);
        slot.options.templateId = realmsSlot.options.templateId;
        slot.options.templateImage = realmsSlot.options.templateImage;
        RealmsClient realmsClient = RealmsClient.create();
        try {
            if (this.server.activeSlot != slot.slotId) {
                throw new RealmsServiceException((RealmsError)RealmsError.SimpleHttpError.configurationError());
            }
            realmsClient.updateSlot(this.server.id, slot.slotId, slot.options, slot.settings);
            this.server.slots.put(this.server.activeSlot, slot);
            if (slot.options.gameMode != realmsSlot.options.gameMode || slot.isHardcore() != realmsSlot.isHardcore()) {
                RealmsMainScreen.resetServerList();
            }
            this.stateChanged();
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't save slot settings", (Throwable)realmsServiceException);
            this.client.setScreen((Screen)new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
            return;
        }
        this.client.setScreen((Screen)this);
    }

    public void saveSettings(String name, String description, RegionSelectionMethod regionSelectionMethod, @Nullable RealmsRegion region) {
        String string = StringHelper.isBlank((String)description) ? "" : description;
        String string2 = StringHelper.isBlank((String)name) ? "" : name;
        RealmsClient realmsClient = RealmsClient.create();
        try {
            RealmsSlot realmsSlot = (RealmsSlot)this.server.slots.get(this.server.activeSlot);
            RealmsRegion realmsRegion = regionSelectionMethod == RegionSelectionMethod.MANUAL ? region : null;
            RealmsRegionSelectionPreference realmsRegionSelectionPreference = new RealmsRegionSelectionPreference(regionSelectionMethod, realmsRegion);
            realmsClient.configure(this.server.id, string2, string, realmsRegionSelectionPreference, realmsSlot.slotId, realmsSlot.options, realmsSlot.settings);
            this.server.regionSelectionPreference = realmsRegionSelectionPreference;
            this.server.name = name;
            this.server.description = string;
            this.stateChanged();
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't save settings", (Throwable)realmsServiceException);
            this.client.setScreen((Screen)new RealmsGenericErrorScreen(realmsServiceException, (Screen)this));
            return;
        }
        this.client.setScreen((Screen)this);
    }

    public void openTheWorld(boolean join) {
        RealmsConfigureWorldScreen realmsConfigureWorldScreen = this.withServer(this.server);
        this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen((Screen)this.getNewScreen(), new LongRunningTask[]{new OpenServerTask(this.server, (Screen)realmsConfigureWorldScreen, join, this.client)}));
    }

    public void closeTheWorld() {
        RealmsConfigureWorldScreen realmsConfigureWorldScreen = this.withServer(this.server);
        this.client.setScreen((Screen)new RealmsLongRunningMcoTaskScreen((Screen)this.getNewScreen(), new LongRunningTask[]{new CloseServerTask(this.server, realmsConfigureWorldScreen)}));
    }

    public void stateChanged() {
        this.stateChanged = true;
        if (this.tabNavigation != null) {
            for (Tab tab : this.tabNavigation.getTabs()) {
                if (!(tab instanceof RealmsUpdatableTab)) continue;
                RealmsUpdatableTab realmsUpdatableTab = (RealmsUpdatableTab)tab;
                realmsUpdatableTab.update(this.server);
            }
        }
    }

    public boolean invite(long worldId, String profileName) {
        RealmsClient realmsClient = RealmsClient.create();
        try {
            List list = realmsClient.invite(worldId, profileName);
            if (this.server != null) {
                this.server.players = list;
            } else {
                this.server = realmsClient.getOwnWorld(worldId);
            }
            this.stateChanged();
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Couldn't invite user", (Throwable)realmsServiceException);
            return false;
        }
        return true;
    }

    public RealmsConfigureWorldScreen getNewScreen() {
        RealmsConfigureWorldScreen realmsConfigureWorldScreen = new RealmsConfigureWorldScreen(this.parent, this.serverId);
        realmsConfigureWorldScreen.stateChanged = this.stateChanged;
        return realmsConfigureWorldScreen;
    }

    public RealmsConfigureWorldScreen withServer(RealmsServer server) {
        RealmsConfigureWorldScreen realmsConfigureWorldScreen = new RealmsConfigureWorldScreen(this.parent, this.serverId, server, this.regionDataList);
        realmsConfigureWorldScreen.stateChanged = this.stateChanged;
        return realmsConfigureWorldScreen;
    }
}

