package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema1928 extends IdentifierNormalizingSchema {
   public Schema1928(int i, Schema schema) {
      super(i, schema);
   }

   protected static void targetEntityItems(Schema schema, Map map, String entityId) {
      schema.registerSimple(map, entityId);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      map.remove("minecraft:illager_beast");
      targetEntityItems(schema, map, "minecraft:ravager");
      return map;
   }
}
