package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;

public class SignTextStrictJsonFix extends ChoiceFix {
   private static final List TEXT_KEYS = List.of("Text1", "Text2", "Text3", "Text4");

   public SignTextStrictJsonFix(Schema outputSchema) {
      super(outputSchema, false, "SignTextStrictJsonFix", TypeReferences.BLOCK_ENTITY, "Sign");
   }

   protected Typed transform(Typed inputTyped) {
      OpticFinder opticFinder;
      OpticFinder opticFinder2;
      for(Iterator var2 = TEXT_KEYS.iterator(); var2.hasNext(); inputTyped = inputTyped.updateTyped(opticFinder, (typed) -> {
         return typed.update(opticFinder2, (pair) -> {
            return pair.mapSecond(TextFixes::parseLenientJson);
         });
      })) {
         String string = (String)var2.next();
         opticFinder = inputTyped.getType().findField(string);
         opticFinder2 = DSL.typeFinder(this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT));
      }

      return inputTyped;
   }
}
