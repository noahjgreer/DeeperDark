package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema3682 extends IdentifierNormalizingSchema {
   public Schema3682(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:crafter", () -> {
         return Schema1458.itemsAndCustomName(schema);
      });
      return map;
   }
}
