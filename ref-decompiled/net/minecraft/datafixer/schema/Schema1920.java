package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema1920 extends IdentifierNormalizingSchema {
   public Schema1920(int i, Schema schema) {
      super(i, schema);
   }

   protected static void method_17343(Schema schema, Map map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      method_17343(schema, map, "minecraft:campfire");
      return map;
   }
}
