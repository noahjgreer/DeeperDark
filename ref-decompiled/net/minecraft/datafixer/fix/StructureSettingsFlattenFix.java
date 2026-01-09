package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class StructureSettingsFlattenFix extends DataFix {
   public StructureSettingsFlattenFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS);
      OpticFinder opticFinder = type.findField("dimensions");
      return this.fixTypeEverywhereTyped("StructureSettingsFlatten", type, (worldGenSettingsTyped) -> {
         return worldGenSettingsTyped.updateTyped(opticFinder, (dimensionsTyped) -> {
            return Util.apply(dimensionsTyped, opticFinder.type(), (dimensionsDynamic) -> {
               return dimensionsDynamic.updateMapValues(StructureSettingsFlattenFix::fixDimensionEntry);
            });
         });
      });
   }

   private static Pair fixDimensionEntry(Pair dimensionEntry) {
      Dynamic dynamic = (Dynamic)dimensionEntry.getSecond();
      return Pair.of((Dynamic)dimensionEntry.getFirst(), dynamic.update("generator", (generatorDynamic) -> {
         return generatorDynamic.update("settings", (generatorSettingsDynamic) -> {
            return generatorSettingsDynamic.update("structures", StructureSettingsFlattenFix::fixStructures);
         });
      }));
   }

   private static Dynamic fixStructures(Dynamic structureSettingsDynamic) {
      Dynamic dynamic = structureSettingsDynamic.get("structures").orElseEmptyMap().updateMapValues((entry) -> {
         return entry.mapSecond((structureDynamic) -> {
            return structureDynamic.set("type", structureSettingsDynamic.createString("minecraft:random_spread"));
         });
      });
      return (Dynamic)DataFixUtils.orElse(structureSettingsDynamic.get("stronghold").result().map((strongholdDynamic) -> {
         return dynamic.set("minecraft:stronghold", strongholdDynamic.set("type", structureSettingsDynamic.createString("minecraft:concentric_rings")));
      }), dynamic);
   }
}
