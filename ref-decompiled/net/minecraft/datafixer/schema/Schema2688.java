package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema2688 extends IdentifierNormalizingSchema {
   public Schema2688(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:glow_squid");
      schema.register(map, "minecraft:glow_item_frame", (string) -> {
         return DSL.optionalFields("Item", TypeReferences.ITEM_STACK.in(schema));
      });
      return map;
   }
}
