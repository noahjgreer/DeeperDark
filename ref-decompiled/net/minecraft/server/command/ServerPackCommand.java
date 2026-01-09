package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.ResourcePackRemoveS2CPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class ServerPackCommand {
   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("serverpack").requires(CommandManager.requirePermissionLevel(2))).then(CommandManager.literal("push").then(((RequiredArgumentBuilder)CommandManager.argument("url", StringArgumentType.string()).then(((RequiredArgumentBuilder)CommandManager.argument("uuid", UuidArgumentType.uuid()).then(CommandManager.argument("hash", StringArgumentType.word()).executes((context) -> {
         return executePush((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "url"), Optional.of(UuidArgumentType.getUuid(context, "uuid")), Optional.of(StringArgumentType.getString(context, "hash")));
      }))).executes((context) -> {
         return executePush((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "url"), Optional.of(UuidArgumentType.getUuid(context, "uuid")), Optional.empty());
      }))).executes((context) -> {
         return executePush((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "url"), Optional.empty(), Optional.empty());
      })))).then(CommandManager.literal("pop").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).executes((context) -> {
         return executePop((ServerCommandSource)context.getSource(), UuidArgumentType.getUuid(context, "uuid"));
      }))));
   }

   private static void sendToAll(ServerCommandSource source, Packet packet) {
      source.getServer().getNetworkIo().getConnections().forEach((connection) -> {
         connection.send(packet);
      });
   }

   private static int executePush(ServerCommandSource source, String url, Optional uuid, Optional hash) {
      UUID uUID = (UUID)uuid.orElseGet(() -> {
         return UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
      });
      String string = (String)hash.orElse("");
      ResourcePackSendS2CPacket resourcePackSendS2CPacket = new ResourcePackSendS2CPacket(uUID, url, string, false, (Optional)null);
      sendToAll(source, resourcePackSendS2CPacket);
      return 0;
   }

   private static int executePop(ServerCommandSource source, UUID uuid) {
      ResourcePackRemoveS2CPacket resourcePackRemoveS2CPacket = new ResourcePackRemoveS2CPacket(Optional.of(uuid));
      sendToAll(source, resourcePackRemoveS2CPacket);
      return 0;
   }
}
