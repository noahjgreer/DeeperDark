package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AdvancementCommand {
   private static final DynamicCommandExceptionType GENERIC_EXCEPTION = new DynamicCommandExceptionType((message) -> {
      return (Text)message;
   });
   private static final Dynamic2CommandExceptionType CRITERION_NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((advancement, criterion) -> {
      return Text.stringifiedTranslatable("commands.advancement.criterionNotFound", advancement, criterion);
   });

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("advancement").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.GRANT, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.ONLY));
      })).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((context, builder) -> {
         return CommandSource.suggestMatching((Iterable)RegistryKeyArgumentType.getAdvancementEntry(context, "advancement").value().criteria().keySet(), builder);
      }).executes((context) -> {
         return executeCriterion((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.GRANT, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), StringArgumentType.getString(context, "criterion"));
      }))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.GRANT, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.FROM));
      })))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.GRANT, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.UNTIL));
      })))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.GRANT, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.THROUGH));
      })))).then(CommandManager.literal("everything").executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.GRANT, ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().getAdvancements(), false);
      }))))).then(CommandManager.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.literal("only").then(((RequiredArgumentBuilder)CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.REVOKE, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.ONLY));
      })).then(CommandManager.argument("criterion", StringArgumentType.greedyString()).suggests((context, builder) -> {
         return CommandSource.suggestMatching((Iterable)RegistryKeyArgumentType.getAdvancementEntry(context, "advancement").value().criteria().keySet(), builder);
      }).executes((context) -> {
         return executeCriterion((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.REVOKE, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), StringArgumentType.getString(context, "criterion"));
      }))))).then(CommandManager.literal("from").then(CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.REVOKE, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.FROM));
      })))).then(CommandManager.literal("until").then(CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.REVOKE, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.UNTIL));
      })))).then(CommandManager.literal("through").then(CommandManager.argument("advancement", RegistryKeyArgumentType.registryKey(RegistryKeys.ADVANCEMENT)).executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.REVOKE, select(context, RegistryKeyArgumentType.getAdvancementEntry(context, "advancement"), AdvancementCommand.Selection.THROUGH));
      })))).then(CommandManager.literal("everything").executes((context) -> {
         return executeAdvancement((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), AdvancementCommand.Operation.REVOKE, ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().getAdvancements());
      })))));
   }

   private static int executeAdvancement(ServerCommandSource source, Collection targets, Operation operation, Collection selection) throws CommandSyntaxException {
      return executeAdvancement(source, targets, operation, selection, true);
   }

   private static int executeAdvancement(ServerCommandSource source, Collection targets, Operation operation, Collection selection, boolean skipSync) throws CommandSyntaxException {
      int i = 0;

      ServerPlayerEntity serverPlayerEntity;
      for(Iterator var6 = targets.iterator(); var6.hasNext(); i += operation.processAll(serverPlayerEntity, selection, skipSync)) {
         serverPlayerEntity = (ServerPlayerEntity)var6.next();
      }

      if (i == 0) {
         if (selection.size() == 1) {
            if (targets.size() == 1) {
               throw GENERIC_EXCEPTION.create(Text.translatable(operation.getCommandPrefix() + ".one.to.one.failure", Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()));
            } else {
               throw GENERIC_EXCEPTION.create(Text.translatable(operation.getCommandPrefix() + ".one.to.many.failure", Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()), targets.size()));
            }
         } else if (targets.size() == 1) {
            throw GENERIC_EXCEPTION.create(Text.translatable(operation.getCommandPrefix() + ".many.to.one.failure", selection.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()));
         } else {
            throw GENERIC_EXCEPTION.create(Text.translatable(operation.getCommandPrefix() + ".many.to.many.failure", selection.size(), targets.size()));
         }
      } else {
         if (selection.size() == 1) {
            if (targets.size() == 1) {
               source.sendFeedback(() -> {
                  return Text.translatable(operation.getCommandPrefix() + ".one.to.one.success", Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName());
               }, true);
            } else {
               source.sendFeedback(() -> {
                  return Text.translatable(operation.getCommandPrefix() + ".one.to.many.success", Advancement.getNameFromIdentity((AdvancementEntry)selection.iterator().next()), targets.size());
               }, true);
            }
         } else if (targets.size() == 1) {
            source.sendFeedback(() -> {
               return Text.translatable(operation.getCommandPrefix() + ".many.to.one.success", selection.size(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName());
            }, true);
         } else {
            source.sendFeedback(() -> {
               return Text.translatable(operation.getCommandPrefix() + ".many.to.many.success", selection.size(), targets.size());
            }, true);
         }

         return i;
      }
   }

   private static int executeCriterion(ServerCommandSource source, Collection targets, Operation operation, AdvancementEntry advancement, String criterion) throws CommandSyntaxException {
      int i = 0;
      Advancement advancement2 = advancement.value();
      if (!advancement2.criteria().containsKey(criterion)) {
         throw CRITERION_NOT_FOUND_EXCEPTION.create(Advancement.getNameFromIdentity(advancement), criterion);
      } else {
         Iterator var7 = targets.iterator();

         while(var7.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var7.next();
            if (operation.processEachCriterion(serverPlayerEntity, advancement, criterion)) {
               ++i;
            }
         }

         if (i == 0) {
            if (targets.size() == 1) {
               throw GENERIC_EXCEPTION.create(Text.translatable(operation.getCommandPrefix() + ".criterion.to.one.failure", criterion, Advancement.getNameFromIdentity(advancement), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()));
            } else {
               throw GENERIC_EXCEPTION.create(Text.translatable(operation.getCommandPrefix() + ".criterion.to.many.failure", criterion, Advancement.getNameFromIdentity(advancement), targets.size()));
            }
         } else {
            if (targets.size() == 1) {
               source.sendFeedback(() -> {
                  return Text.translatable(operation.getCommandPrefix() + ".criterion.to.one.success", criterion, Advancement.getNameFromIdentity(advancement), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName());
               }, true);
            } else {
               source.sendFeedback(() -> {
                  return Text.translatable(operation.getCommandPrefix() + ".criterion.to.many.success", criterion, Advancement.getNameFromIdentity(advancement), targets.size());
               }, true);
            }

            return i;
         }
      }
   }

   private static List select(CommandContext context, AdvancementEntry advancement, Selection selection) {
      AdvancementManager advancementManager = ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().getManager();
      PlacedAdvancement placedAdvancement = advancementManager.get(advancement);
      if (placedAdvancement == null) {
         return List.of(advancement);
      } else {
         List list = new ArrayList();
         if (selection.before) {
            for(PlacedAdvancement placedAdvancement2 = placedAdvancement.getParent(); placedAdvancement2 != null; placedAdvancement2 = placedAdvancement2.getParent()) {
               list.add(placedAdvancement2.getAdvancementEntry());
            }
         }

         list.add(advancement);
         if (selection.after) {
            addChildrenRecursivelyToList(placedAdvancement, list);
         }

         return list;
      }
   }

   private static void addChildrenRecursivelyToList(PlacedAdvancement parent, List childList) {
      Iterator var2 = parent.getChildren().iterator();

      while(var2.hasNext()) {
         PlacedAdvancement placedAdvancement = (PlacedAdvancement)var2.next();
         childList.add(placedAdvancement.getAdvancementEntry());
         addChildrenRecursivelyToList(placedAdvancement, childList);
      }

   }

   private static enum Operation {
      GRANT("grant") {
         protected boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement) {
            AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
            if (advancementProgress.isDone()) {
               return false;
            } else {
               Iterator var4 = advancementProgress.getUnobtainedCriteria().iterator();

               while(var4.hasNext()) {
                  String string = (String)var4.next();
                  player.getAdvancementTracker().grantCriterion(advancement, string);
               }

               return true;
            }
         }

         protected boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
            return player.getAdvancementTracker().grantCriterion(advancement, criterion);
         }
      },
      REVOKE("revoke") {
         protected boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement) {
            AdvancementProgress advancementProgress = player.getAdvancementTracker().getProgress(advancement);
            if (!advancementProgress.isAnyObtained()) {
               return false;
            } else {
               Iterator var4 = advancementProgress.getObtainedCriteria().iterator();

               while(var4.hasNext()) {
                  String string = (String)var4.next();
                  player.getAdvancementTracker().revokeCriterion(advancement, string);
               }

               return true;
            }
         }

         protected boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion) {
            return player.getAdvancementTracker().revokeCriterion(advancement, criterion);
         }
      };

      private final String commandPrefix;

      Operation(final String name) {
         this.commandPrefix = "commands.advancement." + name;
      }

      public int processAll(ServerPlayerEntity player, Iterable advancements, boolean skipSync) {
         int i = 0;
         if (!skipSync) {
            player.getAdvancementTracker().sendUpdate(player, true);
         }

         Iterator var5 = advancements.iterator();

         while(var5.hasNext()) {
            AdvancementEntry advancementEntry = (AdvancementEntry)var5.next();
            if (this.processEach(player, advancementEntry)) {
               ++i;
            }
         }

         if (!skipSync) {
            player.getAdvancementTracker().sendUpdate(player, false);
         }

         return i;
      }

      protected abstract boolean processEach(ServerPlayerEntity player, AdvancementEntry advancement);

      protected abstract boolean processEachCriterion(ServerPlayerEntity player, AdvancementEntry advancement, String criterion);

      protected String getCommandPrefix() {
         return this.commandPrefix;
      }

      // $FF: synthetic method
      private static Operation[] method_36964() {
         return new Operation[]{GRANT, REVOKE};
      }
   }

   private static enum Selection {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      final boolean before;
      final boolean after;

      private Selection(final boolean before, final boolean after) {
         this.before = before;
         this.after = after;
      }

      // $FF: synthetic method
      private static Selection[] method_36965() {
         return new Selection[]{ONLY, THROUGH, FROM, UNTIL, EVERYTHING};
      }
   }
}
