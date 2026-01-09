package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema3799 extends IdentifierNormalizingSchema {
   public Schema3799(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:armadillo");
      return map;
   }
}
