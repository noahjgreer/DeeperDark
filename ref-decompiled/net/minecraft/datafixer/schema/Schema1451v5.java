package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema1451v5 extends IdentifierNormalizingSchema {
   public Schema1451v5(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      map.remove("minecraft:flower_pot");
      map.remove("minecraft:noteblock");
      return map;
   }
}
