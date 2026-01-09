package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.packrat.ParsingRule;
import net.minecraft.util.packrat.ParsingState;
import org.jetbrains.annotations.Nullable;

public class NbtParsingRule implements ParsingRule {
   private final StringNbtReader nbtReader;

   public NbtParsingRule(DynamicOps ops) {
      this.nbtReader = StringNbtReader.fromOps(ops);
   }

   @Nullable
   public Dynamic parse(ParsingState parsingState) {
      ((StringReader)parsingState.getReader()).skipWhitespace();
      int i = parsingState.getCursor();

      try {
         return new Dynamic(this.nbtReader.getOps(), this.nbtReader.readAsArgument((StringReader)parsingState.getReader()));
      } catch (Exception var4) {
         parsingState.getErrors().add(i, var4);
         return null;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParsingState state) {
      return this.parse(state);
   }
}
