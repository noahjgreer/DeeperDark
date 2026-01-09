package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

public class KillCommand {
   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("kill").requires(CommandManager.requirePermissionLevel(2))).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), ImmutableList.of(((ServerCommandSource)context.getSource()).getEntityOrThrow()));
      })).then(CommandManager.argument("targets", EntityArgumentType.entities()).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"));
      })));
   }

   private static int execute(ServerCommandSource source, Collection targets) {
      Iterator var2 = targets.iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         entity.kill(source.getWorld());
      }

      if (targets.size() == 1) {
         source.sendFeedback(() -> {
            return Text.translatable("commands.kill.success.single", ((Entity)targets.iterator().next()).getDisplayName());
         }, true);
      } else {
         source.sendFeedback(() -> {
            return Text.translatable("commands.kill.success.multiple", targets.size());
         }, true);
      }

      return targets.size();
   }
}
