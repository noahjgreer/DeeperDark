package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class BanCommand {
   private static final SimpleCommandExceptionType ALREADY_BANNED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.ban.failed"));

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("ban").requires(CommandManager.requirePermissionLevel(3))).then(((RequiredArgumentBuilder)CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).executes((context) -> {
         return ban((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), (Text)null);
      })).then(CommandManager.argument("reason", MessageArgumentType.message()).executes((context) -> {
         return ban((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), MessageArgumentType.getMessage(context, "reason"));
      }))));
   }

   private static int ban(ServerCommandSource source, Collection targets, @Nullable Text reason) throws CommandSyntaxException {
      BannedPlayerList bannedPlayerList = source.getServer().getPlayerManager().getUserBanList();
      int i = 0;
      Iterator var5 = targets.iterator();

      while(var5.hasNext()) {
         GameProfile gameProfile = (GameProfile)var5.next();
         if (!bannedPlayerList.contains(gameProfile)) {
            BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(gameProfile, (Date)null, source.getName(), (Date)null, reason == null ? null : reason.getString());
            bannedPlayerList.add(bannedPlayerEntry);
            ++i;
            source.sendFeedback(() -> {
               return Text.translatable("commands.ban.success", Text.literal(gameProfile.getName()), bannedPlayerEntry.getReason());
            }, true);
            ServerPlayerEntity serverPlayerEntity = source.getServer().getPlayerManager().getPlayer(gameProfile.getId());
            if (serverPlayerEntity != null) {
               serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.banned"));
            }
         }
      }

      if (i == 0) {
         throw ALREADY_BANNED_EXCEPTION.create();
      } else {
         return i;
      }
   }
}
