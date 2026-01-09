package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public class GameModeCommand {
   public static final int REQUIRED_PERMISSION_LEVEL = 2;

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("gamemode").requires(CommandManager.requirePermissionLevel(2))).then(((RequiredArgumentBuilder)CommandManager.argument("gamemode", GameModeArgumentType.gameMode()).executes((context) -> {
         return execute((CommandContext)context, (Collection)Collections.singleton(((ServerCommandSource)context.getSource()).getPlayerOrThrow()), GameModeArgumentType.getGameMode(context, "gamemode"));
      })).then(CommandManager.argument("target", EntityArgumentType.players()).executes((context) -> {
         return execute(context, EntityArgumentType.getPlayers(context, "target"), GameModeArgumentType.getGameMode(context, "gamemode"));
      }))));
   }

   private static void sendFeedback(ServerCommandSource source, ServerPlayerEntity player, GameMode gameMode) {
      Text text = Text.translatable("gameMode." + gameMode.getId());
      if (source.getEntity() == player) {
         source.sendFeedback(() -> {
            return Text.translatable("commands.gamemode.success.self", text);
         }, true);
      } else {
         if (source.getWorld().getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK)) {
            player.sendMessage(Text.translatable("gameMode.changed", text));
         }

         source.sendFeedback(() -> {
            return Text.translatable("commands.gamemode.success.other", player.getDisplayName(), text);
         }, true);
      }

   }

   private static int execute(CommandContext context, Collection targets, GameMode gameMode) {
      int i = 0;
      Iterator var4 = targets.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
         if (execute((ServerCommandSource)context.getSource(), serverPlayerEntity, gameMode)) {
            ++i;
         }
      }

      return i;
   }

   public static void execute(ServerPlayerEntity target, GameMode gameMode) {
      execute(target.getCommandSource(), target, gameMode);
   }

   private static boolean execute(ServerCommandSource source, ServerPlayerEntity target, GameMode gameMode) {
      if (target.changeGameMode(gameMode)) {
         sendFeedback(source, target, gameMode);
         return true;
      } else {
         return false;
      }
   }
}
