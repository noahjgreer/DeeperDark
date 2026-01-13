/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.multiplayer.CodeOfConductScreen
 *  net.minecraft.client.network.ClientCommonNetworkHandler
 *  net.minecraft.client.network.ClientConfigurationNetworkHandler
 *  net.minecraft.client.network.ClientConnectionState
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.ClientRegistries
 *  net.minecraft.client.resource.ClientDataPackManager
 *  net.minecraft.client.world.ClientChunkLoadProgress
 *  net.minecraft.network.ClientConnection
 *  net.minecraft.network.DisconnectionInfo
 *  net.minecraft.network.NetworkThreadUtils
 *  net.minecraft.network.PacketApplyBatcher
 *  net.minecraft.network.RegistryByteBuf
 *  net.minecraft.network.listener.ClientConfigurationPacketListener
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.listener.TickablePacketListener
 *  net.minecraft.network.packet.CustomPayload
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.config.AcceptCodeOfConductC2SPacket
 *  net.minecraft.network.packet.c2s.config.ReadyC2SPacket
 *  net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket
 *  net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket
 *  net.minecraft.network.packet.s2c.config.CodeOfConductS2CPacket
 *  net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket
 *  net.minecraft.network.packet.s2c.config.FeaturesS2CPacket
 *  net.minecraft.network.packet.s2c.config.ReadyS2CPacket
 *  net.minecraft.network.packet.s2c.config.ResetChatS2CPacket
 *  net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket
 *  net.minecraft.network.state.PlayStateFactories
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.DynamicRegistryManager$Immutable
 *  net.minecraft.resource.LifecycledResourceManager
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.resource.featuretoggle.FeatureFlags
 *  net.minecraft.resource.featuretoggle.FeatureSet
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.multiplayer.CodeOfConductScreen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientRegistries;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketApplyBatcher;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.config.AcceptCodeOfConductC2SPacket;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.CodeOfConductS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ClientConfigurationNetworkHandler
extends ClientCommonNetworkHandler
implements ClientConfigurationPacketListener,
TickablePacketListener {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final Text CODE_OF_CONDUCT_DISCONNECT_REASON = Text.translatable((String)"multiplayer.disconnect.code_of_conduct");
    private final ClientChunkLoadProgress chunkLoadProgress;
    private final GameProfile profile;
    private FeatureSet enabledFeatures;
    private final DynamicRegistryManager.Immutable registryManager;
    private final ClientRegistries clientRegistries = new ClientRegistries();
    private @Nullable ClientDataPackManager dataPackManager;
    protected // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ChatHud.ChatState chatState;
    private boolean receivedCodeOfConduct;

    public ClientConfigurationNetworkHandler(MinecraftClient minecraftClient, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
        super(minecraftClient, clientConnection, clientConnectionState);
        this.chunkLoadProgress = clientConnectionState.chunkLoadProgress();
        this.profile = clientConnectionState.localGameProfile();
        this.registryManager = clientConnectionState.receivedRegistries();
        this.enabledFeatures = clientConnectionState.enabledFeatures();
        this.chatState = clientConnectionState.chatState();
    }

    public boolean isConnectionOpen() {
        return this.connection.isOpen();
    }

    protected void onCustomPayload(CustomPayload payload) {
        this.handleCustomPayload(payload);
    }

    private void handleCustomPayload(CustomPayload payload) {
        LOGGER.warn("Unknown custom packet payload: {}", (Object)payload.getId().id());
    }

    public void onDynamicRegistries(DynamicRegistriesS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.clientRegistries.putDynamicRegistry(packet.registry(), packet.entries());
    }

    public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        this.clientRegistries.putTags(packet.getGroups());
    }

    public void onFeatures(FeaturesS2CPacket packet) {
        this.enabledFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf((Iterable)packet.features());
    }

    public void onSelectKnownPacks(SelectKnownPacksS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (this.dataPackManager == null) {
            this.dataPackManager = new ClientDataPackManager();
        }
        List list = this.dataPackManager.getCommonKnownPacks(packet.knownPacks());
        this.sendPacket((Packet)new SelectKnownPacksC2SPacket(list));
    }

    public void onResetChat(ResetChatS2CPacket packet) {
        this.chatState = null;
    }

    private <T> T openClientDataPack(Function<ResourceFactory, T> opener) {
        if (this.dataPackManager == null) {
            return opener.apply(ResourceFactory.MISSING);
        }
        try (LifecycledResourceManager lifecycledResourceManager = this.dataPackManager.createResourceManager();){
            T t = opener.apply((ResourceFactory)lifecycledResourceManager);
            return t;
        }
    }

    public void onCodeOfConduct(CodeOfConductS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        if (this.receivedCodeOfConduct) {
            throw new IllegalStateException("Server sent duplicate Code of Conduct");
        }
        this.receivedCodeOfConduct = true;
        String string = packet.codeOfConduct();
        if (this.serverInfo != null && this.serverInfo.hasAcceptedCodeOfConduct(string)) {
            this.sendPacket((Packet)AcceptCodeOfConductC2SPacket.INSTANCE);
        } else {
            Screen screen = this.client.currentScreen;
            this.client.setScreen((Screen)new CodeOfConductScreen(this.serverInfo, screen, string, acknowledged -> {
                if (acknowledged) {
                    this.sendPacket((Packet)AcceptCodeOfConductC2SPacket.INSTANCE);
                    this.client.setScreen(screen);
                } else {
                    this.createDialogNetworkAccess().disconnect(CODE_OF_CONDUCT_DISCONNECT_REASON);
                }
            }));
        }
    }

    public void onReady(ReadyS2CPacket packet) {
        NetworkThreadUtils.forceMainThread((Packet)packet, (PacketListener)this, (PacketApplyBatcher)this.client.getPacketApplyBatcher());
        DynamicRegistryManager.Immutable immutable = (DynamicRegistryManager.Immutable)this.openClientDataPack(factory -> this.clientRegistries.createRegistryManager(factory, this.registryManager, this.connection.isLocal()));
        this.connection.transitionInbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory((DynamicRegistryManager)immutable)), (PacketListener)new ClientPlayNetworkHandler(this.client, this.connection, new ClientConnectionState(this.chunkLoadProgress, this.profile, this.worldSession, immutable, this.enabledFeatures, this.brand, this.serverInfo, this.postDisconnectScreen, this.serverCookies, this.chatState, this.customReportDetails, this.getServerLinks(), this.seenPlayers, this.seenInsecureChatWarning)));
        this.connection.send((Packet)ReadyC2SPacket.INSTANCE);
        this.connection.transitionOutbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory((DynamicRegistryManager)immutable), (Object)new /* Unavailable Anonymous Inner Class!! */));
    }

    public void tick() {
        this.sendQueuedPackets();
    }

    public void onDisconnected(DisconnectionInfo info) {
        super.onDisconnected(info);
        this.client.onDisconnected();
    }

    protected DialogNetworkAccess createDialogNetworkAccess() {
        return new /* Unavailable Anonymous Inner Class!! */;
    }
}

