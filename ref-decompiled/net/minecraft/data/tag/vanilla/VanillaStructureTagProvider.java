package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.world.gen.structure.StructureKeys;

public class VanillaStructureTagProvider extends SimpleTagProvider {
   public VanillaStructureTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.STRUCTURE, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(StructureTags.VILLAGE).add((Object)StructureKeys.VILLAGE_PLAINS).add((Object)StructureKeys.VILLAGE_DESERT).add((Object)StructureKeys.VILLAGE_SAVANNA).add((Object)StructureKeys.VILLAGE_SNOWY).add((Object)StructureKeys.VILLAGE_TAIGA);
      this.builder(StructureTags.MINESHAFT).add((Object)StructureKeys.MINESHAFT).add((Object)StructureKeys.MINESHAFT_MESA);
      this.builder(StructureTags.OCEAN_RUIN).add((Object)StructureKeys.OCEAN_RUIN_COLD).add((Object)StructureKeys.OCEAN_RUIN_WARM);
      this.builder(StructureTags.SHIPWRECK).add((Object)StructureKeys.SHIPWRECK).add((Object)StructureKeys.SHIPWRECK_BEACHED);
      this.builder(StructureTags.RUINED_PORTAL).add((Object)StructureKeys.RUINED_PORTAL_DESERT).add((Object)StructureKeys.RUINED_PORTAL_JUNGLE).add((Object)StructureKeys.RUINED_PORTAL_MOUNTAIN).add((Object)StructureKeys.RUINED_PORTAL_NETHER).add((Object)StructureKeys.RUINED_PORTAL_OCEAN).add((Object)StructureKeys.RUINED_PORTAL).add((Object)StructureKeys.RUINED_PORTAL_SWAMP);
      this.builder(StructureTags.CATS_SPAWN_IN).add((Object)StructureKeys.SWAMP_HUT);
      this.builder(StructureTags.CATS_SPAWN_AS_BLACK).add((Object)StructureKeys.SWAMP_HUT);
      this.builder(StructureTags.EYE_OF_ENDER_LOCATED).add((Object)StructureKeys.STRONGHOLD);
      this.builder(StructureTags.DOLPHIN_LOCATED).addTag(StructureTags.OCEAN_RUIN).addTag(StructureTags.SHIPWRECK);
      this.builder(StructureTags.ON_WOODLAND_EXPLORER_MAPS).add((Object)StructureKeys.MANSION);
      this.builder(StructureTags.ON_OCEAN_EXPLORER_MAPS).add((Object)StructureKeys.MONUMENT);
      this.builder(StructureTags.ON_TREASURE_MAPS).add((Object)StructureKeys.BURIED_TREASURE);
      this.builder(StructureTags.ON_TRIAL_CHAMBERS_MAPS).add((Object)StructureKeys.TRIAL_CHAMBERS);
      this.builder(StructureTags.ON_SAVANNA_VILLAGE_MAPS).add((Object)StructureKeys.VILLAGE_SAVANNA);
      this.builder(StructureTags.ON_DESERT_VILLAGE_MAPS).add((Object)StructureKeys.VILLAGE_DESERT);
      this.builder(StructureTags.ON_PLAINS_VILLAGE_MAPS).add((Object)StructureKeys.VILLAGE_PLAINS);
      this.builder(StructureTags.ON_TAIGA_VILLAGE_MAPS).add((Object)StructureKeys.VILLAGE_TAIGA);
      this.builder(StructureTags.ON_SNOWY_VILLAGE_MAPS).add((Object)StructureKeys.VILLAGE_SNOWY);
      this.builder(StructureTags.ON_SWAMP_EXPLORER_MAPS).add((Object)StructureKeys.SWAMP_HUT);
      this.builder(StructureTags.ON_JUNGLE_EXPLORER_MAPS).add((Object)StructureKeys.JUNGLE_PYRAMID);
   }
}
