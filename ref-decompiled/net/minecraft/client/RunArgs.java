package net.minecraft.client;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceIndex;
import net.minecraft.client.session.Session;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RunArgs {
   public final Network network;
   public final WindowSettings windowSettings;
   public final Directories directories;
   public final Game game;
   public final QuickPlay quickPlay;

   public RunArgs(Network network, WindowSettings windowSettings, Directories dirs, Game game, QuickPlay quickPlay) {
      this.network = network;
      this.windowSettings = windowSettings;
      this.directories = dirs;
      this.game = game;
      this.quickPlay = quickPlay;
   }

   @Environment(EnvType.CLIENT)
   public static class Network {
      public final Session session;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy netProxy;

      public Network(Session session, PropertyMap userProperties, PropertyMap profileProperties, Proxy proxy) {
         this.session = session;
         this.userProperties = userProperties;
         this.profileProperties = profileProperties;
         this.netProxy = proxy;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Directories {
      public final File runDir;
      public final File resourcePackDir;
      public final File assetDir;
      @Nullable
      public final String assetIndex;

      public Directories(File runDir, File resPackDir, File assetDir, @Nullable String assetIndex) {
         this.runDir = runDir;
         this.resourcePackDir = resPackDir;
         this.assetDir = assetDir;
         this.assetIndex = assetIndex;
      }

      public Path getAssetDir() {
         return this.assetIndex == null ? this.assetDir.toPath() : ResourceIndex.buildFileSystem(this.assetDir.toPath(), this.assetIndex);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Game {
      public final boolean demo;
      public final String version;
      public final String versionType;
      public final boolean multiplayerDisabled;
      public final boolean onlineChatDisabled;
      public final boolean tracyEnabled;
      public final boolean renderDebugLabels;

      public Game(boolean demo, String version, String versionType, boolean multiplayerDisabled, boolean onlineChatDisabled, boolean tracyEnabled, boolean renderDebugLabels) {
         this.demo = demo;
         this.version = version;
         this.versionType = versionType;
         this.multiplayerDisabled = multiplayerDisabled;
         this.onlineChatDisabled = onlineChatDisabled;
         this.tracyEnabled = tracyEnabled;
         this.renderDebugLabels = renderDebugLabels;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record QuickPlay(@Nullable String logPath, QuickPlayVariant variant) {
      public QuickPlay(@Nullable String string, QuickPlayVariant quickPlayVariant) {
         this.logPath = string;
         this.variant = quickPlayVariant;
      }

      public boolean isEnabled() {
         return this.variant.isEnabled();
      }

      @Nullable
      public String logPath() {
         return this.logPath;
      }

      public QuickPlayVariant variant() {
         return this.variant;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record DisabledQuickPlay() implements QuickPlayVariant {
      public boolean isEnabled() {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record RealmsQuickPlay(String realmId) implements QuickPlayVariant {
      public RealmsQuickPlay(String string) {
         this.realmId = string;
      }

      public boolean isEnabled() {
         return !StringHelper.isBlank(this.realmId);
      }

      public String realmId() {
         return this.realmId;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record MultiplayerQuickPlay(String serverAddress) implements QuickPlayVariant {
      public MultiplayerQuickPlay(String string) {
         this.serverAddress = string;
      }

      public boolean isEnabled() {
         return !StringHelper.isBlank(this.serverAddress);
      }

      public String serverAddress() {
         return this.serverAddress;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record SingleplayerQuickPlay(@Nullable String worldId) implements QuickPlayVariant {
      public SingleplayerQuickPlay(@Nullable String string) {
         this.worldId = string;
      }

      public boolean isEnabled() {
         return true;
      }

      @Nullable
      public String worldId() {
         return this.worldId;
      }
   }

   @Environment(EnvType.CLIENT)
   public sealed interface QuickPlayVariant permits RunArgs.SingleplayerQuickPlay, RunArgs.MultiplayerQuickPlay, RunArgs.RealmsQuickPlay, RunArgs.DisabledQuickPlay {
      QuickPlayVariant DEFAULT = new DisabledQuickPlay();

      boolean isEnabled();
   }
}
