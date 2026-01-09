package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema2100 extends IdentifierNormalizingSchema {
   public Schema2100(int i, Schema schema) {
      super(i, schema);
   }

   protected static void registerEntity(Schema schema, Map entityTypes, String name) {
      schema.registerSimple(entityTypes, name);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      registerEntity(schema, map, "minecraft:bee");
      registerEntity(schema, map, "minecraft:bee_stinger");
      return map;
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:beehive", () -> {
         return DSL.optionalFields("Bees", DSL.list(DSL.optionalFields("EntityData", TypeReferences.ENTITY_TREE.in(schema))));
      });
      return map;
   }
}
