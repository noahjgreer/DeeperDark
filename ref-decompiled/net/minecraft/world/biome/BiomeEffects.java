package net.minecraft.world.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import org.jetbrains.annotations.Nullable;

public class BiomeEffects {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.INT.fieldOf("fog_color").forGetter((effects) -> {
         return effects.fogColor;
      }), Codec.INT.fieldOf("water_color").forGetter((effects) -> {
         return effects.waterColor;
      }), Codec.INT.fieldOf("water_fog_color").forGetter((effects) -> {
         return effects.waterFogColor;
      }), Codec.INT.fieldOf("sky_color").forGetter((effects) -> {
         return effects.skyColor;
      }), Codec.INT.optionalFieldOf("foliage_color").forGetter((effects) -> {
         return effects.foliageColor;
      }), Codec.INT.optionalFieldOf("dry_foliage_color").forGetter((effects) -> {
         return effects.dryFoliageColor;
      }), Codec.INT.optionalFieldOf("grass_color").forGetter((effects) -> {
         return effects.grassColor;
      }), BiomeEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier", BiomeEffects.GrassColorModifier.NONE).forGetter((effects) -> {
         return effects.grassColorModifier;
      }), BiomeParticleConfig.CODEC.optionalFieldOf("particle").forGetter((effects) -> {
         return effects.particleConfig;
      }), SoundEvent.ENTRY_CODEC.optionalFieldOf("ambient_sound").forGetter((effects) -> {
         return effects.loopSound;
      }), BiomeMoodSound.CODEC.optionalFieldOf("mood_sound").forGetter((effects) -> {
         return effects.moodSound;
      }), BiomeAdditionsSound.CODEC.optionalFieldOf("additions_sound").forGetter((effects) -> {
         return effects.additionsSound;
      }), Pool.createCodec(MusicSound.CODEC).optionalFieldOf("music").forGetter((effects) -> {
         return effects.music;
      }), Codec.FLOAT.fieldOf("music_volume").orElse(1.0F).forGetter((effects) -> {
         return effects.musicVolume;
      })).apply(instance, BiomeEffects::new);
   });
   private final int fogColor;
   private final int waterColor;
   private final int waterFogColor;
   private final int skyColor;
   private final Optional foliageColor;
   private final Optional dryFoliageColor;
   private final Optional grassColor;
   private final GrassColorModifier grassColorModifier;
   private final Optional particleConfig;
   private final Optional loopSound;
   private final Optional moodSound;
   private final Optional additionsSound;
   private final Optional music;
   private final float musicVolume;

   BiomeEffects(int fogColor, int waterColor, int waterFogColor, int skyColor, Optional foliageColor, Optional dryFoliageColor, Optional grassColor, GrassColorModifier grassColorModifier, Optional particleConfig, Optional loopSound, Optional moodSound, Optional additionsSound, Optional music, float musicVolume) {
      this.fogColor = fogColor;
      this.waterColor = waterColor;
      this.waterFogColor = waterFogColor;
      this.skyColor = skyColor;
      this.foliageColor = foliageColor;
      this.dryFoliageColor = dryFoliageColor;
      this.grassColor = grassColor;
      this.grassColorModifier = grassColorModifier;
      this.particleConfig = particleConfig;
      this.loopSound = loopSound;
      this.moodSound = moodSound;
      this.additionsSound = additionsSound;
      this.music = music;
      this.musicVolume = musicVolume;
   }

   public int getFogColor() {
      return this.fogColor;
   }

   public int getWaterColor() {
      return this.waterColor;
   }

   public int getWaterFogColor() {
      return this.waterFogColor;
   }

   public int getSkyColor() {
      return this.skyColor;
   }

   public Optional getFoliageColor() {
      return this.foliageColor;
   }

   public Optional getDryFoliageColor() {
      return this.dryFoliageColor;
   }

   public Optional getGrassColor() {
      return this.grassColor;
   }

   public GrassColorModifier getGrassColorModifier() {
      return this.grassColorModifier;
   }

   public Optional getParticleConfig() {
      return this.particleConfig;
   }

   public Optional getLoopSound() {
      return this.loopSound;
   }

   public Optional getMoodSound() {
      return this.moodSound;
   }

   public Optional getAdditionsSound() {
      return this.additionsSound;
   }

   public Optional getMusic() {
      return this.music;
   }

   public float getMusicVolume() {
      return this.musicVolume;
   }

   public static enum GrassColorModifier implements StringIdentifiable {
      NONE("none") {
         public int getModifiedGrassColor(double x, double z, int color) {
            return color;
         }
      },
      DARK_FOREST("dark_forest") {
         public int getModifiedGrassColor(double x, double z, int color) {
            return (color & 16711422) + 2634762 >> 1;
         }
      },
      SWAMP("swamp") {
         public int getModifiedGrassColor(double x, double z, int color) {
            double d = Biome.FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
            return d < -0.1 ? 5011004 : 6975545;
         }
      };

      private final String name;
      public static final Codec CODEC = StringIdentifiable.createCodec(GrassColorModifier::values);

      public abstract int getModifiedGrassColor(double x, double z, int color);

      GrassColorModifier(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static GrassColorModifier[] method_36701() {
         return new GrassColorModifier[]{NONE, DARK_FOREST, SWAMP};
      }
   }

   public static class Builder {
      private OptionalInt fogColor = OptionalInt.empty();
      private OptionalInt waterColor = OptionalInt.empty();
      private OptionalInt waterFogColor = OptionalInt.empty();
      private OptionalInt skyColor = OptionalInt.empty();
      private Optional foliageColor = Optional.empty();
      private Optional dryFoliageColor = Optional.empty();
      private Optional grassColor = Optional.empty();
      private GrassColorModifier grassColorModifier;
      private Optional particleConfig;
      private Optional loopSound;
      private Optional moodSound;
      private Optional additionsSound;
      private Optional music;
      private float musicVolume;

      public Builder() {
         this.grassColorModifier = BiomeEffects.GrassColorModifier.NONE;
         this.particleConfig = Optional.empty();
         this.loopSound = Optional.empty();
         this.moodSound = Optional.empty();
         this.additionsSound = Optional.empty();
         this.music = Optional.empty();
         this.musicVolume = 1.0F;
      }

      public Builder fogColor(int fogColor) {
         this.fogColor = OptionalInt.of(fogColor);
         return this;
      }

      public Builder waterColor(int waterColor) {
         this.waterColor = OptionalInt.of(waterColor);
         return this;
      }

      public Builder waterFogColor(int waterFogColor) {
         this.waterFogColor = OptionalInt.of(waterFogColor);
         return this;
      }

      public Builder skyColor(int skyColor) {
         this.skyColor = OptionalInt.of(skyColor);
         return this;
      }

      public Builder foliageColor(int foliageColor) {
         this.foliageColor = Optional.of(foliageColor);
         return this;
      }

      public Builder dryFoliageColor(int dryFoliageColor) {
         this.dryFoliageColor = Optional.of(dryFoliageColor);
         return this;
      }

      public Builder grassColor(int grassColor) {
         this.grassColor = Optional.of(grassColor);
         return this;
      }

      public Builder grassColorModifier(GrassColorModifier grassColorModifier) {
         this.grassColorModifier = grassColorModifier;
         return this;
      }

      public Builder particleConfig(BiomeParticleConfig particleConfig) {
         this.particleConfig = Optional.of(particleConfig);
         return this;
      }

      public Builder loopSound(RegistryEntry loopSound) {
         this.loopSound = Optional.of(loopSound);
         return this;
      }

      public Builder moodSound(BiomeMoodSound moodSound) {
         this.moodSound = Optional.of(moodSound);
         return this;
      }

      public Builder additionsSound(BiomeAdditionsSound additionsSound) {
         this.additionsSound = Optional.of(additionsSound);
         return this;
      }

      public Builder music(@Nullable MusicSound music) {
         if (music == null) {
            this.music = Optional.empty();
            return this;
         } else {
            this.music = Optional.of(Pool.of((Object)music));
            return this;
         }
      }

      public Builder noMusic() {
         return this.music(Pool.empty()).musicVolume(0.0F);
      }

      public Builder music(Pool music) {
         this.music = Optional.of(music);
         return this;
      }

      public Builder musicVolume(float musicVolume) {
         this.musicVolume = musicVolume;
         return this;
      }

      public BiomeEffects build() {
         return new BiomeEffects(this.fogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'fog' color.");
         }), this.waterColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water' color.");
         }), this.waterFogColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'water fog' color.");
         }), this.skyColor.orElseThrow(() -> {
            return new IllegalStateException("Missing 'sky' color.");
         }), this.foliageColor, this.dryFoliageColor, this.grassColor, this.grassColorModifier, this.particleConfig, this.loopSound, this.moodSound, this.additionsSound, this.music, this.musicVolume);
      }
   }
}
