package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CommandFunctionArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "#foo");
   private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_TAG_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("arguments.function.tag.unknown", id);
   });
   private static final DynamicCommandExceptionType UNKNOWN_FUNCTION_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("arguments.function.unknown", id);
   });

   public static CommandFunctionArgumentType commandFunction() {
      return new CommandFunctionArgumentType();
   }

   public FunctionArgument parse(StringReader stringReader) throws CommandSyntaxException {
      final Identifier identifier;
      if (stringReader.canRead() && stringReader.peek() == '#') {
         stringReader.skip();
         identifier = Identifier.fromCommandInput(stringReader);
         return new FunctionArgument(this) {
            public Collection getFunctions(CommandContext context) throws CommandSyntaxException {
               return CommandFunctionArgumentType.getFunctionTag(context, identifier);
            }

            public Pair getFunctionOrTag(CommandContext context) throws CommandSyntaxException {
               return Pair.of(identifier, Either.right(CommandFunctionArgumentType.getFunctionTag(context, identifier)));
            }

            public Pair getIdentifiedFunctions(CommandContext context) throws CommandSyntaxException {
               return Pair.of(identifier, CommandFunctionArgumentType.getFunctionTag(context, identifier));
            }
         };
      } else {
         identifier = Identifier.fromCommandInput(stringReader);
         return new FunctionArgument(this) {
            public Collection getFunctions(CommandContext context) throws CommandSyntaxException {
               return Collections.singleton(CommandFunctionArgumentType.getFunction(context, identifier));
            }

            public Pair getFunctionOrTag(CommandContext context) throws CommandSyntaxException {
               return Pair.of(identifier, Either.left(CommandFunctionArgumentType.getFunction(context, identifier)));
            }

            public Pair getIdentifiedFunctions(CommandContext context) throws CommandSyntaxException {
               return Pair.of(identifier, Collections.singleton(CommandFunctionArgumentType.getFunction(context, identifier)));
            }
         };
      }
   }

   static CommandFunction getFunction(CommandContext context, Identifier id) throws CommandSyntaxException {
      return (CommandFunction)((ServerCommandSource)context.getSource()).getServer().getCommandFunctionManager().getFunction(id).orElseThrow(() -> {
         return UNKNOWN_FUNCTION_EXCEPTION.create(id.toString());
      });
   }

   static Collection getFunctionTag(CommandContext context, Identifier id) throws CommandSyntaxException {
      Collection collection = ((ServerCommandSource)context.getSource()).getServer().getCommandFunctionManager().getTag(id);
      if (collection == null) {
         throw UNKNOWN_FUNCTION_TAG_EXCEPTION.create(id.toString());
      } else {
         return collection;
      }
   }

   public static Collection getFunctions(CommandContext context, String name) throws CommandSyntaxException {
      return ((FunctionArgument)context.getArgument(name, FunctionArgument.class)).getFunctions(context);
   }

   public static Pair getFunctionOrTag(CommandContext context, String name) throws CommandSyntaxException {
      return ((FunctionArgument)context.getArgument(name, FunctionArgument.class)).getFunctionOrTag(context);
   }

   public static Pair getIdentifiedFunctions(CommandContext context, String name) throws CommandSyntaxException {
      return ((FunctionArgument)context.getArgument(name, FunctionArgument.class)).getIdentifiedFunctions(context);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   public interface FunctionArgument {
      Collection getFunctions(CommandContext context) throws CommandSyntaxException;

      Pair getFunctionOrTag(CommandContext context) throws CommandSyntaxException;

      Pair getIdentifiedFunctions(CommandContext context) throws CommandSyntaxException;
   }
}
