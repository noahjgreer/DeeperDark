package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.SequencedMap;
import net.minecraft.datafixer.TypeReferences;

public class Schema4307 extends IdentifierNormalizingSchema {
   public Schema4307(int i, Schema schema) {
      super(i, schema);
   }

   public static SequencedMap method_67118(Schema schema) {
      SequencedMap sequencedMap = Schema4059.method_63584(schema);
      sequencedMap.put("minecraft:can_place_on", () -> {
         return method_67119(schema);
      });
      sequencedMap.put("minecraft:can_break", () -> {
         return method_67119(schema);
      });
      return sequencedMap;
   }

   private static TypeTemplate method_67119(Schema schema) {
      TypeTemplate typeTemplate = DSL.optionalFields("blocks", DSL.or(TypeReferences.BLOCK_NAME.in(schema), DSL.list(TypeReferences.BLOCK_NAME.in(schema))));
      return DSL.or(typeTemplate, DSL.list(typeTemplate));
   }

   public void registerTypes(Schema schema, Map map, Map map2) {
      super.registerTypes(schema, map, map2);
      schema.registerType(true, TypeReferences.DATA_COMPONENTS, () -> {
         return DSL.optionalFieldsLazy(method_67118(schema));
      });
   }
}
