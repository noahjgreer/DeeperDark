package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema3683 extends IdentifierNormalizingSchema {
   public Schema3683(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.register(map, "minecraft:tnt", () -> {
         return DSL.optionalFields("block_state", TypeReferences.BLOCK_STATE.in(schema));
      });
      return map;
   }
}
