package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;

public class DifficultyCommand {
   private static final DynamicCommandExceptionType FAILURE_EXCEPTION = new DynamicCommandExceptionType((difficulty) -> {
      return Text.stringifiedTranslatable("commands.difficulty.failure", difficulty);
   });

   public static void register(CommandDispatcher dispatcher) {
      LiteralArgumentBuilder literalArgumentBuilder = CommandManager.literal("difficulty");
      Difficulty[] var2 = Difficulty.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Difficulty difficulty = var2[var4];
         literalArgumentBuilder.then(CommandManager.literal(difficulty.getName()).executes((context) -> {
            return execute((ServerCommandSource)context.getSource(), difficulty);
         }));
      }

      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.requires(CommandManager.requirePermissionLevel(2))).executes((context) -> {
         Difficulty difficulty = ((ServerCommandSource)context.getSource()).getWorld().getDifficulty();
         ((ServerCommandSource)context.getSource()).sendFeedback(() -> {
            return Text.translatable("commands.difficulty.query", difficulty.getTranslatableName());
         }, false);
         return difficulty.getId();
      }));
   }

   public static int execute(ServerCommandSource source, Difficulty difficulty) throws CommandSyntaxException {
      MinecraftServer minecraftServer = source.getServer();
      if (minecraftServer.getSaveProperties().getDifficulty() == difficulty) {
         throw FAILURE_EXCEPTION.create(difficulty.getName());
      } else {
         minecraftServer.setDifficulty(difficulty, true);
         source.sendFeedback(() -> {
            return Text.translatable("commands.difficulty.success", difficulty.getTranslatableName());
         }, true);
         return 0;
      }
   }
}
