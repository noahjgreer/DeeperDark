package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema808 extends IdentifierNormalizingSchema {
   public Schema808(int i, Schema schema) {
      super(i, schema);
   }

   protected static void targetItems(Schema schema, Map map, String blockEntityId) {
      schema.register(map, blockEntityId, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      targetItems(schema, map, "minecraft:shulker_box");
      return map;
   }
}
