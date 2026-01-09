package net.minecraft.server.dedicated.command;

import com.google.common.net.InetAddresses;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.BannedIpList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class BanIpCommand {
   private static final SimpleCommandExceptionType INVALID_IP_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.banip.invalid"));
   private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.banip.failed"));

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban-ip").requires(CommandManager.requirePermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("target", StringArgumentType.word()).executes((context) -> {
         return checkIp((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "target"), (Text)null);
      })).then(CommandManager.argument("reason", MessageArgumentType.message()).executes((context) -> {
         return checkIp((ServerCommandSource)context.getSource(), StringArgumentType.getString(context, "target"), MessageArgumentType.getMessage(context, "reason"));
      }))));
   }

   private static int checkIp(ServerCommandSource source, String target, @Nullable Text reason) throws CommandSyntaxException {
      if (InetAddresses.isInetAddress(target)) {
         return banIp(source, target, reason);
      } else {
         ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(target);
         if (serverPlayerEntity != null) {
            return banIp(source, serverPlayerEntity.getIp(), reason);
         } else {
            throw INVALID_IP_EXCEPTION.create();
         }
      }
   }

   private static int banIp(ServerCommandSource source, String targetIp, @Nullable Text reason) throws CommandSyntaxException {
      BannedIpList bannedIpList = source.getServer().getPlayerManager().getIpBanList();
      if (bannedIpList.isBanned(targetIp)) {
         throw ALREADY_BANNED_EXCEPTION.create();
      } else {
         List list = source.getServer().getPlayerManager().getPlayersByIp(targetIp);
         BannedIpEntry bannedIpEntry = new BannedIpEntry(targetIp, (Date)null, source.getName(), (Date)null, reason == null ? null : reason.getString());
         bannedIpList.add(bannedIpEntry);
         source.sendFeedback(() -> {
            return Text.translatable("commands.banip.success", targetIp, bannedIpEntry.getReason());
         }, true);
         if (!list.isEmpty()) {
            source.sendFeedback(() -> {
               return Text.translatable("commands.banip.info", list.size(), EntitySelector.getNames(list));
            }, true);
         }

         Iterator var6 = list.iterator();

         while(var6.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var6.next();
            serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.ip_banned"));
         }

         return list.size();
      }
   }
}
