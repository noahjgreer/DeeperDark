package net.minecraft.server.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class DebugConfigCommand {
   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess registryAccess) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("debugconfig").requires(CommandManager.requirePermissionLevel(3))).then(CommandManager.literal("config").then(CommandManager.argument("target", EntityArgumentType.player()).executes((context) -> {
         return executeConfig((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayer(context, "target"));
      })))).then(CommandManager.literal("unconfig").then(CommandManager.argument("target", UuidArgumentType.uuid()).suggests((context, suggestionsBuilder) -> {
         return CommandSource.suggestMatching(collectConfiguringPlayers(((ServerCommandSource)context.getSource()).getServer()), suggestionsBuilder);
      }).executes((context) -> {
         return executeUnconfig((ServerCommandSource)context.getSource(), UuidArgumentType.getUuid(context, "target"));
      })))).then(CommandManager.literal("dialog").then(CommandManager.argument("target", UuidArgumentType.uuid()).suggests((context, suggestionsBuilder) -> {
         return CommandSource.suggestMatching(collectConfiguringPlayers(((ServerCommandSource)context.getSource()).getServer()), suggestionsBuilder);
      }).then(CommandManager.argument("dialog", RegistryEntryArgumentType.dialog(registryAccess)).executes((context) -> {
         return executeDialog((ServerCommandSource)context.getSource(), UuidArgumentType.getUuid(context, "target"), RegistryEntryArgumentType.getDialog(context, "dialog"));
      })))));
   }

   private static Iterable collectConfiguringPlayers(MinecraftServer server) {
      Set set = new HashSet();
      Iterator var2 = server.getNetworkIo().getConnections().iterator();

      while(var2.hasNext()) {
         ClientConnection clientConnection = (ClientConnection)var2.next();
         PacketListener var5 = clientConnection.getPacketListener();
         if (var5 instanceof ServerConfigurationNetworkHandler serverConfigurationNetworkHandler) {
            set.add(serverConfigurationNetworkHandler.getDebugProfile().getId().toString());
         }
      }

      return set;
   }

   private static int executeConfig(ServerCommandSource source, ServerPlayerEntity player) {
      GameProfile gameProfile = player.getGameProfile();
      player.networkHandler.reconfigure();
      source.sendFeedback(() -> {
         String var10000 = gameProfile.getName();
         return Text.literal("Switched player " + var10000 + "(" + String.valueOf(gameProfile.getId()) + ") to config mode");
      }, false);
      return 1;
   }

   @Nullable
   private static ServerConfigurationNetworkHandler findConfigurationNetworkHandler(MinecraftServer server, UUID uuid) {
      Iterator var2 = server.getNetworkIo().getConnections().iterator();

      while(var2.hasNext()) {
         ClientConnection clientConnection = (ClientConnection)var2.next();
         PacketListener var5 = clientConnection.getPacketListener();
         if (var5 instanceof ServerConfigurationNetworkHandler serverConfigurationNetworkHandler) {
            if (serverConfigurationNetworkHandler.getDebugProfile().getId().equals(uuid)) {
               return serverConfigurationNetworkHandler;
            }
         }
      }

      return null;
   }

   private static int executeUnconfig(ServerCommandSource source, UUID uuid) {
      ServerConfigurationNetworkHandler serverConfigurationNetworkHandler = findConfigurationNetworkHandler(source.getServer(), uuid);
      if (serverConfigurationNetworkHandler != null) {
         serverConfigurationNetworkHandler.endConfiguration();
         return 1;
      } else {
         source.sendError(Text.literal("Can't find player to unconfig"));
         return 0;
      }
   }

   private static int executeDialog(ServerCommandSource source, UUID uuid, RegistryEntry dialog) {
      ServerConfigurationNetworkHandler serverConfigurationNetworkHandler = findConfigurationNetworkHandler(source.getServer(), uuid);
      if (serverConfigurationNetworkHandler != null) {
         serverConfigurationNetworkHandler.sendPacket(new ShowDialogS2CPacket(dialog));
         return 1;
      } else {
         source.sendError(Text.literal("Can't find player to talk to"));
         return 0;
      }
   }
}
