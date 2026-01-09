package net.minecraft.registry;

@FunctionalInterface
public interface RegistryKeyedValue {
   Object get(RegistryKey registryKey);

   static RegistryKeyedValue fixed(Object value) {
      return (registryKey) -> {
         return value;
      };
   }
}
