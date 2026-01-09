package net.minecraft.structure;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface StructureSetKeys {
   RegistryKey VILLAGES = of("villages");
   RegistryKey DESERT_PYRAMIDS = of("desert_pyramids");
   RegistryKey IGLOOS = of("igloos");
   RegistryKey JUNGLE_TEMPLES = of("jungle_temples");
   RegistryKey SWAMP_HUTS = of("swamp_huts");
   RegistryKey PILLAGER_OUTPOSTS = of("pillager_outposts");
   RegistryKey OCEAN_MONUMENTS = of("ocean_monuments");
   RegistryKey WOODLAND_MANSIONS = of("woodland_mansions");
   RegistryKey BURIED_TREASURES = of("buried_treasures");
   RegistryKey MINESHAFTS = of("mineshafts");
   RegistryKey RUINED_PORTALS = of("ruined_portals");
   RegistryKey SHIPWRECKS = of("shipwrecks");
   RegistryKey OCEAN_RUINS = of("ocean_ruins");
   RegistryKey NETHER_COMPLEXES = of("nether_complexes");
   RegistryKey NETHER_FOSSILS = of("nether_fossils");
   RegistryKey END_CITIES = of("end_cities");
   RegistryKey ANCIENT_CITIES = of("ancient_cities");
   RegistryKey STRONGHOLDS = of("strongholds");
   RegistryKey TRAIL_RUINS = of("trail_ruins");
   RegistryKey TRIAL_CHAMBERS = of("trial_chambers");

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.STRUCTURE_SET, Identifier.ofVanilla(id));
   }
}
