package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;

public class JigsawRotationFix extends BlockPropertyRenameFix {
   private static final Map ORIENTATION_UPDATES = ImmutableMap.builder().put("down", "down_south").put("up", "up_north").put("north", "north_up").put("south", "south_up").put("west", "west_up").put("east", "east_up").build();

   public JigsawRotationFix(Schema outputSchema) {
      super(outputSchema, "jigsaw_rotation_fix");
   }

   protected boolean shouldFix(String id) {
      return id.equals("minecraft:jigsaw");
   }

   protected Dynamic fix(String id, Dynamic properties) {
      String string = properties.get("facing").asString("north");
      return properties.remove("facing").set("orientation", properties.createString((String)ORIENTATION_UPDATES.getOrDefault(string, string)));
   }
}
