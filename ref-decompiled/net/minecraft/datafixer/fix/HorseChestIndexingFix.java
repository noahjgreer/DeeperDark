package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.datafixer.TypeReferences;

public class HorseChestIndexingFix extends DataFix {
   public HorseChestIndexingFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      OpticFinder opticFinder = DSL.typeFinder(this.getInputSchema().getType(TypeReferences.ITEM_STACK));
      Type type = this.getInputSchema().getType(TypeReferences.ENTITY);
      return TypeRewriteRule.seq(this.fixIndexing(opticFinder, type, "minecraft:llama"), new TypeRewriteRule[]{this.fixIndexing(opticFinder, type, "minecraft:trader_llama"), this.fixIndexing(opticFinder, type, "minecraft:mule"), this.fixIndexing(opticFinder, type, "minecraft:donkey")});
   }

   private TypeRewriteRule fixIndexing(OpticFinder itemStackOpticFinder, Type entityType, String entityId) {
      Type type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, entityId);
      OpticFinder opticFinder = DSL.namedChoice(entityId, type);
      OpticFinder opticFinder2 = type.findField("Items");
      return this.fixTypeEverywhereTyped("Fix non-zero indexing in chest horse type " + entityId, entityType, (entityTyped) -> {
         return entityTyped.updateTyped(opticFinder, (specificEntityTyped) -> {
            return specificEntityTyped.updateTyped(opticFinder2, (entityItemsTyped) -> {
               return entityItemsTyped.update(itemStackOpticFinder, (itemStackEntry) -> {
                  return itemStackEntry.mapSecond((pair) -> {
                     return pair.mapSecond((pairx) -> {
                        return pairx.mapSecond((itemStackDynamic) -> {
                           return itemStackDynamic.update("Slot", (slotDynamic) -> {
                              return slotDynamic.createByte((byte)(slotDynamic.asInt(2) - 2));
                           });
                        });
                     });
                  });
               });
            });
         });
      });
   }
}
