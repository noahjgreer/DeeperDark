package net.minecraft.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public abstract class TestFunctionProvider {
   private static final List PROVIDERS = new ArrayList();

   public static void addProvider(TestFunctionProvider provider) {
      PROVIDERS.add(provider);
   }

   public static void registerAll(Registry registry) {
      Iterator var1 = PROVIDERS.iterator();

      while(var1.hasNext()) {
         TestFunctionProvider testFunctionProvider = (TestFunctionProvider)var1.next();
         testFunctionProvider.register((key, value) -> {
            Registry.register(registry, (RegistryKey)key, value);
         });
      }

   }

   public abstract void register(BiConsumer registry);
}
