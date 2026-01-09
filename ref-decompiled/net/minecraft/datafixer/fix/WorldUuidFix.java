package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import org.slf4j.Logger;

public class WorldUuidFix extends AbstractUuidFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public WorldUuidFix(Schema outputSchema) {
      super(outputSchema, TypeReferences.LEVEL);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(this.typeReference);
      OpticFinder opticFinder = type.findField("CustomBossEvents");
      OpticFinder opticFinder2 = DSL.typeFinder(DSL.and(DSL.optional(DSL.field("Name", this.getInputSchema().getTypeRaw(TypeReferences.TEXT_COMPONENT))), DSL.remainderType()));
      return this.fixTypeEverywhereTyped("LevelUUIDFix", type, (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            dynamic = this.fixDragonUuid(dynamic);
            dynamic = this.fixWanderingTraderId(dynamic);
            return dynamic;
         }).updateTyped(opticFinder, (typedx) -> {
            return typedx.updateTyped(opticFinder2, (typed) -> {
               return typed.update(DSL.remainderFinder(), this::fixCustomBossEvents);
            });
         });
      });
   }

   private Dynamic fixWanderingTraderId(Dynamic levelDynamic) {
      return (Dynamic)updateStringUuid(levelDynamic, "WanderingTraderId", "WanderingTraderId").orElse(levelDynamic);
   }

   private Dynamic fixDragonUuid(Dynamic levelDynamic) {
      return levelDynamic.update("DimensionData", (dimensionDataDynamic) -> {
         return dimensionDataDynamic.updateMapValues((entry) -> {
            return entry.mapSecond((dimensionDataValueDynamic) -> {
               return dimensionDataValueDynamic.update("DragonFight", (dragonFightDynamic) -> {
                  return (Dynamic)updateRegularMostLeast(dragonFightDynamic, "DragonUUID", "Dragon").orElse(dragonFightDynamic);
               });
            });
         });
      });
   }

   private Dynamic fixCustomBossEvents(Dynamic levelDynamic) {
      return levelDynamic.update("Players", (dynamic2) -> {
         return levelDynamic.createList(dynamic2.asStream().map((dynamic) -> {
            return (Dynamic)createArrayFromCompoundUuid(dynamic).orElseGet(() -> {
               LOGGER.warn("CustomBossEvents contains invalid UUIDs.");
               return dynamic;
            });
         }));
      });
   }
}
