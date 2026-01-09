package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class FireResistantToDamageResistantComponentFix extends ComponentFix {
   public FireResistantToDamageResistantComponentFix(Schema outputSchema) {
      super(outputSchema, "FireResistantToDamageResistantComponentFix", "minecraft:fire_resistant", "minecraft:damage_resistant");
   }

   protected Dynamic fixComponent(Dynamic dynamic) {
      return dynamic.emptyMap().set("types", dynamic.createString("#minecraft:is_fire"));
   }
}
