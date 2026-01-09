package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

public class MessageCommand {
   public static void register(CommandDispatcher dispatcher) {
      LiteralCommandNode literalCommandNode = dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("msg").then(CommandManager.argument("targets", EntityArgumentType.players()).then(CommandManager.argument("message", MessageArgumentType.message()).executes((context) -> {
         Collection collection = EntityArgumentType.getPlayers(context, "targets");
         if (!collection.isEmpty()) {
            MessageArgumentType.getSignedMessage(context, "message", (message) -> {
               execute((ServerCommandSource)context.getSource(), collection, message);
            });
         }

         return collection.size();
      }))));
      dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("tell").redirect(literalCommandNode));
      dispatcher.register((LiteralArgumentBuilder)CommandManager.literal("w").redirect(literalCommandNode));
   }

   private static void execute(ServerCommandSource source, Collection targets, SignedMessage message) {
      MessageType.Parameters parameters = MessageType.params(MessageType.MSG_COMMAND_INCOMING, source);
      SentMessage sentMessage = SentMessage.of(message);
      boolean bl = false;

      boolean bl2;
      for(Iterator var6 = targets.iterator(); var6.hasNext(); bl |= bl2 && message.isFullyFiltered()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var6.next();
         MessageType.Parameters parameters2 = MessageType.params(MessageType.MSG_COMMAND_OUTGOING, source).withTargetName(serverPlayerEntity.getDisplayName());
         source.sendChatMessage(sentMessage, false, parameters2);
         bl2 = source.shouldFilterText(serverPlayerEntity);
         serverPlayerEntity.sendChatMessage(sentMessage, bl2, parameters);
      }

      if (bl) {
         source.sendMessage(PlayerManager.FILTERED_FULL_TEXT);
      }

   }
}
