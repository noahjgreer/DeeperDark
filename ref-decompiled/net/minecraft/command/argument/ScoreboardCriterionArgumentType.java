package net.minecraft.command.argument;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.text.Text;

public class ScoreboardCriterionArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
   public static final DynamicCommandExceptionType INVALID_CRITERION_EXCEPTION = new DynamicCommandExceptionType((name) -> {
      return Text.stringifiedTranslatable("argument.criteria.invalid", name);
   });

   private ScoreboardCriterionArgumentType() {
   }

   public static ScoreboardCriterionArgumentType scoreboardCriterion() {
      return new ScoreboardCriterionArgumentType();
   }

   public static ScoreboardCriterion getCriterion(CommandContext context, String name) {
      return (ScoreboardCriterion)context.getArgument(name, ScoreboardCriterion.class);
   }

   public ScoreboardCriterion parse(StringReader stringReader) throws CommandSyntaxException {
      int i = stringReader.getCursor();

      while(stringReader.canRead() && stringReader.peek() != ' ') {
         stringReader.skip();
      }

      String string = stringReader.getString().substring(i, stringReader.getCursor());
      return (ScoreboardCriterion)ScoreboardCriterion.getOrCreateStatCriterion(string).orElseThrow(() -> {
         stringReader.setCursor(i);
         return INVALID_CRITERION_EXCEPTION.createWithContext(stringReader, string);
      });
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      List list = Lists.newArrayList(ScoreboardCriterion.getAllSimpleCriteria());
      Iterator var4 = Registries.STAT_TYPE.iterator();

      while(var4.hasNext()) {
         StatType statType = (StatType)var4.next();
         Iterator var6 = statType.getRegistry().iterator();

         while(var6.hasNext()) {
            Object object = var6.next();
            String string = this.getStatName(statType, object);
            list.add(string);
         }
      }

      return CommandSource.suggestMatching((Iterable)list, builder);
   }

   public String getStatName(StatType stat, Object value) {
      return Stat.getName(stat, value);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
