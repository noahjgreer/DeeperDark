package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;

public class VersionCommand {
   private static final Text HEADER_TEXT = Text.translatable("commands.version.header");
   private static final Text STABLE_YES_TEXT = Text.translatable("commands.version.stable.yes");
   private static final Text STABLE_NO_TEXT = Text.translatable("commands.version.stable.no");

   public static void register(CommandDispatcher dispatcher, boolean dedicated) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("version").requires(CommandManager.requirePermissionLevel(dedicated ? 2 : 0))).executes((context) -> {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         serverCommandSource.sendMessage(HEADER_TEXT);
         Objects.requireNonNull(serverCommandSource);
         acceptInfo(serverCommandSource::sendMessage);
         return 1;
      }));
   }

   public static void acceptInfo(Consumer sender) {
      GameVersion gameVersion = SharedConstants.getGameVersion();
      sender.accept(Text.translatable("commands.version.id", gameVersion.id()));
      sender.accept(Text.translatable("commands.version.name", gameVersion.name()));
      sender.accept(Text.translatable("commands.version.data", gameVersion.dataVersion().id()));
      sender.accept(Text.translatable("commands.version.series", gameVersion.dataVersion().series()));
      Object[] var10002 = new Object[]{gameVersion.protocolVersion(), null};
      String var10005 = Integer.toHexString(gameVersion.protocolVersion());
      var10002[1] = "0x" + var10005;
      sender.accept(Text.translatable("commands.version.protocol", var10002));
      sender.accept(Text.translatable("commands.version.build_time", Text.of(gameVersion.buildTime())));
      sender.accept(Text.translatable("commands.version.pack.resource", gameVersion.packVersion(ResourceType.CLIENT_RESOURCES)));
      sender.accept(Text.translatable("commands.version.pack.data", gameVersion.packVersion(ResourceType.SERVER_DATA)));
      sender.accept(gameVersion.stable() ? STABLE_YES_TEXT : STABLE_NO_TEXT);
   }
}
