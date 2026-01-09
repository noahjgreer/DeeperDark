package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ClearCommand {
   private static final DynamicCommandExceptionType FAILED_SINGLE_EXCEPTION = new DynamicCommandExceptionType((playerName) -> {
      return Text.stringifiedTranslatable("clear.failed.single", playerName);
   });
   private static final DynamicCommandExceptionType FAILED_MULTIPLE_EXCEPTION = new DynamicCommandExceptionType((playerCount) -> {
      return Text.stringifiedTranslatable("clear.failed.multiple", playerCount);
   });

   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess commandRegistryAccess) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("clear").requires(CommandManager.requirePermissionLevel(2))).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), Collections.singleton(((ServerCommandSource)context.getSource()).getPlayerOrThrow()), (stack) -> {
            return true;
         });
      })).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), (stack) -> {
            return true;
         });
      })).then(((RequiredArgumentBuilder)CommandManager.argument("item", ItemPredicateArgumentType.itemPredicate(commandRegistryAccess)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), ItemPredicateArgumentType.getItemStackPredicate(context, "item"));
      })).then(CommandManager.argument("maxCount", IntegerArgumentType.integer(0)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), ItemPredicateArgumentType.getItemStackPredicate(context, "item"), IntegerArgumentType.getInteger(context, "maxCount"));
      })))));
   }

   private static int execute(ServerCommandSource source, Collection targets, Predicate item) throws CommandSyntaxException {
      return execute(source, targets, item, -1);
   }

   private static int execute(ServerCommandSource source, Collection targets, Predicate item, int maxCount) throws CommandSyntaxException {
      int i = 0;
      Iterator var5 = targets.iterator();

      while(var5.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var5.next();
         i += serverPlayerEntity.getInventory().remove(item, maxCount, serverPlayerEntity.playerScreenHandler.getCraftingInput());
         serverPlayerEntity.currentScreenHandler.sendContentUpdates();
         serverPlayerEntity.playerScreenHandler.onContentChanged(serverPlayerEntity.getInventory());
      }

      if (i == 0) {
         if (targets.size() == 1) {
            throw FAILED_SINGLE_EXCEPTION.create(((ServerPlayerEntity)targets.iterator().next()).getName());
         } else {
            throw FAILED_MULTIPLE_EXCEPTION.create(targets.size());
         }
      } else {
         if (maxCount == 0) {
            if (targets.size() == 1) {
               source.sendFeedback(() -> {
                  return Text.translatable("commands.clear.test.single", i, ((ServerPlayerEntity)targets.iterator().next()).getDisplayName());
               }, true);
            } else {
               source.sendFeedback(() -> {
                  return Text.translatable("commands.clear.test.multiple", i, targets.size());
               }, true);
            }
         } else if (targets.size() == 1) {
            source.sendFeedback(() -> {
               return Text.translatable("commands.clear.success.single", i, ((ServerPlayerEntity)targets.iterator().next()).getDisplayName());
            }, true);
         } else {
            source.sendFeedback(() -> {
               return Text.translatable("commands.clear.success.multiple", i, targets.size());
            }, true);
         }

         return i;
      }
   }
}
