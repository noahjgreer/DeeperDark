package net.minecraft.util.packrat;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public abstract class IdentifiableParsingRule implements ParsingRule, IdentifierSuggestable {
   private final ParsingRuleEntry idParsingRule;
   protected final Object callbacks;
   private final CursorExceptionType exception;

   protected IdentifiableParsingRule(ParsingRuleEntry idParsingRule, Object callbacks) {
      this.idParsingRule = idParsingRule;
      this.callbacks = callbacks;
      this.exception = CursorExceptionType.create(Identifier.COMMAND_EXCEPTION);
   }

   @Nullable
   public Object parse(ParsingState state) {
      ((StringReader)state.getReader()).skipWhitespace();
      int i = state.getCursor();
      Identifier identifier = (Identifier)state.parse(this.idParsingRule);
      if (identifier != null) {
         try {
            return this.parse((ImmutableStringReader)state.getReader(), identifier);
         } catch (Exception var5) {
            state.getErrors().add(i, this, var5);
            return null;
         }
      } else {
         state.getErrors().add(i, this, this.exception);
         return null;
      }
   }

   protected abstract Object parse(ImmutableStringReader reader, Identifier id) throws Exception;
}
