package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema501 extends Schema {
   public Schema501(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "PolarBear");
      return map;
   }
}
