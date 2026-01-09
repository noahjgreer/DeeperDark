package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class OperationArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("=", ">", "<");
   private static final SimpleCommandExceptionType INVALID_OPERATION = new SimpleCommandExceptionType(Text.translatable("arguments.operation.invalid"));
   private static final SimpleCommandExceptionType DIVISION_ZERO_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("arguments.operation.div0"));

   public static OperationArgumentType operation() {
      return new OperationArgumentType();
   }

   public static Operation getOperation(CommandContext context, String name) {
      return (Operation)context.getArgument(name, Operation.class);
   }

   public Operation parse(StringReader stringReader) throws CommandSyntaxException {
      if (!stringReader.canRead()) {
         throw INVALID_OPERATION.createWithContext(stringReader);
      } else {
         int i = stringReader.getCursor();

         while(stringReader.canRead() && stringReader.peek() != ' ') {
            stringReader.skip();
         }

         return getOperator(stringReader.getString().substring(i, stringReader.getCursor()));
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.suggestMatching(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   private static Operation getOperator(String operator) throws CommandSyntaxException {
      return (Operation)(operator.equals("><") ? (a, b) -> {
         int i = a.getScore();
         a.setScore(b.getScore());
         b.setScore(i);
      } : getIntOperator(operator));
   }

   private static IntOperator getIntOperator(String operator) throws CommandSyntaxException {
      IntOperator var10000;
      switch (operator) {
         case "=":
            var10000 = (a, b) -> {
               return b;
            };
            break;
         case "+=":
            var10000 = Integer::sum;
            break;
         case "-=":
            var10000 = (a, b) -> {
               return a - b;
            };
            break;
         case "*=":
            var10000 = (a, b) -> {
               return a * b;
            };
            break;
         case "/=":
            var10000 = (a, b) -> {
               if (b == 0) {
                  throw DIVISION_ZERO_EXCEPTION.create();
               } else {
                  return MathHelper.floorDiv(a, b);
               }
            };
            break;
         case "%=":
            var10000 = (a, b) -> {
               if (b == 0) {
                  throw DIVISION_ZERO_EXCEPTION.create();
               } else {
                  return MathHelper.floorMod(a, b);
               }
            };
            break;
         case "<":
            var10000 = Math::min;
            break;
         case ">":
            var10000 = Math::max;
            break;
         default:
            throw INVALID_OPERATION.create();
      }

      return var10000;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   @FunctionalInterface
   public interface Operation {
      void apply(ScoreAccess a, ScoreAccess b) throws CommandSyntaxException;
   }

   @FunctionalInterface
   private interface IntOperator extends Operation {
      int apply(int a, int b) throws CommandSyntaxException;

      default void apply(ScoreAccess scoreAccess, ScoreAccess scoreAccess2) throws CommandSyntaxException {
         scoreAccess.setScore(this.apply(scoreAccess.getScore(), scoreAccess2.getScore()));
      }
   }
}
