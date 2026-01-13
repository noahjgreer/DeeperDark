/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.exceptions.ForcedUsernameChangeException
 *  com.mojang.authlib.exceptions.InsufficientPrivilegesException
 *  com.mojang.authlib.exceptions.InvalidCredentialsException
 *  com.mojang.authlib.exceptions.UserBannedException
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.ClientBrandRetriever
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.DisconnectedScreen
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.network.ClientConfigurationNetworkHandler
 *  net.minecraft.client.network.ClientConnectionState
 *  net.minecraft.client.network.ClientDynamicRegistryType
 *  net.minecraft.client.network.ClientLoginNetworkHandler
 *  net.minecraft.client.network.ClientLoginNetworkHandler$State
 *  net.minecraft.client.network.CookieStorage
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.world.ClientChunkLoadProgress
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.DisconnectionInfo
 *  net.minecraft.network.PacketCallbacks
 *  net.minecraft.network.encryption.NetworkEncryptionUtils
 *  net.minecraft.network.listener.ClientLoginPacketListener
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.packet.BrandCustomPayload
 *  net.minecraft.network.packet.CustomPayload
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket
 *  net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket
 *  net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket
 *  net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket
 *  net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket
 *  net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket
 *  net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket
 *  net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket
 *  net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket
 *  net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket
 *  net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket
 *  net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket
 *  net.minecraft.network.state.ConfigurationStates
 *  net.minecraft.resource.featuretoggle.FeatureFlags
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.server.ServerLinks
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.ForcedUsernameChangeException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.Key;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientConfigurationNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ClientOptionsC2SPacket;
import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientLoginNetworkHandler
implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final MinecraftClient client;
    private final @Nullable ServerInfo serverInfo;
    private final @Nullable Screen parentScreen;
    private final Consumer<Text> statusConsumer;
    private final ClientConnection connection;
    private final boolean newWorld;
    private final @Nullable Duration worldLoadTime;
    private @Nullable String minigameName;
    private final ClientChunkLoadProgress clientChunkLoadProgress;
    private final Map<Identifier, byte[]> serverCookies;
    private final boolean hasCookies;
    private final Map<UUID, PlayerListEntry> playersByUuid;
    private final boolean seenInsecureChatWarning;
    private final AtomicReference<State> state = new AtomicReference<State>(State.CONNECTING);

    public ClientLoginNetworkHandler(ClientConnection connection, MinecraftClient client, @Nullable ServerInfo serverInfo, @Nullable Screen parentScreen, boolean newWorld, @Nullable Duration worldLoadTime, Consumer<Text> statusConsumer, ClientChunkLoadProgress clientChunkLoadProgress, @Nullable CookieStorage cookieStorage) {
        this.connection = connection;
        this.client = client;
        this.serverInfo = serverInfo;
        this.parentScreen = parentScreen;
        this.statusConsumer = statusConsumer;
        this.newWorld = newWorld;
        this.worldLoadTime = worldLoadTime;
        this.clientChunkLoadProgress = clientChunkLoadProgress;
        this.serverCookies = cookieStorage != null ? new HashMap(cookieStorage.cookies()) : new HashMap();
        this.playersByUuid = cookieStorage != null ? cookieStorage.seenPlayers() : Map.of();
        this.seenInsecureChatWarning = cookieStorage != null ? cookieStorage.seenInsecureChatWarning() : false;
        this.hasCookies = cookieStorage != null;
    }

    private void switchTo(State state) {
        State state2 = (State)this.state.updateAndGet(currentState -> {
            if (!state.prevStates.contains(currentState)) {
                throw new IllegalStateException("Tried to switch to " + String.valueOf(state) + " from " + String.valueOf(currentState) + ", but expected one of " + String.valueOf(state.prevStates));
            }
            return state;
        });
        this.statusConsumer.accept(state2.name);
    }

    public void onHello(LoginHelloS2CPacket packet) {
        LoginKeyC2SPacket loginKeyC2SPacket;
        Cipher cipher2;
        Cipher cipher;
        String string;
        this.switchTo(State.AUTHORIZING);
        try {
            SecretKey secretKey = NetworkEncryptionUtils.generateSecretKey();
            PublicKey publicKey = packet.getPublicKey();
            string = new BigInteger(NetworkEncryptionUtils.computeServerId((String)packet.getServerId(), (PublicKey)publicKey, (SecretKey)secretKey)).toString(16);
            cipher = NetworkEncryptionUtils.cipherFromKey((int)2, (Key)secretKey);
            cipher2 = NetworkEncryptionUtils.cipherFromKey((int)1, (Key)secretKey);
            byte[] bs = packet.getNonce();
            loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, bs);
        }
        catch (Exception exception) {
            throw new IllegalStateException("Protocol error", exception);
        }
        if (packet.needsAuthentication()) {
            Util.getIoWorkerExecutor().execute(() -> {
                Text text = this.joinServerSession(string);
                if (text != null) {
                    if (this.serverInfo != null && this.serverInfo.isLocal()) {
                        LOGGER.warn(text.getString());
                    } else {
                        this.connection.disconnect(text);
                        return;
                    }
                }
                this.setupEncryption(loginKeyC2SPacket, cipher, cipher2);
            });
        } else {
            this.setupEncryption(loginKeyC2SPacket, cipher, cipher2);
        }
    }

    private void setupEncryption(LoginKeyC2SPacket keyPacket, Cipher decryptionCipher, Cipher encryptionCipher) {
        this.switchTo(State.ENCRYPTING);
        this.connection.send((Packet)keyPacket, PacketCallbacks.always(() -> this.connection.setupEncryption(decryptionCipher, encryptionCipher)));
    }

    private @Nullable Text joinServerSession(String serverId) {
        try {
            this.client.getApiServices().sessionService().joinServer(this.client.getSession().getUuidOrNull(), this.client.getSession().getAccessToken(), serverId);
        }
        catch (AuthenticationUnavailableException authenticationUnavailableException) {
            return Text.translatable((String)"disconnect.loginFailedInfo", (Object[])new Object[]{Text.translatable((String)"disconnect.loginFailedInfo.serversUnavailable")});
        }
        catch (InvalidCredentialsException invalidCredentialsException) {
            return Text.translatable((String)"disconnect.loginFailedInfo", (Object[])new Object[]{Text.translatable((String)"disconnect.loginFailedInfo.invalidSession")});
        }
        catch (InsufficientPrivilegesException insufficientPrivilegesException) {
            return Text.translatable((String)"disconnect.loginFailedInfo", (Object[])new Object[]{Text.translatable((String)"disconnect.loginFailedInfo.insufficientPrivileges")});
        }
        catch (ForcedUsernameChangeException | UserBannedException authenticationException) {
            return Text.translatable((String)"disconnect.loginFailedInfo", (Object[])new Object[]{Text.translatable((String)"disconnect.loginFailedInfo.userBanned")});
        }
        catch (AuthenticationException authenticationException) {
            return Text.translatable((String)"disconnect.loginFailedInfo", (Object[])new Object[]{authenticationException.getMessage()});
        }
        return null;
    }

    public void onSuccess(LoginSuccessS2CPacket packet) {
        this.switchTo(State.JOINING);
        GameProfile gameProfile = packet.profile();
        this.connection.transitionInbound(ConfigurationStates.S2C, (PacketListener)new ClientConfigurationNetworkHandler(this.client, this.connection, new ClientConnectionState(this.clientChunkLoadProgress, gameProfile, this.client.getTelemetryManager().createWorldSession(this.newWorld, this.worldLoadTime, this.minigameName), ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(), FeatureFlags.DEFAULT_ENABLED_FEATURES, null, this.serverInfo, this.parentScreen, this.serverCookies, null, Map.of(), ServerLinks.EMPTY, this.playersByUuid, false)));
        this.connection.send((Packet)EnterConfigurationC2SPacket.INSTANCE);
        this.connection.transitionOutbound(ConfigurationStates.C2S);
        this.connection.send((Packet)new CustomPayloadC2SPacket((CustomPayload)new BrandCustomPayload(ClientBrandRetriever.getClientModName())));
        this.connection.send((Packet)new ClientOptionsC2SPacket(this.client.options.getSyncedOptions()));
    }

    public void onDisconnected(DisconnectionInfo info) {
        Text text;
        Text text2 = text = this.hasCookies ? ScreenTexts.CONNECT_FAILED_TRANSFER : ScreenTexts.CONNECT_FAILED;
        if (this.serverInfo != null && this.serverInfo.isRealm()) {
            this.client.setScreen((Screen)new DisconnectedScreen(this.parentScreen, text, info.reason(), ScreenTexts.BACK));
        } else {
            this.client.setScreen((Screen)new DisconnectedScreen(this.parentScreen, text, info));
        }
    }

    public boolean isConnectionOpen() {
        return this.connection.isOpen();
    }

    public void onDisconnect(LoginDisconnectS2CPacket packet) {
        this.connection.disconnect(packet.reason());
    }

    public void onCompression(LoginCompressionS2CPacket packet) {
        if (!this.connection.isLocal()) {
            this.connection.setCompressionThreshold(packet.getCompressionThreshold(), false);
        }
    }

    public void onQueryRequest(LoginQueryRequestS2CPacket packet) {
        this.statusConsumer.accept(Text.translatable((String)"connect.negotiating"));
        this.connection.send((Packet)new LoginQueryResponseC2SPacket(packet.queryId(), null));
    }

    public void setMinigameName(@Nullable String minigameName) {
        this.minigameName = minigameName;
    }

    public void onCookieRequest(CookieRequestS2CPacket packet) {
        this.connection.send((Packet)new CookieResponseC2SPacket(packet.key(), (byte[])this.serverCookies.get(packet.key())));
    }

    public void addCustomCrashReportInfo(CrashReport report, CrashReportSection section) {
        section.add("Server type", () -> this.serverInfo != null ? this.serverInfo.getServerType().toString() : "<unknown>");
        section.add("Login phase", () -> ((State)this.state.get()).toString());
        section.add("Is Local", () -> String.valueOf(this.connection.isLocal()));
    }
}

