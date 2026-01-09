package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema1488 extends IdentifierNormalizingSchema {
   public Schema1488(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:command_block", () -> {
         return DSL.optionalFields("CustomName", TypeReferences.TEXT_COMPONENT.in(schema), "LastOutput", TypeReferences.TEXT_COMPONENT.in(schema));
      });
      return map;
   }
}
