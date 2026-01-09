package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.packrat.Parser;

public abstract class ParserBackedArgumentType implements ArgumentType {
   private final Parser parser;

   public ParserBackedArgumentType(Parser parser) {
      this.parser = parser;
   }

   public Object parse(StringReader reader) throws CommandSyntaxException {
      return this.parser.parse(reader);
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return this.parser.listSuggestions(builder);
   }
}
