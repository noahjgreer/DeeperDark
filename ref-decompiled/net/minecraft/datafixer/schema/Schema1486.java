package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.function.Supplier;

public class Schema1486 extends IdentifierNormalizingSchema {
   public Schema1486(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      map.put("minecraft:cod", (Supplier)map.remove("minecraft:cod_mob"));
      map.put("minecraft:salmon", (Supplier)map.remove("minecraft:salmon_mob"));
      return map;
   }
}
