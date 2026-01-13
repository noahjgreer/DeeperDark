/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.DisconnectedScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TitleScreen
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.dialog.DialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogScreen$WarningScreen
 *  net.minecraft.client.gui.screen.dialog.DialogScreens
 *  net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen
 *  net.minecraft.client.gui.screen.multiplayer.ConnectScreen
 *  net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
 *  net.minecraft.client.network.ClientCommonNetworkHandler
 *  net.minecraft.client.network.ClientCommonNetworkHandler$ConfirmServerResourcePackScreen
 *  net.minecraft.client.network.ClientCommonNetworkHandler$ConfirmServerResourcePackScreen$Pack
 *  net.minecraft.client.network.ClientCommonNetworkHandler$QueuedPacket
 *  net.minecraft.client.network.ClientConnectionState
 *  net.minecraft.client.network.CookieStorage
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.network.ServerInfo$ResourcePackPolicy
 *  net.minecraft.client.session.telemetry.WorldSession
 *  net.minecraft.dialog.type.Dialog
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.DisconnectionInfo
 *  net.minecraft.network.NetworkThreadUtils
 *  net.minecraft.network.PacketApplyBatcher
 *  net.minecraft.network.listener.ClientCommonPacketListener
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.listener.ServerPacketListener
 *  net.minecraft.network.packet.BrandCustomPayload
 *  net.minecraft.network.packet.CustomPayload
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.UnknownCustomPayload
 *  net.minecraft.network.packet.c2s.common.CommonPongC2SPacket
 *  net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket
 *  net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket
 *  net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket
 *  net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket$Status
 *  net.minecraft.network.packet.s2c.common.ClearDialogS2CPacket
 *  net.minecraft.network.packet.s2c.common.CommonPingS2CPacket
 *  net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket
 *  net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket
 *  net.minecraft.network.packet.s2c.common.CustomReportDetailsS2CPacket
 *  net.minecraft.network.packet.s2c.common.DisconnectS2CPacket
 *  net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket
 *  net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket
 *  net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket
 *  net.minecraft.network.packet.s2c.common.ServerLinksS2CPacket
 *  net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket
 *  net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket
 *  net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.ServerLinks
 *  net.minecraft.server.ServerLinks$Entry
 *  net.minecraft.server.ServerLinks$Known
 *  net.minecraft.server.ServerLinks$StringifiedEntry
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.crash.ReportType
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
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
import java.util.HashMap;
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
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.DialogScreens;
import net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketApplyBatcher;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerPacketListener;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class ClientCommonNetworkHandler
implements ClientCommonPacketListener {
    private static final Text LOST_CONNECTION_TEXT = Text.translatable((String)"disconnect.lost");
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final MinecraftClient client;
    protected final ClientConnection connection;
    protected final @Nullable ServerInfo serverInfo;
    protected @Nullable String brand;
    protected final WorldSession worldSession;
    protected final @Nullable Screen postDisconnectScreen;
    protected boolean transferring;
    private final List<QueuedPacket> queuedPackets = new ArrayList();
    protected final Map<Identifier, byte[]> serverCookies;
    protected Map<String, String> customReportDetails;
    private ServerLinks serverLinks;
    protected final Map<UUID, PlayerListEntry> seenPlayers;
    protected boolean seenInsecureChatWarning;

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
        this.seenPlayers = new HashMap(connectionState.seenPlayers());
        this.seenInsecureChatWarning = connectionState.seenInsecureChatWarning();
    }

    public ServerLinks getServerLinks() {
        return this.serverLinks;
    }

    public void onPacketException(Packet packet, Exception exception) {
        LOGGER.error("Failed to handle packet {}, disconnecting", (Object)packet, (Object)exception);
        Optional optional = this.savePacketErrorReport(packet, (Throwable)exception);
        Optional<URI> optional2 = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT).map(ServerLinks.Entry::link);
        this.connection.disconnect(new DisconnectionInfo((Text)Text.translatable((String)"disconnect.packetError"), optional, optional2));
    }

    public DisconnectionInfo createDisconnectionInfo(Text reason, Throwable exception) {
        Optional optional = this.savePacketErrorReport(null, exception);
        Optional<URI> optional2 = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT).map(ServerLinks.Entry::link);
        return new DisconnectionInfo(reason, optional, optional2);
    }

    private Optional<Path> savePacketErrorReport(@Nullable Packet packet, Throwable exception) {
        CrashReport crashReport = CrashReport.create((Throwable)exception, (String)"Packet handling error");
        NetworkThreadUtils.fillCrashReport((CrashReport)crashReport, (PacketListener)this, (Packet)packet);
        Path path = this.client.runDirectory.toPath().resolve("debug");
        Path path2 = path.resolve("disconnect-" + Util.getFormattedCurrentTime() + "-client.txt");
        Optional optional = this.serverLinks.getEntryFor(ServerLinks.Known.BUG_REPORT);
        List list = optional.map(bugReportEntry -> List.of("Server bug reporting link: " + String.valueOf(bugReportEntry.link()))).orElse(List.of());
        if (crashReport.writeToFile(path2, ReportType.MINECRAFT_NETWORK_PROTOCOL_ERROR_REPORT, list)) {
            return Optional.of(path2);
        }
        return Optional.empty();
    }

    public boolean accepts(Packet<?> packet) {
        if (super.accepts(packet)) {
            return true;
        }
        return this.transferring && (packet instanceof StoreCookieS2CPacket || packet instanceof ServerTransferS2CPacket);
    }

    public void onKeepAlive(KeepAliveS2CPacket packet) {
        this.send((Packet)new KeepAliveC2SPacket(packet.getId()), () -> !RenderSystem.isFrozenAtPollEvents(), Duration.ofMinutes(1L));
    }

    public void onPing(CommonPingS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.sendPacket((Packet)new CommonPongC2SPacket(packet.getParameter()));
    }

    public void onCustomPayload(CustomPayloadS2CPacket packet) {
        CustomPayload customPayload = packet.payload();
        if (customPayload instanceof UnknownCustomPayload) {
            return;
        }
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (customPayload instanceof BrandCustomPayload) {
            BrandCustomPayload brandCustomPayload = (BrandCustomPayload)customPayload;
            this.brand = brandCustomPayload.brand();
            this.worldSession.setBrand(brandCustomPayload.brand());
        } else {
            this.onCustomPayload(customPayload);
        }
    }

    protected abstract void onCustomPayload(CustomPayload var1);

    public void onResourcePackSend(ResourcePackSendS2CPacket packet) {
        ServerInfo.ResourcePackPolicy resourcePackPolicy;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        UUID uUID = packet.id();
        URL uRL = ClientCommonNetworkHandler.getParsedResourcePackUrl((String)packet.url());
        if (uRL == null) {
            this.connection.send((Packet)new ResourcePackStatusC2SPacket(uUID, ResourcePackStatusC2SPacket.Status.INVALID_URL));
            return;
        }
        String string = packet.hash();
        boolean bl = packet.required();
        ServerInfo.ResourcePackPolicy resourcePackPolicy2 = resourcePackPolicy = this.serverInfo != null ? this.serverInfo.getResourcePackPolicy() : ServerInfo.ResourcePackPolicy.PROMPT;
        if (resourcePackPolicy == ServerInfo.ResourcePackPolicy.PROMPT || bl && resourcePackPolicy == ServerInfo.ResourcePackPolicy.DISABLED) {
            this.client.setScreen(this.createConfirmServerResourcePackScreen(uUID, uRL, string, bl, (Text)packet.prompt().orElse(null)));
        } else {
            this.client.getServerResourcePackProvider().addResourcePack(uUID, uRL, string);
        }
    }

    public void onResourcePackRemove(ResourcePackRemoveS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        packet.id().ifPresentOrElse(id -> this.client.getServerResourcePackProvider().remove(id), () -> this.client.getServerResourcePackProvider().removeAll());
    }

    static Text getPrompt(Text requirementPrompt, @Nullable Text customPrompt) {
        if (customPrompt == null) {
            return requirementPrompt;
        }
        return Text.translatable((String)"multiplayer.texturePrompt.serverPrompt", (Object[])new Object[]{requirementPrompt, customPrompt});
    }

    private static @Nullable URL getParsedResourcePackUrl(String url) {
        try {
            URL uRL = new URL(url);
            String string = uRL.getProtocol();
            if ("http".equals(string) || "https".equals(string)) {
                return uRL;
            }
        }
        catch (MalformedURLException malformedURLException) {
            return null;
        }
        return null;
    }

    public void onCookieRequest(CookieRequestS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.connection.send((Packet)new CookieResponseC2SPacket(packet.key(), (byte[])this.serverCookies.get(packet.key())));
    }

    public void onStoreCookie(StoreCookieS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.serverCookies.put(packet.key(), packet.payload());
    }

    public void onCustomReportDetails(CustomReportDetailsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.customReportDetails = packet.details();
    }

    public void onServerLinks(ServerLinksS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        List list = packet.links();
        ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize((int)list.size());
        for (ServerLinks.StringifiedEntry stringifiedEntry : list) {
            try {
                URI uRI = Util.validateUri((String)stringifiedEntry.link());
                builder.add((Object)new ServerLinks.Entry(stringifiedEntry.type(), uRI));
            }
            catch (Exception exception) {
                LOGGER.warn("Received invalid link for type {}:{}", new Object[]{stringifiedEntry.type(), stringifiedEntry.link(), exception});
            }
        }
        this.serverLinks = new ServerLinks((List)builder.build());
    }

    public void onShowDialog(ShowDialogS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.showDialog(packet.dialog(), this.client.currentScreen);
    }

    protected abstract DialogNetworkAccess createDialogNetworkAccess();

    public void showDialog(RegistryEntry<Dialog> dialog, @Nullable Screen previousScreen) {
        this.showDialog(dialog, this.createDialogNetworkAccess(), previousScreen);
    }

    protected void showDialog(RegistryEntry<Dialog> dialog, DialogNetworkAccess networkAccess, @Nullable Screen previousScreen) {
        Screen screen3;
        if (previousScreen instanceof DialogScreen.WarningScreen) {
            Screen screen;
            DialogScreen dialogScreen;
            DialogScreen.WarningScreen warningScreen = (DialogScreen.WarningScreen)previousScreen;
            Screen screen2 = warningScreen.getDialogScreen();
            if (screen2 instanceof DialogScreen) {
                dialogScreen = (DialogScreen)screen2;
                screen = dialogScreen.getParentScreen();
            } else {
                screen = screen2;
            }
            Screen screen22 = screen;
            dialogScreen = DialogScreens.create((Dialog)((Dialog)dialog.value()), (Screen)screen22, (DialogNetworkAccess)networkAccess);
            if (dialogScreen != null) {
                warningScreen.setDialogScreen((Screen)dialogScreen);
            } else {
                LOGGER.warn("Failed to show dialog for data {}", dialog);
            }
            return;
        }
        if (previousScreen instanceof DialogScreen) {
            DialogScreen dialogScreen2 = (DialogScreen)previousScreen;
            screen3 = dialogScreen2.getParentScreen();
        } else if (previousScreen instanceof WaitingForResponseScreen) {
            WaitingForResponseScreen waitingForResponseScreen = (WaitingForResponseScreen)previousScreen;
            screen3 = waitingForResponseScreen.getParentScreen();
        } else {
            screen3 = previousScreen;
        }
        DialogScreen screen = DialogScreens.create((Dialog)((Dialog)dialog.value()), (Screen)screen3, (DialogNetworkAccess)networkAccess);
        if (screen != null) {
            this.client.setScreen((Screen)screen);
        } else {
            LOGGER.warn("Failed to show dialog for data {}", dialog);
        }
    }

    public void onClearDialog(ClearDialogS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.clearDialog();
    }

    public void clearDialog() {
        Screen screen = this.client.currentScreen;
        if (screen instanceof DialogScreen.WarningScreen) {
            DialogScreen.WarningScreen warningScreen = (DialogScreen.WarningScreen)screen;
            Screen screen2 = warningScreen.getDialogScreen();
            if (screen2 instanceof DialogScreen) {
                DialogScreen dialogScreen = (DialogScreen)screen2;
                warningScreen.setDialogScreen(dialogScreen.getParentScreen());
            }
        } else {
            screen = this.client.currentScreen;
            if (screen instanceof DialogScreen) {
                DialogScreen dialogScreen2 = (DialogScreen)screen;
                this.client.setScreen(dialogScreen2.getParentScreen());
            }
        }
    }

    public void onServerTransfer(ServerTransferS2CPacket packet) {
        this.transferring = true;
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (this.serverInfo == null) {
            throw new IllegalStateException("Cannot transfer to server from singleplayer");
        }
        this.connection.disconnect((Text)Text.translatable((String)"disconnect.transfer"));
        this.connection.tryDisableAutoRead();
        this.connection.handleDisconnection();
        ServerAddress serverAddress = new ServerAddress(packet.host(), packet.port());
        ConnectScreen.connect((Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, TitleScreen::new), (MinecraftClient)this.client, (ServerAddress)serverAddress, (ServerInfo)this.serverInfo, (boolean)false, (CookieStorage)new CookieStorage(this.serverCookies, this.seenPlayers, this.seenInsecureChatWarning));
    }

    public void onDisconnect(DisconnectS2CPacket packet) {
        this.connection.disconnect(packet.reason());
    }

    protected void sendQueuedPackets() {
        Iterator iterator = this.queuedPackets.iterator();
        while (iterator.hasNext()) {
            QueuedPacket queuedPacket = (QueuedPacket)iterator.next();
            if (queuedPacket.sendCondition().getAsBoolean()) {
                this.sendPacket(queuedPacket.packet);
                iterator.remove();
                continue;
            }
            if (queuedPacket.expirationTime() > Util.getMeasuringTimeMs()) continue;
            iterator.remove();
        }
    }

    public void sendPacket(Packet<?> packet) {
        this.connection.send(packet);
    }

    public void onDisconnected(DisconnectionInfo info) {
        this.worldSession.onUnload();
        this.client.disconnect(this.createDisconnectedScreen(info), this.transferring);
        LOGGER.warn("Client disconnected with reason: {}", (Object)info.reason().getString());
    }

    public void addCustomCrashReportInfo(CrashReport report, CrashReportSection section) {
        section.add("Is Local", () -> String.valueOf(this.connection.isLocal()));
        section.add("Server type", () -> this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<none>");
        section.add("Server brand", () -> this.brand);
        if (!this.customReportDetails.isEmpty()) {
            CrashReportSection crashReportSection = report.addElement("Custom Server Details");
            this.customReportDetails.forEach((arg_0, arg_1) -> ((CrashReportSection)crashReportSection).add(arg_0, arg_1));
        }
    }

    protected Screen createDisconnectedScreen(DisconnectionInfo info) {
        Screen screen = Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> this.serverInfo != null ? new MultiplayerScreen((Screen)new TitleScreen()) : new TitleScreen());
        if (this.serverInfo != null && this.serverInfo.isRealm()) {
            return new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, info, ScreenTexts.BACK);
        }
        return new DisconnectedScreen(screen, LOST_CONNECTION_TEXT, info);
    }

    public @Nullable String getBrand() {
        return this.brand;
    }

    private void send(Packet<? extends ServerPacketListener> packet, BooleanSupplier sendCondition, Duration expiry) {
        if (sendCondition.getAsBoolean()) {
            this.sendPacket(packet);
        } else {
            this.queuedPackets.add(new QueuedPacket(packet, sendCondition, Util.getMeasuringTimeMs() + expiry.toMillis()));
        }
    }

    private Screen createConfirmServerResourcePackScreen(UUID id, URL url, String hash, boolean required, @Nullable Text prompt) {
        Screen screen = this.client.currentScreen;
        if (screen instanceof ConfirmServerResourcePackScreen) {
            ConfirmServerResourcePackScreen confirmServerResourcePackScreen = (ConfirmServerResourcePackScreen)screen;
            return confirmServerResourcePackScreen.add(this.client, id, url, hash, required, prompt);
        }
        return new ConfirmServerResourcePackScreen(this, this.client, screen, List.of(new ConfirmServerResourcePackScreen.Pack(id, url, hash)), required, prompt);
    }
}

