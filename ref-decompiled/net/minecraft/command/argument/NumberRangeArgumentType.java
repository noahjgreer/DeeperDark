package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.predicate.NumberRange;

public interface NumberRangeArgumentType extends ArgumentType {
   static IntRangeArgumentType intRange() {
      return new IntRangeArgumentType();
   }

   static FloatRangeArgumentType floatRange() {
      return new FloatRangeArgumentType();
   }

   public static class IntRangeArgumentType implements NumberRangeArgumentType {
      private static final Collection EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

      public static NumberRange.IntRange getRangeArgument(CommandContext context, String name) {
         return (NumberRange.IntRange)context.getArgument(name, NumberRange.IntRange.class);
      }

      public NumberRange.IntRange parse(StringReader stringReader) throws CommandSyntaxException {
         return NumberRange.IntRange.parse(stringReader);
      }

      public Collection getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(final StringReader reader) throws CommandSyntaxException {
         return this.parse(reader);
      }
   }

   public static class FloatRangeArgumentType implements NumberRangeArgumentType {
      private static final Collection EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

      public static NumberRange.DoubleRange getRangeArgument(CommandContext context, String name) {
         return (NumberRange.DoubleRange)context.getArgument(name, NumberRange.DoubleRange.class);
      }

      public NumberRange.DoubleRange parse(StringReader stringReader) throws CommandSyntaxException {
         return NumberRange.DoubleRange.parse(stringReader);
      }

      public Collection getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(final StringReader reader) throws CommandSyntaxException {
         return this.parse(reader);
      }
   }
}
