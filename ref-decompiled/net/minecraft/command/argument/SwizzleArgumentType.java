package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

public class SwizzleArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("xyz", "x");
   private static final SimpleCommandExceptionType INVALID_SWIZZLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("arguments.swizzle.invalid"));

   public static SwizzleArgumentType swizzle() {
      return new SwizzleArgumentType();
   }

   public static EnumSet getSwizzle(CommandContext context, String name) {
      return (EnumSet)context.getArgument(name, EnumSet.class);
   }

   public EnumSet parse(StringReader stringReader) throws CommandSyntaxException {
      EnumSet enumSet = EnumSet.noneOf(Direction.Axis.class);

      while(stringReader.canRead() && stringReader.peek() != ' ') {
         char c = stringReader.read();
         Direction.Axis axis;
         switch (c) {
            case 'x':
               axis = Direction.Axis.X;
               break;
            case 'y':
               axis = Direction.Axis.Y;
               break;
            case 'z':
               axis = Direction.Axis.Z;
               break;
            default:
               throw INVALID_SWIZZLE_EXCEPTION.createWithContext(stringReader);
         }

         if (enumSet.contains(axis)) {
            throw INVALID_SWIZZLE_EXCEPTION.createWithContext(stringReader);
         }

         enumSet.add(axis);
      }

      return enumSet;
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
