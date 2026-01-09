package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class PointOfInterestTypeTags {
   public static final TagKey ACQUIRABLE_JOB_SITE = of("acquirable_job_site");
   public static final TagKey VILLAGE = of("village");
   public static final TagKey BEE_HOME = of("bee_home");

   private PointOfInterestTypeTags() {
   }

   private static TagKey of(String id) {
      return TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.ofVanilla(id));
   }
}
