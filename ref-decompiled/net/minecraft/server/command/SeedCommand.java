package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class SeedCommand {
   public static void register(CommandDispatcher dispatcher, boolean dedicated) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("seed").requires(CommandManager.requirePermissionLevel(dedicated ? 2 : 0))).executes((context) -> {
         long l = ((ServerCommandSource)context.getSource()).getWorld().getSeed();
         Text text = Texts.bracketedCopyable(String.valueOf(l));
         ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
            return Text.translatable("commands.seed.success", text);
         }, false);
         return (int)l;
      }));
   }
}
