package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.PopupScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.ProfilesTooltipComponent;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipState;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
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
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.input.KeyCodes;
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
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.client.realms.util.PeriodicRunnerFactory;
import net.minecraft.client.realms.util.RealmsPersistence;
import net.minecraft.client.realms.util.RealmsServerFilterer;
import net.minecraft.client.realms.util.RealmsUtil;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsMainScreen extends RealmsScreen {
   static final Identifier INFO_ICON_TEXTURE = Identifier.ofVanilla("icon/info");
   static final Identifier NEW_REALM_ICON_TEXTURE = Identifier.ofVanilla("icon/new_realm");
   static final Identifier EXPIRED_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/expired");
   static final Identifier EXPIRES_SOON_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/expires_soon");
   static final Identifier OPEN_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/open");
   static final Identifier CLOSED_STATUS_TEXTURE = Identifier.ofVanilla("realm_status/closed");
   private static final Identifier INVITE_ICON_TEXTURE = Identifier.ofVanilla("icon/invite");
   private static final Identifier NEWS_ICON_TEXTURE = Identifier.ofVanilla("icon/news");
   public static final Identifier HARDCORE_ICON_TEXTURE = Identifier.ofVanilla("hud/heart/hardcore_full");
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Identifier NO_REALMS_TEXTURE = Identifier.ofVanilla("textures/gui/realms/no_realms.png");
   private static final Text MENU_TEXT = Text.translatable("menu.online");
   private static final Text LOADING_TEXT = Text.translatable("mco.selectServer.loading");
   static final Text UNINITIALIZED_TEXT = Text.translatable("mco.selectServer.uninitialized");
   static final Text EXPIRED_LIST_TEXT = Text.translatable("mco.selectServer.expiredList");
   private static final Text EXPIRED_RENEW_TEXT = Text.translatable("mco.selectServer.expiredRenew");
   static final Text EXPIRED_TRIAL_TEXT = Text.translatable("mco.selectServer.expiredTrial");
   private static final Text PLAY_TEXT = Text.translatable("mco.selectServer.play");
   private static final Text LEAVE_TEXT = Text.translatable("mco.selectServer.leave");
   private static final Text CONFIGURE_TEXT = Text.translatable("mco.selectServer.configure");
   static final Text EXPIRED_TEXT = Text.translatable("mco.selectServer.expired");
   static final Text EXPIRES_SOON_TEXT = Text.translatable("mco.selectServer.expires.soon");
   static final Text EXPIRES_IN_A_DAY_TEXT = Text.translatable("mco.selectServer.expires.day");
   static final Text OPEN_TEXT = Text.translatable("mco.selectServer.open");
   static final Text CLOSED_TEXT = Text.translatable("mco.selectServer.closed");
   static final Text UNINITIALIZED_BUTTON_NARRATION;
   private static final Text NO_REALMS_TEXT;
   private static final Text NO_PENDING_TOOLTIP;
   private static final Text PENDING_TOOLTIP;
   private static final Text INCOMPATIBLE_POPUP_TITLE;
   private static final Text INCOMPATIBLE_RELEASE_TYPE_MESSAGE;
   private static final int field_42862 = 100;
   private static final int field_45209 = 3;
   private static final int field_45210 = 4;
   private static final int field_45211 = 308;
   private static final int field_44513 = 5;
   private static final int field_44514 = 44;
   private static final int field_45212 = 11;
   private static final int field_46670 = 40;
   private static final int field_46671 = 20;
   private static final int field_46215 = 216;
   private static final int field_46216 = 36;
   private static final boolean GAME_ON_SNAPSHOT;
   private static boolean showingSnapshotRealms;
   private final CompletableFuture availabilityInfo = RealmsAvailability.check();
   @Nullable
   private PeriodicRunnerFactory.RunnersManager periodicRunnersManager;
   private final Set seenNotifications = new HashSet();
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
   List availableSnapshotServers = List.of();
   RealmsServerPlayerList onlinePlayers = new RealmsServerPlayerList();
   private volatile boolean trialAvailable;
   @Nullable
   private volatile String newsLink;
   long lastPlayButtonClickTime;
   final List notifications = new ArrayList();
   private ButtonWidget purchaseButton;
   private NotificationButtonWidget inviteButton;
   private NotificationButtonWidget newsButton;
   private LoadStatus loadStatus;
   @Nullable
   private ThreePartsLayoutWidget layout;

   public RealmsMainScreen(Screen parent) {
      super(MENU_TEXT);
      this.parent = parent;
      this.rateLimiter = RateLimiter.create(0.01666666753590107);
   }

   public void init() {
      this.serverFilterer = new RealmsServerFilterer(this.client);
      this.realmSelectionList = new RealmSelectionList();
      Text text = Text.translatable("mco.invites.title");
      this.inviteButton = new NotificationButtonWidget(text, INVITE_ICON_TEXTURE, (button) -> {
         this.client.setScreen(new RealmsPendingInvitesScreen(this, text));
      });
      Text text2 = Text.translatable("mco.news");
      this.newsButton = new NotificationButtonWidget(text2, NEWS_ICON_TEXTURE, (button) -> {
         String string = this.newsLink;
         if (string != null) {
            ConfirmLinkScreen.open(this, (String)string);
            if (this.newsButton.getNotificationCount() != 0) {
               RealmsPersistence.RealmsPersistenceData realmsPersistenceData = RealmsPersistence.readFile();
               realmsPersistenceData.hasUnreadNews = false;
               RealmsPersistence.writeFile(realmsPersistenceData);
               this.newsButton.setNotificationCount(0);
            }

         }
      });
      this.newsButton.setTooltip(Tooltip.of(text2));
      this.playButton = ButtonWidget.builder(PLAY_TEXT, (button) -> {
         play(this.getSelectedServer(), this);
      }).width(100).build();
      this.configureButton = ButtonWidget.builder(CONFIGURE_TEXT, (button) -> {
         this.configureClicked(this.getSelectedServer());
      }).width(100).build();
      this.renewButton = ButtonWidget.builder(EXPIRED_RENEW_TEXT, (button) -> {
         this.onRenew(this.getSelectedServer());
      }).width(100).build();
      this.leaveButton = ButtonWidget.builder(LEAVE_TEXT, (button) -> {
         this.leaveClicked(this.getSelectedServer());
      }).width(100).build();
      this.purchaseButton = ButtonWidget.builder(Text.translatable("mco.selectServer.purchase"), (button) -> {
         this.showBuyRealmsScreen();
      }).size(100, 20).build();
      this.backButton = ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
         this.close();
      }).width(100).build();
      if (RealmsClient.ENVIRONMENT == RealmsClient.Environment.STAGE) {
         this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("Snapshot"), Text.literal("Release")).build(5, 5, 100, 20, Text.literal("Realm"), (button, snapshot) -> {
            showingSnapshotRealms = snapshot;
            this.availableSnapshotServers = List.of();
            this.resetPeriodicCheckers();
         }));
      }

      this.onLoadStatusChange(RealmsMainScreen.LoadStatus.LOADING);
      this.refreshButtons();
      this.availabilityInfo.thenAcceptAsync((availabilityInfo) -> {
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
         this.onLoadStatusChange(RealmsMainScreen.LoadStatus.NO_REALMS);
      } else {
         this.onLoadStatusChange(RealmsMainScreen.LoadStatus.LIST);
      }

   }

   private void onLoadStatusChange(LoadStatus loadStatus) {
      if (this.loadStatus != loadStatus) {
         if (this.layout != null) {
            this.layout.forEachChild((child) -> {
               this.remove(child);
            });
         }

         this.layout = this.makeLayoutFor(loadStatus);
         this.loadStatus = loadStatus;
         this.layout.forEachChild((child) -> {
            ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
         });
         this.refreshWidgetPositions();
      }
   }

   private ThreePartsLayoutWidget makeLayoutFor(LoadStatus loadStatus) {
      ThreePartsLayoutWidget threePartsLayoutWidget = new ThreePartsLayoutWidget(this);
      threePartsLayoutWidget.setHeaderHeight(44);
      threePartsLayoutWidget.addHeader(this.makeHeader());
      LayoutWidget layoutWidget = this.makeInnerLayout(loadStatus);
      layoutWidget.refreshPositions();
      threePartsLayoutWidget.setFooterHeight(layoutWidget.getHeight() + 22);
      threePartsLayoutWidget.addFooter(layoutWidget);
      switch (loadStatus.ordinal()) {
         case 0:
            threePartsLayoutWidget.addBody(new LoadingWidget(this.textRenderer, LOADING_TEXT));
            break;
         case 1:
            threePartsLayoutWidget.addBody(this.makeNoRealmsLayout());
            break;
         case 2:
            threePartsLayoutWidget.addBody(this.realmSelectionList);
      }

      return threePartsLayoutWidget;
   }

   private LayoutWidget makeHeader() {
      int i = true;
      DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(4);
      directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
      directionalLayoutWidget.add(this.inviteButton);
      directionalLayoutWidget.add(this.newsButton);
      DirectionalLayoutWidget directionalLayoutWidget2 = DirectionalLayoutWidget.horizontal();
      directionalLayoutWidget2.getMainPositioner().alignVerticalCenter();
      directionalLayoutWidget2.add(EmptyWidget.ofWidth(90));
      directionalLayoutWidget2.add(createRealmsLogoIconWidget(), (Consumer)(Positioner::alignHorizontalCenter));
      ((SimplePositioningWidget)directionalLayoutWidget2.add(new SimplePositioningWidget(90, 44))).add(directionalLayoutWidget, (Consumer)(Positioner::alignRight));
      return directionalLayoutWidget2;
   }

   private LayoutWidget makeInnerLayout(LoadStatus loadStatus) {
      GridWidget gridWidget = (new GridWidget()).setSpacing(4);
      GridWidget.Adder adder = gridWidget.createAdder(3);
      if (loadStatus == RealmsMainScreen.LoadStatus.LIST) {
         adder.add(this.playButton);
         adder.add(this.configureButton);
         adder.add(this.renewButton);
         adder.add(this.leaveButton);
      }

      adder.add(this.purchaseButton);
      adder.add(this.backButton);
      return gridWidget;
   }

   private DirectionalLayoutWidget makeNoRealmsLayout() {
      DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(8);
      directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
      directionalLayoutWidget.add(IconWidget.create(130, 64, NO_REALMS_TEXTURE, 130, 64));
      NarratedMultilineTextWidget narratedMultilineTextWidget = new NarratedMultilineTextWidget(308, NO_REALMS_TEXT, this.textRenderer, false, true, 4);
      directionalLayoutWidget.add(narratedMultilineTextWidget);
      return directionalLayoutWidget;
   }

   void refreshButtons() {
      RealmsServer realmsServer = this.getSelectedServer();
      boolean bl = realmsServer != null;
      this.purchaseButton.active = this.loadStatus != RealmsMainScreen.LoadStatus.LOADING;
      this.playButton.active = bl && realmsServer.shouldAllowPlay();
      if (!this.playButton.active && bl && realmsServer.state == RealmsServer.State.CLOSED) {
         this.playButton.setTooltip(Tooltip.of(RealmsServer.REALM_CLOSED_TEXT));
      }

      this.renewButton.active = bl && this.shouldRenewButtonBeActive(realmsServer);
      this.leaveButton.active = bl && this.shouldLeaveButtonBeActive(realmsServer);
      this.configureButton.active = bl && this.shouldConfigureButtonBeActive(realmsServer);
   }

   private boolean shouldRenewButtonBeActive(RealmsServer server) {
      return server.expired && isSelfOwnedServer(server);
   }

   private boolean shouldConfigureButtonBeActive(RealmsServer server) {
      return isSelfOwnedServer(server) && server.state != RealmsServer.State.UNINITIALIZED;
   }

   private boolean shouldLeaveButtonBeActive(RealmsServer server) {
      return !isSelfOwnedServer(server);
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
      Iterator var1 = this.client.getRealmsPeriodicCheckers().getCheckers().iterator();

      while(var1.hasNext()) {
         PeriodicRunnerFactory.PeriodicRunner periodicRunner = (PeriodicRunnerFactory.PeriodicRunner)var1.next();
         periodicRunner.reset();
      }

   }

   private PeriodicRunnerFactory.RunnersManager createPeriodicRunnersManager(RealmsPeriodicCheckers periodicCheckers) {
      PeriodicRunnerFactory.RunnersManager runnersManager = periodicCheckers.runnerFactory.create();
      runnersManager.add(periodicCheckers.serverList, (availableServers) -> {
         this.serverFilterer.filterAndSort(availableServers.serverList());
         this.availableSnapshotServers = availableServers.availableSnapshotServers();
         this.refresh();
         boolean bl = false;
         Iterator var3 = this.serverFilterer.iterator();

         while(var3.hasNext()) {
            RealmsServer realmsServer = (RealmsServer)var3.next();
            if (this.isOwnedNotExpired(realmsServer)) {
               bl = true;
            }
         }

         if (!regionsPinged && bl) {
            regionsPinged = true;
            this.pingRegions();
         }

      });
      request(RealmsClient::listNotifications, (notifications) -> {
         this.notifications.clear();
         this.notifications.addAll(notifications);
         Iterator var2 = notifications.iterator();

         while(var2.hasNext()) {
            RealmsNotification realmsNotification = (RealmsNotification)var2.next();
            if (realmsNotification instanceof RealmsNotification.InfoPopup infoPopup) {
               PopupScreen popupScreen = infoPopup.createScreen(this, this::dismissNotification);
               if (popupScreen != null) {
                  this.client.setScreen(popupScreen);
                  this.markAsSeen(List.of(realmsNotification));
                  break;
               }
            }
         }

         if (!this.notifications.isEmpty() && this.loadStatus != RealmsMainScreen.LoadStatus.LOADING) {
            this.refresh();
         }

      });
      runnersManager.add(periodicCheckers.pendingInvitesCount, (pendingInvitesCount) -> {
         this.inviteButton.setNotificationCount(pendingInvitesCount);
         this.inviteButton.setTooltip(pendingInvitesCount == 0 ? Tooltip.of(NO_PENDING_TOOLTIP) : Tooltip.of(PENDING_TOOLTIP));
         if (pendingInvitesCount > 0 && this.rateLimiter.tryAcquire(1)) {
            this.client.getNarratorManager().narrateSystemImmediately((Text)Text.translatable("mco.configure.world.invite.narration", pendingInvitesCount));
         }

      });
      runnersManager.add(periodicCheckers.trialAvailability, (trialAvailable) -> {
         this.trialAvailable = trialAvailable;
      });
      runnersManager.add(periodicCheckers.onlinePlayers, (onlinePlayers) -> {
         this.onlinePlayers = onlinePlayers;
      });
      runnersManager.add(periodicCheckers.news, (news) -> {
         periodicCheckers.newsUpdater.updateNews(news);
         this.newsLink = periodicCheckers.newsUpdater.getNewsLink();
         this.newsButton.setNotificationCount(periodicCheckers.newsUpdater.hasUnreadNews() ? Integer.MAX_VALUE : 0);
      });
      return runnersManager;
   }

   void markAsSeen(Collection notifications) {
      List list = new ArrayList(notifications.size());
      Iterator var3 = notifications.iterator();

      while(var3.hasNext()) {
         RealmsNotification realmsNotification = (RealmsNotification)var3.next();
         if (!realmsNotification.isSeen() && !this.seenNotifications.contains(realmsNotification.getUuid())) {
            list.add(realmsNotification.getUuid());
         }
      }

      if (!list.isEmpty()) {
         request((client) -> {
            client.markNotificationsAsSeen(list);
            return null;
         }, (result) -> {
            this.seenNotifications.addAll(list);
         });
      }

   }

   private static void request(Request request, Consumer resultConsumer) {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      CompletableFuture.supplyAsync(() -> {
         try {
            return request.request(RealmsClient.createRealmsClient(minecraftClient));
         } catch (RealmsServiceException var3) {
            throw new RuntimeException(var3);
         }
      }).thenAcceptAsync(resultConsumer, minecraftClient).exceptionally((throwable) -> {
         LOGGER.error("Failed to execute call to Realms Service", throwable);
         return null;
      });
   }

   private void refresh() {
      this.realmSelectionList.refresh(this, this.getSelectedServer());
      this.updateLoadStatus();
      this.refreshButtons();
   }

   private void pingRegions() {
      (new Thread(() -> {
         List list = Ping.pingAllRegions();
         RealmsClient realmsClient = RealmsClient.create();
         PingResult pingResult = new PingResult();
         pingResult.pingResults = list;
         pingResult.worldIds = this.getOwnedNonExpiredWorldIds();

         try {
            realmsClient.sendPingResults(pingResult);
         } catch (Throwable var5) {
            LOGGER.warn("Could not send ping result to Realms: ", var5);
         }

      })).start();
   }

   private List getOwnedNonExpiredWorldIds() {
      List list = Lists.newArrayList();
      Iterator var2 = this.serverFilterer.iterator();

      while(var2.hasNext()) {
         RealmsServer realmsServer = (RealmsServer)var2.next();
         if (this.isOwnedNotExpired(realmsServer)) {
            list.add(realmsServer.id);
         }
      }

      return list;
   }

   private void onRenew(@Nullable RealmsServer realmsServer) {
      if (realmsServer != null) {
         String string = Urls.getExtendJavaRealmsUrl(realmsServer.remoteSubscriptionId, this.client.getSession().getUuidOrNull(), realmsServer.expiredTrial);
         this.client.keyboard.setClipboard(string);
         Util.getOperatingSystem().open(string);
      }

   }

   private void configureClicked(@Nullable RealmsServer serverData) {
      if (serverData != null && this.client.uuidEquals(serverData.ownerUUID)) {
         this.client.setScreen(new RealmsConfigureWorldScreen(this, serverData.id));
      }

   }

   private void leaveClicked(@Nullable RealmsServer selectedServer) {
      if (selectedServer != null && !this.client.uuidEquals(selectedServer.ownerUUID)) {
         Text text = Text.translatable("mco.configure.world.leave.question.line1");
         this.client.setScreen(RealmsPopups.createInfoPopup(this, text, (popup) -> {
            this.leaveServer(selectedServer);
         }));
      }

   }

   @Nullable
   private RealmsServer getSelectedServer() {
      EntryListWidget.Entry var2 = this.realmSelectionList.getSelectedOrNull();
      if (var2 instanceof RealmSelectionListEntry realmSelectionListEntry) {
         return realmSelectionListEntry.getRealmsServer();
      } else {
         return null;
      }
   }

   private void leaveServer(final RealmsServer server) {
      (new Thread("Realms-leave-server") {
         public void run() {
            try {
               RealmsClient realmsClient = RealmsClient.create();
               realmsClient.uninviteMyselfFrom(server.id);
               RealmsMainScreen.this.client.execute(RealmsMainScreen::resetServerList);
            } catch (RealmsServiceException var2) {
               RealmsMainScreen.LOGGER.error("Couldn't configure world", var2);
               RealmsMainScreen.this.client.execute(() -> {
                  RealmsMainScreen.this.client.setScreen(new RealmsGenericErrorScreen(var2, RealmsMainScreen.this));
               });
            }

         }
      }).start();
      this.client.setScreen(this);
   }

   void dismissNotification(UUID notification) {
      request((client) -> {
         client.dismissNotifications(List.of(notification));
         return null;
      }, (void_) -> {
         this.notifications.removeIf((notificationId) -> {
            return notificationId.isDismissable() && notification.equals(notificationId.getUuid());
         });
         this.refresh();
      });
   }

   public void removeSelection() {
      this.realmSelectionList.setSelected((Entry)null);
      resetServerList();
   }

   public Text getNarratedTitle() {
      Object var10000;
      switch (this.loadStatus.ordinal()) {
         case 0:
            var10000 = ScreenTexts.joinSentences(super.getNarratedTitle(), LOADING_TEXT);
            break;
         case 1:
            var10000 = ScreenTexts.joinSentences(super.getNarratedTitle(), NO_REALMS_TEXT);
            break;
         case 2:
            var10000 = super.getNarratedTitle();
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (Text)var10000;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      if (isSnapshotRealmsEligible()) {
         context.drawTextWithShadow(this.textRenderer, (String)("Minecraft " + SharedConstants.getGameVersion().name()), 2, this.height - 10, -1);
      }

      if (this.trialAvailable && this.purchaseButton.active) {
         BuyRealmsScreen.drawTrialAvailableTexture(context, this.purchaseButton);
      }

      switch (RealmsClient.ENVIRONMENT) {
         case STAGE:
            this.drawEnvironmentText(context, "STAGE!", -256);
            break;
         case LOCAL:
            this.drawEnvironmentText(context, "LOCAL!", -8388737);
      }

   }

   private void showBuyRealmsScreen() {
      this.client.setScreen(new BuyRealmsScreen(this, this.trialAvailable));
   }

   public static void play(@Nullable RealmsServer serverData, Screen parent) {
      play(serverData, parent, false);
   }

   public static void play(@Nullable RealmsServer server, Screen parent, boolean needsPreparation) {
      if (server != null) {
         if (!isSnapshotRealmsEligible() || needsPreparation || server.isMinigame()) {
            MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(parent, server)}));
            return;
         }

         switch (server.compatibility) {
            case COMPATIBLE:
               MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(parent, server)}));
               break;
            case UNVERIFIABLE:
               showCompatibilityScreen(server, parent, Text.translatable("mco.compatibility.unverifiable.title").withColor(-171), Text.translatable("mco.compatibility.unverifiable.message"), ScreenTexts.CONTINUE);
               break;
            case NEEDS_DOWNGRADE:
               showCompatibilityScreen(server, parent, Text.translatable("selectWorld.backupQuestion.downgrade").withColor(-2142128), Text.translatable("mco.compatibility.downgrade.description", Text.literal(server.activeVersion).withColor(-171), Text.literal(SharedConstants.getGameVersion().name()).withColor(-171)), Text.translatable("mco.compatibility.downgrade"));
               break;
            case NEEDS_UPGRADE:
               showNeedsUpgradeScreen(server, parent);
               break;
            case INCOMPATIBLE:
               MinecraftClient.getInstance().setScreen((new PopupScreen.Builder(parent, INCOMPATIBLE_POPUP_TITLE)).message(Text.translatable("mco.compatibility.incompatible.series.popup.message", Text.literal(server.activeVersion).withColor(-171), Text.literal(SharedConstants.getGameVersion().name()).withColor(-171))).button(ScreenTexts.BACK, PopupScreen::close).build());
               break;
            case RELEASE_TYPE_INCOMPATIBLE:
               MinecraftClient.getInstance().setScreen((new PopupScreen.Builder(parent, INCOMPATIBLE_POPUP_TITLE)).message(INCOMPATIBLE_RELEASE_TYPE_MESSAGE).button(ScreenTexts.BACK, PopupScreen::close).build());
         }
      }

   }

   private static void showCompatibilityScreen(RealmsServer server, Screen parent, Text title, Text description, Text confirmText) {
      MinecraftClient.getInstance().setScreen((new PopupScreen.Builder(parent, title)).message(description).button(confirmText, (popup) -> {
         MinecraftClient.getInstance().setScreen(new RealmsLongRunningMcoTaskScreen(parent, new LongRunningTask[]{new RealmsPrepareConnectionTask(parent, server)}));
         resetServerList();
      }).button(ScreenTexts.CANCEL, PopupScreen::close).build());
   }

   private static void showNeedsUpgradeScreen(RealmsServer serverData, Screen parent) {
      Text text = Text.translatable("mco.compatibility.upgrade.title").withColor(-171);
      Text text2 = Text.translatable("mco.compatibility.upgrade");
      Text text3 = Text.literal(serverData.activeVersion).withColor(-171);
      Text text4 = Text.literal(SharedConstants.getGameVersion().name()).withColor(-171);
      Text text5 = isSelfOwnedServer(serverData) ? Text.translatable("mco.compatibility.upgrade.description", text3, text4) : Text.translatable("mco.compatibility.upgrade.friend.description", text3, text4);
      showCompatibilityScreen(serverData, parent, text, text5, text2);
   }

   public static Text getVersionText(String version, boolean compatible) {
      return getVersionText(version, compatible ? -8355712 : -2142128);
   }

   public static Text getVersionText(String version, int color) {
      return (Text)(StringUtils.isBlank(version) ? ScreenTexts.EMPTY : Text.literal(version).withColor(color));
   }

   public static Text getGameModeText(int id, boolean hardcore) {
      return (Text)(hardcore ? Text.translatable("gameMode.hardcore").withColor(-65536) : GameMode.byIndex(id).getTranslatableName());
   }

   static boolean isSelfOwnedServer(RealmsServer server) {
      return MinecraftClient.getInstance().uuidEquals(server.ownerUUID);
   }

   private boolean isOwnedNotExpired(RealmsServer serverData) {
      return isSelfOwnedServer(serverData) && !serverData.expired;
   }

   private void drawEnvironmentText(DrawContext context, String text, int color) {
      context.getMatrices().pushMatrix();
      context.getMatrices().translate((float)(this.width / 2 - 25), 20.0F);
      context.getMatrices().rotate(-0.34906584F);
      context.getMatrices().scale(1.5F, 1.5F);
      context.drawTextWithShadow(this.textRenderer, (String)text, 0, 0, color);
      context.getMatrices().popMatrix();
   }

   static {
      UNINITIALIZED_BUTTON_NARRATION = Text.translatable("gui.narrate.button", UNINITIALIZED_TEXT);
      NO_REALMS_TEXT = Text.translatable("mco.selectServer.noRealms");
      NO_PENDING_TOOLTIP = Text.translatable("mco.invites.nopending");
      PENDING_TOOLTIP = Text.translatable("mco.invites.pending");
      INCOMPATIBLE_POPUP_TITLE = Text.translatable("mco.compatibility.incompatible.popup.title");
      INCOMPATIBLE_RELEASE_TYPE_MESSAGE = Text.translatable("mco.compatibility.incompatible.releaseType.popup.message");
      GAME_ON_SNAPSHOT = !SharedConstants.getGameVersion().stable();
      showingSnapshotRealms = GAME_ON_SNAPSHOT;
   }

   @Environment(EnvType.CLIENT)
   private class RealmSelectionList extends AlwaysSelectedEntryListWidget {
      public RealmSelectionList() {
         super(MinecraftClient.getInstance(), RealmsMainScreen.this.width, RealmsMainScreen.this.height, 0, 36);
      }

      public void setSelected(@Nullable Entry entry) {
         super.setSelected(entry);
         RealmsMainScreen.this.refreshButtons();
      }

      public int getRowWidth() {
         return 300;
      }

      void refresh(RealmsMainScreen mainScreen, @Nullable RealmsServer selectedServer) {
         this.clearEntries();
         Iterator var3 = RealmsMainScreen.this.notifications.iterator();

         while(var3.hasNext()) {
            RealmsNotification realmsNotification = (RealmsNotification)var3.next();
            if (realmsNotification instanceof RealmsNotification.VisitUrl visitUrl) {
               this.addVisitEntries(visitUrl, mainScreen);
               RealmsMainScreen.this.markAsSeen(List.of(realmsNotification));
               break;
            }
         }

         this.addServerEntries(selectedServer);
      }

      private void addServerEntries(@Nullable RealmsServer selectedServer) {
         Iterator var2 = RealmsMainScreen.this.availableSnapshotServers.iterator();

         RealmsServer realmsServer;
         while(var2.hasNext()) {
            realmsServer = (RealmsServer)var2.next();
            this.addEntry(RealmsMainScreen.this.new SnapshotEntry(realmsServer));
         }

         var2 = RealmsMainScreen.this.serverFilterer.iterator();

         while(true) {
            Object entry;
            while(true) {
               if (!var2.hasNext()) {
                  return;
               }

               realmsServer = (RealmsServer)var2.next();
               if (RealmsMainScreen.isSnapshotRealmsEligible() && !realmsServer.isPrerelease()) {
                  if (realmsServer.state == RealmsServer.State.UNINITIALIZED) {
                     continue;
                  }

                  entry = RealmsMainScreen.this.new ParentRealmSelectionListEntry(RealmsMainScreen.this, realmsServer);
                  break;
               }

               entry = RealmsMainScreen.this.new RealmSelectionListEntry(realmsServer);
               break;
            }

            this.addEntry((EntryListWidget.Entry)entry);
            if (selectedServer != null && selectedServer.id == realmsServer.id) {
               this.setSelected((Entry)entry);
            }
         }
      }

      private void addVisitEntries(RealmsNotification.VisitUrl url, RealmsMainScreen mainScreen) {
         Text text = url.getDefaultMessage();
         int i = RealmsMainScreen.this.textRenderer.getWrappedLinesHeight((StringVisitable)text, 216);
         int j = MathHelper.ceilDiv(i + 7, 36) - 1;
         this.addEntry(RealmsMainScreen.this.new VisitUrlNotification(text, j + 2, url));

         for(int k = 0; k < j; ++k) {
            this.addEntry(RealmsMainScreen.this.new EmptyEntry(RealmsMainScreen.this));
         }

         this.addEntry(RealmsMainScreen.this.new VisitButtonEntry(url.createButton(mainScreen)));
      }
   }

   @Environment(EnvType.CLIENT)
   static class NotificationButtonWidget extends TextIconButtonWidget.IconOnly {
      private static final Identifier[] TEXTURES = new Identifier[]{Identifier.ofVanilla("notification/1"), Identifier.ofVanilla("notification/2"), Identifier.ofVanilla("notification/3"), Identifier.ofVanilla("notification/4"), Identifier.ofVanilla("notification/5"), Identifier.ofVanilla("notification/more")};
      private static final int field_45228 = Integer.MAX_VALUE;
      private static final int SIZE = 20;
      private static final int TEXTURE_SIZE = 14;
      private int notificationCount;

      public NotificationButtonWidget(Text message, Identifier texture, ButtonWidget.PressAction onPress) {
         super(20, 20, message, 14, 14, texture, onPress, (ButtonWidget.NarrationSupplier)null);
      }

      int getNotificationCount() {
         return this.notificationCount;
      }

      public void setNotificationCount(int notificationCount) {
         this.notificationCount = notificationCount;
      }

      public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         super.renderWidget(context, mouseX, mouseY, deltaTicks);
         if (this.active && this.notificationCount != 0) {
            this.render(context);
         }

      }

      private void render(DrawContext context) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURES[Math.min(this.notificationCount, 6) - 1], this.getX() + this.getWidth() - 5, this.getY() - 3, 8, 8);
      }
   }

   @Environment(EnvType.CLIENT)
   private static enum LoadStatus {
      LOADING,
      NO_REALMS,
      LIST;

      // $FF: synthetic method
      private static LoadStatus[] method_52650() {
         return new LoadStatus[]{LOADING, NO_REALMS, LIST};
      }
   }

   @Environment(EnvType.CLIENT)
   interface Request {
      Object request(RealmsClient client) throws RealmsServiceException;
   }

   @Environment(EnvType.CLIENT)
   private class RealmSelectionListEntry extends Entry {
      private static final Text ONLINE_PLAYERS_TEXT = Text.translatable("mco.onlinePlayers");
      private static final int field_52120 = 9;
      private static final int field_32054 = 36;
      private final RealmsServer server;
      private final TooltipState tooltip = new TooltipState();

      public RealmSelectionListEntry(final RealmsServer server) {
         super();
         this.server = server;
         boolean bl = RealmsMainScreen.isSelfOwnedServer(server);
         if (RealmsMainScreen.isSnapshotRealmsEligible() && bl && server.isPrerelease()) {
            this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.paired", server.parentWorldName)));
         } else if (!bl && server.needsDowngrade()) {
            this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.friendsRealm.downgrade", server.activeVersion)));
         }

      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         if (this.server.state == RealmsServer.State.UNINITIALIZED) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, RealmsMainScreen.NEW_REALM_ICON_TEXTURE, x - 5, y + entryHeight / 2 - 10, 40, 20);
            int var10000 = y + entryHeight / 2;
            Objects.requireNonNull(RealmsMainScreen.this.textRenderer);
            int i = var10000 - 9 / 2;
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, RealmsMainScreen.UNINITIALIZED_TEXT, x + 40 - 2, i, -8388737);
         } else {
            RealmsUtil.drawPlayerHead(context, x, y, 32, this.server.ownerUUID);
            this.drawServerNameAndVersion(context, y, x, entryWidth, -1, this.server);
            this.drawDescription(context, y, x, entryWidth, this.server);
            this.drawOwnerOrExpiredText(context, y, x, this.server);
            this.renderStatusIcon(this.server, context, x + entryWidth, y, mouseX, mouseY);
            boolean bl = this.drawPlayers(context, y, x, entryWidth, entryHeight, mouseX, mouseY, tickProgress);
            if (!bl) {
               this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
            }

         }
      }

      private boolean drawPlayers(DrawContext context, int top, int left, int width, int height, int mouseX, int mouseY, float tickProgress) {
         List list = RealmsMainScreen.this.onlinePlayers.get(this.server.id);
         if (!list.isEmpty()) {
            int i = left + width - 21;
            int j = top + height - 9 - 2;
            int k = i;

            for(int l = 0; l < list.size(); ++l) {
               k -= 9 + (l == 0 ? 0 : 3);
               PlayerSkinDrawer.draw(context, MinecraftClient.getInstance().getSkinProvider().getSkinTextures(((ProfileResult)list.get(l)).profile()), k, j, 9);
            }

            if (mouseX >= k && mouseX <= i && mouseY >= j && mouseY <= j + 9) {
               context.drawTooltip(RealmsMainScreen.this.textRenderer, List.of(ONLINE_PLAYERS_TEXT), Optional.of(new ProfilesTooltipComponent.ProfilesData(list)), mouseX, mouseY);
               return true;
            }
         }

         return false;
      }

      private void play() {
         RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsMainScreen.play(this.server, RealmsMainScreen.this);
      }

      private void createRealm() {
         RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsCreateRealmScreen realmsCreateRealmScreen = new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server, this.server.isPrerelease());
         RealmsMainScreen.this.client.setScreen(realmsCreateRealmScreen);
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (this.server.state == RealmsServer.State.UNINITIALIZED) {
            this.createRealm();
         } else if (this.server.shouldAllowPlay()) {
            if (Util.getMeasuringTimeMs() - RealmsMainScreen.this.lastPlayButtonClickTime < 250L && this.isFocused()) {
               this.play();
            }

            RealmsMainScreen.this.lastPlayButtonClickTime = Util.getMeasuringTimeMs();
         }

         return true;
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         if (KeyCodes.isToggle(keyCode)) {
            if (this.server.state == RealmsServer.State.UNINITIALIZED) {
               this.createRealm();
               return true;
            }

            if (this.server.shouldAllowPlay()) {
               this.play();
               return true;
            }
         }

         return super.keyPressed(keyCode, scanCode, modifiers);
      }

      public Text getNarration() {
         return (Text)(this.server.state == RealmsServer.State.UNINITIALIZED ? RealmsMainScreen.UNINITIALIZED_BUTTON_NARRATION : Text.translatable("narrator.select", Objects.requireNonNullElse(this.server.name, "unknown server")));
      }

      public RealmsServer getRealmsServer() {
         return this.server;
      }
   }

   @Environment(EnvType.CLIENT)
   private abstract class Entry extends AlwaysSelectedEntryListWidget.Entry {
      protected static final int field_46680 = 10;
      private static final int field_46681 = 28;
      protected static final int field_52117 = 7;
      protected static final int field_52118 = 2;

      Entry() {
      }

      protected void renderStatusIcon(RealmsServer server, DrawContext context, int x, int y, int mouseX, int mouseY) {
         int i = x - 10 - 7;
         int j = y + 2;
         if (server.expired) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.EXPIRED_STATUS_TEXTURE, () -> {
               return RealmsMainScreen.EXPIRED_TEXT;
            });
         } else if (server.state == RealmsServer.State.CLOSED) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.CLOSED_STATUS_TEXTURE, () -> {
               return RealmsMainScreen.CLOSED_TEXT;
            });
         } else if (RealmsMainScreen.isSelfOwnedServer(server) && server.daysLeft < 7) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.EXPIRES_SOON_STATUS_TEXTURE, () -> {
               if (server.daysLeft <= 0) {
                  return RealmsMainScreen.EXPIRES_SOON_TEXT;
               } else {
                  return (Text)(server.daysLeft == 1 ? RealmsMainScreen.EXPIRES_IN_A_DAY_TEXT : Text.translatable("mco.selectServer.expires.days", server.daysLeft));
               }
            });
         } else if (server.state == RealmsServer.State.OPEN) {
            this.drawTextureWithTooltip(context, i, j, mouseX, mouseY, RealmsMainScreen.OPEN_STATUS_TEXTURE, () -> {
               return RealmsMainScreen.OPEN_TEXT;
            });
         }

      }

      private void drawTextureWithTooltip(DrawContext context, int x, int y, int mouseX, int mouseY, Identifier texture, Supplier tooltip) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, 10, 28);
         if (RealmsMainScreen.this.realmSelectionList.isMouseOver((double)mouseX, (double)mouseY) && mouseX >= x && mouseX <= x + 10 && mouseY >= y && mouseY <= y + 28) {
            context.drawTooltip((Text)tooltip.get(), mouseX, mouseY);
         }

      }

      protected void drawServerNameAndVersion(DrawContext context, int y, int x, int width, int color, RealmsServer server) {
         int i = this.getNameX(x);
         int j = this.getNameY(y);
         Text text = RealmsMainScreen.getVersionText(server.activeVersion, server.isCompatible());
         int k = this.getVersionRight(x, width, text);
         this.drawTrimmedText(context, server.getName(), i, j, k, color);
         if (text != ScreenTexts.EMPTY && !server.isMinigame()) {
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, k, j, -8355712);
         }

      }

      protected void drawDescription(DrawContext context, int y, int x, int width, RealmsServer server) {
         int i = this.getNameX(x);
         int j = this.getNameY(y);
         int k = this.getDescriptionY(j);
         String string = server.getMinigameName();
         boolean bl = server.isMinigame();
         if (bl && string != null) {
            Text text = Text.literal(string).formatted(Formatting.GRAY);
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, (Text)Text.translatable("mco.selectServer.minigameName", text).withColor(-171), i, k, -1);
         } else {
            int l = this.drawGameMode(server, context, x, width, j);
            this.drawTrimmedText(context, server.getDescription(), i, this.getDescriptionY(j), l, -8355712);
         }

      }

      protected void drawOwnerOrExpiredText(DrawContext context, int y, int x, RealmsServer server) {
         int i = this.getNameX(x);
         int j = this.getNameY(y);
         int k = this.getStatusY(j);
         if (!RealmsMainScreen.isSelfOwnedServer(server)) {
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, server.owner, i, this.getStatusY(j), -8355712);
         } else if (server.expired) {
            Text text = server.expiredTrial ? RealmsMainScreen.EXPIRED_TRIAL_TEXT : RealmsMainScreen.EXPIRED_LIST_TEXT;
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, i, k, -2142128);
         }

      }

      protected void drawTrimmedText(DrawContext context, @Nullable String string, int left, int y, int right, int color) {
         if (string != null) {
            int i = right - left;
            if (RealmsMainScreen.this.textRenderer.getWidth(string) > i) {
               String string2 = RealmsMainScreen.this.textRenderer.trimToWidth(string, i - RealmsMainScreen.this.textRenderer.getWidth("... "));
               context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, string2 + "...", left, y, color);
            } else {
               context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, string, left, y, color);
            }

         }
      }

      protected int getVersionRight(int x, int width, Text version) {
         return x + width - RealmsMainScreen.this.textRenderer.getWidth((StringVisitable)version) - 20;
      }

      protected int getGameModeRight(int x, int width, Text gameMode) {
         return x + width - RealmsMainScreen.this.textRenderer.getWidth((StringVisitable)gameMode) - 20;
      }

      protected int drawGameMode(RealmsServer server, DrawContext context, int x, int entryWidth, int y) {
         boolean bl = server.hardcore;
         int i = server.gameMode;
         int j = x;
         if (GameMode.isValid(i)) {
            Text text = RealmsMainScreen.getGameModeText(i, bl);
            j = this.getGameModeRight(x, entryWidth, text);
            context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, text, j, this.getDescriptionY(y), -8355712);
         }

         if (bl) {
            j -= 10;
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, RealmsMainScreen.HARDCORE_ICON_TEXTURE, j, this.getDescriptionY(y), 8, 8);
         }

         return j;
      }

      protected int getNameY(int y) {
         return y + 1;
      }

      protected int getTextHeight() {
         Objects.requireNonNull(RealmsMainScreen.this.textRenderer);
         return 2 + 9;
      }

      protected int getNameX(int x) {
         return x + 36 + 2;
      }

      protected int getDescriptionY(int y) {
         return y + this.getTextHeight();
      }

      protected int getStatusY(int y) {
         return y + this.getTextHeight() * 2;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class CrossButton extends TexturedButtonWidget {
      private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("widget/cross_button"), Identifier.ofVanilla("widget/cross_button_highlighted"));

      protected CrossButton(ButtonWidget.PressAction onPress, Text tooltip) {
         super(0, 0, 14, 14, TEXTURES, onPress);
         this.setTooltip(Tooltip.of(tooltip));
      }
   }

   @Environment(EnvType.CLIENT)
   private class ParentRealmSelectionListEntry extends Entry {
      private final RealmsServer server;
      private final TooltipState tooltip = new TooltipState();

      public ParentRealmSelectionListEntry(final RealmsMainScreen realmsMainScreen, final RealmsServer server) {
         super();
         this.server = server;
         if (!server.expired) {
            this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.parent.tooltip")));
         }

      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         this.renderStatusIcon(this.server, context, x + entryWidth, y, mouseX, mouseY);
         RealmsUtil.drawPlayerHead(context, x, y, 32, this.server.ownerUUID);
         this.drawServerNameAndVersion(context, y, x, entryWidth, -8355712, this.server);
         this.drawDescription(context, y, x, entryWidth, this.server);
         this.drawOwnerOrExpiredText(context, y, x, this.server);
         this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
      }

      public Text getNarration() {
         return Text.literal((String)Objects.requireNonNullElse(this.server.name, "unknown server"));
      }
   }

   @Environment(EnvType.CLIENT)
   private class SnapshotEntry extends Entry {
      private static final Text START_TEXT = Text.translatable("mco.snapshot.start");
      private static final int field_46677 = 5;
      private final TooltipState tooltip = new TooltipState();
      private final RealmsServer server;

      public SnapshotEntry(final RealmsServer server) {
         super();
         this.server = server;
         this.tooltip.setTooltip(Tooltip.of(Text.translatable("mco.snapshot.tooltip")));
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, RealmsMainScreen.NEW_REALM_ICON_TEXTURE, x - 5, y + entryHeight / 2 - 10, 40, 20);
         int var10000 = y + entryHeight / 2;
         Objects.requireNonNull(RealmsMainScreen.this.textRenderer);
         int i = var10000 - 9 / 2;
         context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, START_TEXT, x + 40 - 2, i - 5, -8388737);
         context.drawTextWithShadow(RealmsMainScreen.this.textRenderer, (Text)Text.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.server.name, "unknown server")), x + 40 - 2, i + 5, -8355712);
         this.tooltip.render(context, mouseX, mouseY, hovered, this.isFocused(), new ScreenRect(x, y, entryWidth, entryHeight));
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         this.showPopup();
         return true;
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         if (KeyCodes.isToggle(keyCode)) {
            this.showPopup();
            return false;
         } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
         }
      }

      private void showPopup() {
         RealmsMainScreen.this.client.getSoundManager().play(PositionedSoundInstance.master((RegistryEntry)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         RealmsMainScreen.this.client.setScreen((new PopupScreen.Builder(RealmsMainScreen.this, Text.translatable("mco.snapshot.createSnapshotPopup.title"))).message(Text.translatable("mco.snapshot.createSnapshotPopup.text")).button(Text.translatable("mco.selectServer.create"), (screen) -> {
            RealmsMainScreen.this.client.setScreen(new RealmsCreateRealmScreen(RealmsMainScreen.this, this.server, true));
         }).button(ScreenTexts.CANCEL, PopupScreen::close).build());
      }

      public Text getNarration() {
         return Text.translatable("gui.narrate.button", ScreenTexts.joinSentences(START_TEXT, Text.translatable("mco.snapshot.description", Objects.requireNonNullElse(this.server.name, "unknown server"))));
      }
   }

   @Environment(EnvType.CLIENT)
   private class VisitButtonEntry extends Entry {
      private final ButtonWidget button;

      public VisitButtonEntry(final ButtonWidget button) {
         super();
         this.button = button;
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         this.button.mouseClicked(mouseX, mouseY, button);
         return super.mouseClicked(mouseX, mouseY, button);
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         return this.button.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         this.button.setPosition(RealmsMainScreen.this.width / 2 - 75, y + 4);
         this.button.render(context, mouseX, mouseY, tickProgress);
      }

      public void setFocused(boolean focused) {
         super.setFocused(focused);
         this.button.setFocused(focused);
      }

      public Text getNarration() {
         return this.button.getMessage();
      }
   }

   @Environment(EnvType.CLIENT)
   class EmptyEntry extends Entry {
      EmptyEntry(final RealmsMainScreen realmsMainScreen) {
         super();
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
      }

      public Text getNarration() {
         return Text.empty();
      }
   }

   @Environment(EnvType.CLIENT)
   private class VisitUrlNotification extends Entry {
      private static final int field_43002 = 40;
      private static final int field_43004 = -12303292;
      private final Text message;
      private final int lines;
      private final List gridChildren = new ArrayList();
      @Nullable
      private final CrossButton dismissButton;
      private final MultilineTextWidget textWidget;
      private final GridWidget grid;
      private final SimplePositioningWidget textGrid;
      private int width = -1;

      public VisitUrlNotification(final Text message, final int lines, final RealmsNotification notification) {
         super();
         this.message = message;
         this.lines = lines;
         this.grid = new GridWidget();
         int i = true;
         this.grid.add(IconWidget.create(20, 20, RealmsMainScreen.INFO_ICON_TEXTURE), 0, 0, (Positioner)this.grid.copyPositioner().margin(7, 7, 0, 0));
         this.grid.add(EmptyWidget.ofWidth(40), 0, 0);
         GridWidget var10001 = this.grid;
         Objects.requireNonNull(RealmsMainScreen.this.textRenderer);
         this.textGrid = (SimplePositioningWidget)var10001.add(new SimplePositioningWidget(0, 9 * 3 * (lines - 1)), 0, 1, (Positioner)this.grid.copyPositioner().marginTop(7));
         this.textWidget = (MultilineTextWidget)this.textGrid.add((new MultilineTextWidget(message, RealmsMainScreen.this.textRenderer)).setCentered(true), (Positioner)this.textGrid.copyPositioner().alignHorizontalCenter().alignTop());
         this.grid.add(EmptyWidget.ofWidth(40), 0, 2);
         if (notification.isDismissable()) {
            this.dismissButton = (CrossButton)this.grid.add(new CrossButton((button) -> {
               RealmsMainScreen.this.dismissNotification(notification.getUuid());
            }, Text.translatable("mco.notification.dismiss")), 0, 2, (Positioner)this.grid.copyPositioner().alignRight().margin(0, 7, 7, 0));
         } else {
            this.dismissButton = null;
         }

         GridWidget var10000 = this.grid;
         List var6 = this.gridChildren;
         Objects.requireNonNull(var6);
         var10000.forEachChild(var6::add);
      }

      public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
         return this.dismissButton != null && this.dismissButton.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
      }

      private void setWidth(int width) {
         if (this.width != width) {
            this.updateWidth(width);
            this.width = width;
         }

      }

      private void updateWidth(int width) {
         int i = width - 80;
         this.textGrid.setMinWidth(i);
         this.textWidget.setMaxWidth(i);
         this.grid.refreshPositions();
      }

      public void drawBorder(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         super.drawBorder(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickProgress);
         context.drawBorder(x - 2, y - 2, entryWidth, 36 * this.lines - 2, -12303292);
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         this.grid.setPosition(x, y);
         this.setWidth(entryWidth - 4);
         this.gridChildren.forEach((child) -> {
            child.render(context, mouseX, mouseY, tickProgress);
         });
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (this.dismissButton != null) {
            this.dismissButton.mouseClicked(mouseX, mouseY, button);
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }

      public Text getNarration() {
         return this.message;
      }
   }
}
