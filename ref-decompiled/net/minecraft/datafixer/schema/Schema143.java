package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class Schema143 extends Schema {
   public Schema143(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      map.remove("TippedArrow");
      return map;
   }
}
