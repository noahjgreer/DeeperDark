package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class FoodToConsumableFix extends DataFix {
   public FoodToConsumableFix(Schema schema) {
      super(schema, true);
   }

   protected TypeRewriteRule makeRule() {
      return this.writeFixAndRead("Food to consumable fix", this.getInputSchema().getType(TypeReferences.DATA_COMPONENTS), this.getOutputSchema().getType(TypeReferences.DATA_COMPONENTS), (dynamic) -> {
         Optional optional = dynamic.get("minecraft:food").result();
         if (optional.isPresent()) {
            float f = ((Dynamic)optional.get()).get("eat_seconds").asFloat(1.6F);
            Stream stream = ((Dynamic)optional.get()).get("effects").asStream();
            Stream stream2 = stream.map((dynamicx) -> {
               return dynamicx.emptyMap().set("type", dynamicx.createString("minecraft:apply_effects")).set("effects", dynamicx.createList(dynamicx.get("effect").result().stream())).set("probability", dynamicx.createFloat(dynamicx.get("probability").asFloat(1.0F)));
            });
            dynamic = Dynamic.copyField((Dynamic)optional.get(), "using_converts_to", dynamic, "minecraft:use_remainder");
            dynamic = dynamic.set("minecraft:food", ((Dynamic)optional.get()).remove("eat_seconds").remove("effects").remove("using_converts_to"));
            dynamic = dynamic.set("minecraft:consumable", dynamic.emptyMap().set("consume_seconds", dynamic.createFloat(f)).set("on_consume_effects", dynamic.createList(stream2)));
            return dynamic;
         } else {
            return dynamic;
         }
      });
   }
}
