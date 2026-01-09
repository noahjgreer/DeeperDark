package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;

public class DisplayNameFix extends DataFix {
   private final String name;
   private final DSL.TypeReference typeReference;

   public DisplayNameFix(Schema outputSchema, String name, DSL.TypeReference typeReference) {
      super(outputSchema, false);
      this.name = name;
      this.typeReference = typeReference;
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(this.typeReference);
      OpticFinder opticFinder = type.findField("DisplayName");
      OpticFinder opticFinder2 = DSL.typeFinder(this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT));
      return this.fixTypeEverywhereTyped(this.name, type, (typed) -> {
         return typed.updateTyped(opticFinder, (typedx) -> {
            return typedx.update(opticFinder2, (pair) -> {
               return pair.mapSecond(TextFixes::text);
            });
         });
      });
   }
}
