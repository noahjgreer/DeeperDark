package net.minecraft.world.waypoint;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public interface WaypointStyles {
   RegistryKey REGISTRY = RegistryKey.ofRegistry(Identifier.ofVanilla("waypoint_style_asset"));
   RegistryKey DEFAULT = of("default");
   RegistryKey BOWTIE = of("bowtie");

   static RegistryKey of(String id) {
      return RegistryKey.of(REGISTRY, Identifier.ofVanilla(id));
   }
}
