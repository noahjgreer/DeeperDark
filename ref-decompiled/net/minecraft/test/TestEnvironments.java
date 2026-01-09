package net.minecraft.test;

import java.util.List;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface TestEnvironments {
   String DEFAULT_ID = "default";
   RegistryKey DEFAULT = of("default");

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.TEST_ENVIRONMENT, Identifier.ofVanilla(id));
   }

   static void bootstrap(Registerable registry) {
      registry.register(DEFAULT, new TestEnvironmentDefinition.AllOf(List.of()));
   }
}
