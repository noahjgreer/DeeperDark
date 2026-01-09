package net.minecraft.world.gen.feature.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureDebugLogger {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LoadingCache FEATURES;

   public static void incrementTotalChunksCount(ServerWorld world) {
      try {
         ((Features)FEATURES.get(world)).chunksWithFeatures().increment();
      } catch (Exception var2) {
         LOGGER.error("Failed to increment chunk count", var2);
      }

   }

   public static void incrementFeatureCount(ServerWorld world, ConfiguredFeature configuredFeature, Optional placedFeature) {
      try {
         ((Features)FEATURES.get(world)).featureData().computeInt(new FeatureData(configuredFeature, placedFeature), (featureData, count) -> {
            return count == null ? 1 : count + 1;
         });
      } catch (Exception var4) {
         LOGGER.error("Failed to increment feature count", var4);
      }

   }

   public static void clear() {
      FEATURES.invalidateAll();
      LOGGER.debug("Cleared feature counts");
   }

   public static void dump() {
      LOGGER.debug("Logging feature counts:");
      FEATURES.asMap().forEach((world, features) -> {
         String string = world.getRegistryKey().getValue().toString();
         boolean bl = world.getServer().isRunning();
         Registry registry = world.getRegistryManager().getOrThrow(RegistryKeys.PLACED_FEATURE);
         String string2 = (bl ? "running" : "dead") + " " + string;
         Integer integer = features.chunksWithFeatures().getValue();
         LOGGER.debug(string2 + " total_chunks: " + integer);
         features.featureData().forEach((featureData, count) -> {
            Logger var10000 = LOGGER;
            String var10002 = String.format(Locale.ROOT, "%10d ", count);
            String var10003 = String.format(Locale.ROOT, "%10f ", (double)count / (double)integer);
            Optional var10004 = featureData.topFeature();
            Objects.requireNonNull(registry);
            var10000.debug(string2 + " " + var10002 + var10003 + String.valueOf(var10004.flatMap(registry::getKey).map(RegistryKey::getValue)) + " " + String.valueOf(featureData.feature().feature()) + " " + String.valueOf(featureData.feature()));
         });
      });
   }

   static {
      FEATURES = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build(new CacheLoader() {
         public Features load(ServerWorld serverWorld) {
            return new Features(Object2IntMaps.synchronize(new Object2IntOpenHashMap()), new MutableInt(0));
         }

         // $FF: synthetic method
         public Object load(final Object world) throws Exception {
            return this.load((ServerWorld)world);
         }
      });
   }

   private static record Features(Object2IntMap featureData, MutableInt chunksWithFeatures) {
      Features(Object2IntMap object2IntMap, MutableInt mutableInt) {
         this.featureData = object2IntMap;
         this.chunksWithFeatures = mutableInt;
      }

      public Object2IntMap featureData() {
         return this.featureData;
      }

      public MutableInt chunksWithFeatures() {
         return this.chunksWithFeatures;
      }
   }

   static record FeatureData(ConfiguredFeature feature, Optional topFeature) {
      FeatureData(ConfiguredFeature configuredFeature, Optional optional) {
         this.feature = configuredFeature;
         this.topFeature = optional;
      }

      public ConfiguredFeature feature() {
         return this.feature;
      }

      public Optional topFeature() {
         return this.topFeature;
      }
   }
}
