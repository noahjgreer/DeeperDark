package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.LightType;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public final class Biome {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Biome.Weather.CODEC.forGetter((biome) -> {
         return biome.weather;
      }), BiomeEffects.CODEC.fieldOf("effects").forGetter((biome) -> {
         return biome.effects;
      }), GenerationSettings.CODEC.forGetter((biome) -> {
         return biome.generationSettings;
      }), SpawnSettings.CODEC.forGetter((biome) -> {
         return biome.spawnSettings;
      })).apply(instance, Biome::new);
   });
   public static final Codec NETWORK_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Biome.Weather.CODEC.forGetter((biome) -> {
         return biome.weather;
      }), BiomeEffects.CODEC.fieldOf("effects").forGetter((biome) -> {
         return biome.effects;
      })).apply(instance, (weather, effects) -> {
         return new Biome(weather, effects, GenerationSettings.INSTANCE, SpawnSettings.INSTANCE);
      });
   });
   public static final Codec REGISTRY_CODEC;
   public static final Codec REGISTRY_ENTRY_LIST_CODEC;
   private static final OctaveSimplexNoiseSampler TEMPERATURE_NOISE;
   static final OctaveSimplexNoiseSampler FROZEN_OCEAN_NOISE;
   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public static final OctaveSimplexNoiseSampler FOLIAGE_NOISE;
   private static final int MAX_TEMPERATURE_CACHE_SIZE = 1024;
   private final Weather weather;
   private final GenerationSettings generationSettings;
   private final SpawnSettings spawnSettings;
   private final BiomeEffects effects;
   private final ThreadLocal temperatureCache = ThreadLocal.withInitial(() -> {
      return (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
            protected void rehash(int n) {
            }
         };
         long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
         return long2FloatLinkedOpenHashMap;
      });
   });

   Biome(Weather weather, BiomeEffects effects, GenerationSettings generationSettings, SpawnSettings spawnSettings) {
      this.weather = weather;
      this.generationSettings = generationSettings;
      this.spawnSettings = spawnSettings;
      this.effects = effects;
   }

   public int getSkyColor() {
      return this.effects.getSkyColor();
   }

   public SpawnSettings getSpawnSettings() {
      return this.spawnSettings;
   }

   public boolean hasPrecipitation() {
      return this.weather.hasPrecipitation();
   }

   public Precipitation getPrecipitation(BlockPos pos, int seaLevel) {
      if (!this.hasPrecipitation()) {
         return Biome.Precipitation.NONE;
      } else {
         return this.isCold(pos, seaLevel) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
      }
   }

   private float computeTemperature(BlockPos pos, int seaLevel) {
      float f = this.weather.temperatureModifier.getModifiedTemperature(pos, this.getTemperature());
      int i = seaLevel + 17;
      if (pos.getY() > i) {
         float g = (float)(TEMPERATURE_NOISE.sample((double)((float)pos.getX() / 8.0F), (double)((float)pos.getZ() / 8.0F), false) * 8.0);
         return f - (g + (float)pos.getY() - (float)i) * 0.05F / 40.0F;
      } else {
         return f;
      }
   }

   /** @deprecated */
   @Deprecated
   private float getTemperature(BlockPos blockPos, int seaLevel) {
      long l = blockPos.asLong();
      Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = (Long2FloatLinkedOpenHashMap)this.temperatureCache.get();
      float f = long2FloatLinkedOpenHashMap.get(l);
      if (!Float.isNaN(f)) {
         return f;
      } else {
         float g = this.computeTemperature(blockPos, seaLevel);
         if (long2FloatLinkedOpenHashMap.size() == 1024) {
            long2FloatLinkedOpenHashMap.removeFirstFloat();
         }

         long2FloatLinkedOpenHashMap.put(l, g);
         return g;
      }
   }

   public boolean canSetIce(WorldView world, BlockPos blockPos) {
      return this.canSetIce(world, blockPos, true);
   }

   public boolean canSetIce(WorldView world, BlockPos pos, boolean doWaterCheck) {
      if (this.doesNotSnow(pos, world.getSeaLevel())) {
         return false;
      } else {
         if (world.isInHeightLimit(pos.getY()) && world.getLightLevel(LightType.BLOCK, pos) < 10) {
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            if (fluidState.getFluid() == Fluids.WATER && blockState.getBlock() instanceof FluidBlock) {
               if (!doWaterCheck) {
                  return true;
               }

               boolean bl = world.isWater(pos.west()) && world.isWater(pos.east()) && world.isWater(pos.north()) && world.isWater(pos.south());
               if (!bl) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean isCold(BlockPos pos, int seaLevel) {
      return !this.doesNotSnow(pos, seaLevel);
   }

   public boolean doesNotSnow(BlockPos pos, int seaLevel) {
      return this.getTemperature(pos, seaLevel) >= 0.15F;
   }

   public boolean shouldGenerateLowerFrozenOceanSurface(BlockPos pos, int seaLevel) {
      return this.getTemperature(pos, seaLevel) > 0.1F;
   }

   public boolean canSetSnow(WorldView world, BlockPos pos) {
      if (this.doesNotSnow(pos, world.getSeaLevel())) {
         return false;
      } else {
         if (world.isInHeightLimit(pos.getY()) && world.getLightLevel(LightType.BLOCK, pos) < 10) {
            BlockState blockState = world.getBlockState(pos);
            if ((blockState.isAir() || blockState.isOf(Blocks.SNOW)) && Blocks.SNOW.getDefaultState().canPlaceAt(world, pos)) {
               return true;
            }
         }

         return false;
      }
   }

   public GenerationSettings getGenerationSettings() {
      return this.generationSettings;
   }

   public int getFogColor() {
      return this.effects.getFogColor();
   }

   public int getGrassColorAt(double x, double z) {
      int i = this.getGrassColor();
      return this.effects.getGrassColorModifier().getModifiedGrassColor(x, z, i);
   }

   private int getGrassColor() {
      Optional optional = this.effects.getGrassColor();
      return optional.isPresent() ? (Integer)optional.get() : this.getDefaultGrassColor();
   }

   private int getDefaultGrassColor() {
      double d = (double)MathHelper.clamp(this.weather.temperature, 0.0F, 1.0F);
      double e = (double)MathHelper.clamp(this.weather.downfall, 0.0F, 1.0F);
      return GrassColors.getColor(d, e);
   }

   public int getFoliageColor() {
      return (Integer)this.effects.getFoliageColor().orElseGet(this::getDefaultFoliageColor);
   }

   private int getDefaultFoliageColor() {
      double d = (double)MathHelper.clamp(this.weather.temperature, 0.0F, 1.0F);
      double e = (double)MathHelper.clamp(this.weather.downfall, 0.0F, 1.0F);
      return FoliageColors.getColor(d, e);
   }

   public int getDryFoliageColor() {
      return (Integer)this.effects.getDryFoliageColor().orElseGet(this::getDefaultDryFoliageColor);
   }

   private int getDefaultDryFoliageColor() {
      double d = (double)MathHelper.clamp(this.weather.temperature, 0.0F, 1.0F);
      double e = (double)MathHelper.clamp(this.weather.downfall, 0.0F, 1.0F);
      return DryFoliageColors.getColor(d, e);
   }

   public float getTemperature() {
      return this.weather.temperature;
   }

   public BiomeEffects getEffects() {
      return this.effects;
   }

   public int getWaterColor() {
      return this.effects.getWaterColor();
   }

   public int getWaterFogColor() {
      return this.effects.getWaterFogColor();
   }

   public Optional getParticleConfig() {
      return this.effects.getParticleConfig();
   }

   public Optional getLoopSound() {
      return this.effects.getLoopSound();
   }

   public Optional getMoodSound() {
      return this.effects.getMoodSound();
   }

   public Optional getAdditionsSound() {
      return this.effects.getAdditionsSound();
   }

   public Optional getMusic() {
      return this.effects.getMusic();
   }

   public float getMusicVolume() {
      return this.effects.getMusicVolume();
   }

   static {
      REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.BIOME, CODEC);
      REGISTRY_ENTRY_LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.BIOME, CODEC);
      TEMPERATURE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(new CheckedRandom(1234L)), ImmutableList.of(0));
      FROZEN_OCEAN_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(new CheckedRandom(3456L)), ImmutableList.of(-2, -1, 0));
      FOLIAGE_NOISE = new OctaveSimplexNoiseSampler(new ChunkRandom(new CheckedRandom(2345L)), ImmutableList.of(0));
   }

   private static record Weather(boolean hasPrecipitation, float temperature, TemperatureModifier temperatureModifier, float downfall) {
      final float temperature;
      final TemperatureModifier temperatureModifier;
      final float downfall;
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.BOOL.fieldOf("has_precipitation").forGetter((weather) -> {
            return weather.hasPrecipitation;
         }), Codec.FLOAT.fieldOf("temperature").forGetter((weather) -> {
            return weather.temperature;
         }), Biome.TemperatureModifier.CODEC.optionalFieldOf("temperature_modifier", Biome.TemperatureModifier.NONE).forGetter((weather) -> {
            return weather.temperatureModifier;
         }), Codec.FLOAT.fieldOf("downfall").forGetter((weather) -> {
            return weather.downfall;
         })).apply(instance, Weather::new);
      });

      Weather(boolean bl, float temperature, TemperatureModifier temperatureModifier, float downfall) {
         this.hasPrecipitation = bl;
         this.temperature = temperature;
         this.temperatureModifier = temperatureModifier;
         this.downfall = downfall;
      }

      public boolean hasPrecipitation() {
         return this.hasPrecipitation;
      }

      public float temperature() {
         return this.temperature;
      }

      public TemperatureModifier temperatureModifier() {
         return this.temperatureModifier;
      }

      public float downfall() {
         return this.downfall;
      }
   }

   public static enum Precipitation implements StringIdentifiable {
      NONE("none"),
      RAIN("rain"),
      SNOW("snow");

      public static final Codec CODEC = StringIdentifiable.createCodec(Precipitation::values);
      private final String name;

      private Precipitation(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Precipitation[] method_36699() {
         return new Precipitation[]{NONE, RAIN, SNOW};
      }
   }

   public static enum TemperatureModifier implements StringIdentifiable {
      NONE("none") {
         public float getModifiedTemperature(BlockPos pos, float temperature) {
            return temperature;
         }
      },
      FROZEN("frozen") {
         public float getModifiedTemperature(BlockPos pos, float temperature) {
            double d = Biome.FROZEN_OCEAN_NOISE.sample((double)pos.getX() * 0.05, (double)pos.getZ() * 0.05, false) * 7.0;
            double e = Biome.FOLIAGE_NOISE.sample((double)pos.getX() * 0.2, (double)pos.getZ() * 0.2, false);
            double f = d + e;
            if (f < 0.3) {
               double g = Biome.FOLIAGE_NOISE.sample((double)pos.getX() * 0.09, (double)pos.getZ() * 0.09, false);
               if (g < 0.8) {
                  return 0.2F;
               }
            }

            return temperature;
         }
      };

      private final String name;
      public static final Codec CODEC = StringIdentifiable.createCodec(TemperatureModifier::values);

      public abstract float getModifiedTemperature(BlockPos pos, float temperature);

      TemperatureModifier(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static TemperatureModifier[] method_36700() {
         return new TemperatureModifier[]{NONE, FROZEN};
      }
   }

   public static class Builder {
      private boolean precipitation = true;
      @Nullable
      private Float temperature;
      private TemperatureModifier temperatureModifier;
      @Nullable
      private Float downfall;
      @Nullable
      private BiomeEffects specialEffects;
      @Nullable
      private SpawnSettings spawnSettings;
      @Nullable
      private GenerationSettings generationSettings;

      public Builder() {
         this.temperatureModifier = Biome.TemperatureModifier.NONE;
      }

      public Builder precipitation(boolean precipitation) {
         this.precipitation = precipitation;
         return this;
      }

      public Builder temperature(float temperature) {
         this.temperature = temperature;
         return this;
      }

      public Builder downfall(float downfall) {
         this.downfall = downfall;
         return this;
      }

      public Builder effects(BiomeEffects effects) {
         this.specialEffects = effects;
         return this;
      }

      public Builder spawnSettings(SpawnSettings spawnSettings) {
         this.spawnSettings = spawnSettings;
         return this;
      }

      public Builder generationSettings(GenerationSettings generationSettings) {
         this.generationSettings = generationSettings;
         return this;
      }

      public Builder temperatureModifier(TemperatureModifier temperatureModifier) {
         this.temperatureModifier = temperatureModifier;
         return this;
      }

      public Biome build() {
         if (this.temperature != null && this.downfall != null && this.specialEffects != null && this.spawnSettings != null && this.generationSettings != null) {
            return new Biome(new Weather(this.precipitation, this.temperature, this.temperatureModifier, this.downfall), this.specialEffects, this.generationSettings, this.spawnSettings);
         } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome\n" + String.valueOf(this));
         }
      }

      public String toString() {
         boolean var10000 = this.precipitation;
         return "BiomeBuilder{\nhasPrecipitation=" + var10000 + ",\ntemperature=" + this.temperature + ",\ntemperatureModifier=" + String.valueOf(this.temperatureModifier) + ",\ndownfall=" + this.downfall + ",\nspecialEffects=" + String.valueOf(this.specialEffects) + ",\nmobSpawnSettings=" + String.valueOf(this.spawnSettings) + ",\ngenerationSettings=" + String.valueOf(this.generationSettings) + ",\n}";
      }
   }
}
