package net.minecraft.data.advancement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class AdvancementProvider implements DataProvider {
   private final DataOutput.PathResolver pathResolver;
   private final List tabGenerators;
   private final CompletableFuture registriesFuture;

   public AdvancementProvider(DataOutput output, CompletableFuture registriesFuture, List tabGenerators) {
      this.pathResolver = output.getResolver(RegistryKeys.ADVANCEMENT);
      this.tabGenerators = tabGenerators;
      this.registriesFuture = registriesFuture;
   }

   public CompletableFuture run(DataWriter writer) {
      return this.registriesFuture.thenCompose((registries) -> {
         Set set = new HashSet();
         List list = new ArrayList();
         Consumer consumer = (advancement) -> {
            if (!set.add(advancement.id())) {
               throw new IllegalStateException("Duplicate advancement " + String.valueOf(advancement.id()));
            } else {
               Path path = this.pathResolver.resolveJson(advancement.id());
               list.add(DataProvider.writeCodecToPath(writer, (RegistryWrapper.WrapperLookup)registries, Advancement.CODEC, advancement.value(), path));
            }
         };
         Iterator var6 = this.tabGenerators.iterator();

         while(var6.hasNext()) {
            AdvancementTabGenerator advancementTabGenerator = (AdvancementTabGenerator)var6.next();
            advancementTabGenerator.accept(registries, consumer);
         }

         return CompletableFuture.allOf((CompletableFuture[])list.toArray((i) -> {
            return new CompletableFuture[i];
         }));
      });
   }

   public String getName() {
      return "Advancements";
   }
}
