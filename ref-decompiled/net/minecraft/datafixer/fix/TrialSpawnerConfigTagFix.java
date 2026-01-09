package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class TrialSpawnerConfigTagFix extends ChoiceWriteReadFix {
   public TrialSpawnerConfigTagFix(Schema outputSchema) {
      super(outputSchema, true, "Trial Spawner config tag fixer", TypeReferences.BLOCK_ENTITY, "minecraft:trial_spawner");
   }

   private static Dynamic fix(Dynamic data) {
      List list = List.of("spawn_range", "total_mobs", "simultaneous_mobs", "total_mobs_added_per_player", "simultaneous_mobs_added_per_player", "ticks_between_spawn", "spawn_potentials", "loot_tables_to_eject", "items_to_drop_when_ominous");
      Map map = new HashMap(list.size());
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         String string = (String)var3.next();
         Optional optional = data.get(string).get().result();
         if (optional.isPresent()) {
            map.put(data.createString(string), (Dynamic)optional.get());
            data = data.remove(string);
         }
      }

      return map.isEmpty() ? data : data.set("normal_config", data.createMap(map));
   }

   protected Dynamic transform(Dynamic data) {
      return fix(data);
   }
}
