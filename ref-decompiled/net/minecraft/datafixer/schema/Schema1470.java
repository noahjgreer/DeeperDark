package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema1470 extends IdentifierNormalizingSchema {
   public Schema1470(int i, Schema schema) {
      super(i, schema);
   }

   protected static void targetEntityItems(Schema schema, Map map, String entityId) {
      schema.registerSimple(map, entityId);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      targetEntityItems(schema, map, "minecraft:turtle");
      targetEntityItems(schema, map, "minecraft:cod_mob");
      targetEntityItems(schema, map, "minecraft:tropical_fish");
      targetEntityItems(schema, map, "minecraft:salmon_mob");
      targetEntityItems(schema, map, "minecraft:puffer_fish");
      targetEntityItems(schema, map, "minecraft:phantom");
      targetEntityItems(schema, map, "minecraft:dolphin");
      targetEntityItems(schema, map, "minecraft:drowned");
      schema.register(map, "minecraft:trident", (name) -> {
         return DSL.optionalFields("inBlockState", TypeReferences.BLOCK_STATE.in(schema), "Trident", TypeReferences.ITEM_STACK.in(schema));
      });
      return map;
   }
}
