package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AnyIdParsingRule implements ParsingRule {
   public static final ParsingRule INSTANCE = new AnyIdParsingRule();

   private AnyIdParsingRule() {
   }

   @Nullable
   public Identifier parse(ParsingState parsingState) {
      ((StringReader)parsingState.getReader()).skipWhitespace();

      try {
         return Identifier.fromCommandInputNonEmpty((StringReader)parsingState.getReader());
      } catch (CommandSyntaxException var3) {
         return null;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParsingState state) {
      return this.parse(state);
   }
}
