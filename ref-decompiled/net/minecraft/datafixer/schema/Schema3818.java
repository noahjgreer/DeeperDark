package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema3818 extends IdentifierNormalizingSchema {
   public Schema3818(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:beehive", () -> {
         return DSL.optionalFields("bees", DSL.list(DSL.optionalFields("entity_data", TypeReferences.ENTITY_TREE.in(schema))));
      });
      return map;
   }
}
