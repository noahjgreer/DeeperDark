package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.SequencedMap;
import net.minecraft.datafixer.TypeReferences;

public class Schema4059 extends IdentifierNormalizingSchema {
   public Schema4059(int i, Schema schema) {
      super(i, schema);
   }

   public static SequencedMap method_63584(Schema schema) {
      SequencedMap sequencedMap = Schema3818_3.method_63573(schema);
      sequencedMap.remove("minecraft:food");
      sequencedMap.put("minecraft:use_remainder", () -> {
         return TypeReferences.ITEM_STACK.in(schema);
      });
      sequencedMap.put("minecraft:equippable", () -> {
         return DSL.optionalFields("allowed_entities", DSL.or(TypeReferences.ENTITY_NAME.in(schema), DSL.list(TypeReferences.ENTITY_NAME.in(schema))));
      });
      return sequencedMap;
   }

   public void registerTypes(Schema schema, Map map, Map map2) {
      super.registerTypes(schema, map, map2);
      schema.registerType(true, TypeReferences.DATA_COMPONENTS, () -> {
         return DSL.optionalFieldsLazy(method_63584(schema));
      });
   }
}
