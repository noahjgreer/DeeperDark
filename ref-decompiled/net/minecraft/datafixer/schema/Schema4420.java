package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema4420 extends IdentifierNormalizingSchema {
   public Schema4420(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      schema.register(map, "minecraft:area_effect_cloud", (string) -> {
         return DSL.optionalFields("custom_particle", TypeReferences.PARTICLE.in(schema));
      });
      return map;
   }
}
