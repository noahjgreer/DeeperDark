package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ListCommand {
   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes((context) -> {
         return executeNames((ServerCommandSource)context.getSource());
      })).then(CommandManager.literal("uuids").executes((context) -> {
         return executeUuids((ServerCommandSource)context.getSource());
      })));
   }

   private static int executeNames(ServerCommandSource source) {
      return execute(source, PlayerEntity::getDisplayName);
   }

   private static int executeUuids(ServerCommandSource source) {
      return execute(source, (player) -> {
         return Text.translatable("commands.list.nameAndId", player.getName(), Text.of(player.getGameProfile().getId()));
      });
   }

   private static int execute(ServerCommandSource source, Function nameProvider) {
      PlayerManager playerManager = source.getServer().getPlayerManager();
      List list = playerManager.getPlayerList();
      Text text = Texts.join(list, (Function)nameProvider);
      source.sendFeedback(() -> {
         return Text.translatable("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), text);
      }, false);
      return list.size();
   }
}
