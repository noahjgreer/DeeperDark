package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.ServerLinks;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ClientConnectionState(GameProfile localGameProfile, WorldSession worldSession, DynamicRegistryManager.Immutable receivedRegistries, FeatureSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerInfo serverInfo, @Nullable Screen postDisconnectScreen, Map serverCookies, @Nullable ChatHud.ChatState chatState, Map customReportDetails, ServerLinks serverLinks) {
   public ClientConnectionState(GameProfile gameProfile, WorldSession worldSession, DynamicRegistryManager.Immutable immutable, FeatureSet featureSet, @Nullable String string, @Nullable ServerInfo serverInfo, @Nullable Screen screen, Map map, @Nullable ChatHud.ChatState chatState, Map map2, ServerLinks serverLinks) {
      this.localGameProfile = gameProfile;
      this.worldSession = worldSession;
      this.receivedRegistries = immutable;
      this.enabledFeatures = featureSet;
      this.serverBrand = string;
      this.serverInfo = serverInfo;
      this.postDisconnectScreen = screen;
      this.serverCookies = map;
      this.chatState = chatState;
      this.customReportDetails = map2;
      this.serverLinks = serverLinks;
   }

   public GameProfile localGameProfile() {
      return this.localGameProfile;
   }

   public WorldSession worldSession() {
      return this.worldSession;
   }

   public DynamicRegistryManager.Immutable receivedRegistries() {
      return this.receivedRegistries;
   }

   public FeatureSet enabledFeatures() {
      return this.enabledFeatures;
   }

   @Nullable
   public String serverBrand() {
      return this.serverBrand;
   }

   @Nullable
   public ServerInfo serverInfo() {
      return this.serverInfo;
   }

   @Nullable
   public Screen postDisconnectScreen() {
      return this.postDisconnectScreen;
   }

   public Map serverCookies() {
      return this.serverCookies;
   }

   @Nullable
   public ChatHud.ChatState chatState() {
      return this.chatState;
   }

   public Map customReportDetails() {
      return this.customReportDetails;
   }

   public ServerLinks serverLinks() {
      return this.serverLinks;
   }
}
