package net.minecraft.test;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface TestInstances {
   RegistryKey ALWAYS_PASS = of("always_pass");

   static void bootstrap(Registerable registry) {
      RegistryEntryLookup registryEntryLookup = registry.getRegistryLookup(RegistryKeys.TEST_FUNCTION);
      RegistryEntryLookup registryEntryLookup2 = registry.getRegistryLookup(RegistryKeys.TEST_ENVIRONMENT);
      registry.register(ALWAYS_PASS, new FunctionTestInstance(BuiltinTestFunctions.ALWAYS_PASS, new TestData(registryEntryLookup2.getOrThrow(TestEnvironments.DEFAULT), Identifier.ofVanilla("empty"), 1, 1, false)));
   }

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.TEST_INSTANCE, Identifier.ofVanilla(id));
   }
}
