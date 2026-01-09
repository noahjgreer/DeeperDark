package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema2684 extends IdentifierNormalizingSchema {
   public Schema2684(int i, Schema schema) {
      super(i, schema);
   }

   public void registerTypes(Schema schema, Map entityTypes, Map blockEntityTypes) {
      super.registerTypes(schema, entityTypes, blockEntityTypes);
      schema.registerType(false, TypeReferences.GAME_EVENT_NAME, () -> {
         return DSL.constType(getIdentifierType());
      });
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:sculk_sensor", () -> {
         return DSL.optionalFields("listener", DSL.optionalFields("event", DSL.optionalFields("game_event", TypeReferences.GAME_EVENT_NAME.in(schema))));
      });
      return map;
   }
}
