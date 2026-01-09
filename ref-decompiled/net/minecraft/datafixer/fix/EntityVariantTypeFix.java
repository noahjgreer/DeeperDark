package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.function.Function;
import java.util.function.IntFunction;

public class EntityVariantTypeFix extends ChoiceFix {
   private final String variantKey;
   private final IntFunction variantIntToId;

   public EntityVariantTypeFix(Schema outputSchema, String name, DSL.TypeReference type, String entityId, String variantKey, IntFunction variantIntToId) {
      super(outputSchema, false, name, type, entityId);
      this.variantKey = variantKey;
      this.variantIntToId = variantIntToId;
   }

   private static Dynamic updateEntity(Dynamic entityDynamic, String oldVariantKey, String newVariantKey, Function variantIntToId) {
      return entityDynamic.map((object) -> {
         DynamicOps dynamicOps = entityDynamic.getOps();
         Function function2 = (objectx) -> {
            return ((Dynamic)variantIntToId.apply(new Dynamic(dynamicOps, objectx))).getValue();
         };
         return dynamicOps.get(object, oldVariantKey).map((object2) -> {
            return dynamicOps.set(object, newVariantKey, function2.apply(object2));
         }).result().orElse(object);
      });
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), (entityDynamic) -> {
         return updateEntity(entityDynamic, this.variantKey, "variant", (variantDynamic) -> {
            return (Dynamic)DataFixUtils.orElse(variantDynamic.asNumber().map((variantInt) -> {
               return variantDynamic.createString((String)this.variantIntToId.apply(variantInt.intValue()));
            }).result(), variantDynamic);
         });
      });
   }
}
