package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class EntityFallDistanceFloatToDoubleFix extends DataFix {
   private DSL.TypeReference field_55934;

   public EntityFallDistanceFloatToDoubleFix(Schema outputSchema, DSL.TypeReference typeReference) {
      super(outputSchema, false);
      this.field_55934 = typeReference;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityFallDistanceFloatToDoubleFixFor" + this.field_55934.typeName(), this.getOutputSchema().getType(this.field_55934), EntityFallDistanceFloatToDoubleFix::fixFallDistance);
   }

   private static Typed fixFallDistance(Typed typed) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         return dynamic.renameAndFixField("FallDistance", "fall_distance", (dynamicx) -> {
            return dynamicx.createDouble((double)dynamicx.asFloat(0.0F));
         });
      });
   }
}
