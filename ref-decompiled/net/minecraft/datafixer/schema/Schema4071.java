package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema4071 extends IdentifierNormalizingSchema {
   public Schema4071(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:creaking");
      schema.registerSimple(map, "minecraft:creaking_transient");
      return map;
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      this.registerSimple(map, "minecraft:creaking_heart");
      return map;
   }
}
