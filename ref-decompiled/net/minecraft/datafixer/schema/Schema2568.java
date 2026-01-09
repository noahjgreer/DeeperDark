package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema2568 extends IdentifierNormalizingSchema {
   public Schema2568(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:piglin_brute");
      return map;
   }
}
