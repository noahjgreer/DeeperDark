package net.minecraft.world.gen.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface StructureKeys {
   RegistryKey PILLAGER_OUTPOST = of("pillager_outpost");
   RegistryKey MINESHAFT = of("mineshaft");
   RegistryKey MINESHAFT_MESA = of("mineshaft_mesa");
   RegistryKey MANSION = of("mansion");
   RegistryKey JUNGLE_PYRAMID = of("jungle_pyramid");
   RegistryKey DESERT_PYRAMID = of("desert_pyramid");
   RegistryKey IGLOO = of("igloo");
   RegistryKey SHIPWRECK = of("shipwreck");
   RegistryKey SHIPWRECK_BEACHED = of("shipwreck_beached");
   RegistryKey SWAMP_HUT = of("swamp_hut");
   RegistryKey STRONGHOLD = of("stronghold");
   RegistryKey MONUMENT = of("monument");
   RegistryKey OCEAN_RUIN_COLD = of("ocean_ruin_cold");
   RegistryKey OCEAN_RUIN_WARM = of("ocean_ruin_warm");
   RegistryKey FORTRESS = of("fortress");
   RegistryKey NETHER_FOSSIL = of("nether_fossil");
   RegistryKey END_CITY = of("end_city");
   RegistryKey BURIED_TREASURE = of("buried_treasure");
   RegistryKey BASTION_REMNANT = of("bastion_remnant");
   RegistryKey VILLAGE_PLAINS = of("village_plains");
   RegistryKey VILLAGE_DESERT = of("village_desert");
   RegistryKey VILLAGE_SAVANNA = of("village_savanna");
   RegistryKey VILLAGE_SNOWY = of("village_snowy");
   RegistryKey VILLAGE_TAIGA = of("village_taiga");
   RegistryKey RUINED_PORTAL = of("ruined_portal");
   RegistryKey RUINED_PORTAL_DESERT = of("ruined_portal_desert");
   RegistryKey RUINED_PORTAL_JUNGLE = of("ruined_portal_jungle");
   RegistryKey RUINED_PORTAL_SWAMP = of("ruined_portal_swamp");
   RegistryKey RUINED_PORTAL_MOUNTAIN = of("ruined_portal_mountain");
   RegistryKey RUINED_PORTAL_OCEAN = of("ruined_portal_ocean");
   RegistryKey RUINED_PORTAL_NETHER = of("ruined_portal_nether");
   RegistryKey ANCIENT_CITY = of("ancient_city");
   RegistryKey TRAIL_RUINS = of("trail_ruins");
   RegistryKey TRIAL_CHAMBERS = of("trial_chambers");

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.STRUCTURE, Identifier.ofVanilla(id));
   }
}
