package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema3813 extends IdentifierNormalizingSchema {
   public Schema3813(int i, Schema schema) {
      super(i, schema);
   }

   public void registerTypes(Schema schema, Map map, Map map2) {
      super.registerTypes(schema, map, map2);
      schema.registerType(false, TypeReferences.SAVED_DATA_MAP_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("banners", DSL.list(DSL.optionalFields("name", TypeReferences.TEXT_COMPONENT.in(schema)))));
      });
   }
}
