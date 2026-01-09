package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public class MapBannerBlockPosFormatFix extends DataFix {
   public MapBannerBlockPosFormatFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.SAVED_DATA_MAP_DATA);
      OpticFinder opticFinder = type.findField("data");
      OpticFinder opticFinder2 = opticFinder.type().findField("banners");
      OpticFinder opticFinder3 = DSL.typeFinder(((List.ListType)opticFinder2.type()).getElement());
      return this.fixTypeEverywhereTyped("MapBannerBlockPosFormatFix", type, (typed) -> {
         return typed.updateTyped(opticFinder, (typedx) -> {
            return typedx.updateTyped(opticFinder2, (typed) -> {
               return typed.updateTyped(opticFinder3, (typedx) -> {
                  return typedx.update(DSL.remainderFinder(), (dynamic) -> {
                     return dynamic.update("Pos", FixUtil::fixBlockPos);
                  });
               });
            });
         });
      });
   }
}
