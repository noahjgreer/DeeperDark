package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class HexColorArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("F00", "FF0000");
   public static final DynamicCommandExceptionType INVALID_HEX_COLOR_EXCEPTION = new DynamicCommandExceptionType((hexColor) -> {
      return Text.stringifiedTranslatable("argument.hexcolor.invalid", hexColor);
   });

   private HexColorArgumentType() {
   }

   public static HexColorArgumentType hexColor() {
      return new HexColorArgumentType();
   }

   public static Integer getArgbColor(CommandContext context, String hex) {
      return (Integer)context.getArgument(hex, Integer.class);
   }

   public Integer parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readUnquotedString();
      Integer var10000;
      switch (string.length()) {
         case 3:
            var10000 = ColorHelper.getArgb(Integer.valueOf(MessageFormat.format("{0}{0}", string.charAt(0)), 16), Integer.valueOf(MessageFormat.format("{0}{0}", string.charAt(1)), 16), Integer.valueOf(MessageFormat.format("{0}{0}", string.charAt(2)), 16));
            break;
         case 6:
            var10000 = ColorHelper.getArgb(Integer.valueOf(string.substring(0, 2), 16), Integer.valueOf(string.substring(2, 4), 16), Integer.valueOf(string.substring(4, 6), 16));
            break;
         default:
            throw INVALID_HEX_COLOR_EXCEPTION.createWithContext(stringReader, string);
      }

      return var10000;
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching((Iterable)EXAMPLES, builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
