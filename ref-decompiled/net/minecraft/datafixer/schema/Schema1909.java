package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema1909 extends IdentifierNormalizingSchema {
   public Schema1909(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:jigsaw", () -> {
         return DSL.optionalFields("final_state", TypeReferences.FLAT_BLOCK_STATE.in(schema));
      });
      return map;
   }
}
