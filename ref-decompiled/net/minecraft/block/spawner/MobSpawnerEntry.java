package net.minecraft.block.spawner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public record MobSpawnerEntry(NbtCompound entity, Optional customSpawnRules, Optional equipment) {
   public static final String ENTITY_KEY = "entity";
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(NbtCompound.CODEC.fieldOf("entity").forGetter((entry) -> {
         return entry.entity;
      }), MobSpawnerEntry.CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter((entry) -> {
         return entry.customSpawnRules;
      }), EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter((entry) -> {
         return entry.equipment;
      })).apply(instance, MobSpawnerEntry::new);
   });
   public static final Codec DATA_POOL_CODEC;

   public MobSpawnerEntry() {
      this(new NbtCompound(), Optional.empty(), Optional.empty());
   }

   public MobSpawnerEntry(NbtCompound nbtCompound, Optional optional, Optional optional2) {
      Optional optional3 = nbtCompound.get("id", Identifier.CODEC);
      if (optional3.isPresent()) {
         nbtCompound.put("id", Identifier.CODEC, (Identifier)optional3.get());
      } else {
         nbtCompound.remove("id");
      }

      this.entity = nbtCompound;
      this.customSpawnRules = optional;
      this.equipment = optional2;
   }

   public NbtCompound getNbt() {
      return this.entity;
   }

   public Optional getCustomSpawnRules() {
      return this.customSpawnRules;
   }

   public Optional getEquipment() {
      return this.equipment;
   }

   public NbtCompound entity() {
      return this.entity;
   }

   public Optional customSpawnRules() {
      return this.customSpawnRules;
   }

   public Optional equipment() {
      return this.equipment;
   }

   static {
      DATA_POOL_CODEC = Pool.createCodec(CODEC);
   }

   public static record CustomSpawnRules(Range blockLightLimit, Range skyLightLimit) {
      private static final Range DEFAULT = new Range(0, 15);
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(createLightLimitCodec("block_light_limit").forGetter((rules) -> {
            return rules.blockLightLimit;
         }), createLightLimitCodec("sky_light_limit").forGetter((rules) -> {
            return rules.skyLightLimit;
         })).apply(instance, CustomSpawnRules::new);
      });

      public CustomSpawnRules(Range range, Range range2) {
         this.blockLightLimit = range;
         this.skyLightLimit = range2;
      }

      private static DataResult validate(Range provider) {
         return !DEFAULT.contains(provider) ? DataResult.error(() -> {
            return "Light values must be withing range " + String.valueOf(DEFAULT);
         }) : DataResult.success(provider);
      }

      private static MapCodec createLightLimitCodec(String name) {
         return Range.CODEC.lenientOptionalFieldOf(name, DEFAULT).validate(CustomSpawnRules::validate);
      }

      public boolean canSpawn(BlockPos pos, ServerWorld world) {
         return this.blockLightLimit.contains((Comparable)world.getLightLevel(LightType.BLOCK, pos)) && this.skyLightLimit.contains((Comparable)world.getLightLevel(LightType.SKY, pos));
      }

      public Range blockLightLimit() {
         return this.blockLightLimit;
      }

      public Range skyLightLimit() {
         return this.skyLightLimit;
      }
   }
}
