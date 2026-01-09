package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema3325 extends IdentifierNormalizingSchema {
   public Schema3325(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.register(map, "minecraft:item_display", (string) -> {
         return DSL.optionalFields("item", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.register(map, "minecraft:block_display", (string) -> {
         return DSL.optionalFields("block_state", TypeReferences.BLOCK_STATE.in(schema));
      });
      schema.register(map, "minecraft:text_display", () -> {
         return DSL.optionalFields("text", TypeReferences.TEXT_COMPONENT.in(schema));
      });
      return map;
   }
}
