package net.minecraft.world.gen.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface StructureType {
   StructureType BURIED_TREASURE = register("buried_treasure", BuriedTreasureStructure.CODEC);
   StructureType DESERT_PYRAMID = register("desert_pyramid", DesertPyramidStructure.CODEC);
   StructureType END_CITY = register("end_city", EndCityStructure.CODEC);
   StructureType FORTRESS = register("fortress", NetherFortressStructure.CODEC);
   StructureType IGLOO = register("igloo", IglooStructure.CODEC);
   StructureType JIGSAW = register("jigsaw", JigsawStructure.CODEC);
   StructureType JUNGLE_TEMPLE = register("jungle_temple", JungleTempleStructure.CODEC);
   StructureType MINESHAFT = register("mineshaft", MineshaftStructure.CODEC);
   StructureType NETHER_FOSSIL = register("nether_fossil", NetherFossilStructure.CODEC);
   StructureType OCEAN_MONUMENT = register("ocean_monument", OceanMonumentStructure.CODEC);
   StructureType OCEAN_RUIN = register("ocean_ruin", OceanRuinStructure.CODEC);
   StructureType RUINED_PORTAL = register("ruined_portal", RuinedPortalStructure.CODEC);
   StructureType SHIPWRECK = register("shipwreck", ShipwreckStructure.CODEC);
   StructureType STRONGHOLD = register("stronghold", StrongholdStructure.CODEC);
   StructureType SWAMP_HUT = register("swamp_hut", SwampHutStructure.CODEC);
   StructureType WOODLAND_MANSION = register("woodland_mansion", WoodlandMansionStructure.CODEC);

   MapCodec codec();

   private static StructureType register(String id, MapCodec codec) {
      return (StructureType)Registry.register(Registries.STRUCTURE_TYPE, (String)id, () -> {
         return codec;
      });
   }
}
