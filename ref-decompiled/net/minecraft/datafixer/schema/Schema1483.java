package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.function.Supplier;

public class Schema1483 extends IdentifierNormalizingSchema {
   public Schema1483(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      map.put("minecraft:pufferfish", (Supplier)map.remove("minecraft:puffer_fish"));
      return map;
   }
}
