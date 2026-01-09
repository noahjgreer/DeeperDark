package net.minecraft.test;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public record TestEntry(Map tests, RegistryKey functionKey, Consumer function) {
   public TestEntry(Map tests, Identifier functionId, Consumer function) {
      this(tests, RegistryKey.of(RegistryKeys.TEST_FUNCTION, functionId), function);
   }

   public TestEntry(Identifier id, TestData data, Consumer function) {
      this(Map.of(id, data), id, function);
   }

   public TestEntry(Map map, RegistryKey registryKey, Consumer function) {
      this.tests = map;
      this.functionKey = registryKey;
      this.function = function;
   }

   public Map tests() {
      return this.tests;
   }

   public RegistryKey functionKey() {
      return this.functionKey;
   }

   public Consumer function() {
      return this.function;
   }
}
