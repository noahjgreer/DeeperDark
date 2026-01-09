package net.minecraft.world.level;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.OptionalDynamic;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.SaveVersionInfo;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LevelProperties implements ServerWorldProperties, SaveProperties {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String LEVEL_NAME_KEY = "LevelName";
   protected static final String PLAYER_KEY = "Player";
   protected static final String WORLD_GEN_SETTINGS_KEY = "WorldGenSettings";
   private LevelInfo levelInfo;
   private final GeneratorOptions generatorOptions;
   private final SpecialProperty specialProperty;
   private final Lifecycle lifecycle;
   private BlockPos spawnPos;
   private float spawnAngle;
   private long time;
   private long timeOfDay;
   @Nullable
   private final NbtCompound playerData;
   private final int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private boolean initialized;
   private boolean difficultyLocked;
   private WorldBorder.Properties worldBorder;
   private EnderDragonFight.Data dragonFight;
   @Nullable
   private NbtCompound customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   @Nullable
   private UUID wanderingTraderId;
   private final Set serverBrands;
   private boolean modded;
   private final Set removedFeatures;
   private final Timer scheduledEvents;

   private LevelProperties(@Nullable NbtCompound playerData, boolean modded, BlockPos spawnPos, float spawnAngle, long time, long timeOfDay, int version, int clearWeatherTime, int rainTime, boolean raining, int thunderTime, boolean thundering, boolean initialized, boolean difficultyLocked, WorldBorder.Properties worldBorder, int wanderingTraderSpawnDelay, int wanderingTraderSpawnChance, @Nullable UUID wanderingTraderId, Set serverBrands, Set removedFeatures, Timer scheduledEvents, @Nullable NbtCompound customBossEvents, EnderDragonFight.Data dragonFight, LevelInfo levelInfo, GeneratorOptions generatorOptions, SpecialProperty specialProperty, Lifecycle lifecycle) {
      this.modded = modded;
      this.spawnPos = spawnPos;
      this.spawnAngle = spawnAngle;
      this.time = time;
      this.timeOfDay = timeOfDay;
      this.version = version;
      this.clearWeatherTime = clearWeatherTime;
      this.rainTime = rainTime;
      this.raining = raining;
      this.thunderTime = thunderTime;
      this.thundering = thundering;
      this.initialized = initialized;
      this.difficultyLocked = difficultyLocked;
      this.worldBorder = worldBorder;
      this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
      this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
      this.wanderingTraderId = wanderingTraderId;
      this.serverBrands = serverBrands;
      this.removedFeatures = removedFeatures;
      this.playerData = playerData;
      this.scheduledEvents = scheduledEvents;
      this.customBossEvents = customBossEvents;
      this.dragonFight = dragonFight;
      this.levelInfo = levelInfo;
      this.generatorOptions = generatorOptions;
      this.specialProperty = specialProperty;
      this.lifecycle = lifecycle;
   }

   public LevelProperties(LevelInfo levelInfo, GeneratorOptions generatorOptions, SpecialProperty specialProperty, Lifecycle lifecycle) {
      this((NbtCompound)null, false, BlockPos.ORIGIN, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_BORDER, 0, 0, (UUID)null, Sets.newLinkedHashSet(), new HashSet(), new Timer(TimerCallbackSerializer.INSTANCE), (NbtCompound)null, EnderDragonFight.Data.DEFAULT, levelInfo.withCopiedGameRules(), generatorOptions, specialProperty, lifecycle);
   }

   public static LevelProperties readProperties(Dynamic dynamic, LevelInfo info, SpecialProperty specialProperty, GeneratorOptions generatorOptions, Lifecycle lifecycle) {
      long l = dynamic.get("Time").asLong(0L);
      OptionalDynamic var10002 = dynamic.get("Player");
      Codec var10003 = NbtCompound.CODEC;
      Objects.requireNonNull(var10003);
      NbtCompound var7 = (NbtCompound)var10002.flatMap(var10003::parse).result().orElse((Object)null);
      boolean var8 = dynamic.get("WasModded").asBoolean(false);
      BlockPos var10004 = new BlockPos(dynamic.get("SpawnX").asInt(0), dynamic.get("SpawnY").asInt(0), dynamic.get("SpawnZ").asInt(0));
      float var10005 = dynamic.get("SpawnAngle").asFloat(0.0F);
      long var10007 = dynamic.get("DayTime").asLong(l);
      int var10008 = SaveVersionInfo.fromDynamic(dynamic).getLevelFormatVersion();
      int var10009 = dynamic.get("clearWeatherTime").asInt(0);
      int var10010 = dynamic.get("rainTime").asInt(0);
      boolean var10011 = dynamic.get("raining").asBoolean(false);
      int var10012 = dynamic.get("thunderTime").asInt(0);
      boolean var10013 = dynamic.get("thundering").asBoolean(false);
      boolean var10014 = dynamic.get("initialized").asBoolean(true);
      boolean var10015 = dynamic.get("DifficultyLocked").asBoolean(false);
      WorldBorder.Properties var10016 = WorldBorder.Properties.fromDynamic(dynamic, WorldBorder.DEFAULT_BORDER);
      int var10017 = dynamic.get("WanderingTraderSpawnDelay").asInt(0);
      int var10018 = dynamic.get("WanderingTraderSpawnChance").asInt(0);
      UUID var10019 = (UUID)dynamic.get("WanderingTraderId").read(Uuids.INT_STREAM_CODEC).result().orElse((Object)null);
      Set var10020 = (Set)dynamic.get("ServerBrands").asStream().flatMap((dynamicx) -> {
         return dynamicx.asString().result().stream();
      }).collect(Collectors.toCollection(Sets::newLinkedHashSet));
      Set var10021 = (Set)dynamic.get("removed_features").asStream().flatMap((dynamicx) -> {
         return dynamicx.asString().result().stream();
      }).collect(Collectors.toSet());
      Timer var10022 = new Timer(TimerCallbackSerializer.INSTANCE, dynamic.get("ScheduledEvents").asStream());
      NbtCompound var10023 = (NbtCompound)dynamic.get("CustomBossEvents").orElseEmptyMap().getValue();
      DataResult var10024 = dynamic.get("DragonFight").read(EnderDragonFight.Data.CODEC);
      Logger var10025 = LOGGER;
      Objects.requireNonNull(var10025);
      return new LevelProperties(var7, var8, var10004, var10005, l, var10007, var10008, var10009, var10010, var10011, var10012, var10013, var10014, var10015, var10016, var10017, var10018, var10019, var10020, var10021, var10022, var10023, (EnderDragonFight.Data)var10024.resultOrPartial(var10025::error).orElse(EnderDragonFight.Data.DEFAULT), info, generatorOptions, specialProperty, lifecycle);
   }

   public NbtCompound cloneWorldNbt(DynamicRegistryManager registryManager, @Nullable NbtCompound playerNbt) {
      if (playerNbt == null) {
         playerNbt = this.playerData;
      }

      NbtCompound nbtCompound = new NbtCompound();
      this.updateProperties(registryManager, nbtCompound, playerNbt);
      return nbtCompound;
   }

   private void updateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, @Nullable NbtCompound playerNbt) {
      levelNbt.put("ServerBrands", createStringList(this.serverBrands));
      levelNbt.putBoolean("WasModded", this.modded);
      if (!this.removedFeatures.isEmpty()) {
         levelNbt.put("removed_features", createStringList(this.removedFeatures));
      }

      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.putString("Name", SharedConstants.getGameVersion().name());
      nbtCompound.putInt("Id", SharedConstants.getGameVersion().dataVersion().id());
      nbtCompound.putBoolean("Snapshot", !SharedConstants.getGameVersion().stable());
      nbtCompound.putString("Series", SharedConstants.getGameVersion().dataVersion().series());
      levelNbt.put("Version", nbtCompound);
      NbtHelper.putDataVersion(levelNbt);
      DynamicOps dynamicOps = registryManager.getOps(NbtOps.INSTANCE);
      DataResult var10000 = WorldGenSettings.encode(dynamicOps, this.generatorOptions, (DynamicRegistryManager)registryManager);
      Logger var10002 = LOGGER;
      Objects.requireNonNull(var10002);
      var10000.resultOrPartial(Util.addPrefix("WorldGenSettings: ", var10002::error)).ifPresent((nbtElement) -> {
         levelNbt.put("WorldGenSettings", nbtElement);
      });
      levelNbt.putInt("GameType", this.levelInfo.getGameMode().getIndex());
      levelNbt.putInt("SpawnX", this.spawnPos.getX());
      levelNbt.putInt("SpawnY", this.spawnPos.getY());
      levelNbt.putInt("SpawnZ", this.spawnPos.getZ());
      levelNbt.putFloat("SpawnAngle", this.spawnAngle);
      levelNbt.putLong("Time", this.time);
      levelNbt.putLong("DayTime", this.timeOfDay);
      levelNbt.putLong("LastPlayed", Util.getEpochTimeMs());
      levelNbt.putString("LevelName", this.levelInfo.getLevelName());
      levelNbt.putInt("version", 19133);
      levelNbt.putInt("clearWeatherTime", this.clearWeatherTime);
      levelNbt.putInt("rainTime", this.rainTime);
      levelNbt.putBoolean("raining", this.raining);
      levelNbt.putInt("thunderTime", this.thunderTime);
      levelNbt.putBoolean("thundering", this.thundering);
      levelNbt.putBoolean("hardcore", this.levelInfo.isHardcore());
      levelNbt.putBoolean("allowCommands", this.levelInfo.areCommandsAllowed());
      levelNbt.putBoolean("initialized", this.initialized);
      this.worldBorder.writeNbt(levelNbt);
      levelNbt.putByte("Difficulty", (byte)this.levelInfo.getDifficulty().getId());
      levelNbt.putBoolean("DifficultyLocked", this.difficultyLocked);
      levelNbt.put("GameRules", this.levelInfo.getGameRules().toNbt());
      levelNbt.put("DragonFight", EnderDragonFight.Data.CODEC, this.dragonFight);
      if (playerNbt != null) {
         levelNbt.put("Player", playerNbt);
      }

      levelNbt.copyFromCodec(DataConfiguration.MAP_CODEC, this.levelInfo.getDataConfiguration());
      if (this.customBossEvents != null) {
         levelNbt.put("CustomBossEvents", this.customBossEvents);
      }

      levelNbt.put("ScheduledEvents", this.scheduledEvents.toNbt());
      levelNbt.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      levelNbt.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      levelNbt.putNullable("WanderingTraderId", Uuids.INT_STREAM_CODEC, this.wanderingTraderId);
   }

   private static NbtList createStringList(Set strings) {
      NbtList nbtList = new NbtList();
      Stream var10000 = strings.stream().map(NbtString::of);
      Objects.requireNonNull(nbtList);
      var10000.forEach(nbtList::add);
      return nbtList;
   }

   public BlockPos getSpawnPos() {
      return this.spawnPos;
   }

   public float getSpawnAngle() {
      return this.spawnAngle;
   }

   public long getTime() {
      return this.time;
   }

   public long getTimeOfDay() {
      return this.timeOfDay;
   }

   @Nullable
   public NbtCompound getPlayerData() {
      return this.playerData;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public void setTimeOfDay(long timeOfDay) {
      this.timeOfDay = timeOfDay;
   }

   public void setSpawnPos(BlockPos pos, float angle) {
      this.spawnPos = pos.toImmutable();
      this.spawnAngle = angle;
   }

   public String getLevelName() {
      return this.levelInfo.getLevelName();
   }

   public int getVersion() {
      return this.version;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int clearWeatherTime) {
      this.clearWeatherTime = clearWeatherTime;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean thundering) {
      this.thundering = thundering;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int thunderTime) {
      this.thunderTime = thunderTime;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean raining) {
      this.raining = raining;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int rainTime) {
      this.rainTime = rainTime;
   }

   public GameMode getGameMode() {
      return this.levelInfo.getGameMode();
   }

   public void setGameMode(GameMode gameMode) {
      this.levelInfo = this.levelInfo.withGameMode(gameMode);
   }

   public boolean isHardcore() {
      return this.levelInfo.isHardcore();
   }

   public boolean areCommandsAllowed() {
      return this.levelInfo.areCommandsAllowed();
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean initialized) {
      this.initialized = initialized;
   }

   public GameRules getGameRules() {
      return this.levelInfo.getGameRules();
   }

   public WorldBorder.Properties getWorldBorder() {
      return this.worldBorder;
   }

   public void setWorldBorder(WorldBorder.Properties worldBorder) {
      this.worldBorder = worldBorder;
   }

   public Difficulty getDifficulty() {
      return this.levelInfo.getDifficulty();
   }

   public void setDifficulty(Difficulty difficulty) {
      this.levelInfo = this.levelInfo.withDifficulty(difficulty);
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean difficultyLocked) {
      this.difficultyLocked = difficultyLocked;
   }

   public Timer getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void populateCrashReport(CrashReportSection reportSection, HeightLimitView world) {
      ServerWorldProperties.super.populateCrashReport(reportSection, world);
      SaveProperties.super.populateCrashReport(reportSection);
   }

   public GeneratorOptions getGeneratorOptions() {
      return this.generatorOptions;
   }

   public boolean isFlatWorld() {
      return this.specialProperty == LevelProperties.SpecialProperty.FLAT;
   }

   public boolean isDebugWorld() {
      return this.specialProperty == LevelProperties.SpecialProperty.DEBUG;
   }

   public Lifecycle getLifecycle() {
      return this.lifecycle;
   }

   public EnderDragonFight.Data getDragonFight() {
      return this.dragonFight;
   }

   public void setDragonFight(EnderDragonFight.Data dragonFight) {
      this.dragonFight = dragonFight;
   }

   public DataConfiguration getDataConfiguration() {
      return this.levelInfo.getDataConfiguration();
   }

   public void updateLevelInfo(DataConfiguration dataConfiguration) {
      this.levelInfo = this.levelInfo.withDataConfiguration(dataConfiguration);
   }

   @Nullable
   public NbtCompound getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable NbtCompound customBossEvents) {
      this.customBossEvents = customBossEvents;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
      this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
      this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
   }

   @Nullable
   public UUID getWanderingTraderId() {
      return this.wanderingTraderId;
   }

   public void setWanderingTraderId(UUID wanderingTraderId) {
      this.wanderingTraderId = wanderingTraderId;
   }

   public void addServerBrand(String brand, boolean modded) {
      this.serverBrands.add(brand);
      this.modded |= modded;
   }

   public boolean isModded() {
      return this.modded;
   }

   public Set getServerBrands() {
      return ImmutableSet.copyOf(this.serverBrands);
   }

   public Set getRemovedFeatures() {
      return Set.copyOf(this.removedFeatures);
   }

   public ServerWorldProperties getMainWorldProperties() {
      return this;
   }

   public LevelInfo getLevelInfo() {
      return this.levelInfo.withCopiedGameRules();
   }

   /** @deprecated */
   @Deprecated
   public static enum SpecialProperty {
      NONE,
      FLAT,
      DEBUG;

      // $FF: synthetic method
      private static SpecialProperty[] method_45559() {
         return new SpecialProperty[]{NONE, FLAT, DEBUG};
      }
   }
}
