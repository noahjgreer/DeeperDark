package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.slf4j.Logger;

public interface DataProvider {
   ToIntFunction JSON_KEY_SORT_ORDER = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), (map) -> {
      map.put("type", 0);
      map.put("parent", 1);
      map.defaultReturnValue(2);
   });
   Comparator JSON_KEY_SORTING_COMPARATOR = Comparator.comparingInt(JSON_KEY_SORT_ORDER).thenComparing((key) -> {
      return key;
   });
   Logger LOGGER = LogUtils.getLogger();

   CompletableFuture run(DataWriter writer);

   String getName();

   static CompletableFuture writeAllToPath(DataWriter writer, Codec codec, DataOutput.PathResolver pathResolver, Map idsToValues) {
      Objects.requireNonNull(pathResolver);
      return writeAllToPath(writer, codec, pathResolver::resolveJson, idsToValues);
   }

   static CompletableFuture writeAllToPath(DataWriter writer, Codec codec, Function pathResolver, Map idsToValues) {
      return writeAllToPath(writer, (value) -> {
         return (JsonElement)codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
      }, pathResolver, idsToValues);
   }

   static CompletableFuture writeAllToPath(DataWriter writer, Function serializer, Function pathResolver, Map idsToValues) {
      return CompletableFuture.allOf((CompletableFuture[])idsToValues.entrySet().stream().map((entry) -> {
         Path path = (Path)pathResolver.apply(entry.getKey());
         JsonElement jsonElement = (JsonElement)serializer.apply(entry.getValue());
         return writeToPath(writer, jsonElement, path);
      }).toArray((i) -> {
         return new CompletableFuture[i];
      }));
   }

   static CompletableFuture writeCodecToPath(DataWriter writer, RegistryWrapper.WrapperLookup registries, Codec codec, Object value, Path path) {
      RegistryOps registryOps = registries.getOps(JsonOps.INSTANCE);
      return writeCodecToPath(writer, (DynamicOps)registryOps, codec, value, path);
   }

   static CompletableFuture writeCodecToPath(DataWriter writer, Codec codec, Object value, Path path) {
      return writeCodecToPath(writer, (DynamicOps)JsonOps.INSTANCE, codec, value, path);
   }

   private static CompletableFuture writeCodecToPath(DataWriter writer, DynamicOps ops, Codec codec, Object value, Path path) {
      JsonElement jsonElement = (JsonElement)codec.encodeStart(ops, value).getOrThrow();
      return writeToPath(writer, jsonElement, path);
   }

   static CompletableFuture writeToPath(DataWriter writer, JsonElement json, Path path) {
      return CompletableFuture.runAsync(() -> {
         try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8));

            try {
               jsonWriter.setSerializeNulls(false);
               jsonWriter.setIndent("  ");
               JsonHelper.writeSorted(jsonWriter, json, JSON_KEY_SORTING_COMPARATOR);
            } catch (Throwable var9) {
               try {
                  jsonWriter.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }

               throw var9;
            }

            jsonWriter.close();
            writer.write(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
         } catch (IOException var10) {
            LOGGER.error("Failed to save file to {}", path, var10);
         }

      }, Util.getMainWorkerExecutor().named("saveStable"));
   }

   @FunctionalInterface
   public interface Factory {
      DataProvider create(DataOutput output);
   }
}
