package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.listener.ClientConfigurationPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.network.packet.c2s.config.SelectKnownPacksC2SPacket;
import net.minecraft.network.packet.s2c.common.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.config.DynamicRegistriesS2CPacket;
import net.minecraft.network.packet.s2c.config.FeaturesS2CPacket;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;
import net.minecraft.network.packet.s2c.config.ResetChatS2CPacket;
import net.minecraft.network.packet.s2c.config.SelectKnownPacksS2CPacket;
import net.minecraft.network.state.PlayStateFactories;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ClientConfigurationNetworkHandler extends ClientCommonNetworkHandler implements ClientConfigurationPacketListener, TickablePacketListener {
   static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile profile;
   private FeatureSet enabledFeatures;
   private final DynamicRegistryManager.Immutable registryManager;
   private final ClientRegistries clientRegistries = new ClientRegistries();
   @Nullable
   private ClientDataPackManager dataPackManager;
   @Nullable
   protected ChatHud.ChatState chatState;

   public ClientConfigurationNetworkHandler(MinecraftClient minecraftClient, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
      super(minecraftClient, clientConnection, clientConnectionState);
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
      LOGGER.warn("Unknown custom packet payload: {}", payload.getId().id());
   }

   public void onDynamicRegistries(DynamicRegistriesS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.clientRegistries.putDynamicRegistry(packet.registry(), packet.entries());
   }

   public void onSynchronizeTags(SynchronizeTagsS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      this.clientRegistries.putTags(packet.getGroups());
   }

   public void onFeatures(FeaturesS2CPacket packet) {
      this.enabledFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf((Iterable)packet.features());
   }

   public void onSelectKnownPacks(SelectKnownPacksS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      if (this.dataPackManager == null) {
         this.dataPackManager = new ClientDataPackManager();
      }

      List list = this.dataPackManager.getCommonKnownPacks(packet.knownPacks());
      this.sendPacket(new SelectKnownPacksC2SPacket(list));
   }

   public void onResetChat(ResetChatS2CPacket packet) {
      this.chatState = null;
   }

   private Object openClientDataPack(Function opener) {
      if (this.dataPackManager == null) {
         return opener.apply(ResourceFactory.MISSING);
      } else {
         LifecycledResourceManager lifecycledResourceManager = this.dataPackManager.createResourceManager();

         Object var3;
         try {
            var3 = opener.apply(lifecycledResourceManager);
         } catch (Throwable var6) {
            if (lifecycledResourceManager != null) {
               try {
                  lifecycledResourceManager.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (lifecycledResourceManager != null) {
            lifecycledResourceManager.close();
         }

         return var3;
      }
   }

   public void onReady(ReadyS2CPacket packet) {
      NetworkThreadUtils.forceMainThread(packet, this, (ThreadExecutor)this.client);
      DynamicRegistryManager.Immutable immutable = (DynamicRegistryManager.Immutable)this.openClientDataPack((factory) -> {
         return this.clientRegistries.createRegistryManager(factory, this.registryManager, this.connection.isLocal());
      });
      this.connection.transitionInbound(PlayStateFactories.S2C.bind(RegistryByteBuf.makeFactory(immutable)), new ClientPlayNetworkHandler(this.client, this.connection, new ClientConnectionState(this.profile, this.worldSession, immutable, this.enabledFeatures, this.brand, this.serverInfo, this.postDisconnectScreen, this.serverCookies, this.chatState, this.customReportDetails, this.getServerLinks())));
      this.connection.send(ReadyC2SPacket.INSTANCE);
      this.connection.transitionOutbound(PlayStateFactories.C2S.bind(RegistryByteBuf.makeFactory(immutable), new PlayStateFactories.PacketCodecModifierContext(this) {
         public boolean isInCreativeMode() {
            return true;
         }
      }));
   }

   public void tick() {
      this.sendQueuedPackets();
   }

   public void onDisconnected(DisconnectionInfo info) {
      super.onDisconnected(info);
      this.client.onDisconnected();
   }

   protected DialogNetworkAccess createDialogNetworkAccess() {
      return new DialogNetworkAccess() {
         public void disconnect(Text reason) {
            ClientConfigurationNetworkHandler.this.connection.disconnect(reason);
         }

         public void runClickEventCommand(String command, @Nullable Screen afterActionScreen) {
            ClientConfigurationNetworkHandler.LOGGER.warn("Commands are not supported in configuration phase, trying to run '{}'", command);
         }

         public void showDialog(RegistryEntry dialog, @Nullable Screen afterActionScreen) {
            ClientConfigurationNetworkHandler.this.showDialog(dialog, this, afterActionScreen);
         }

         public void sendCustomClickActionPacket(Identifier id, Optional payload) {
            ClientConfigurationNetworkHandler.this.sendPacket(new CustomClickActionC2SPacket(id, payload));
         }

         public ServerLinks getServerLinks() {
            return ClientConfigurationNetworkHandler.this.getServerLinks();
         }
      };
   }
}
