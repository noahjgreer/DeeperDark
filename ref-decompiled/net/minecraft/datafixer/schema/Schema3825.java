package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema3825 extends IdentifierNormalizingSchema {
   public Schema3825(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.register(map, "minecraft:ominous_item_spawner", () -> {
         return DSL.optionalFields("item", TypeReferences.ITEM_STACK.in(schema));
      });
      return map;
   }
}
