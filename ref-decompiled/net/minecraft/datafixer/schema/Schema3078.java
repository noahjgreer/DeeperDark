package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema3078 extends IdentifierNormalizingSchema {
   public Schema3078(int i, Schema schema) {
      super(i, schema);
   }

   protected static void targetEntityItems(Schema schema, Map map, String entityId) {
      schema.registerSimple(map, entityId);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      targetEntityItems(schema, map, "minecraft:frog");
      targetEntityItems(schema, map, "minecraft:tadpole");
      return map;
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:sculk_shrieker", () -> {
         return DSL.optionalFields("listener", DSL.optionalFields("event", DSL.optionalFields("game_event", TypeReferences.GAME_EVENT_NAME.in(schema))));
      });
      return map;
   }
}
