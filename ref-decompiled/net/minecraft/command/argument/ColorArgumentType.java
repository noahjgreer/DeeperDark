package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ColorArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("red", "green");
   public static final DynamicCommandExceptionType INVALID_COLOR_EXCEPTION = new DynamicCommandExceptionType((color) -> {
      return Text.stringifiedTranslatable("argument.color.invalid", color);
   });

   private ColorArgumentType() {
   }

   public static ColorArgumentType color() {
      return new ColorArgumentType();
   }

   public static Formatting getColor(CommandContext context, String name) {
      return (Formatting)context.getArgument(name, Formatting.class);
   }

   public Formatting parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readUnquotedString();
      Formatting formatting = Formatting.byName(string);
      if (formatting != null && !formatting.isModifier()) {
         return formatting;
      } else {
         throw INVALID_COLOR_EXCEPTION.createWithContext(stringReader, string);
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching((Iterable)Formatting.getNames(true, false), builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
