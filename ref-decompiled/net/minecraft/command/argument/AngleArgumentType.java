package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class AngleArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("0", "~", "~-5");
   public static final SimpleCommandExceptionType INCOMPLETE_ANGLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.angle.incomplete"));
   public static final SimpleCommandExceptionType INVALID_ANGLE_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.angle.invalid"));

   public static AngleArgumentType angle() {
      return new AngleArgumentType();
   }

   public static float getAngle(CommandContext context, String name) {
      return ((Angle)context.getArgument(name, Angle.class)).getAngle((ServerCommandSource)context.getSource());
   }

   public Angle parse(StringReader stringReader) throws CommandSyntaxException {
      if (!stringReader.canRead()) {
         throw INCOMPLETE_ANGLE_EXCEPTION.createWithContext(stringReader);
      } else {
         boolean bl = CoordinateArgument.isRelative(stringReader);
         float f = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readFloat() : 0.0F;
         if (!Float.isNaN(f) && !Float.isInfinite(f)) {
            return new Angle(f, bl);
         } else {
            throw INVALID_ANGLE_EXCEPTION.createWithContext(stringReader);
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

   public static final class Angle {
      private final float angle;
      private final boolean relative;

      Angle(float angle, boolean relative) {
         this.angle = angle;
         this.relative = relative;
      }

      public float getAngle(ServerCommandSource source) {
         return MathHelper.wrapDegrees(this.relative ? this.angle + source.getRotation().y : this.angle);
      }
   }
}
