package net.minecraft.server.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class TagCommand {
   private static final SimpleCommandExceptionType ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.tag.add.failed"));
   private static final SimpleCommandExceptionType REMOVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.tag.remove.failed"));

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tag").requires(CommandManager.requirePermissionLevel(2))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.literal("add").then(CommandManager.argument("name", StringArgumentType.word()).executes((context) -> {
         return executeAdd((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StringArgumentType.getString(context, "name"));
      })))).then(CommandManager.literal("remove").then(CommandManager.argument("name", StringArgumentType.word()).suggests((context, builder) -> {
         return CommandSource.suggestMatching((Iterable)getTags(EntityArgumentType.getEntities(context, "targets")), builder);
      }).executes((context) -> {
         return executeRemove((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"), StringArgumentType.getString(context, "name"));
      })))).then(CommandManager.literal("list").executes((context) -> {
         return executeList((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"));
      }))));
   }

   private static Collection getTags(Collection entities) {
      Set set = Sets.newHashSet();
      Iterator var2 = entities.iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         set.addAll(entity.getCommandTags());
      }

      return set;
   }

   private static int executeAdd(ServerCommandSource source, Collection targets, String tag) throws CommandSyntaxException {
      int i = 0;
      Iterator var4 = targets.iterator();

      while(var4.hasNext()) {
         Entity entity = (Entity)var4.next();
         if (entity.addCommandTag(tag)) {
            ++i;
         }
      }

      if (i == 0) {
         throw ADD_FAILED_EXCEPTION.create();
      } else {
         if (targets.size() == 1) {
            source.sendFeedback(() -> {
               return Text.translatable("commands.tag.add.success.single", tag, ((Entity)targets.iterator().next()).getDisplayName());
            }, true);
         } else {
            source.sendFeedback(() -> {
               return Text.translatable("commands.tag.add.success.multiple", tag, targets.size());
            }, true);
         }

         return i;
      }
   }

   private static int executeRemove(ServerCommandSource source, Collection targets, String tag) throws CommandSyntaxException {
      int i = 0;
      Iterator var4 = targets.iterator();

      while(var4.hasNext()) {
         Entity entity = (Entity)var4.next();
         if (entity.removeCommandTag(tag)) {
            ++i;
         }
      }

      if (i == 0) {
         throw REMOVE_FAILED_EXCEPTION.create();
      } else {
         if (targets.size() == 1) {
            source.sendFeedback(() -> {
               return Text.translatable("commands.tag.remove.success.single", tag, ((Entity)targets.iterator().next()).getDisplayName());
            }, true);
         } else {
            source.sendFeedback(() -> {
               return Text.translatable("commands.tag.remove.success.multiple", tag, targets.size());
            }, true);
         }

         return i;
      }
   }

   private static int executeList(ServerCommandSource source, Collection targets) {
      Set set = Sets.newHashSet();
      Iterator var3 = targets.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         set.addAll(entity.getCommandTags());
      }

      if (targets.size() == 1) {
         Entity entity2 = (Entity)targets.iterator().next();
         if (set.isEmpty()) {
            source.sendFeedback(() -> {
               return Text.translatable("commands.tag.list.single.empty", entity2.getDisplayName());
            }, false);
         } else {
            source.sendFeedback(() -> {
               return Text.translatable("commands.tag.list.single.success", entity2.getDisplayName(), set.size(), Texts.joinOrdered(set));
            }, false);
         }
      } else if (set.isEmpty()) {
         source.sendFeedback(() -> {
            return Text.translatable("commands.tag.list.multiple.empty", targets.size());
         }, false);
      } else {
         source.sendFeedback(() -> {
            return Text.translatable("commands.tag.list.multiple.success", targets.size(), set.size(), Texts.joinOrdered(set));
         }, false);
      }

      return set.size();
   }
}
