package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.text.Text;

public class RotationArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");
   public static final SimpleCommandExceptionType INCOMPLETE_ROTATION_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.rotation.incomplete"));

   public static RotationArgumentType rotation() {
      return new RotationArgumentType();
   }

   public static PosArgument getRotation(CommandContext context, String name) {
      return (PosArgument)context.getArgument(name, PosArgument.class);
   }

   public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
      int i = stringReader.getCursor();
      if (!stringReader.canRead()) {
         throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
      } else {
         CoordinateArgument coordinateArgument = CoordinateArgument.parse(stringReader, false);
         if (stringReader.canRead() && stringReader.peek() == ' ') {
            stringReader.skip();
            CoordinateArgument coordinateArgument2 = CoordinateArgument.parse(stringReader, false);
            return new DefaultPosArgument(coordinateArgument2, coordinateArgument, new CoordinateArgument(true, 0.0));
         } else {
            stringReader.setCursor(i);
            throw INCOMPLETE_ROTATION_EXCEPTION.createWithContext(stringReader);
         }
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
