package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.SaveProperties;
import org.slf4j.Logger;

public class ReloadCommand {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void tryReloadDataPacks(Collection dataPacks, ServerCommandSource source) {
      source.getServer().reloadResources(dataPacks).exceptionally((throwable) -> {
         LOGGER.warn("Failed to execute reload", throwable);
         source.sendError(Text.translatable("commands.reload.failure"));
         return null;
      });
   }

   private static Collection findNewDataPacks(ResourcePackManager dataPackManager, SaveProperties saveProperties, Collection enabledDataPacks) {
      dataPackManager.scanPacks();
      Collection collection = Lists.newArrayList(enabledDataPacks);
      Collection collection2 = saveProperties.getDataConfiguration().dataPacks().getDisabled();
      Iterator var5 = dataPackManager.getIds().iterator();

      while(var5.hasNext()) {
         String string = (String)var5.next();
         if (!collection2.contains(string) && !collection.contains(string)) {
            collection.add(string);
         }
      }

      return collection;
   }

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("reload").requires(CommandManager.requirePermissionLevel(2))).executes((context) -> {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         MinecraftServer minecraftServer = serverCommandSource.getServer();
         ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
         SaveProperties saveProperties = minecraftServer.getSaveProperties();
         Collection collection = resourcePackManager.getEnabledIds();
         Collection collection2 = findNewDataPacks(resourcePackManager, saveProperties, collection);
         serverCommandSource.sendFeedback(() -> {
            return Text.translatable("commands.reload.success");
         }, true);
         tryReloadDataPacks(collection2, serverCommandSource);
         return 0;
      }));
   }
}
