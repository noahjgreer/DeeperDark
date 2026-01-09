package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema2707 extends IdentifierNormalizingSchema {
   public Schema2707(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      this.registerSimple(map, "minecraft:marker");
      return map;
   }
}
