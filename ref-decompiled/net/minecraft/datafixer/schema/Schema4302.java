package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema4302 extends IdentifierNormalizingSchema {
   public Schema4302(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.registerSimple(map, "minecraft:test_block");
      schema.registerSimple(map, "minecraft:test_instance_block");
      return map;
   }
}
