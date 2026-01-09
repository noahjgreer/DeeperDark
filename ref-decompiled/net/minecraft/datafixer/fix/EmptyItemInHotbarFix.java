package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class EmptyItemInHotbarFix extends DataFix {
   public EmptyItemInHotbarFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder opticFinder = DSL.typeFinder(this.getInputSchema().getType(TypeReferences.ITEM_STACK));
      return this.fixTypeEverywhereTyped("EmptyItemInHotbarFix", this.getInputSchema().getType(TypeReferences.HOTBAR), (hotbarTyped) -> {
         return hotbarTyped.update(opticFinder, (pair) -> {
            return pair.mapSecond((pairx) -> {
               Optional optional = ((Either)pairx.getFirst()).left().map(Pair::getSecond);
               Dynamic dynamic = (Dynamic)((Pair)pairx.getSecond()).getSecond();
               boolean bl = optional.isEmpty() || ((String)optional.get()).equals("minecraft:air");
               boolean bl2 = dynamic.get("Count").asInt(0) <= 0;
               return !bl && !bl2 ? pairx : Pair.of(Either.right(Unit.INSTANCE), Pair.of(Either.right(Unit.INSTANCE), dynamic.emptyMap()));
            });
         });
      });
   }
}
