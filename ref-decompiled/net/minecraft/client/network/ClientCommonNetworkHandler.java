package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogScreens;
import net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ClearDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.common.CustomReportDetailsS2CPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerLinksS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.thread.ThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class ClientCommonNetworkHandler implements ClientCommonPacketListener {
   private static final Text LOST_CONNECTION_TEXT = Text.translatable("disconnect.lost");
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final MinecraftClient client;
   protected final ClientConnection connection;
   @Nullable
   protected final ServerInfo serverInfo;
   @Nullable
   protected String brand;
   protected final WorldSession worldSession;
   @Nullable
   protected final Screen postDisconnectScreen;
   protected boolean transferring;
   private final List queuedPackets = new ArrayList();
   protected final Map serverCookies;
   protected Map customReportDetails;
   private ServerLinks serverLinks;

   protected ClientCommonNetworkHandler(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
      this.client = client;
      this.connection = connection;
      this.serverInfo = connectionState.serverInfo();
      this.brand = connectionState.serverBrand();
      this.worldSession = connectionState.worldSession();
      this.postDisconnectScreen = connectionState.postDisconnectScreen();
      this.serverCookies = connectionState.serverCookies();
      this.customReportDetails = connectionState.customReportDetails();
      this.serverLinks = connectionState.serverLinks();
   }

   public ServerLinks getServerLinks() {
      return this.serverLinks;
   }

   public void onPacketException(Packet packet, Exception exception) {
      LOGGER.error("Failed to handle packet {}, disconnecting", packet, exception);
      Optional optional = this.savePacketErrorReport(packet, exception);
      Optional optional2 = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT).map(ServerLinks.Entry::link);
      this.connection.disconnect(new DisconnectionInfo(Text.translatable("disconnect.packetError"), optional, optional2));
   }

   public DisconnectionInfo createDisconnectionInfo(Text reason, Throwable exception) {
      Optional optional = this.savePacketErrorReport((Packet)null, exception);
      Optional optional2 = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT).map(ServerLinks.Entry::link);
      return new DisconnectionInfo(reason, optional, optional2);
   }

   private Optional savePacketErrorReport(@Nullable Packet packet, Throwable exception) {
      CrashReport crashReport = CrashReport.create(exception, "Packet handling error");
      NetworkThreadUtils.fillCrashReport(crashReport, this, packet);
      Path path = this.client.runDirectory.toPath().resolve("debug");
      Path path2 = path.resolve("disconnect-" + Util.getFormattedCurrentTime() + "-client.txt");
      Optional optional = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT);
      List list = (List)optional.map((bugReportEntry) -> {
         return List.of("Server bug reporting link: " + String.valueOf(bugReportEntry.link()));
      }).orElse(List.of());
      return crashReport.writeToFile(path2, ReportType.MINECRAFT_NETWORK_PROTOCOL_ERROR_REPORT, list) ? Optional.of(path2) : Optional.empty();
   }

   public boolean accepts(Packet packet) {
      if (ClientCommonPacketListener.super.accepts(packet)) {
         return true;
      } else {
         return this.transferring && (packet instanceof StoreCookieS2CPacket || packet instanceof ServerTransferS2CPacket);
      }
   }

   public void onKeepAlive(KeepAliveS2CPacket packet) {
      this.send(new KeepAliveC2SPacket(packet.getId()), () -> {
         return !RenderSystem.isFrozenAtPollEvents();
      }, Duration.ofMinutes(1L));
   }

   public void onPing(CommonPingS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.sendPacket(new CommonPongC2SPacket(packet.getParameter()));
   }

   public void onCustomPayload(CustomPayloadS2CPacket packet) {
      CustomPayload customPayload = packet.payload();
      if (!(customPayload instanceof UnknownCustomPayload)) {
         NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
         if (customPayload instanceof BrandCustomPayload) {
            BrandCustomPayload brandCustomPayload = (BrandCustomPayload)customPayload;
            this.brand = brandCustomPayload.brand();
            this.worldSession.setBrand(brandCustomPayload.brand());
         } else {
            this.onCustomPayload(customPayload);
         }

      }
   }

   protected abstract void onCustomPayload(CustomPayload payload);

   public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      UUID uUID = packet.id();
      URL uRL = getParsedResourcePackUrl(packet.url());
      if (uRL == null) {
         this.connection.send(new ResourcePackStatusC2SPacket(uUID, ResourcePackStatusC2SPacket.Status.INVALID_URL));
      } else {
         String string = packet.hash();
         boolean bl = packet.required();
         ServerInfo.ResourcePackPolicy resourcePackPolicy = this.serverInfo != null ? this.serverInfo.getResourcePackPolicy() : ServerInfo.ResourcePackPolicy.PROMPT;
         if (resourcePackPolicy != ServerInfo.ResourcePackPolicy.PROMPT && (!bl || resourcePackPolicy != ServerInfo.ResourcePackPolicy.DISABLED)) {
            this.client.getServerResourcePackProvider().addResourcePack(uUID, uRL, string);
         } else {
            this.client.setScreen(this.createConfirmServerResourcePackScreen(uUID, uRL, string, bl, (Text)packet.prompt().orElse((Object)null)));
         }

      }
   }

   public void onResourcePackRemove(ResourcePackRemoveS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      packet.id().ifPresentOrElse((id) -> {
         this.client.getServerResourcePackProvider().remove(id);
      }, () -> {
         this.client.getServerResourcePackProvider().removeAll();
      });
   }

   static Text getPrompt(Text requirementPrompt, @Nullable Text customPrompt) {
      return (Text)(customPrompt == null ? requirementPrompt : Text.translatable("multiplayer.texturePrompt.serverPrompt", requirementPrompt, customPrompt));
   }

   @Nullable
   private static URL getParsedResourcePackUrl(String url) {
      try {
         URL uRL = new URL(url);
         String string = uRL.getProtocol();
         return !"http".equals(string) && !"https".equals(string) ? null : uRL;
      } catch (MalformedURLException var3) {
         return null;
      }
   }

   public void onCookieRequest(CookieRequestS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.connection.send(new CookieResponseC2SPacket(packet.key(), (byte[])this.serverCookies.get(packet.key())));
   }

   public void onStoreCookie(StoreCookieS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.serverCookies.put(packet.key(), packet.payload());
   }

   public void onCustomReportDetails(CustomReportDetailsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.customReportDetails = packet.details();
   }

   public void onServerLinks(ServerLinksS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      List list = packet.links();
      ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(list.size());
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         ServerLinks.StringifiedEntry stringifiedEntry = (ServerLinks.StringifiedEntry)var4.next();

         try {
            URI uRI = Util.validateUri(stringifiedEntry.link());
            builder.add(new ServerLinks.Entry(stringifiedEntry.type(), uRI));
         } catch (Exception var7) {
            LOGGER.warn("Received invalid link for type {}:{}", new Object[]{stringifiedEntry.type(), stringifiedEntry.link(), var7});
         }
      }

      this.serverLinks = new ServerLinks(builder.build());
   }

   public void onShowDialog(ShowDialogS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.showDialog(packet.dialog(), this.client.currentScreen);
   }

   protected abstract DialogNetworkAccess createDialogNetworkAccess();

   public void showDialog(RegistryEntry dialog, @Nullable Screen previousScreen) {
      this.showDialog(dialog, this.createDialogNetworkAccess(), previousScreen);
   }

   protected void showDialog(RegistryEntry dialog, DialogNetworkAccess networkAccess, @Nullable Screen previousScreen) {
      if (previousScreen instanceof DialogScreen.WarningScreen warningScreen) {
         Screen screen = warningScreen.getDialogScreen();
         Screen var10000;
         if (screen instanceof DialogScreen dialogScreen) {
            var10000 = dialogScreen.getParentScreen();
         } else {
            var10000 = screen;
         }

         Screen screen2 = var10000;
         dialogScreen = DialogScreens.create((Dialog)dialog.value(), screen2, networkAccess);
         if (dialogScreen != null) {
            warningScreen.setDialogScreen(dialogScreen);
         } else {
            LOGGER.warn("Failed to show dialog for data {}", dialog);
         }

      } else {
         Screen screen3;
         if (previousScreen instanceof DialogScreen screen) {
            screen3 = screen.getParentScreen();
         } else if (previousScreen instanceof WaitingForResponseScreen waitingForResponseScreen) {
            screen3 = waitingForResponseScreen.getParentScreen();
         } else {
            screen3 = previousScreen;
         }

         screen = DialogScreens.create((Dialog)dialog.value(), screen3, networkAccess);
         if (screen != null) {
            this.client.setScreen(screen);
         } else {
            LOGGER.warn("Failed to show dialog for data {}", dialog);
         }

      }
   }

   public void onClearDialog(ClearDialogS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.clearDialog();
   }

   public void clearDialog() {
      Screen screen = this.client.currentScreen;
      if (screen instanceof DialogScreen.WarningScreen warningScreen) {
         screen = warningScreen.getDialogScreen();
         if (screen instanceof DialogScreen dialogScreen) {
            warningScreen.setDialogScreen(dialogScreen.getParentScreen());
         }
      } else {
         screen = this.client.currentScreen;
         if (screen instanceof DialogScreen dialogScreen2) {
            this.client.setScreen(dialogScreen2.getParentScreen());
         }
      }

   }

   public void onServerTransfer(ServerTransferS2CPacket packet) {
      this.transferring = true;
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (this.serverInfo == null) {
         throw new IllegalStateException("Cannot transfer to server from singleplayer");
      } else {
         this.connection.disconnect((Text)Text.translatable("disconnect.transfer"));
         this.connection.tryDisableAutoRead();
         this.connection.handleDisconnection();
         ServerAddress serverAddress = new ServerAddress(packet.host(), packet.port());
         ConnectScreen.connect((Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, TitleScreen::new), this.client, serverAddress, this.serverInfo, false, new CookieStorage(this.serverCookies));
      }
   }

   public void onDisconnect(DisconnectS2CPacket packet) {
      this.connection.disconnect(packet.reason());
   }

   protected void sendQueuedPackets() {
      Iterator iterator = this.queuedPackets.iterator();

      while(iterator.hasNext()) {
         QueuedPacket queuedPacket = (QueuedPacket)iterator.next();
         if (queuedPacket.sendCondition().getAsBoolean()) {
            this.sendPacket(queuedPacket.packet);
            iterator.remove();
         } else if (queuedPacket.expirationTime() <= Util.getMeasuringTimeMs()) {
            iterator.remove();
         }
      }

   }

   public void sendPacket(Packet packet) {
      this.connection.send(packet);
   }

   public void onDisconnected(DisconnectionInfo info) {
      this.worldSession.onUnload();
      this.client.disconnect(this.createDisconnectedScreen(info), this.transferring);
      LOGGER.warn("Client disconnected with reason: {}", info.reason().getString());
   }

   public void addCustomCrashReportInfo(CrashReport report, CrashReportSection section) {
      section.add("Is Local", () -> {
         return String.valueOf(this.connection.isLocal());
      });
      section.add("Server type", () -> {
         return this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<none>";
      });
      section.add("Server brand", () -> {
         return this.brand;
      });
      if (!this.customReportDetails.isEmpty()) {
         CrashReportSection crashReportSection = report.addElement("Custom Server Details");
         Map var10000 = this.customReportDetails;
         Objects.requireNonNull(crashReportSection);
         var10000.forEach(crashReportSection::add);
      }

   }

   protected Screen createDisconnectedScreen(DisconnectionInfo info) {
      Screen screen = (Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> {
         return new MultiplayerScreen(new TitleScreen());
      });
      return this.serverInfo != null && this.serverInfo.isRealm() ? new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, info, ScreenTexts.BACK) : new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, info);
   }

   @Nullable
   public String getBrand() {
      return this.brand;
   }

   private void send(Packet packet, BooleanSupplier sendCondition, Duration expiry) {
      if (sendCondition.getAsBoolean()) {
         this.sendPacket(packet);
      } else {
         this.queuedPackets.add(new QueuedPacket(packet, sendCondition, Util.getMeasuringTimeMs() + expiry.toMillis()));
      }

   }

   private Screen createConfirmServerResourcePackScreen(UUID id, URL url, String hash, boolean required, @Nullable Text prompt) {
      Screen screen = this.client.currentScreen;
      if (screen instanceof ConfirmServerResourcePackScreen confirmServerResourcePackScreen) {
         return confirmServerResourcePackScreen.add(this.client, id, url, hash, required, prompt);
      } else {
         return new ConfirmServerResourcePackScreen(this.client, screen, List.of(new ConfirmServerResourcePackScreen.Pack(id, url, hash)), required, prompt);
      }
   }

   @Environment(EnvType.CLIENT)
   static record QueuedPacket(Packet packet, BooleanSupplier sendCondition, long expirationTime) {
      final Packet packet;

      QueuedPacket(Packet packet, BooleanSupplier booleanSupplier, long l) {
         this.packet = packet;
         this.sendCondition = booleanSupplier;
         this.expirationTime = l;
      }

      public Packet packet() {
         return this.packet;
      }

      public BooleanSupplier sendCondition() {
         return this.sendCondition;
      }

      public long expirationTime() {
         return this.expirationTime;
      }
   }

   @Environment(EnvType.CLIENT)
   private class ConfirmServerResourcePackScreen extends ConfirmScreen {
      private final List packs;
      @Nullable
      private final Screen parent;

      ConfirmServerResourcePackScreen(final MinecraftClient client, @Nullable final Screen parent, final List pack, final boolean required, @Nullable final Text prompt) {
         super((confirmed) -> {
            client.setScreen(parent);
            ServerResourcePackLoader serverResourcePackLoader = client.getServerResourcePackProvider();
            if (confirmed) {
               if (ClientCommonNetworkHandler.this.serverInfo != null) {
                  ClientCommonNetworkHandler.this.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
               }

               serverResourcePackLoader.acceptAll();
            } else {
               serverResourcePackLoader.declineAll();
               if (required) {
                  ClientCommonNetworkHandler.this.connection.disconnect((Text)Text.translatable("multiplayer.requiredTexturePrompt.disconnect"));
               } else if (ClientCommonNetworkHandler.this.serverInfo != null) {
                  ClientCommonNetworkHandler.this.serverInfo.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.DISABLED);
               }
            }

            Iterator var7 = pack.iterator();

            while(var7.hasNext()) {
               Pack packx = (Pack)var7.next();
               serverResourcePackLoader.addResourcePack(packx.id, packx.url, packx.hash);
            }

            if (ClientCommonNetworkHandler.this.serverInfo != null) {
               ServerList.updateServerListEntry(ClientCommonNetworkHandler.this.serverInfo);
            }

         }, required ? Text.translatable("multiplayer.requiredTexturePrompt.line1") : Text.translatable("multiplayer.texturePrompt.line1"), ClientCommonNetworkHandler.getPrompt(required ? Text.translatable("multiplayer.requiredTexturePrompt.line2").formatted(Formatting.YELLOW, Formatting.BOLD) : Text.translatable("multiplayer.texturePrompt.line2"), prompt), required ? ScreenTexts.PROCEED : ScreenTexts.YES, required ? ScreenTexts.DISCONNECT : ScreenTexts.NO);
         this.packs = pack;
         this.parent = parent;
      }

      public ConfirmServerResourcePackScreen add(MinecraftClient client, UUID id, URL url, String hash, boolean required, @Nullable Text prompt) {
         List list = ImmutableList.builderWithExpectedSize(this.packs.size() + 1).addAll(this.packs).add(new Pack(id, url, hash)).build();
         return ClientCommonNetworkHandler.this.new ConfirmServerResourcePackScreen(client, this.parent, list, required, prompt);
      }

      @Environment(EnvType.CLIENT)
      static record Pack(UUID id, URL url, String hash) {
         final UUID id;
         final URL url;
         final String hash;

         Pack(UUID uUID, URL uRL, String string) {
            this.id = uUID;
            this.url = uRL;
            this.hash = string;
         }

         public UUID id() {
            return this.id;
         }

         public URL url() {
            return this.url;
         }

         public String hash() {
            return this.hash;
         }
      }
   }
}
