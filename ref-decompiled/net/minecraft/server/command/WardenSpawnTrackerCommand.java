package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.block.entity.SculkShriekerWarningManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class WardenSpawnTrackerCommand {
   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("warden_spawn_tracker").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.literal("clear").executes((context) -> {
         return clearTracker((ServerCommandSource)context.getSource(), ImmutableList.of(((ServerCommandSource)context.getSource()).getPlayerOrThrow()));
      }))).then(CommandManager.literal("set").then(CommandManager.argument("warning_level", IntegerArgumentType.integer(0, 4)).executes((context) -> {
         return setWarningLevel((ServerCommandSource)context.getSource(), ImmutableList.of(((ServerCommandSource)context.getSource()).getPlayerOrThrow()), IntegerArgumentType.getInteger(context, "warning_level"));
      }))));
   }

   private static int setWarningLevel(ServerCommandSource source, Collection players, int warningCount) {
      Iterator var3 = players.iterator();

      while(var3.hasNext()) {
         PlayerEntity playerEntity = (PlayerEntity)var3.next();
         playerEntity.getSculkShriekerWarningManager().ifPresent((warningManager) -> {
            warningManager.setWarningLevel(warningCount);
         });
      }

      if (players.size() == 1) {
         source.sendFeedback(() -> {
            return Text.translatable("commands.warden_spawn_tracker.set.success.single", ((PlayerEntity)players.iterator().next()).getDisplayName());
         }, true);
      } else {
         source.sendFeedback(() -> {
            return Text.translatable("commands.warden_spawn_tracker.set.success.multiple", players.size());
         }, true);
      }

      return players.size();
   }

   private static int clearTracker(ServerCommandSource source, Collection players) {
      Iterator var2 = players.iterator();

      while(var2.hasNext()) {
         PlayerEntity playerEntity = (PlayerEntity)var2.next();
         playerEntity.getSculkShriekerWarningManager().ifPresent(SculkShriekerWarningManager::reset);
      }

      if (players.size() == 1) {
         source.sendFeedback(() -> {
            return Text.translatable("commands.warden_spawn_tracker.clear.success.single", ((PlayerEntity)players.iterator().next()).getDisplayName());
         }, true);
      } else {
         source.sendFeedback(() -> {
            return Text.translatable("commands.warden_spawn_tracker.clear.success.multiple", players.size());
         }, true);
      }

      return players.size();
   }
}
