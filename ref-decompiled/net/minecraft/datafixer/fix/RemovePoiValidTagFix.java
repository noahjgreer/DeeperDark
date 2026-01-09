package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class RemovePoiValidTagFix extends DataFix {
   public RemovePoiValidTagFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   protected TypeRewriteRule makeRule() {
      Type type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI rebuild", type, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(RemovePoiValidTagFix::removeValidTag);
            };
         });
      }
   }

   private static Dynamic removeValidTag(Dynamic dynamic) {
      return dynamic.update("Sections", (dynamicx) -> {
         return dynamicx.updateMapValues((pair) -> {
            return pair.mapSecond((dynamic) -> {
               return dynamic.remove("Valid");
            });
         });
      });
   }
}
