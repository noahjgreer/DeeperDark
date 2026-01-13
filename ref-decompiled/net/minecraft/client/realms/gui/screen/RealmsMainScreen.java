/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ConfirmLinkScreen
 *  net.minecraft.client.gui.screen.PopupScreen
 *  net.minecraft.client.gui.screen.PopupScreen$Builder
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EmptyWidget
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.GridWidget$Adder
 *  net.minecraft.client.gui.widget.IconWidget
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.LoadingWidget
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget
 *  net.minecraft.client.gui.widget.NarratedMultilineTextWidget$BackgroundRendering
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.realms.Ping
 *  net.minecraft.client.realms.RealmsAvailability
 *  net.minecraft.client.realms.RealmsAvailability$Info
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.RealmsClient$Environment
 *  net.minecraft.client.realms.RealmsPeriodicCheckers
 *  net.minecraft.client.realms.dto.PingResult
 *  net.minecraft.client.realms.dto.RealmsNotification
 *  net.minecraft.client.realms.dto.RealmsNotification$InfoPopup
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$State
 *  net.minecraft.client.realms.dto.RealmsServerPlayerList
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.gui.RealmsPopups
 *  net.minecraft.client.realms.gui.screen.BuyRealmsScreen
 *  net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen
 *  net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen$2
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen$LoadStatus
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen$NotificationButtonWidget
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen$RealmSelectionList
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen$RealmSelectionListEntry
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen$Request
 *  net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen
 *  net.minecraft.client.realms.gui.screen.RealmsScreen
 *  net.minecraft.client.realms.task.LongRunningTask
 *  net.minecraft.client.realms.task.RealmsPrepareConnectionTask
 *  net.minecraft.client.realms.util.PeriodicRunnerFactory$PeriodicRunner
 *  net.minecraft.client.realms.util.PeriodicRunnerFactory$RunnersManager
 *  net.minecraft.client.realms.util.RealmsPersistence
 *  net.minecraft.client.realms.util.RealmsPersistence$RealmsPersistenceData
 *  net.minecraft.client.realms.util.RealmsServerFilterer
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Urls
 *  net.minecraft.util.Util
 *  net.minecraft.world.GameMode
 *  org.apache.commons.lang3.StringUtils
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.realms.Ping;
import net.minecraft.client.realms.RealmsAvailability;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.RealmsPopups;
import net.minecraft.client.realms.gui.screen.BuyRealmsScreen;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsPendingInvitesScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsMainScreen
extends RealmsScreen {
    static final Identifier INFO_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/info");
    static final Identifier NEW_REALM_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/new_realm");
    static final Identifier EXPIRED_STATUS_TEXTURE = Identifier.ofVanilla((String)"realm_status/expired");
    static final Identifier EXPIRES_SOON_STATUS_TEXTURE = Identifier.ofVanilla((String)"realm_status/expires_soon");
    static final Identifier OPEN_STATUS_TEXTURE = Identifier.ofVanilla((String)"realm_status/open");
    static final Identifier CLOSED_STATUS_TEXTURE = Identifier.ofVanilla((String)"realm_status/closed");
    private static final Identifier INVITE_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/invite");
    private static final Identifier NEWS_ICON_TEXTURE = Identifier.ofVanilla((String)"icon/news");
    public static final Identifier HARDCORE_ICON_TEXTURE = Identifier.ofVanilla((String)"hud/heart/hardcore_full");
    static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier NO_REALMS_TEXTURE = Identifier.ofVanilla((String)"textures/gui/realms/no_realms.png");
    private static final Text MENU_TEXT = Text.translatable((String)"menu.online");
    private static final Text LOADING_TEXT = Text.translatable((String)"mco.selectServer.loading");
    static final Text UNINITIALIZED_TEXT = Text.translatable((String)"mco.selectServer.uninitialized");
    static final Text EXPIRED_LIST_TEXT = Text.translatable((String)"mco.selectServer.expiredList");
    private static final Text EXPIRED_RENEW_TEXT = Text.translatable((String)"mco.selectServer.expiredRenew");
    static final Text EXPIRED_TRIAL_TEXT = Text.translatable((String)"mco.selectServer.expiredTrial");
    private static final Text PLAY_TEXT = Text.translatable((String)"mco.selectServer.play");
    private static final Text LEAVE_TEXT = Text.translatable((String)"mco.selectServer.leave");
    private static final Text CONFIGURE_TEXT = Text.translatable((String)"mco.selectServer.configure");
    static final Text EXPIRED_TEXT = Text.translatable((String)"mco.selectServer.expired");
    static final Text EXPIRES_SOON_TEXT = Text.translatable((String)"mco.selectServer.expires.soon");
    static final Text EXPIRES_IN_A_DAY_TEXT = Text.translatable((String)"mco.selectServer.expires.day");
    static final Text OPEN_TEXT = Text.translatable((String)"mco.selectServer.open");
    static final Text CLOSED_TEXT = Text.translatable((String)"mco.selectServer.closed");
    static final Text UNINITIALIZED_BUTTON_NARRATION = Text.translatable((String)"gui.narrate.button", (Object[])new Object[]{UNINITIALIZED_TEXT});
    private static final Text NO_REALMS_TEXT = Text.translatable((String)"mco.selectServer.noRealms");
    private static final Text NO_PENDING_TOOLTIP = Text.translatable((String)"mco.invites.nopending");
    private static final Text PENDING_TOOLTIP = Text.translatable((String)"mco.invites.pending");
    private static final Text INCOMPATIBLE_POPUP_TITLE = Text.translatable((String)"mco.compatibility.incompatible.popup.title");
    private static final Text INCOMPATIBLE_RELEASE_TYPE_MESSAGE = Text.translatable((String)"mco.compatibility.incompatible.releaseType.popup.message");
    private static final int field_42862 = 100;
    private static final int field_45209 = 3;
    private static final int field_45210 = 4;
    private static final int field_45211 = 308;
    private static final int field_44513 = 5;
    private static final int field_44514 = 44;
    private static final int field_45212 = 11;
    private static final int field_46670 = 40;
    private static final int field_46671 = 20;
    private static final boolean GAME_ON_SNAPSHOT;
    private static boolean showingSnapshotRealms;
    private final CompletableFuture<RealmsAvailability.Info> availabilityInfo = RealmsAvailability.check();
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
    private final Set<UUID> seenNotifications = new HashSet();
    private static boolean regionsPinged;
    private final RateLimiter rateLimiter;
    private final Screen parent;
    private ButtonWidget playButton;
    private ButtonWidget backButton;
    private ButtonWidget renewButton;
    private ButtonWidget configureButton;
    private ButtonWidget leaveButton;
    RealmSelectionList realmSelectionList;
    RealmsServerFilterer serverFilterer;
    List<RealmsServer> availableSnapshotServers = List.of();
    RealmsServerPlayerList onlinePlayers = new RealmsServerPlayerList(Map.of());
    private volatile boolean trialAvailable;
    private volatile @Nullable String newsLink;
    final List<RealmsNotification> notifications = new ArrayList();
    private ButtonWidget purchaseButton;
    private NotificationButtonWidget inviteButton;
    private NotificationButtonWidget newsButton;
    private LoadStatus loadStatus;
    private @Nullable ThreePartsLayoutWidget layout;

    public RealmsMainScreen(Screen parent) {
        super(MENU_TEXT);
        this.parent = parent;
        this.rateLimiter = RateLimiter.create((double)0.01666666753590107);
    }

    public void init() {
        this.serverFilterer = new RealmsServerFilterer(this.client);
        this.realmSelectionList = new RealmSelectionList(this);
        MutableText text = Text.translatable((String)"mco.invites.title");
        this.inviteButton = new NotificationButtonWidget((Text)text, INVITE_ICON_TEXTURE, arg_0 -> this.method_52640((Text)text, arg_0), null);
        MutableText text2 = Text.translatable((String)"mco.news");
        this.newsButton = new NotificationButtonWidget((Text)text2, NEWS_ICON_TEXTURE, button -> {
            String string = this.newsLink;
            if (string == null) {
                return;
            }
            ConfirmLinkScreen.open((Screen)this, (String)string);
            if (this.newsButton.getNotificationCount() != 0) {
                RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
                realmsPersistenceData.hasUnreadNews = false;
                RealmsPersistence.writeFile((RealmsPersistence.RealmsPersistenceData)realmsPersistenceData);
                this.newsButton.setNotificationCount(0);
            }
        }, (Text)text2);
        this.playButton = ButtonWidget.builder((Text)PLAY_TEXT, button -> RealmsMainScreen.play((RealmsServer)this.getSelectedServer(), (Screen)this)).width(100).build();
        this.configureButton = ButtonWidget.builder((Text)CONFIGURE_TEXT, button -> this.configureClicked(this.getSelectedServer())).width(100).build();
        this.renewButton = ButtonWidget.builder((Text)EXPIRED_RENEW_TEXT, button -> this.onRenew(this.getSelectedServer())).width(100).build();
        this.leaveButton = ButtonWidget.builder((Text)LEAVE_TEXT, button -> this.leaveClicked(this.getSelectedServer())).width(100).build();
        this.purchaseButton = ButtonWidget.builder((Text)Text.translatable((String)"mco.selectServer.purchase"), button -> this.showBuyRealmsScreen()).size(100, 20).build();
        this.backButton = ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).width(100).build();
        if (RealmsClient.ENVIRONMENT == RealmsClient.Environment.STAGE) {
            this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((Text)Text.literal((String)"Snapshot"), (Text)Text.literal((String)"Release"), (boolean)showingSnapshotRealms).build(5, 5, 100, 20, (Text)Text.literal((String)"Realm"), (button, snapshot) -> {
                showingSnapshotRealms = snapshot;
                this.availableSnapshotServers = List.of();
                this.resetPeriodicCheckers();
            }));
        }
        this.onLoadStatusChange(LoadStatus.LOADING);
        this.refreshButtons();
        this.availabilityInfo.thenAcceptAsync(availabilityInfo -> {
            Screen screen = availabilityInfo.createScreen(this.parent);
            if (screen == null) {
                this.periodicRunnersManager = this.createPeriodicRunnersManager(this.client.getRealmsPeriodicCheckers());
            } else {
                this.client.setScreen(screen);
            }
        }, this.executor);
    }

    public static boolean isSnapshotRealmsEligible() {
        return GAME_ON_SNAPSHOT && showingSnapshotRealms;
    }

    protected void refreshWidgetPositions() {
        if (this.layout != null) {
            this.realmSelectionList.position(this.width, this.layout);
            this.layout.refreshPositions();
        }
    }

    public void close() {
        this.client.setScreen(this.parent);
    }

    private void updateLoadStatus() {
        if (this.serverFilterer.isEmpty() && this.availableSnapshotServers.isEmpty() && this.notifications.isEmpty()) {
            this.onLoadStatusChange(LoadStatus.NO_REALMS);
        } else {
            this.onLoadStatusChange(LoadStatus.LIST);
        }
    }

    private void onLoadStatusChange(LoadStatus loadStatus) {
        if (this.loadStatus == loadStatus) {
            return;
        }
        if (this.layout != null) {
            this.layout.forEachChild(child -> this.remove(child));
        }
        this.layout = this.makeLayoutFor(loadStatus);
        this.loadStatus = loadStatus;
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    private ThreePartsLayoutWidget makeLayoutFor(LoadStatus loadStatus) {
        ThreePartsLayoutWidget threePartsLayoutWidget = new ThreePartsLayoutWidget((Screen)this);
        threePartsLayoutWidget.setHeaderHeight(44);
        threePartsLayoutWidget.addHeader((Widget)this.makeHeader());
        LayoutWidget layoutWidget = this.makeInnerLayout(loadStatus);
        layoutWidget.refreshPositions();
        threePartsLayoutWidget.setFooterHeight(layoutWidget.getHeight() + 22);
        threePartsLayoutWidget.addFooter((Widget)layoutWidget);
        switch (loadStatus.ordinal()) {
            case 0: {
                threePartsLayoutWidget.addBody((Widget)new LoadingWidget(this.textRenderer, LOADING_TEXT));
                break;
            }
            case 1: {
                threePartsLayoutWidget.addBody((Widget)this.makeNoRealmsLayout());
                break;
            }
            case 2: {
                threePartsLayoutWidget.addBody((Widget)this.realmSelectionList);
            }
        }
        return threePartsLayoutWidget;
    }

    private LayoutWidget makeHeader() {
        int i = 90;
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
        directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
        directionalLayoutWidget.add((Widget)this.inviteButton);
        directionalLayoutWidget.add((Widget)this.newsButton);
        DirectionalLayoutWidget directionalLayoutWidget2 = DirectionalLayoutWidget.horizontal();
        directionalLayoutWidget2.getMainPositioner().alignVerticalCenter();
        directionalLayoutWidget2.add((Widget)EmptyWidget.ofWidth((int)90));
        directionalLayoutWidget2.add((Widget)RealmsMainScreen.createRealmsLogoIconWidget(), Positioner::alignHorizontalCenter);
        ((SimplePositioningWidget)directionalLayoutWidget2.add((Widget)new SimplePositioningWidget(90, 44))).add((Widget)directionalLayoutWidget, Positioner::alignRight);
        return directionalLayoutWidget2;
    }

    private LayoutWidget makeInnerLayout(LoadStatus loadStatus) {
        GridWidget gridWidget = new GridWidget().setSpacing(4);
        GridWidget.Adder adder = gridWidget.createAdder(3);
        if (loadStatus == LoadStatus.LIST) {
            adder.add((Widget)this.playButton);
            adder.add((Widget)this.configureButton);
            adder.add((Widget)this.renewButton);
            adder.add((Widget)this.leaveButton);
        }
        adder.add((Widget)this.purchaseButton);
        adder.add((Widget)this.backButton);
        return gridWidget;
    }

    private DirectionalLayoutWidget makeNoRealmsLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(8);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add((Widget)IconWidget.create((int)130, (int)64, (Identifier)NO_REALMS_TEXTURE, (int)130, (int)64));
        directionalLayoutWidget.add((Widget)NarratedMultilineTextWidget.builder((Text)NO_REALMS_TEXT, (TextRenderer)this.textRenderer).width(308).alwaysShowBorders(false).backgroundRendering(NarratedMultilineTextWidget.BackgroundRendering.ON_FOCUS).build());
        return directionalLayoutWidget;
    }

    void refreshButtons() {
        RealmsServer realmsServer = this.getSelectedServer();
        boolean bl = realmsServer != null;
        this.purchaseButton.active = this.loadStatus != LoadStatus.LOADING;
        boolean bl2 = this.playButton.active = bl && realmsServer.shouldAllowPlay();
        if (!this.playButton.active && bl && realmsServer.state == RealmsServer.State.CLOSED) {
            this.playButton.setTooltip(Tooltip.of((Text)RealmsServer.REALM_CLOSED_TEXT));
        }
        this.renewButton.active = bl && this.shouldRenewButtonBeActive(realmsServer);
        this.leaveButton.active = bl && this.shouldLeaveButtonBeActive(realmsServer);
        this.configureButton.active = bl && this.shouldConfigureButtonBeActive(realmsServer);
    }

    private boolean shouldRenewButtonBeActive(RealmsServer server) {
        return server.expired && RealmsMainScreen.isSelfOwnedServer((RealmsServer)server);
    }

    private boolean shouldConfigureButtonBeActive(RealmsServer server) {
        return RealmsMainScreen.isSelfOwnedServer((RealmsServer)server) && server.state != RealmsServer.State.UNINITIALIZED;
    }

    private boolean shouldLeaveButtonBeActive(RealmsServer server) {
        return !RealmsMainScreen.isSelfOwnedServer((RealmsServer)server);
    }

    public void tick() {
        super.tick();
        if (this.periodicRunnersManager != null) {
            this.periodicRunnersManager.runAll();
        }
    }

    public static void resetPendingInvitesCount() {
        MinecraftClient.getInstance().getRealmsPeriodicCheckers().pendingInvitesCount.reset();
    }

    public static void resetServerList() {
        MinecraftClient.getInstance().getRealmsPeriodicCheckers().serverList.reset();
    }

    private void resetPeriodicCheckers() {
        for (PeriodicRunnerFactory.PeriodicRunner periodicRunner : this.client.getRealmsPeriodicCheckers().getCheckers()) {
            periodicRunner.reset();
        }
    }

    private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
        PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
        runnersManager.add(periodicCheckers.serverList, availableServers -> {
            this.serverFilterer.filterAndSort(availableServers.serverList());
            this.availableSnapshotServers = availableServers.availableSnapshotServers();
            this.refresh();
            boolean bl = false;
            for (RealmsServer realmsServer : this.serverFilterer) {
                if (!this.isOwnedNotExpired(realmsServer)) continue;
                bl = true;
            }
            if (!regionsPinged && bl) {
                regionsPinged = true;
                this.pingRegions();
            }
        });
        RealmsMainScreen.request(RealmsClient::listNotifications, (T notifications) -> {
            this.notifications.clear();
            this.notifications.addAll(notifications);
            for (RealmsNotification realmsNotification : notifications) {
                RealmsNotification.InfoPopup infoPopup;
                PopupScreen popupScreen;
                if (!(realmsNotification instanceof RealmsNotification.InfoPopup) || (popupScreen = (infoPopup = (RealmsNotification.InfoPopup)realmsNotification).createScreen((Screen)this, arg_0 -> this.dismissNotification(arg_0))) == null) continue;
                this.client.setScreen((Screen)popupScreen);
                this.markAsSeen(List.of(realmsNotification));
                break;
            }
            if (!this.notifications.isEmpty() && this.loadStatus != LoadStatus.LOADING) {
                this.refresh();
            }
        });
        runnersManager.add(periodicCheckers.pendingInvitesCount, pendingInvitesCount -> {
            this.inviteButton.setNotificationCount(pendingInvitesCount.intValue());
            this.inviteButton.setTooltip(pendingInvitesCount == 0 ? Tooltip.of((Text)NO_PENDING_TOOLTIP) : Tooltip.of((Text)PENDING_TOOLTIP));
            if (pendingInvitesCount > 0 && this.rateLimiter.tryAcquire(1)) {
                this.client.getNarratorManager().narrateSystemImmediately((Text)Text.translatable((String)"mco.configure.world.invite.narration", (Object[])new Object[]{pendingInvitesCount}));
            }
        });
        runnersManager.add(periodicCheckers.trialAvailability, trialAvailable -> {
            this.trialAvailable = trialAvailable;
        });
        runnersManager.add(periodicCheckers.onlinePlayers, onlinePlayers -> {
            this.onlinePlayers = onlinePlayers;
        });
        runnersManager.add(periodicCheckers.news, news -> {
            realmsPeriodicCheckers.newsUpdater.updateNews(news);
            this.newsLink = realmsPeriodicCheckers.newsUpdater.getNewsLink();
            this.newsButton.setNotificationCount(realmsPeriodicCheckers.newsUpdater.hasUnreadNews() ? Integer.MAX_VALUE : 0);
        });
        return runnersManager;
    }

    void markAsSeen(Collection<RealmsNotification> notifications) {
        ArrayList<UUID> list = new ArrayList<UUID>(notifications.size());
        for (RealmsNotification realmsNotification : notifications) {
            if (realmsNotification.isSeen() || this.seenNotifications.contains(realmsNotification.getUuid())) continue;
            list.add(realmsNotification.getUuid());
        }
        if (!list.isEmpty()) {
            RealmsMainScreen.request(client -> {
                client.markNotificationsAsSeen(list);
                return null;
            }, (T result) -> this.seenNotifications.addAll(list));
        }
    }

    private static <T> void request(Request<T> request, Consumer<T> resultConsumer) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ((CompletableFuture)CompletableFuture.supplyAsync(() -> {
            try {
                return request.request(RealmsClient.createRealmsClient((MinecraftClient)minecraftClient));
            }
            catch (RealmsServiceException realmsServiceException) {
                throw new RuntimeException(realmsServiceException);
            }
        }).thenAcceptAsync(resultConsumer, (Executor)minecraftClient)).exceptionally(throwable -> {
            LOGGER.error("Failed to execute call to Realms Service", throwable);
            return null;
        });
    }

    private void refresh() {
        this.realmSelectionList.refresh(this);
        this.updateLoadStatus();
        this.refreshButtons();
    }

    private void pingRegions() {
        new Thread(() -> {
            List list = Ping.pingAllRegions();
            RealmsClient realmsClient = RealmsClient.create();
            PingResult pingResult = new PingResult(list, this.getOwnedNonExpiredWorldIds());
            try {
                realmsClient.sendPingResults(pingResult);
            }
            catch (Throwable throwable) {
                LOGGER.warn("Could not send ping result to Realms: ", throwable);
            }
        }).start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        ArrayList list = Lists.newArrayList();
        for (RealmsServer realmsServer : this.serverFilterer) {
            if (!this.isOwnedNotExpired(realmsServer)) continue;
            list.add(realmsServer.id);
        }
        return list;
    }

    private void onRenew(@Nullable RealmsServer realmsServer) {
        if (realmsServer != null) {
            String string = Urls.getExtendJavaRealmsUrl((String)realmsServer.remoteSubscriptionId, (UUID)this.client.getSession().getUuidOrNull(), (boolean)realmsServer.expiredTrial);
            this.client.setScreen((Screen)new ConfirmLinkScreen(bl -> {
                if (bl) {
                    Util.getOperatingSystem().open(string);
                } else {
                    this.client.setScreen((Screen)this);
                }
            }, string, true));
        }
    }

    private void configureClicked(@Nullable RealmsServer serverData) {
        if (serverData != null && this.client.uuidEquals(serverData.ownerUUID)) {
            this.client.setScreen((Screen)new RealmsConfigureWorldScreen(this, serverData.id));
        }
    }

    private void leaveClicked(@Nullable RealmsServer selectedServer) {
        if (selectedServer != null && !this.client.uuidEquals(selectedServer.ownerUUID)) {
            MutableText text = Text.translatable((String)"mco.configure.world.leave.question.line1");
            this.client.setScreen((Screen)RealmsPopups.createInfoPopup((Screen)this, (Text)text, popup -> this.leaveServer(selectedServer)));
        }
    }

    private @Nullable RealmsServer getSelectedServer() {
        EntryListWidget.Entry entry = this.realmSelectionList.getSelectedOrNull();
        if (entry instanceof RealmSelectionListEntry) {
            RealmSelectionListEntry realmSelectionListEntry = (RealmSelectionListEntry)entry;
            return realmSelectionListEntry.getRealmsServer();
        }
        return null;
    }

    private void leaveServer(RealmsServer server) {
        new /* Unavailable Anonymous Inner Class!! */.start();
        this.client.setScreen((Screen)this);
    }

    void dismissNotification(UUID notification) {
        RealmsMainScreen.request(client -> {
            client.dismissNotifications(List.of(notification));
            return null;
        }, (T void_) -> {
            this.notifications.removeIf(notificationId -> notificationId.isDismissable() && notification.equals(notificationId.getUuid()));
            this.refresh();
        });
    }

    public void removeSelection() {
        this.realmSelectionList.setSelected(null);
        RealmsMainScreen.resetServerList();
    }

    public Text getNarratedTitle() {
        return switch (this.loadStatus.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), LOADING_TEXT});
            case 1 -> ScreenTexts.joinSentences((Text[])new Text[]{super.getNarratedTitle(), NO_REALMS_TEXT});
            case 2 -> super.getNarratedTitle();
        };
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        if (RealmsMainScreen.isSnapshotRealmsEligible()) {
            context.drawTextWithShadow(this.textRenderer, "Minecraft " + SharedConstants.getGameVersion().name(), 2, this.height - 10, -1);
        }
        if (this.trialAvailable && this.purchaseButton.active) {
            BuyRealmsScreen.drawTrialAvailableTexture((DrawContext)context, (ButtonWidget)this.purchaseButton);
        }
        switch (2.field_45221[RealmsClient.ENVIRONMENT.ordinal()]) {
            case 1: {
                this.drawEnvironmentText(context, "STAGE!", -256);
                break;
            }
            case 2: {
                this.drawEnvironmentText(context, "LOCAL!", -8388737);
            }
        }
    }

    private void showBuyRealmsScreen() {
        this.client.setScreen((Screen)new BuyRealmsScreen((Screen)this, this.trialAvailable));
    }

    public static void play(@Nullable RealmsServer serverData, Screen parent) {
        RealmsMainScreen.play((RealmsServer)serverData, (Screen)parent, (boolean)false);
    }

    public static void play(@Nullable RealmsServer server, Screen parent, boolean needsPreparation) {
        if (server != null) {
            if (!RealmsMainScreen.isSnapshotRealmsEligible() || needsPreparation || server.isMinigame()) {
                MinecraftClient.getInstance().setScreen((Screen)new RealmsLongRunningMcoTaskScreen(parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(parent, server)}));
                return;
            }
            switch (2.field_46674[server.compatibility.ordinal()]) {
                case 1: {
                    MinecraftClient.getInstance().setScreen((Screen)new RealmsLongRunningMcoTaskScreen(parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(parent, server)}));
                    break;
                }
                case 2: {
                    RealmsMainScreen.showCompatibilityScreen((RealmsServer)server, (Screen)parent, (Text)Text.translatable((String)"mco.compatibility.unverifiable.title").withColor(-171), (Text)Text.translatable((String)"mco.compatibility.unverifiable.message"), (Text)ScreenTexts.CONTINUE);
                    break;
                }
                case 3: {
                    RealmsMainScreen.showCompatibilityScreen((RealmsServer)server, (Screen)parent, (Text)Text.translatable((String)"selectWorld.backupQuestion.downgrade").withColor(-2142128), (Text)Text.translatable((String)"mco.compatibility.downgrade.description", (Object[])new Object[]{Text.literal((String)server.activeVersion).withColor(-171), Text.literal((String)SharedConstants.getGameVersion().name()).withColor(-171)}), (Text)Text.translatable((String)"mco.compatibility.downgrade"));
                    break;
                }
                case 4: {
                    RealmsMainScreen.showNeedsUpgradeScreen((RealmsServer)server, (Screen)parent);
                    break;
                }
                case 5: {
                    MinecraftClient.getInstance().setScreen((Screen)new PopupScreen.Builder(parent, INCOMPATIBLE_POPUP_TITLE).message((Text)Text.translatable((String)"mco.compatibility.incompatible.series.popup.message", (Object[])new Object[]{Text.literal((String)server.activeVersion).withColor(-171), Text.literal((String)SharedConstants.getGameVersion().name()).withColor(-171)})).button(ScreenTexts.BACK, PopupScreen::close).build());
                    break;
                }
                case 6: {
                    MinecraftClient.getInstance().setScreen((Screen)new PopupScreen.Builder(parent, INCOMPATIBLE_POPUP_TITLE).message(INCOMPATIBLE_RELEASE_TYPE_MESSAGE).button(ScreenTexts.BACK, PopupScreen::close).build());
                }
            }
        }
    }

    private static void showCompatibilityScreen(RealmsServer server, Screen parent, Text title, Text description, Text confirmText) {
        MinecraftClient.getInstance().setScreen((Screen)new PopupScreen.Builder(parent, title).message(description).button(confirmText, popup -> {
            MinecraftClient.getInstance().setScreen((Screen)new RealmsLongRunningMcoTaskScreen(parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(parent, server)}));
            RealmsMainScreen.resetServerList();
        }).button(ScreenTexts.CANCEL, PopupScreen::close).build());
    }

    private static void showNeedsUpgradeScreen(RealmsServer serverData, Screen parent) {
        MutableText text = Text.translatable((String)"mco.compatibility.upgrade.title").withColor(-171);
        MutableText text2 = Text.translatable((String)"mco.compatibility.upgrade");
        MutableText text3 = Text.literal((String)serverData.activeVersion).withColor(-171);
        MutableText text4 = Text.literal((String)SharedConstants.getGameVersion().name()).withColor(-171);
        MutableText text5 = RealmsMainScreen.isSelfOwnedServer((RealmsServer)serverData) ? Text.translatable((String)"mco.compatibility.upgrade.description", (Object[])new Object[]{text3, text4}) : Text.translatable((String)"mco.compatibility.upgrade.friend.description", (Object[])new Object[]{text3, text4});
        RealmsMainScreen.showCompatibilityScreen((RealmsServer)serverData, (Screen)parent, (Text)text, (Text)text5, (Text)text2);
    }

    public static Text getVersionText(String version, boolean compatible) {
        return RealmsMainScreen.getVersionText((String)version, (int)(compatible ? -8355712 : -2142128));
    }

    public static Text getVersionText(String version, int color) {
        if (StringUtils.isBlank((CharSequence)version)) {
            return ScreenTexts.EMPTY;
        }
        return Text.literal((String)version).withColor(color);
    }

    public static Text getGameModeText(int id, boolean hardcore) {
        if (hardcore) {
            return Text.translatable((String)"gameMode.hardcore").withColor(-65536);
        }
        return GameMode.byIndex((int)id).getTranslatableName();
    }

    static boolean isSelfOwnedServer(RealmsServer server) {
        return MinecraftClient.getInstance().uuidEquals(server.ownerUUID);
    }

    private boolean isOwnedNotExpired(RealmsServer serverData) {
        return RealmsMainScreen.isSelfOwnedServer((RealmsServer)serverData) && !serverData.expired;
    }

    private void drawEnvironmentText(DrawContext context, String text, int color) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)(this.width / 2 - 25), 20.0f);
        context.getMatrices().rotate(-0.34906584f);
        context.getMatrices().scale(1.5f, 1.5f);
        context.drawTextWithShadow(this.textRenderer, text, 0, 0, color);
        context.getMatrices().popMatrix();
    }

    private /* synthetic */ void method_52640(Text text, ButtonWidget button) {
        this.client.setScreen((Screen)new RealmsPendingInvitesScreen((Screen)this, text));
    }

    static /* synthetic */ MinecraftClient method_36825(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ MinecraftClient method_36826(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ MinecraftClient method_36827(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ TextRenderer method_55791(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_20885(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_36828(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_36829(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_36830(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_24992(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_53884(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_24996(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_24998(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_24994(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_44254(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_49562(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_36832(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_54332(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_54333(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_54334(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_54563(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_64835(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ MinecraftClient method_64836(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ MinecraftClient method_64837(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ MinecraftClient method_71038(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ TextRenderer method_73336(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ TextRenderer method_54341(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ MinecraftClient method_54342(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ TextRenderer method_54318(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.textRenderer;
    }

    static /* synthetic */ MinecraftClient method_54319(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ MinecraftClient method_54321(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static /* synthetic */ MinecraftClient method_60859(RealmsMainScreen realmsMainScreen) {
        return realmsMainScreen.client;
    }

    static {
        showingSnapshotRealms = GAME_ON_SNAPSHOT = !SharedConstants.getGameVersion().stable();
    }
}

