package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher dispatcher, CommandRegistryAccess commandRegistryAccess) {
      final LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder)CommandManager.literal("gamerule").requires(CommandManager.requirePermissionLevel(2));
      (new GameRules(commandRegistryAccess.getEnabledFeatures())).accept(new GameRules.Visitor() {
         public void visit(GameRules.Key key, GameRules.Type type) {
            LiteralArgumentBuilder literalArgumentBuilderx = CommandManager.literal(key.getName());
            literalArgumentBuilder.then(((LiteralArgumentBuilder)literalArgumentBuilderx.executes((context) -> {
               return GameRuleCommand.executeQuery((ServerCommandSource)context.getSource(), key);
            })).then(type.argument("value").executes((context) -> {
               return GameRuleCommand.executeSet(context, key);
            })));
         }
      });
      dispatcher.register(literalArgumentBuilder);
   }

   static int executeSet(CommandContext context, GameRules.Key key) {
      ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
      GameRules.Rule rule = serverCommandSource.getServer().getGameRules().get(key);
      rule.set(context, "value");
      serverCommandSource.sendFeedback(() -> {
         return Text.translatable("commands.gamerule.set", key.getName(), rule.toString());
      }, true);
      return rule.getCommandResult();
   }

   static int executeQuery(ServerCommandSource source, GameRules.Key key) {
      GameRules.Rule rule = source.getServer().getGameRules().get(key);
      source.sendFeedback(() -> {
         return Text.translatable("commands.gamerule.query", key.getName(), rule.toString());
      }, false);
      return rule.getCommandResult();
   }
}
