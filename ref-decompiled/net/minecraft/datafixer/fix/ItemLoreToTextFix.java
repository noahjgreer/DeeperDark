package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;

public class ItemLoreToTextFix extends DataFix {
   public ItemLoreToTextFix(Schema schema) {
      super(schema, false);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      Type type2 = this.getInputSchema().getType(TypeReferences.TEXT_COMPONENT);
      OpticFinder opticFinder = type.findField("tag");
      OpticFinder opticFinder2 = opticFinder.type().findField("display");
      OpticFinder opticFinder3 = opticFinder2.type().findField("Lore");
      OpticFinder opticFinder4 = DSL.typeFinder(type2);
      return this.fixTypeEverywhereTyped("Item Lore componentize", type, (typed) -> {
         return typed.updateTyped(opticFinder, (typedx) -> {
            return typedx.updateTyped(opticFinder2, (typed) -> {
               return typed.updateTyped(opticFinder3, (typedx) -> {
                  return typedx.update(opticFinder4, (pair) -> {
                     return pair.mapSecond(TextFixes::text);
                  });
               });
            });
         });
      });
   }
}
