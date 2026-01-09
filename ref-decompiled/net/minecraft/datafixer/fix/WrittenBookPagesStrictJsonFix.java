package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;

public class WrittenBookPagesStrictJsonFix extends ItemNbtFix {
   public WrittenBookPagesStrictJsonFix(Schema outputSchema) {
      super(outputSchema, "WrittenBookPagesStrictJsonFix", (string) -> {
         return string.equals("minecraft:written_book");
      });
   }

   protected Typed fix(Typed typed) {
      Type type = this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT);
      Type type2 = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder opticFinder = type2.findField("tag");
      OpticFinder opticFinder2 = opticFinder.type().findField("pages");
      OpticFinder opticFinder3 = DSL.typeFinder(type);
      return typed.updateTyped(opticFinder2, (typedx) -> {
         return typedx.update(opticFinder3, (pair) -> {
            return pair.mapSecond(TextFixes::parseLenientJson);
         });
      });
   }
}
