package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema3439_1 extends IdentifierNormalizingSchema {
   public Schema3439_1(int i, Schema schema) {
      super(i, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      this.register(map, "minecraft:hanging_sign", () -> {
         return Schema3439.method_66179(schema);
      });
      return map;
   }
}
