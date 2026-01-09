package net.minecraft.structure;

import net.minecraft.registry.Registerable;

public class VillageGenerator {
   public static void bootstrap(Registerable poolRegisterable) {
      PlainsVillageData.bootstrap(poolRegisterable);
      SnowyVillageData.bootstrap(poolRegisterable);
      SavannaVillageData.bootstrap(poolRegisterable);
      DesertVillageData.bootstrap(poolRegisterable);
      TaigaVillageData.bootstrap(poolRegisterable);
   }
}
