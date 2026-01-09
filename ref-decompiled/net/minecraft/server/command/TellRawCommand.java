package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;

public class TellRawCommand {
   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess registryAccess) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("tellraw").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("message", TextArgumentType.text(registryAccess)).executes((context) -> {
         int i = 0;

         for(Iterator var2 = EntityArgumentType.getPlayers(context, "targets").iterator(); var2.hasNext(); ++i) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
            serverPlayerEntity.sendMessageToClient(TextArgumentType.parseTextArgument(context, "message", serverPlayerEntity), false);
         }

         return i;
      }))));
   }
}
