package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema107 extends Schema {
   public Schema107(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      map.remove("Minecart");
      return map;
   }
}
