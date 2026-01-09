package net.minecraft.world.biome;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class SpawnSettings {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final float field_30983 = 0.1F;
   public static final Pool EMPTY_ENTRY_POOL = Pool.empty();
   public static final SpawnSettings INSTANCE = (new Builder()).build();
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      RecordCodecBuilder var10001 = Codec.floatRange(0.0F, 0.9999999F).optionalFieldOf("creature_spawn_probability", 0.1F).forGetter((settings) -> {
         return settings.creatureSpawnProbability;
      });
      Codec var10002 = SpawnGroup.CODEC;
      Codec var10003 = Pool.createCodec(SpawnSettings.SpawnEntry.CODEC);
      Logger var10005 = LOGGER;
      Objects.requireNonNull(var10005);
      return instance.group(var10001, Codec.simpleMap(var10002, var10003.promotePartial(Util.addPrefix("Spawn data: ", var10005::error)), StringIdentifiable.toKeyable(SpawnGroup.values())).fieldOf("spawners").forGetter((settings) -> {
         return settings.spawners;
      }), Codec.simpleMap(Registries.ENTITY_TYPE.getCodec(), SpawnSettings.SpawnDensity.CODEC, Registries.ENTITY_TYPE).fieldOf("spawn_costs").forGetter((settings) -> {
         return settings.spawnCosts;
      })).apply(instance, SpawnSettings::new);
   });
   private final float creatureSpawnProbability;
   private final Map spawners;
   private final Map spawnCosts;

   SpawnSettings(float creatureSpawnProbability, Map spawners, Map spawnCosts) {
      this.creatureSpawnProbability = creatureSpawnProbability;
      this.spawners = ImmutableMap.copyOf(spawners);
      this.spawnCosts = ImmutableMap.copyOf(spawnCosts);
   }

   public Pool getSpawnEntries(SpawnGroup spawnGroup) {
      return (Pool)this.spawners.getOrDefault(spawnGroup, EMPTY_ENTRY_POOL);
   }

   @Nullable
   public SpawnDensity getSpawnDensity(EntityType entityType) {
      return (SpawnDensity)this.spawnCosts.get(entityType);
   }

   public float getCreatureSpawnProbability() {
      return this.creatureSpawnProbability;
   }

   public static record SpawnDensity(double gravityLimit, double mass) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.DOUBLE.fieldOf("energy_budget").forGetter((spawnDensity) -> {
            return spawnDensity.gravityLimit;
         }), Codec.DOUBLE.fieldOf("charge").forGetter((spawnDensity) -> {
            return spawnDensity.mass;
         })).apply(instance, SpawnDensity::new);
      });

      public SpawnDensity(double gravityLimit, double mass) {
         this.gravityLimit = gravityLimit;
         this.mass = mass;
      }

      public double gravityLimit() {
         return this.gravityLimit;
      }

      public double mass() {
         return this.mass;
      }
   }

   public static record SpawnEntry(EntityType type, int minGroupSize, int maxGroupSize) {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Registries.ENTITY_TYPE.getCodec().fieldOf("type").forGetter((spawnEntry) -> {
            return spawnEntry.type;
         }), Codecs.POSITIVE_INT.fieldOf("minCount").forGetter((spawnEntry) -> {
            return spawnEntry.minGroupSize;
         }), Codecs.POSITIVE_INT.fieldOf("maxCount").forGetter((spawnEntry) -> {
            return spawnEntry.maxGroupSize;
         })).apply(instance, SpawnEntry::new);
      }).validate((spawnEntry) -> {
         return spawnEntry.minGroupSize > spawnEntry.maxGroupSize ? DataResult.error(() -> {
            return "minCount needs to be smaller or equal to maxCount";
         }) : DataResult.success(spawnEntry);
      });

      public SpawnEntry(EntityType type, int i, int minGroupSize) {
         type = type.getSpawnGroup() == SpawnGroup.MISC ? EntityType.PIG : type;
         this.type = type;
         this.minGroupSize = i;
         this.maxGroupSize = minGroupSize;
      }

      public String toString() {
         String var10000 = String.valueOf(EntityType.getId(this.type));
         return var10000 + "*(" + this.minGroupSize + "-" + this.maxGroupSize + ")";
      }

      public EntityType type() {
         return this.type;
      }

      public int minGroupSize() {
         return this.minGroupSize;
      }

      public int maxGroupSize() {
         return this.maxGroupSize;
      }
   }

   public static class Builder {
      private final Map spawners = Util.mapEnum(SpawnGroup.class, (group) -> {
         return Pool.builder();
      });
      private final Map spawnCosts = Maps.newLinkedHashMap();
      private float creatureSpawnProbability = 0.1F;

      public Builder spawn(SpawnGroup spawnGroup, int weight, SpawnEntry entry) {
         ((Pool.Builder)this.spawners.get(spawnGroup)).add(entry, weight);
         return this;
      }

      public Builder spawnCost(EntityType entityType, double mass, double gravityLimit) {
         this.spawnCosts.put(entityType, new SpawnDensity(gravityLimit, mass));
         return this;
      }

      public Builder creatureSpawnProbability(float probability) {
         this.creatureSpawnProbability = probability;
         return this;
      }

      public SpawnSettings build() {
         return new SpawnSettings(this.creatureSpawnProbability, (Map)this.spawners.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (spawner) -> {
            return ((Pool.Builder)spawner.getValue()).build();
         })), ImmutableMap.copyOf(this.spawnCosts));
      }
   }
}
