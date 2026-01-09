package net.minecraft.server.dedicated.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class OpCommand {
   private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.op.failed"));

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("op").requires(CommandManager.requirePermissionLevel(3))).then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile()).suggests((context, builder) -> {
         PlayerManager playerManager = ((ServerCommandSource)context.getSource()).getServer().getPlayerManager();
         return CommandSource.suggestMatching(playerManager.getPlayerList().stream().filter((player) -> {
            return !playerManager.isOperator(player.getGameProfile());
         }).map((player) -> {
            return player.getGameProfile().getName();
         }), builder);
      }).executes((context) -> {
         return op((ServerCommandSource)context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"));
      })));
   }

   private static int op(ServerCommandSource source, Collection targets) throws CommandSyntaxException {
      PlayerManager playerManager = source.getServer().getPlayerManager();
      int i = 0;
      Iterator var4 = targets.iterator();

      while(var4.hasNext()) {
         GameProfile gameProfile = (GameProfile)var4.next();
         if (!playerManager.isOperator(gameProfile)) {
            playerManager.addToOperators(gameProfile);
            ++i;
            source.sendFeedback(() -> {
               return Text.translatable("commands.op.success", gameProfile.getName());
            }, true);
         }
      }

      if (i == 0) {
         throw ALREADY_OPPED_EXCEPTION.create();
      } else {
         return i;
      }
   }
}
