package net.minecraft.resource;

import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

public abstract class JsonDataLoader extends SinglePreparationResourceReloader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DynamicOps ops;
   private final Codec codec;
   private final ResourceFinder finder;

   protected JsonDataLoader(RegistryWrapper.WrapperLookup registries, Codec codec, RegistryKey registryRef) {
      this((DynamicOps)registries.getOps(JsonOps.INSTANCE), codec, (ResourceFinder)ResourceFinder.json(registryRef));
   }

   protected JsonDataLoader(Codec codec, ResourceFinder finder) {
      this((DynamicOps)JsonOps.INSTANCE, codec, (ResourceFinder)finder);
   }

   private JsonDataLoader(DynamicOps ops, Codec codec, ResourceFinder finder) {
      this.ops = ops;
      this.codec = codec;
      this.finder = finder;
   }

   protected Map prepare(ResourceManager resourceManager, Profiler profiler) {
      Map map = new HashMap();
      load(resourceManager, (ResourceFinder)this.finder, this.ops, this.codec, map);
      return map;
   }

   public static void load(ResourceManager manager, RegistryKey registryRef, DynamicOps ops, Codec codec, Map results) {
      load(manager, ResourceFinder.json(registryRef), ops, codec, results);
   }

   public static void load(ResourceManager manager, ResourceFinder finder, DynamicOps ops, Codec codec, Map results) {
      Iterator var5 = finder.findResources(manager).entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry entry = (Map.Entry)var5.next();
         Identifier identifier = (Identifier)entry.getKey();
         Identifier identifier2 = finder.toResourceId(identifier);

         try {
            Reader reader = ((Resource)entry.getValue()).getReader();

            try {
               codec.parse(ops, StrictJsonParser.parse((Reader)reader)).ifSuccess((value) -> {
                  if (results.putIfAbsent(identifier2, value) != null) {
                     throw new IllegalStateException("Duplicate data file ignored with ID " + String.valueOf(identifier2));
                  }
               }).ifError((error) -> {
                  LOGGER.error("Couldn't parse data file '{}' from '{}': {}", new Object[]{identifier2, identifier, error});
               });
            } catch (Throwable var13) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (IllegalArgumentException | IOException | JsonParseException var14) {
            LOGGER.error("Couldn't parse data file '{}' from '{}'", new Object[]{identifier2, identifier, var14});
         }
      }

   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager manager, final Profiler profiler) {
      return this.prepare(manager, profiler);
   }
}
