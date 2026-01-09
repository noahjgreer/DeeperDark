package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class VillagerGossipFix extends ChoiceFix {
   public VillagerGossipFix(Schema outputSchema, String choiceType) {
      super(outputSchema, false, "Gossip for for " + choiceType, TypeReferences.ENTITY, choiceType);
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), (entityDynamic) -> {
         return entityDynamic.update("Gossips", (gossipsDynamic) -> {
            Optional var10000 = gossipsDynamic.asStreamOpt().result().map((gossips) -> {
               return gossips.map((gossipDynamic) -> {
                  return (Dynamic)AbstractUuidFix.updateRegularMostLeast(gossipDynamic, "Target", "Target").orElse(gossipDynamic);
               });
            });
            Objects.requireNonNull(gossipsDynamic);
            return (Dynamic)DataFixUtils.orElse(var10000.map(gossipsDynamic::createList), gossipsDynamic);
         });
      });
   }
}
