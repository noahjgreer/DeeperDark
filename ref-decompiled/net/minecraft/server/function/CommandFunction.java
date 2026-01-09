package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import net.minecraft.command.SingleCommandAction;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface CommandFunction {
   Identifier id();

   Procedure withMacroReplaced(@Nullable NbtCompound arguments, CommandDispatcher dispatcher) throws MacroException;

   private static boolean continuesToNextLine(CharSequence string) {
      int i = string.length();
      return i > 0 && string.charAt(i - 1) == '\\';
   }

   static CommandFunction create(Identifier id, CommandDispatcher dispatcher, AbstractServerCommandSource source, List lines) {
      FunctionBuilder functionBuilder = new FunctionBuilder();

      for(int i = 0; i < lines.size(); ++i) {
         int j = i + 1;
         String string = ((String)lines.get(i)).trim();
         String string3;
         String string2;
         if (continuesToNextLine(string)) {
            StringBuilder stringBuilder = new StringBuilder(string);

            while(true) {
               ++i;
               if (i == lines.size()) {
                  throw new IllegalArgumentException("Line continuation at end of file");
               }

               stringBuilder.deleteCharAt(stringBuilder.length() - 1);
               string2 = ((String)lines.get(i)).trim();
               stringBuilder.append(string2);
               validateCommandLength(stringBuilder);
               if (!continuesToNextLine(stringBuilder)) {
                  string3 = stringBuilder.toString();
                  break;
               }
            }
         } else {
            string3 = string;
         }

         validateCommandLength(string3);
         StringReader stringReader = new StringReader(string3);
         if (stringReader.canRead() && stringReader.peek() != '#') {
            if (stringReader.peek() == '/') {
               stringReader.skip();
               if (stringReader.peek() == '/') {
                  throw new IllegalArgumentException("Unknown or invalid command '" + string3 + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
               }

               string2 = stringReader.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command '" + string3 + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)");
            }

            if (stringReader.peek() == '$') {
               functionBuilder.addMacroCommand(string3.substring(1), j, source);
            } else {
               try {
                  functionBuilder.addAction(parse(dispatcher, source, stringReader));
               } catch (CommandSyntaxException var11) {
                  throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var11.getMessage());
               }
            }
         }
      }

      return functionBuilder.toCommandFunction(id);
   }

   static void validateCommandLength(CharSequence command) {
      if (command.length() > 2000000) {
         CharSequence charSequence = command.subSequence(0, Math.min(512, 2000000));
         int var10002 = command.length();
         throw new IllegalStateException("Command too long: " + var10002 + " characters, contents: " + String.valueOf(charSequence) + "...");
      }
   }

   static SourcedCommandAction parse(CommandDispatcher dispatcher, AbstractServerCommandSource source, StringReader reader) throws CommandSyntaxException {
      ParseResults parseResults = dispatcher.parse(reader, source);
      CommandManager.throwException(parseResults);
      Optional optional = ContextChain.tryFlatten(parseResults.getContext().build(reader.getString()));
      if (optional.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
      } else {
         return new SingleCommandAction.Sourced(reader.getString(), (ContextChain)optional.get());
      }
   }
}
