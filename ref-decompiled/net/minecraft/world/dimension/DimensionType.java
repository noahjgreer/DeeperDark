package net.minecraft.world.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.World;

public record DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean bedWorks, boolean respawnAnchorWorks, int minY, int height, int logicalHeight, TagKey infiniburn, Identifier effects, float ambientLight, Optional cloudHeight, MonsterSettings monsterSettings) {
   public static final int SIZE_BITS_Y;
   public static final int field_33411 = 16;
   public static final int MAX_HEIGHT;
   public static final int MAX_COLUMN_HEIGHT;
   public static final int MIN_HEIGHT;
   public static final int field_35478;
   public static final int field_35479;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final int field_31440 = 8;
   public static final float[] MOON_SIZES;
   public static final Codec REGISTRY_CODEC;

   public DimensionType(OptionalLong fixedTime, boolean hasSkylight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean bedWorks, boolean piglinSafe, int minY, int height, int logicalHeight, TagKey infiniburn, Identifier effects, float ambientLight, Optional cloudHeight, MonsterSettings monsterSettings) {
      if (height < 16) {
         throw new IllegalStateException("height has to be at least 16");
      } else if (minY + height > MAX_COLUMN_HEIGHT + 1) {
         throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_COLUMN_HEIGHT + 1));
      } else if (logicalHeight > height) {
         throw new IllegalStateException("logical_height cannot be higher than height");
      } else if (height % 16 != 0) {
         throw new IllegalStateException("height has to be multiple of 16");
      } else if (minY % 16 != 0) {
         throw new IllegalStateException("min_y has to be a multiple of 16");
      } else {
         this.fixedTime = fixedTime;
         this.hasSkyLight = hasSkylight;
         this.hasCeiling = hasCeiling;
         this.ultrawarm = ultrawarm;
         this.natural = natural;
         this.coordinateScale = coordinateScale;
         this.bedWorks = bedWorks;
         this.respawnAnchorWorks = piglinSafe;
         this.minY = minY;
         this.height = height;
         this.logicalHeight = logicalHeight;
         this.infiniburn = infiniburn;
         this.effects = effects;
         this.ambientLight = ambientLight;
         this.cloudHeight = cloudHeight;
         this.monsterSettings = monsterSettings;
      }
   }

   public static double getCoordinateScaleFactor(DimensionType fromDimension, DimensionType toDimension) {
      double d = fromDimension.coordinateScale();
      double e = toDimension.coordinateScale();
      return d / e;
   }

   public static Path getSaveDirectory(RegistryKey worldRef, Path worldDirectory) {
      if (worldRef == World.OVERWORLD) {
         return worldDirectory;
      } else if (worldRef == World.END) {
         return worldDirectory.resolve("DIM1");
      } else {
         return worldRef == World.NETHER ? worldDirectory.resolve("DIM-1") : worldDirectory.resolve("dimensions").resolve(worldRef.getValue().getNamespace()).resolve(worldRef.getValue().getPath());
      }
   }

   public boolean hasFixedTime() {
      return this.fixedTime.isPresent();
   }

   public float getSkyAngle(long time) {
      double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(time) / 24000.0 - 0.25);
      double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
      return (float)(d * 2.0 + e) / 3.0F;
   }

   public int getMoonPhase(long time) {
      return (int)(time / 24000L % 8L + 8L) % 8;
   }

   public boolean piglinSafe() {
      return this.monsterSettings.piglinSafe();
   }

   public boolean hasRaids() {
      return this.monsterSettings.hasRaids();
   }

   public IntProvider monsterSpawnLightTest() {
      return this.monsterSettings.monsterSpawnLightTest();
   }

   public int monsterSpawnBlockLightLimit() {
      return this.monsterSettings.monsterSpawnBlockLightLimit();
   }

   public OptionalLong fixedTime() {
      return this.fixedTime;
   }

   public boolean hasSkyLight() {
      return this.hasSkyLight;
   }

   public boolean hasCeiling() {
      return this.hasCeiling;
   }

   public boolean ultrawarm() {
      return this.ultrawarm;
   }

   public boolean natural() {
      return this.natural;
   }

   public double coordinateScale() {
      return this.coordinateScale;
   }

   public boolean bedWorks() {
      return this.bedWorks;
   }

   public boolean respawnAnchorWorks() {
      return this.respawnAnchorWorks;
   }

   public int minY() {
      return this.minY;
   }

   public int height() {
      return this.height;
   }

   public int logicalHeight() {
      return this.logicalHeight;
   }

   public TagKey infiniburn() {
      return this.infiniburn;
   }

   public Identifier effects() {
      return this.effects;
   }

   public float ambientLight() {
      return this.ambientLight;
   }

   public Optional cloudHeight() {
      return this.cloudHeight;
   }

   public MonsterSettings monsterSettings() {
      return this.monsterSettings;
   }

   static {
      SIZE_BITS_Y = BlockPos.SIZE_BITS_Y;
      MAX_HEIGHT = (1 << SIZE_BITS_Y) - 32;
      MAX_COLUMN_HEIGHT = (MAX_HEIGHT >> 1) - 1;
      MIN_HEIGHT = MAX_COLUMN_HEIGHT - MAX_HEIGHT + 1;
      field_35478 = MAX_COLUMN_HEIGHT << 4;
      field_35479 = MIN_HEIGHT << 4;
      CODEC = Codecs.exceptionCatching(RecordCodecBuilder.create((instance) -> {
         return instance.group(Codecs.optionalLong(Codec.LONG.lenientOptionalFieldOf("fixed_time")).forGetter(DimensionType::fixedTime), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultrawarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange(9.999999747378752E-6, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), Codec.intRange(MIN_HEIGHT, MAX_COLUMN_HEIGHT).fieldOf("min_y").forGetter(DimensionType::minY), Codec.intRange(16, MAX_HEIGHT).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, MAX_HEIGHT).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.codec(RegistryKeys.BLOCK).fieldOf("infiniburn").forGetter(DimensionType::infiniburn), Identifier.CODEC.fieldOf("effects").orElse(DimensionTypes.OVERWORLD_ID).forGetter(DimensionType::effects), Codec.FLOAT.fieldOf("ambient_light").forGetter(DimensionType::ambientLight), Codec.intRange(MIN_HEIGHT, MAX_COLUMN_HEIGHT).optionalFieldOf("cloud_height").forGetter(DimensionType::cloudHeight), DimensionType.MonsterSettings.CODEC.forGetter(DimensionType::monsterSettings)).apply(instance, DimensionType::new);
      }));
      PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.DIMENSION_TYPE);
      MOON_SIZES = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
      REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.DIMENSION_TYPE, CODEC);
   }

   public static record MonsterSettings(boolean piglinSafe, boolean hasRaids, IntProvider monsterSpawnLightTest, int monsterSpawnBlockLightLimit) {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.BOOL.fieldOf("piglin_safe").forGetter(MonsterSettings::piglinSafe), Codec.BOOL.fieldOf("has_raids").forGetter(MonsterSettings::hasRaids), IntProvider.createValidatingCodec(0, 15).fieldOf("monster_spawn_light_level").forGetter(MonsterSettings::monsterSpawnLightTest), Codec.intRange(0, 15).fieldOf("monster_spawn_block_light_limit").forGetter(MonsterSettings::monsterSpawnBlockLightLimit)).apply(instance, MonsterSettings::new);
      });

      public MonsterSettings(boolean bl, boolean bl2, IntProvider intProvider, int i) {
         this.piglinSafe = bl;
         this.hasRaids = bl2;
         this.monsterSpawnLightTest = intProvider;
         this.monsterSpawnBlockLightLimit = i;
      }

      public boolean piglinSafe() {
         return this.piglinSafe;
      }

      public boolean hasRaids() {
         return this.hasRaids;
      }

      public IntProvider monsterSpawnLightTest() {
         return this.monsterSpawnLightTest;
      }

      public int monsterSpawnBlockLightLimit() {
         return this.monsterSpawnBlockLightLimit;
      }
   }
}
