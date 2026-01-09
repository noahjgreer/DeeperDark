package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class DefaultGameModeCommand {
   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("defaultgamemode").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.argument("gamemode", GameModeArgumentType.gameMode()).executes((commandContext) -> {
         return execute((ServerCommandSource)commandContext.getSource(), GameModeArgumentType.getGameMode(commandContext, "gamemode"));
      })));
   }

   private static int execute(ServerCommandSource source, GameMode defaultGameMode) {
      int i = 0;
      MinecraftServer minecraftServer = source.getServer();
      minecraftServer.setDefaultGameMode(defaultGameMode);
      GameMode gameMode = minecraftServer.getForcedGameMode();
      if (gameMode != null) {
         Iterator var5 = minecraftServer.getPlayerManager().getPlayerList().iterator();

         while(var5.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var5.next();
            if (serverPlayerEntity.changeGameMode(gameMode)) {
               ++i;
            }
         }
      }

      source.sendFeedback(() -> {
         return Text.translatable("commands.defaultgamemode.success", defaultGameMode.getTranslatableName());
      }, true);
      return i;
   }
}
