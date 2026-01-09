package net.minecraft.test;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BuiltinTestFunctions extends TestFunctionProvider {
   public static final RegistryKey ALWAYS_PASS = of("always_pass");
   public static final Consumer ALWAYS_PASS_FUNCTION = TestContext::complete;

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.TEST_FUNCTION, Identifier.ofVanilla(id));
   }

   public static Consumer registerAndGetDefault(Registry registry) {
      addProvider(new BuiltinTestFunctions());
      registerAll(registry);
      return ALWAYS_PASS_FUNCTION;
   }

   public void register(BiConsumer registry) {
      registry.accept(ALWAYS_PASS, ALWAYS_PASS_FUNCTION);
   }
}
