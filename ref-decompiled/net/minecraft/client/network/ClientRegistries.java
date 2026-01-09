package net.minecraft.client.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagPacketSerializer;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientRegistries {
   @Nullable
   private DynamicRegistries dynamicRegistries;
   @Nullable
   private Tags tags;

   public void putDynamicRegistry(RegistryKey registryRef, List entries) {
      if (this.dynamicRegistries == null) {
         this.dynamicRegistries = new DynamicRegistries();
      }

      this.dynamicRegistries.put(registryRef, entries);
   }

   public void putTags(Map tags) {
      if (this.tags == null) {
         this.tags = new Tags();
      }

      Tags var10001 = this.tags;
      Objects.requireNonNull(var10001);
      tags.forEach(var10001::put);
   }

   private static Registry.PendingTagLoad startTagReload(DynamicRegistryManager.Immutable registryManager, RegistryKey registryRef, TagPacketSerializer.Serialized tags) {
      Registry registry = registryManager.getOrThrow(registryRef);
      return registry.startTagReload(tags.toRegistryTags(registry));
   }

   private DynamicRegistryManager createRegistryManager(ResourceFactory resourceFactory, DynamicRegistries dynamicRegistries, boolean local) {
      CombinedDynamicRegistries combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();
      DynamicRegistryManager.Immutable immutable = combinedDynamicRegistries.getPrecedingRegistryManagers(ClientDynamicRegistryType.REMOTE);
      Map map = new HashMap();
      dynamicRegistries.dynamicRegistries.forEach((registryRef, entries) -> {
         map.put(registryRef, new RegistryLoader.ElementsAndTags(entries, TagPacketSerializer.Serialized.NONE));
      });
      List list = new ArrayList();
      if (this.tags != null) {
         this.tags.forEach((registryRef, tags) -> {
            if (!tags.isEmpty()) {
               if (SerializableRegistries.isSynced(registryRef)) {
                  map.compute(registryRef, (key, value) -> {
                     List list = value != null ? value.elements() : List.of();
                     return new RegistryLoader.ElementsAndTags(list, tags);
                  });
               } else if (!local) {
                  list.add(startTagReload(immutable, registryRef, tags));
               }

            }
         });
      }

      List list2 = TagGroupLoader.collectRegistries(immutable, list);

      DynamicRegistryManager.Immutable immutable2;
      try {
         immutable2 = RegistryLoader.loadFromNetwork(map, resourceFactory, list2, RegistryLoader.SYNCED_REGISTRIES).toImmutable();
      } catch (Exception var13) {
         CrashReport crashReport = CrashReport.create(var13, "Network Registry Load");
         addCrashReportSection(crashReport, map, list);
         throw new CrashException(crashReport);
      }

      DynamicRegistryManager dynamicRegistryManager = combinedDynamicRegistries.with(ClientDynamicRegistryType.REMOTE, (DynamicRegistryManager.Immutable[])(immutable2)).getCombinedRegistryManager();
      list.forEach(Registry.PendingTagLoad::apply);
      return dynamicRegistryManager;
   }

   private static void addCrashReportSection(CrashReport crashReport, Map data, List tags) {
      CrashReportSection crashReportSection = crashReport.addElement("Received Elements and Tags");
      crashReportSection.add("Dynamic Registries", () -> {
         return (String)data.entrySet().stream().sorted(Comparator.comparing((entry) -> {
            return ((RegistryKey)entry.getKey()).getValue();
         })).map((entry) -> {
            return String.format(Locale.ROOT, "\n\t\t%s: elements=%d tags=%d", ((RegistryKey)entry.getKey()).getValue(), ((RegistryLoader.ElementsAndTags)entry.getValue()).elements().size(), ((RegistryLoader.ElementsAndTags)entry.getValue()).tags().size());
         }).collect(Collectors.joining());
      });
      crashReportSection.add("Static Registries", () -> {
         return (String)tags.stream().sorted(Comparator.comparing((tag) -> {
            return tag.getKey().getValue();
         })).map((tag) -> {
            return String.format(Locale.ROOT, "\n\t\t%s: tags=%d", tag.getKey().getValue(), tag.size());
         }).collect(Collectors.joining());
      });
   }

   private void loadTags(Tags tags, DynamicRegistryManager.Immutable registryManager, boolean local) {
      tags.forEach((registryRef, serialized) -> {
         if (local || SerializableRegistries.isSynced(registryRef)) {
            startTagReload(registryManager, registryRef, serialized).apply();
         }

      });
   }

   public DynamicRegistryManager.Immutable createRegistryManager(ResourceFactory resourceFactory, DynamicRegistryManager.Immutable registryManager, boolean local) {
      Object dynamicRegistryManager;
      if (this.dynamicRegistries != null) {
         dynamicRegistryManager = this.createRegistryManager(resourceFactory, this.dynamicRegistries, local);
      } else {
         if (this.tags != null) {
            this.loadTags(this.tags, registryManager, !local);
         }

         dynamicRegistryManager = registryManager;
      }

      return ((DynamicRegistryManager)dynamicRegistryManager).toImmutable();
   }

   @Environment(EnvType.CLIENT)
   private static class DynamicRegistries {
      final Map dynamicRegistries = new HashMap();

      DynamicRegistries() {
      }

      public void put(RegistryKey registryRef, List entries) {
         ((List)this.dynamicRegistries.computeIfAbsent(registryRef, (registries) -> {
            return new ArrayList();
         })).addAll(entries);
      }
   }

   @Environment(EnvType.CLIENT)
   static class Tags {
      private final Map tags = new HashMap();

      public void put(RegistryKey registryRef, TagPacketSerializer.Serialized tags) {
         this.tags.put(registryRef, tags);
      }

      public void forEach(BiConsumer consumer) {
         this.tags.forEach(consumer);
      }
   }
}
