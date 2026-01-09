package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class PointOfInterestReorganizationFix extends DataFix {
   public PointOfInterestReorganizationFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   protected TypeRewriteRule makeRule() {
      Type type = DSL.named(TypeReferences.POI_CHUNK.typeName(), DSL.remainderType());
      if (!Objects.equals(type, this.getInputSchema().getType(TypeReferences.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI reorganization", type, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(PointOfInterestReorganizationFix::reorganize);
            };
         });
      }
   }

   private static Dynamic reorganize(Dynamic dynamic) {
      Map map = Maps.newHashMap();

      for(int i = 0; i < 16; ++i) {
         String string = String.valueOf(i);
         Optional optional = dynamic.get(string).result();
         if (optional.isPresent()) {
            Dynamic dynamic2 = (Dynamic)optional.get();
            Dynamic dynamic3 = dynamic.createMap(ImmutableMap.of(dynamic.createString("Records"), dynamic2));
            map.put(dynamic.createString(Integer.toString(i)), dynamic3);
            dynamic = dynamic.remove(string);
         }
      }

      return dynamic.set("Sections", dynamic.createMap(map));
   }
}
