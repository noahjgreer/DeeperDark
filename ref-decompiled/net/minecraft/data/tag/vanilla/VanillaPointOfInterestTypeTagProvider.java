package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.world.poi.PointOfInterestTypes;

public class VanillaPointOfInterestTypeTagProvider extends SimpleTagProvider {
   public VanillaPointOfInterestTypeTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.POINT_OF_INTEREST_TYPE, registriesFuture);
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE).add((Object[])(PointOfInterestTypes.ARMORER, PointOfInterestTypes.BUTCHER, PointOfInterestTypes.CARTOGRAPHER, PointOfInterestTypes.CLERIC, PointOfInterestTypes.FARMER, PointOfInterestTypes.FISHERMAN, PointOfInterestTypes.FLETCHER, PointOfInterestTypes.LEATHERWORKER, PointOfInterestTypes.LIBRARIAN, PointOfInterestTypes.MASON, PointOfInterestTypes.SHEPHERD, PointOfInterestTypes.TOOLSMITH, PointOfInterestTypes.WEAPONSMITH));
      this.builder(PointOfInterestTypeTags.VILLAGE).addTag(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE).add((Object[])(PointOfInterestTypes.HOME, PointOfInterestTypes.MEETING));
      this.builder(PointOfInterestTypeTags.BEE_HOME).add((Object[])(PointOfInterestTypes.BEEHIVE, PointOfInterestTypes.BEE_NEST));
   }
}
