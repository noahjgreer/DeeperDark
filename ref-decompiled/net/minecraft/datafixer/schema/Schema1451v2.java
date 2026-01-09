package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema1451v2 extends IdentifierNormalizingSchema {
   public Schema1451v2(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:piston", (name) -> {
         return DSL.optionalFields("blockState", TypeReferences.BLOCK_STATE.in(schema));
      });
      return map;
   }
}
