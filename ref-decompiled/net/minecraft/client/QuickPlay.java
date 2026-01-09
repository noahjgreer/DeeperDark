package net.minecraft.client;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsLongRunningMcoTaskScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.task.LongRunningTask;
import net.minecraft.client.realms.task.RealmsPrepareConnectionTask;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class QuickPlay {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Text ERROR_TITLE = Text.translatable("quickplay.error.title");
   private static final Text ERROR_INVALID_IDENTIFIER = Text.translatable("quickplay.error.invalid_identifier");
   private static final Text ERROR_REALM_CONNECT = Text.translatable("quickplay.error.realm_connect");
   private static final Text ERROR_REALM_PERMISSION = Text.translatable("quickplay.error.realm_permission");
   private static final Text TO_TITLE = Text.translatable("gui.toTitle");
   private static final Text TO_WORLD = Text.translatable("gui.toWorld");
   private static final Text TO_REALMS = Text.translatable("gui.toRealms");

   public static void startQuickPlay(MinecraftClient client, RunArgs.QuickPlayVariant variant, RealmsClient realmsClient) {
      if (!variant.isEnabled()) {
         LOGGER.error("Quick play disabled");
         client.setScreen(new TitleScreen());
      } else {
         Objects.requireNonNull(variant);
         byte var4 = 0;
         switch (variant.typeSwitch<invokedynamic>(variant, var4)) {
            case 0:
               RunArgs.MultiplayerQuickPlay multiplayerQuickPlay = (RunArgs.MultiplayerQuickPlay)variant;
               startMultiplayer(client, multiplayerQuickPlay.serverAddress());
               break;
            case 1:
               RunArgs.RealmsQuickPlay realmsQuickPlay = (RunArgs.RealmsQuickPlay)variant;
               startRealms(client, realmsClient, realmsQuickPlay.realmId());
               break;
            case 2:
               RunArgs.SingleplayerQuickPlay singleplayerQuickPlay = (RunArgs.SingleplayerQuickPlay)variant;
               String string = singleplayerQuickPlay.worldId();
               if (StringHelper.isBlank(string)) {
                  string = getLatestLevelName(client.getLevelStorage());
               }

               startSingleplayer(client, string);
               break;
            case 3:
               RunArgs.DisabledQuickPlay disabledQuickPlay = (RunArgs.DisabledQuickPlay)variant;
               LOGGER.error("Quick play disabled");
               client.setScreen(new TitleScreen());
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

      }
   }

   @Nullable
   private static String getLatestLevelName(LevelStorage storage) {
      try {
         List list = (List)storage.loadSummaries(storage.getLevelList()).get();
         if (list.isEmpty()) {
            LOGGER.warn("no latest singleplayer world found");
            return null;
         } else {
            return ((LevelSummary)list.getFirst()).getName();
         }
      } catch (ExecutionException | InterruptedException var2) {
         LOGGER.error("failed to load singleplayer world summaries", var2);
         return null;
      }
   }

   private static void startSingleplayer(MinecraftClient client, @Nullable String levelName) {
      if (!StringHelper.isBlank(levelName) && client.getLevelStorage().levelExists(levelName)) {
         client.createIntegratedServerLoader().start(levelName, () -> {
            client.setScreen(new TitleScreen());
         });
      } else {
         Screen screen = new SelectWorldScreen(new TitleScreen());
         client.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, ERROR_INVALID_IDENTIFIER, TO_WORLD));
      }
   }

   private static void startMultiplayer(MinecraftClient client, String serverAddress) {
      ServerList serverList = new ServerList(client);
      serverList.loadFile();
      ServerInfo serverInfo = serverList.get(serverAddress);
      if (serverInfo == null) {
         serverInfo = new ServerInfo(I18n.translate("selectServer.defaultName"), serverAddress, ServerInfo.ServerType.OTHER);
         serverList.add(serverInfo, true);
         serverList.saveFile();
      }

      ServerAddress serverAddress2 = ServerAddress.parse(serverAddress);
      ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), client, serverAddress2, serverInfo, true, (CookieStorage)null);
   }

   private static void startRealms(MinecraftClient client, RealmsClient realmsClient, String realmId) {
      long l;
      RealmsServerList realmsServerList;
      TitleScreen titleScreen;
      RealmsMainScreen screen;
      try {
         l = Long.parseLong(realmId);
         realmsServerList = realmsClient.listWorlds();
      } catch (NumberFormatException var8) {
         screen = new RealmsMainScreen(new TitleScreen());
         client.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, ERROR_INVALID_IDENTIFIER, TO_REALMS));
         return;
      } catch (RealmsServiceException var9) {
         titleScreen = new TitleScreen();
         client.setScreen(new DisconnectedScreen(titleScreen, ERROR_TITLE, ERROR_REALM_CONNECT, TO_TITLE));
         return;
      }

      RealmsServer realmsServer = (RealmsServer)realmsServerList.servers.stream().filter((server) -> {
         return server.id == l;
      }).findFirst().orElse((Object)null);
      if (realmsServer == null) {
         screen = new RealmsMainScreen(new TitleScreen());
         client.setScreen(new DisconnectedScreen(screen, ERROR_TITLE, ERROR_REALM_PERMISSION, TO_REALMS));
      } else {
         titleScreen = new TitleScreen();
         client.setScreen(new RealmsLongRunningMcoTaskScreen(titleScreen, new LongRunningTask[]{new RealmsPrepareConnectionTask(titleScreen, realmsServer)}));
      }
   }
}
