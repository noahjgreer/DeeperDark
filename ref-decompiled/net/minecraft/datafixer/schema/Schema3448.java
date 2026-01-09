package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema3448 extends IdentifierNormalizingSchema {
   public Schema3448(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:decorated_pot", () -> {
         return DSL.optionalFields("sherds", DSL.list(TypeReferences.ITEM_NAME.in(schema)), "item", TypeReferences.ITEM_STACK.in(schema));
      });
      return map;
   }
}
