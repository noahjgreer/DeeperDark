package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ScoreboardObjectiveArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "*", "012");
   private static final DynamicCommandExceptionType UNKNOWN_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType((name) -> {
      return Text.stringifiedTranslatable("arguments.objective.notFound", name);
   });
   private static final DynamicCommandExceptionType READONLY_OBJECTIVE_EXCEPTION = new DynamicCommandExceptionType((name) -> {
      return Text.stringifiedTranslatable("arguments.objective.readonly", name);
   });

   public static ScoreboardObjectiveArgumentType scoreboardObjective() {
      return new ScoreboardObjectiveArgumentType();
   }

   public static ScoreboardObjective getObjective(CommandContext context, String name) throws CommandSyntaxException {
      String string = (String)context.getArgument(name, String.class);
      Scoreboard scoreboard = ((ServerCommandSource)context.getSource()).getServer().getScoreboard();
      ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
      if (scoreboardObjective == null) {
         throw UNKNOWN_OBJECTIVE_EXCEPTION.create(string);
      } else {
         return scoreboardObjective;
      }
   }

   public static ScoreboardObjective getWritableObjective(CommandContext context, String name) throws CommandSyntaxException {
      ScoreboardObjective scoreboardObjective = getObjective(context, name);
      if (scoreboardObjective.getCriterion().isReadOnly()) {
         throw READONLY_OBJECTIVE_EXCEPTION.create(scoreboardObjective.getName());
      } else {
         return scoreboardObjective;
      }
   }

   public String parse(StringReader stringReader) throws CommandSyntaxException {
      return stringReader.readUnquotedString();
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      Object object = context.getSource();
      if (object instanceof ServerCommandSource serverCommandSource) {
         return CommandSource.suggestMatching((Iterable)serverCommandSource.getServer().getScoreboard().getObjectiveNames(), builder);
      } else if (object instanceof CommandSource commandSource) {
         return commandSource.getCompletions(context);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
