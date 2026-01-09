package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeTypeCache;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerWaypointHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.function.LazyIterationConsumer;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.village.raid.Raid;
import net.minecraft.village.raid.RaidManager;
import net.minecraft.world.EntityList;
import net.minecraft.world.EntityLookupView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IdCountsState;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.StructureLocator;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.dimension.PortalForcer;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.event.listener.GameEventDispatchManager;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.world.spawner.SpecialSpawner;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkPosKeyedStorage;
import net.minecraft.world.storage.EntityChunkDataAccess;
import net.minecraft.world.storage.StorageKey;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import net.minecraft.world.tick.WorldTickScheduler;
import net.minecraft.world.waypoint.ServerWaypoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerWorld extends World implements EntityLookupView, StructureWorldAccess {
   public static final BlockPos END_SPAWN_POS = new BlockPos(100, 50, 0);
   public static final IntProvider CLEAR_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 180000);
   public static final IntProvider RAIN_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 24000);
   private static final IntProvider CLEAR_THUNDER_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(12000, 180000);
   public static final IntProvider THUNDER_WEATHER_DURATION_PROVIDER = UniformIntProvider.create(3600, 15600);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SERVER_IDLE_COOLDOWN = 300;
   private static final int MAX_TICKS = 65536;
   final List players = Lists.newArrayList();
   private final ServerChunkManager chunkManager;
   private final MinecraftServer server;
   private final ServerWorldProperties worldProperties;
   private int spawnChunkRadius;
   final EntityList entityList = new EntityList();
   private final ServerWaypointHandler waypointHandler;
   private final ServerEntityManager entityManager;
   private final GameEventDispatchManager gameEventDispatchManager;
   public boolean savingDisabled;
   private final SleepManager sleepManager;
   private int idleTimeout;
   private final PortalForcer portalForcer;
   private final WorldTickScheduler blockTickScheduler = new WorldTickScheduler(this::isTickingFutureReady);
   private final WorldTickScheduler fluidTickScheduler = new WorldTickScheduler(this::isTickingFutureReady);
   private final PathNodeTypeCache pathNodeTypeCache = new PathNodeTypeCache();
   final Set loadedMobs = new ObjectOpenHashSet();
   volatile boolean duringListenerUpdate;
   protected final RaidManager raidManager;
   private final ObjectLinkedOpenHashSet syncedBlockEventQueue = new ObjectLinkedOpenHashSet();
   private final List blockEventQueue = new ArrayList(64);
   private boolean inBlockTick;
   private final List spawners;
   @Nullable
   private EnderDragonFight enderDragonFight;
   final Int2ObjectMap enderDragonParts = new Int2ObjectOpenHashMap();
   private final StructureAccessor structureAccessor;
   private final StructureLocator structureLocator;
   private final boolean shouldTickTime;
   private final RandomSequencesState randomSequences;

   public ServerWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState) {
      super(properties, worldKey, server.getRegistryManager(), dimensionOptions.dimensionTypeEntry(), false, debugWorld, seed, server.getMaxChainedNeighborUpdates());
      this.shouldTickTime = shouldTickTime;
      this.server = server;
      this.spawners = spawners;
      this.worldProperties = properties;
      ChunkGenerator chunkGenerator = dimensionOptions.chunkGenerator();
      boolean bl = server.syncChunkWrites();
      DataFixer dataFixer = server.getDataFixer();
      ChunkDataAccess chunkDataAccess = new EntityChunkDataAccess(new ChunkPosKeyedStorage(new StorageKey(session.getDirectoryName(), worldKey, "entities"), session.getWorldDirectory(worldKey).resolve("entities"), dataFixer, bl, DataFixTypes.ENTITY_CHUNK), this, server);
      this.entityManager = new ServerEntityManager(Entity.class, new ServerEntityHandler(), chunkDataAccess);
      StructureTemplateManager var10006 = server.getStructureTemplateManager();
      int var10009 = server.getPlayerManager().getViewDistance();
      int var10010 = server.getPlayerManager().getSimulationDistance();
      ServerEntityManager var10013 = this.entityManager;
      Objects.requireNonNull(var10013);
      this.chunkManager = new ServerChunkManager(this, session, dataFixer, var10006, workerExecutor, chunkGenerator, var10009, var10010, bl, worldGenerationProgressListener, var10013::updateTrackingStatus, () -> {
         return server.getOverworld().getPersistentStateManager();
      });
      this.chunkManager.getStructurePlacementCalculator().tryCalculate();
      this.portalForcer = new PortalForcer(this);
      this.calculateAmbientDarkness();
      this.initWeatherGradients();
      this.getWorldBorder().setMaxRadius(server.getMaxWorldBorderRadius());
      this.raidManager = (RaidManager)this.getPersistentStateManager().getOrCreate(RaidManager.getPersistentStateType(this.getDimensionEntry()));
      if (!server.isSingleplayer()) {
         properties.setGameMode(server.getDefaultGameMode());
      }

      long l = server.getSaveProperties().getGeneratorOptions().getSeed();
      this.structureLocator = new StructureLocator(this.chunkManager.getChunkIoWorker(), this.getRegistryManager(), server.getStructureTemplateManager(), worldKey, chunkGenerator, this.chunkManager.getNoiseConfig(), this, chunkGenerator.getBiomeSource(), l, dataFixer);
      this.structureAccessor = new StructureAccessor(this, server.getSaveProperties().getGeneratorOptions(), this.structureLocator);
      if (this.getRegistryKey() == World.END && this.getDimensionEntry().matchesKey(DimensionTypes.THE_END)) {
         this.enderDragonFight = new EnderDragonFight(this, l, server.getSaveProperties().getDragonFight());
      } else {
         this.enderDragonFight = null;
      }

      this.sleepManager = new SleepManager();
      this.gameEventDispatchManager = new GameEventDispatchManager(this);
      this.randomSequences = (RandomSequencesState)Objects.requireNonNullElseGet(randomSequencesState, () -> {
         return (RandomSequencesState)this.getPersistentStateManager().getOrCreate(RandomSequencesState.STATE_TYPE);
      });
      this.waypointHandler = new ServerWaypointHandler();
   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void setEnderDragonFight(@Nullable EnderDragonFight enderDragonFight) {
      this.enderDragonFight = enderDragonFight;
   }

   public void setWeather(int clearDuration, int rainDuration, boolean raining, boolean thundering) {
      this.worldProperties.setClearWeatherTime(clearDuration);
      this.worldProperties.setRainTime(rainDuration);
      this.worldProperties.setThunderTime(rainDuration);
      this.worldProperties.setRaining(raining);
      this.worldProperties.setThundering(thundering);
   }

   public RegistryEntry getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
      return this.getChunkManager().getChunkGenerator().getBiomeSource().getBiome(biomeX, biomeY, biomeZ, this.getChunkManager().getNoiseConfig().getMultiNoiseSampler());
   }

   public StructureAccessor getStructureAccessor() {
      return this.structureAccessor;
   }

   public void tick(BooleanSupplier shouldKeepTicking) {
      Profiler profiler = Profilers.get();
      this.inBlockTick = true;
      TickManager tickManager = this.getTickManager();
      boolean bl = tickManager.shouldTick();
      if (bl) {
         profiler.push("world border");
         this.getWorldBorder().tick();
         profiler.swap("weather");
         this.tickWeather();
         profiler.pop();
      }

      int i = this.getGameRules().getInt(GameRules.PLAYERS_SLEEPING_PERCENTAGE);
      long l;
      if (this.sleepManager.canSkipNight(i) && this.sleepManager.canResetTime(i, this.players)) {
         if (this.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            l = this.properties.getTimeOfDay() + 24000L;
            this.setTimeOfDay(l - l % 24000L);
         }

         this.wakeSleepingPlayers();
         if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE) && this.isRaining()) {
            this.resetWeather();
         }
      }

      this.calculateAmbientDarkness();
      if (bl) {
         this.tickTime();
      }

      profiler.push("tickPending");
      if (!this.isDebugWorld() && bl) {
         l = this.getTime();
         profiler.push("blockTicks");
         this.blockTickScheduler.tick(l, 65536, this::tickBlock);
         profiler.swap("fluidTicks");
         this.fluidTickScheduler.tick(l, 65536, this::tickFluid);
         profiler.pop();
      }

      profiler.swap("raid");
      if (bl) {
         this.raidManager.tick(this);
      }

      profiler.swap("chunkSource");
      this.getChunkManager().tick(shouldKeepTicking, true);
      profiler.swap("blockEvents");
      if (bl) {
         this.processSyncedBlockEvents();
      }

      this.inBlockTick = false;
      profiler.pop();
      boolean bl2 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if (bl2) {
         this.resetIdleTimeout();
      }

      if (bl2 || this.idleTimeout++ < 300) {
         profiler.push("entities");
         if (this.enderDragonFight != null && bl) {
            profiler.push("dragonFight");
            this.enderDragonFight.tick();
            profiler.pop();
         }

         this.entityList.forEach((entity) -> {
            if (!entity.isRemoved()) {
               if (!tickManager.shouldSkipTick(entity)) {
                  profiler.push("checkDespawn");
                  entity.checkDespawn();
                  profiler.pop();
                  if (entity instanceof ServerPlayerEntity || this.chunkManager.chunkLoadingManager.getLevelManager().shouldTickEntities(entity.getChunkPos().toLong())) {
                     Entity entity2 = entity.getVehicle();
                     if (entity2 != null) {
                        if (!entity2.isRemoved() && entity2.hasPassenger(entity)) {
                           return;
                        }

                        entity.stopRiding();
                     }

                     profiler.push("tick");
                     this.tickEntity(this::tickEntity, entity);
                     profiler.pop();
                  }
               }
            }
         });
         profiler.pop();
         this.tickBlockEntities();
      }

      profiler.push("entityManagement");
      this.entityManager.tick();
      profiler.pop();
   }

   public boolean shouldTickBlocksInChunk(long chunkPos) {
      return this.chunkManager.chunkLoadingManager.getLevelManager().shouldTickBlocks(chunkPos);
   }

   protected void tickTime() {
      if (this.shouldTickTime) {
         long l = this.properties.getTime() + 1L;
         this.worldProperties.setTime(l);
         Profilers.get().push("scheduledFunctions");
         this.worldProperties.getScheduledEvents().processEvents(this.server, l);
         Profilers.get().pop();
         if (this.worldProperties.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            this.setTimeOfDay(this.properties.getTimeOfDay() + 1L);
         }

      }
   }

   public void setTimeOfDay(long timeOfDay) {
      this.worldProperties.setTimeOfDay(timeOfDay);
   }

   public void tickSpawners(boolean spawnMonsters, boolean spawnAnimals) {
      Iterator var3 = this.spawners.iterator();

      while(var3.hasNext()) {
         SpecialSpawner specialSpawner = (SpecialSpawner)var3.next();
         specialSpawner.spawn(this, spawnMonsters, spawnAnimals);
      }

   }

   private void wakeSleepingPlayers() {
      this.sleepManager.clearSleeping();
      ((List)this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList())).forEach((player) -> {
         player.wakeUp(false, false);
      });
   }

   public void tickChunk(WorldChunk chunk, int randomTickSpeed) {
      ChunkPos chunkPos = chunk.getPos();
      int i = chunkPos.getStartX();
      int j = chunkPos.getStartZ();
      Profiler profiler = Profilers.get();
      profiler.push("iceandsnow");

      for(int k = 0; k < randomTickSpeed; ++k) {
         if (this.random.nextInt(48) == 0) {
            this.tickIceAndSnow(this.getRandomPosInChunk(i, 0, j, 15));
         }
      }

      profiler.swap("tickBlocks");
      if (randomTickSpeed > 0) {
         ChunkSection[] chunkSections = chunk.getSectionArray();

         for(int l = 0; l < chunkSections.length; ++l) {
            ChunkSection chunkSection = chunkSections[l];
            if (chunkSection.hasRandomTicks()) {
               int m = chunk.sectionIndexToCoord(l);
               int n = ChunkSectionPos.getBlockCoord(m);

               for(int o = 0; o < randomTickSpeed; ++o) {
                  BlockPos blockPos = this.getRandomPosInChunk(i, n, j, 15);
                  profiler.push("randomTick");
                  BlockState blockState = chunkSection.getBlockState(blockPos.getX() - i, blockPos.getY() - n, blockPos.getZ() - j);
                  if (blockState.hasRandomTicks()) {
                     blockState.randomTick(this, blockPos, this.random);
                  }

                  FluidState fluidState = blockState.getFluidState();
                  if (fluidState.hasRandomTicks()) {
                     fluidState.onRandomTick(this, blockPos, this.random);
                  }

                  profiler.pop();
               }
            }
         }
      }

      profiler.pop();
   }

   public void tickThunder(WorldChunk chunk) {
      ChunkPos chunkPos = chunk.getPos();
      boolean bl = this.isRaining();
      int i = chunkPos.getStartX();
      int j = chunkPos.getStartZ();
      Profiler profiler = Profilers.get();
      profiler.push("thunder");
      if (bl && this.isThundering() && this.random.nextInt(100000) == 0) {
         BlockPos blockPos = this.getLightningPos(this.getRandomPosInChunk(i, 0, j, 15));
         if (this.hasRain(blockPos)) {
            LocalDifficulty localDifficulty = this.getLocalDifficulty(blockPos);
            boolean bl2 = this.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.01 && !this.getBlockState(blockPos.down()).isOf(Blocks.LIGHTNING_ROD);
            if (bl2) {
               SkeletonHorseEntity skeletonHorseEntity = (SkeletonHorseEntity)EntityType.SKELETON_HORSE.create(this, SpawnReason.EVENT);
               if (skeletonHorseEntity != null) {
                  skeletonHorseEntity.setTrapped(true);
                  skeletonHorseEntity.setBreedingAge(0);
                  skeletonHorseEntity.setPosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
                  this.spawnEntity(skeletonHorseEntity);
               }
            }

            LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.create(this, SpawnReason.EVENT);
            if (lightningEntity != null) {
               lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
               lightningEntity.setCosmetic(bl2);
               this.spawnEntity(lightningEntity);
            }
         }
      }

      profiler.pop();
   }

   @VisibleForTesting
   public void tickIceAndSnow(BlockPos pos) {
      BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
      BlockPos blockPos2 = blockPos.down();
      Biome biome = (Biome)this.getBiome(blockPos).value();
      if (biome.canSetIce(this, blockPos2)) {
         this.setBlockState(blockPos2, Blocks.ICE.getDefaultState());
      }

      if (this.isRaining()) {
         int i = this.getGameRules().getInt(GameRules.SNOW_ACCUMULATION_HEIGHT);
         if (i > 0 && biome.canSetSnow(this, blockPos)) {
            BlockState blockState = this.getBlockState(blockPos);
            if (blockState.isOf(Blocks.SNOW)) {
               int j = (Integer)blockState.get(SnowBlock.LAYERS);
               if (j < Math.min(i, 8)) {
                  BlockState blockState2 = (BlockState)blockState.with(SnowBlock.LAYERS, j + 1);
                  Block.pushEntitiesUpBeforeBlockChange(blockState, blockState2, this, blockPos);
                  this.setBlockState(blockPos, blockState2);
               }
            } else {
               this.setBlockState(blockPos, Blocks.SNOW.getDefaultState());
            }
         }

         Biome.Precipitation precipitation = biome.getPrecipitation(blockPos2, this.getSeaLevel());
         if (precipitation != Biome.Precipitation.NONE) {
            BlockState blockState3 = this.getBlockState(blockPos2);
            blockState3.getBlock().precipitationTick(blockState3, this, blockPos2, precipitation);
         }
      }

   }

   private Optional getLightningRodPos(BlockPos pos) {
      Optional optional = this.getPointOfInterestStorage().getNearestPosition((poiType) -> {
         return poiType.matchesKey(PointOfInterestTypes.LIGHTNING_ROD);
      }, (innerPos) -> {
         return innerPos.getY() == this.getTopY(Heightmap.Type.WORLD_SURFACE, innerPos.getX(), innerPos.getZ()) - 1;
      }, pos, 128, PointOfInterestStorage.OccupationStatus.ANY);
      return optional.map((innerPos) -> {
         return innerPos.up(1);
      });
   }

   protected BlockPos getLightningPos(BlockPos pos) {
      BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
      Optional optional = this.getLightningRodPos(blockPos);
      if (optional.isPresent()) {
         return (BlockPos)optional.get();
      } else {
         Box box = Box.enclosing(blockPos, blockPos.withY(this.getTopYInclusive() + 1)).expand(3.0);
         List list = this.getEntitiesByClass(LivingEntity.class, box, (entity) -> {
            return entity != null && entity.isAlive() && this.isSkyVisible(entity.getBlockPos());
         });
         if (!list.isEmpty()) {
            return ((LivingEntity)list.get(this.random.nextInt(list.size()))).getBlockPos();
         } else {
            if (blockPos.getY() == this.getBottomY() - 1) {
               blockPos = blockPos.up(2);
            }

            return blockPos;
         }
      }
   }

   public boolean isInBlockTick() {
      return this.inBlockTick;
   }

   public boolean isSleepingEnabled() {
      return this.getGameRules().getInt(GameRules.PLAYERS_SLEEPING_PERCENTAGE) <= 100;
   }

   private void sendSleepingStatus() {
      if (this.isSleepingEnabled()) {
         if (!this.getServer().isSingleplayer() || this.getServer().isRemote()) {
            int i = this.getGameRules().getInt(GameRules.PLAYERS_SLEEPING_PERCENTAGE);
            MutableText text;
            if (this.sleepManager.canSkipNight(i)) {
               text = Text.translatable("sleep.skipping_night");
            } else {
               text = Text.translatable("sleep.players_sleeping", this.sleepManager.getSleeping(), this.sleepManager.getNightSkippingRequirement(i));
            }

            Iterator var3 = this.players.iterator();

            while(var3.hasNext()) {
               ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
               serverPlayerEntity.sendMessage(text, true);
            }

         }
      }
   }

   public void updateSleepingPlayers() {
      if (!this.players.isEmpty() && this.sleepManager.update(this.players)) {
         this.sendSleepingStatus();
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   public ServerWaypointHandler getWaypointHandler() {
      return this.waypointHandler;
   }

   private void tickWeather() {
      boolean bl = this.isRaining();
      if (this.getDimension().hasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
            int i = this.worldProperties.getClearWeatherTime();
            int j = this.worldProperties.getThunderTime();
            int k = this.worldProperties.getRainTime();
            boolean bl2 = this.properties.isThundering();
            boolean bl3 = this.properties.isRaining();
            if (i > 0) {
               --i;
               j = bl2 ? 0 : 1;
               k = bl3 ? 0 : 1;
               bl2 = false;
               bl3 = false;
            } else {
               if (j > 0) {
                  --j;
                  if (j == 0) {
                     bl2 = !bl2;
                  }
               } else if (bl2) {
                  j = THUNDER_WEATHER_DURATION_PROVIDER.get(this.random);
               } else {
                  j = CLEAR_THUNDER_WEATHER_DURATION_PROVIDER.get(this.random);
               }

               if (k > 0) {
                  --k;
                  if (k == 0) {
                     bl3 = !bl3;
                  }
               } else if (bl3) {
                  k = RAIN_WEATHER_DURATION_PROVIDER.get(this.random);
               } else {
                  k = CLEAR_WEATHER_DURATION_PROVIDER.get(this.random);
               }
            }

            this.worldProperties.setThunderTime(j);
            this.worldProperties.setRainTime(k);
            this.worldProperties.setClearWeatherTime(i);
            this.worldProperties.setThundering(bl2);
            this.worldProperties.setRaining(bl3);
         }

         this.lastThunderGradient = this.thunderGradient;
         if (this.properties.isThundering()) {
            this.thunderGradient += 0.01F;
         } else {
            this.thunderGradient -= 0.01F;
         }

         this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
         this.lastRainGradient = this.rainGradient;
         if (this.properties.isRaining()) {
            this.rainGradient += 0.01F;
         } else {
            this.rainGradient -= 0.01F;
         }

         this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0F, 1.0F);
      }

      if (this.lastRainGradient != this.rainGradient) {
         this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, this.rainGradient), this.getRegistryKey());
      }

      if (this.lastThunderGradient != this.thunderGradient) {
         this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, this.thunderGradient), this.getRegistryKey());
      }

      if (bl != this.isRaining()) {
         if (bl) {
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STOPPED, 0.0F));
         } else {
            this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0F));
         }

         this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, this.rainGradient));
         this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, this.thunderGradient));
      }

   }

   @VisibleForTesting
   public void resetWeather() {
      this.worldProperties.setRainTime(0);
      this.worldProperties.setRaining(false);
      this.worldProperties.setThunderTime(0);
      this.worldProperties.setThundering(false);
   }

   public void resetIdleTimeout() {
      this.idleTimeout = 0;
   }

   private void tickFluid(BlockPos pos, Fluid fluid) {
      BlockState blockState = this.getBlockState(pos);
      FluidState fluidState = blockState.getFluidState();
      if (fluidState.isOf(fluid)) {
         fluidState.onScheduledTick(this, pos, blockState);
      }

   }

   private void tickBlock(BlockPos pos, Block block) {
      BlockState blockState = this.getBlockState(pos);
      if (blockState.isOf(block)) {
         blockState.scheduledTick(this, pos, this.random);
      }

   }

   public void tickEntity(Entity entity) {
      entity.resetPosition();
      Profiler profiler = Profilers.get();
      ++entity.age;
      profiler.push(() -> {
         return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
      });
      profiler.visit("tickNonPassenger");
      entity.tick();
      profiler.pop();
      Iterator var3 = entity.getPassengerList().iterator();

      while(var3.hasNext()) {
         Entity entity2 = (Entity)var3.next();
         this.tickPassenger(entity, entity2);
      }

   }

   private void tickPassenger(Entity vehicle, Entity passenger) {
      if (!passenger.isRemoved() && passenger.getVehicle() == vehicle) {
         if (passenger instanceof PlayerEntity || this.entityList.has(passenger)) {
            passenger.resetPosition();
            ++passenger.age;
            Profiler profiler = Profilers.get();
            profiler.push(() -> {
               return Registries.ENTITY_TYPE.getId(passenger.getType()).toString();
            });
            profiler.visit("tickPassenger");
            passenger.tickRiding();
            profiler.pop();
            Iterator var4 = passenger.getPassengerList().iterator();

            while(var4.hasNext()) {
               Entity entity = (Entity)var4.next();
               this.tickPassenger(passenger, entity);
            }

         }
      } else {
         passenger.stopRiding();
      }
   }

   public void onStateReplacedWithCommands(BlockPos pos, BlockState oldState) {
      BlockState blockState = this.getBlockState(pos);
      Block block = blockState.getBlock();
      boolean bl = !oldState.isOf(block);
      if (bl) {
         oldState.onStateReplaced(this, pos, false);
      }

      this.updateNeighbors(pos, blockState.getBlock());
      if (blockState.hasComparatorOutput()) {
         this.updateComparators(pos, block);
      }

   }

   public boolean canEntityModifyAt(Entity entity, BlockPos pos) {
      boolean var10000;
      if (entity instanceof PlayerEntity playerEntity) {
         if (this.server.isSpawnProtected(this, pos, playerEntity) || !this.getWorldBorder().contains(pos)) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public void save(@Nullable ProgressListener progressListener, boolean flush, boolean savingDisabled) {
      ServerChunkManager serverChunkManager = this.getChunkManager();
      if (!savingDisabled) {
         if (progressListener != null) {
            progressListener.setTitle(Text.translatable("menu.savingLevel"));
         }

         this.savePersistentState(flush);
         if (progressListener != null) {
            progressListener.setTask(Text.translatable("menu.savingChunks"));
         }

         serverChunkManager.save(flush);
         if (flush) {
            this.entityManager.flush();
         } else {
            this.entityManager.save();
         }

      }
   }

   private void savePersistentState(boolean flush) {
      if (this.enderDragonFight != null) {
         this.server.getSaveProperties().setDragonFight(this.enderDragonFight.toData());
      }

      PersistentStateManager persistentStateManager = this.getChunkManager().getPersistentStateManager();
      if (flush) {
         persistentStateManager.save();
      } else {
         persistentStateManager.startSaving();
      }

   }

   public List getEntitiesByType(TypeFilter filter, Predicate predicate) {
      List list = Lists.newArrayList();
      this.collectEntitiesByType(filter, predicate, list);
      return list;
   }

   public void collectEntitiesByType(TypeFilter filter, Predicate predicate, List result) {
      this.collectEntitiesByType(filter, predicate, result, Integer.MAX_VALUE);
   }

   public void collectEntitiesByType(TypeFilter filter, Predicate predicate, List result, int limit) {
      this.getEntityLookup().forEach(filter, (entity) -> {
         if (predicate.test(entity)) {
            result.add(entity);
            if (result.size() >= limit) {
               return LazyIterationConsumer.NextIteration.ABORT;
            }
         }

         return LazyIterationConsumer.NextIteration.CONTINUE;
      });
   }

   public List getAliveEnderDragons() {
      return this.getEntitiesByType(EntityType.ENDER_DRAGON, LivingEntity::isAlive);
   }

   public List getPlayers(Predicate predicate) {
      return this.getPlayers(predicate, Integer.MAX_VALUE);
   }

   public List getPlayers(Predicate predicate, int limit) {
      List list = Lists.newArrayList();
      Iterator var4 = this.players.iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
         if (predicate.test(serverPlayerEntity)) {
            list.add(serverPlayerEntity);
            if (list.size() >= limit) {
               return list;
            }
         }
      }

      return list;
   }

   @Nullable
   public ServerPlayerEntity getRandomAlivePlayer() {
      List list = this.getPlayers(LivingEntity::isAlive);
      return list.isEmpty() ? null : (ServerPlayerEntity)list.get(this.random.nextInt(list.size()));
   }

   public boolean spawnEntity(Entity entity) {
      return this.addEntity(entity);
   }

   public boolean tryLoadEntity(Entity entity) {
      return this.addEntity(entity);
   }

   public void onDimensionChanged(Entity entity) {
      if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
         this.addPlayer(serverPlayerEntity);
      } else {
         this.addEntity(entity);
      }

   }

   public void onPlayerConnected(ServerPlayerEntity player) {
      this.addPlayer(player);
   }

   public void onPlayerRespawned(ServerPlayerEntity player) {
      this.addPlayer(player);
   }

   private void addPlayer(ServerPlayerEntity player) {
      Entity entity = this.getEntity(player.getUuid());
      if (entity != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", player.getUuid());
         entity.detach();
         this.removePlayer((ServerPlayerEntity)entity, Entity.RemovalReason.DISCARDED);
      }

      this.entityManager.addEntity(player);
   }

   private boolean addEntity(Entity entity) {
      if (entity.isRemoved()) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
         return false;
      } else {
         return this.entityManager.addEntity(entity);
      }
   }

   public boolean spawnNewEntityAndPassengers(Entity entity) {
      Stream var10000 = entity.streamSelfAndPassengers().map(Entity::getUuid);
      ServerEntityManager var10001 = this.entityManager;
      Objects.requireNonNull(var10001);
      if (var10000.anyMatch(var10001::has)) {
         return false;
      } else {
         this.spawnEntityAndPassengers(entity);
         return true;
      }
   }

   public void unloadEntities(WorldChunk chunk) {
      chunk.clear();
      chunk.removeChunkTickSchedulers(this);
   }

   public void removePlayer(ServerPlayerEntity player, Entity.RemovalReason reason) {
      player.remove(reason);
   }

   public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
      Iterator var4 = this.server.getPlayerManager().getPlayerList().iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
         if (serverPlayerEntity != null && serverPlayerEntity.getWorld() == this && serverPlayerEntity.getId() != entityId) {
            double d = (double)pos.getX() - serverPlayerEntity.getX();
            double e = (double)pos.getY() - serverPlayerEntity.getY();
            double f = (double)pos.getZ() - serverPlayerEntity.getZ();
            if (d * d + e * e + f * f < 1024.0) {
               serverPlayerEntity.networkHandler.sendPacket(new BlockBreakingProgressS2CPacket(entityId, pos, progress));
            }
         }
      }

   }

   public void playSound(@Nullable Entity source, double x, double y, double z, RegistryEntry sound, SoundCategory category, float volume, float pitch, long seed) {
      PlayerManager var10000 = this.server.getPlayerManager();
      PlayerEntity var10001;
      if (source instanceof PlayerEntity playerEntity) {
         var10001 = playerEntity;
      } else {
         var10001 = null;
      }

      var10000.sendToAround(var10001, x, y, z, (double)((SoundEvent)sound.value()).getDistanceToTravel(volume), this.getRegistryKey(), new PlaySoundS2CPacket(sound, category, x, y, z, volume, pitch, seed));
   }

   public void playSoundFromEntity(@Nullable Entity source, Entity entity, RegistryEntry sound, SoundCategory category, float volume, float pitch, long seed) {
      PlayerManager var10000 = this.server.getPlayerManager();
      PlayerEntity var10001;
      if (source instanceof PlayerEntity playerEntity) {
         var10001 = playerEntity;
      } else {
         var10001 = null;
      }

      var10000.sendToAround(var10001, entity.getX(), entity.getY(), entity.getZ(), (double)((SoundEvent)sound.value()).getDistanceToTravel(volume), this.getRegistryKey(), new PlaySoundFromEntityS2CPacket(sound, category, entity, volume, pitch, seed));
   }

   public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
      if (this.getGameRules().getBoolean(GameRules.GLOBAL_SOUND_EVENTS)) {
         this.server.getPlayerManager().getPlayerList().forEach((player) -> {
            Vec3d vec3d2;
            if (player.getWorld() == this) {
               Vec3d vec3d = Vec3d.ofCenter(pos);
               if (player.squaredDistanceTo(vec3d) < (double)MathHelper.square(32)) {
                  vec3d2 = vec3d;
               } else {
                  Vec3d vec3d3 = vec3d.subtract(player.getPos()).normalize();
                  vec3d2 = player.getPos().add(vec3d3.multiply(32.0));
               }
            } else {
               vec3d2 = player.getPos();
            }

            player.networkHandler.sendPacket(new WorldEventS2CPacket(eventId, BlockPos.ofFloored(vec3d2), data, true));
         });
      } else {
         this.syncWorldEvent((Entity)null, eventId, pos, data);
      }

   }

   public void syncWorldEvent(@Nullable Entity source, int eventId, BlockPos pos, int data) {
      PlayerManager var10000 = this.server.getPlayerManager();
      PlayerEntity var10001;
      if (source instanceof PlayerEntity playerEntity) {
         var10001 = playerEntity;
      } else {
         var10001 = null;
      }

      var10000.sendToAround(var10001, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 64.0, this.getRegistryKey(), new WorldEventS2CPacket(eventId, pos, data, false));
   }

   public int getLogicalHeight() {
      return this.getDimension().logicalHeight();
   }

   public void emitGameEvent(RegistryEntry event, Vec3d emitterPos, GameEvent.Emitter emitter) {
      this.gameEventDispatchManager.dispatch(event, emitterPos, emitter);
   }

   public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      if (this.duringListenerUpdate) {
         String string = "recursive call to sendBlockUpdated";
         Util.logErrorOrPause("recursive call to sendBlockUpdated", new IllegalStateException("recursive call to sendBlockUpdated"));
      }

      this.getChunkManager().markForUpdate(pos);
      this.pathNodeTypeCache.invalidate(pos);
      VoxelShape voxelShape = oldState.getCollisionShape(this, pos);
      VoxelShape voxelShape2 = newState.getCollisionShape(this, pos);
      if (VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME)) {
         List list = new ObjectArrayList();
         Iterator var8 = this.loadedMobs.iterator();

         while(var8.hasNext()) {
            MobEntity mobEntity = (MobEntity)var8.next();
            EntityNavigation entityNavigation = mobEntity.getNavigation();
            if (entityNavigation.shouldRecalculatePath(pos)) {
               list.add(entityNavigation);
            }
         }

         try {
            this.duringListenerUpdate = true;
            var8 = list.iterator();

            while(var8.hasNext()) {
               EntityNavigation entityNavigation2 = (EntityNavigation)var8.next();
               entityNavigation2.recalculatePath();
            }
         } finally {
            this.duringListenerUpdate = false;
         }

      }
   }

   public void updateNeighbors(BlockPos pos, Block block) {
      this.updateNeighborsAlways(pos, block, OrientationHelper.getEmissionOrientation(this, (Direction)null, (Direction)null));
   }

   public void updateNeighborsAlways(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation) {
      this.neighborUpdater.updateNeighbors(pos, sourceBlock, (Direction)null, orientation);
   }

   public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction, @Nullable WireOrientation orientation) {
      this.neighborUpdater.updateNeighbors(pos, sourceBlock, direction, orientation);
   }

   public void updateNeighbor(BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation) {
      this.neighborUpdater.updateNeighbor(pos, sourceBlock, orientation);
   }

   public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, @Nullable WireOrientation orientation, boolean notify) {
      this.neighborUpdater.updateNeighbor(state, pos, sourceBlock, orientation, notify);
   }

   public void sendEntityStatus(Entity entity, byte status) {
      this.getChunkManager().sendToNearbyPlayers(entity, new EntityStatusS2CPacket(entity, status));
   }

   public void sendEntityDamage(Entity entity, DamageSource damageSource) {
      this.getChunkManager().sendToNearbyPlayers(entity, new EntityDamageS2CPacket(entity, damageSource));
   }

   public ServerChunkManager getChunkManager() {
      return this.chunkManager;
   }

   public void createExplosion(@Nullable Entity entity, @Nullable DamageSource damageSource, @Nullable ExplosionBehavior behavior, double x, double y, double z, float power, boolean createFire, World.ExplosionSourceType explosionSourceType, ParticleEffect smallParticle, ParticleEffect largeParticle, RegistryEntry soundEvent) {
      Explosion.DestructionType var10000;
      switch (explosionSourceType) {
         case NONE:
            var10000 = Explosion.DestructionType.KEEP;
            break;
         case BLOCK:
            var10000 = this.getDestructionType(GameRules.BLOCK_EXPLOSION_DROP_DECAY);
            break;
         case MOB:
            var10000 = this.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) ? this.getDestructionType(GameRules.MOB_EXPLOSION_DROP_DECAY) : Explosion.DestructionType.KEEP;
            break;
         case TNT:
            var10000 = this.getDestructionType(GameRules.TNT_EXPLOSION_DROP_DECAY);
            break;
         case TRIGGER:
            var10000 = Explosion.DestructionType.TRIGGER_BLOCK;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Explosion.DestructionType destructionType = var10000;
      Vec3d vec3d = new Vec3d(x, y, z);
      ExplosionImpl explosionImpl = new ExplosionImpl(this, entity, damageSource, behavior, vec3d, power, createFire, destructionType);
      explosionImpl.explode();
      ParticleEffect particleEffect = explosionImpl.isSmall() ? smallParticle : largeParticle;
      Iterator var20 = this.players.iterator();

      while(var20.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var20.next();
         if (serverPlayerEntity.squaredDistanceTo(vec3d) < 4096.0) {
            Optional optional = Optional.ofNullable((Vec3d)explosionImpl.getKnockbackByPlayer().get(serverPlayerEntity));
            serverPlayerEntity.networkHandler.sendPacket(new ExplosionS2CPacket(vec3d, optional, particleEffect, soundEvent));
         }
      }

   }

   private Explosion.DestructionType getDestructionType(GameRules.Key decayRule) {
      return this.getGameRules().getBoolean(decayRule) ? Explosion.DestructionType.DESTROY_WITH_DECAY : Explosion.DestructionType.DESTROY;
   }

   public void addSyncedBlockEvent(BlockPos pos, Block block, int type, int data) {
      this.syncedBlockEventQueue.add(new BlockEvent(pos, block, type, data));
   }

   private void processSyncedBlockEvents() {
      this.blockEventQueue.clear();

      while(!this.syncedBlockEventQueue.isEmpty()) {
         BlockEvent blockEvent = (BlockEvent)this.syncedBlockEventQueue.removeFirst();
         if (this.shouldTickBlockPos(blockEvent.pos())) {
            if (this.processBlockEvent(blockEvent)) {
               this.server.getPlayerManager().sendToAround((PlayerEntity)null, (double)blockEvent.pos().getX(), (double)blockEvent.pos().getY(), (double)blockEvent.pos().getZ(), 64.0, this.getRegistryKey(), new BlockEventS2CPacket(blockEvent.pos(), blockEvent.block(), blockEvent.type(), blockEvent.data()));
            }
         } else {
            this.blockEventQueue.add(blockEvent);
         }
      }

      this.syncedBlockEventQueue.addAll(this.blockEventQueue);
   }

   private boolean processBlockEvent(BlockEvent event) {
      BlockState blockState = this.getBlockState(event.pos());
      return blockState.isOf(event.block()) ? blockState.onSyncedBlockEvent(this, event.pos(), event.type(), event.data()) : false;
   }

   public WorldTickScheduler getBlockTickScheduler() {
      return this.blockTickScheduler;
   }

   public WorldTickScheduler getFluidTickScheduler() {
      return this.fluidTickScheduler;
   }

   @NotNull
   public MinecraftServer getServer() {
      return this.server;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureTemplateManager getStructureTemplateManager() {
      return this.server.getStructureTemplateManager();
   }

   public int spawnParticles(ParticleEffect parameters, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed) {
      return this.spawnParticles(parameters, false, false, x, y, z, count, offsetX, offsetY, offsetZ, speed);
   }

   public int spawnParticles(ParticleEffect parameters, boolean force, boolean important, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed) {
      ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(parameters, force, important, x, y, z, (float)offsetX, (float)offsetY, (float)offsetZ, (float)speed, count);
      int i = 0;

      for(int j = 0; j < this.players.size(); ++j) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(j);
         if (this.sendToPlayerIfNearby(serverPlayerEntity, force, x, y, z, particleS2CPacket)) {
            ++i;
         }
      }

      return i;
   }

   public boolean spawnParticles(ServerPlayerEntity viewer, ParticleEffect parameters, boolean force, boolean important, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed) {
      Packet packet = new ParticleS2CPacket(parameters, force, important, x, y, z, (float)offsetX, (float)offsetY, (float)offsetZ, (float)speed, count);
      return this.sendToPlayerIfNearby(viewer, force, x, y, z, packet);
   }

   public final boolean sendToPlayerIfNearby(ServerPlayerEntity player, boolean force, double x, double y, double z, Packet packet) {
      if (player.getWorld() != this) {
         return false;
      } else {
         BlockPos blockPos = player.getBlockPos();
         if (blockPos.isWithinDistance(new Vec3d(x, y, z), force ? 512.0 : 32.0)) {
            player.networkHandler.sendPacket(packet);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getEntityById(int id) {
      return (Entity)this.getEntityLookup().get(id);
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public Entity getEntityOrDragonPart(int id) {
      Entity entity = (Entity)this.getEntityLookup().get(id);
      return entity != null ? entity : (Entity)this.enderDragonParts.get(id);
   }

   public Collection getEnderDragonParts() {
      return this.enderDragonParts.values();
   }

   @Nullable
   public BlockPos locateStructure(TagKey structureTag, BlockPos pos, int radius, boolean skipReferencedStructures) {
      if (!this.server.getSaveProperties().getGeneratorOptions().shouldGenerateStructures()) {
         return null;
      } else {
         Optional optional = this.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE).getOptional(structureTag);
         if (optional.isEmpty()) {
            return null;
         } else {
            Pair pair = this.getChunkManager().getChunkGenerator().locateStructure(this, (RegistryEntryList)optional.get(), pos, radius, skipReferencedStructures);
            return pair != null ? (BlockPos)pair.getFirst() : null;
         }
      }
   }

   @Nullable
   public Pair locateBiome(Predicate predicate, BlockPos pos, int radius, int horizontalBlockCheckInterval, int verticalBlockCheckInterval) {
      return this.getChunkManager().getChunkGenerator().getBiomeSource().locateBiome(pos, radius, horizontalBlockCheckInterval, verticalBlockCheckInterval, predicate, this.getChunkManager().getNoiseConfig().getMultiNoiseSampler(), this);
   }

   public ServerRecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public TickManager getTickManager() {
      return this.server.getTickManager();
   }

   public boolean isSavingDisabled() {
      return this.savingDisabled;
   }

   public PersistentStateManager getPersistentStateManager() {
      return this.getChunkManager().getPersistentStateManager();
   }

   @Nullable
   public MapState getMapState(MapIdComponent id) {
      return (MapState)this.getServer().getOverworld().getPersistentStateManager().get(MapState.createStateType(id));
   }

   public void putMapState(MapIdComponent id, MapState state) {
      this.getServer().getOverworld().getPersistentStateManager().set(MapState.createStateType(id), state);
   }

   public MapIdComponent increaseAndGetMapId() {
      return ((IdCountsState)this.getServer().getOverworld().getPersistentStateManager().getOrCreate(IdCountsState.STATE_TYPE)).createNextMapId();
   }

   public void setSpawnPos(BlockPos pos, float angle) {
      BlockPos blockPos = this.properties.getSpawnPos();
      float f = this.properties.getSpawnAngle();
      if (!blockPos.equals(pos) || f != angle) {
         this.properties.setSpawnPos(pos, angle);
         this.getServer().getPlayerManager().sendToAll(new PlayerSpawnPositionS2CPacket(pos, angle));
      }

      if (this.spawnChunkRadius > 1) {
         this.getChunkManager().removeTicket(ChunkTicketType.START, new ChunkPos(blockPos), this.spawnChunkRadius);
      }

      int i = this.getGameRules().getInt(GameRules.SPAWN_CHUNK_RADIUS) + 1;
      if (i > 1) {
         this.getChunkManager().addTicket(ChunkTicketType.START, new ChunkPos(pos), i);
      }

      this.spawnChunkRadius = i;
   }

   public LongSet getForcedChunks() {
      return this.chunkManager.getForcedChunks();
   }

   public boolean setChunkForced(int x, int z, boolean forced) {
      boolean bl = this.chunkManager.setChunkForced(new ChunkPos(x, z), forced);
      if (forced && bl) {
         this.getChunk(x, z);
      }

      return bl;
   }

   public List getPlayers() {
      return this.players;
   }

   public void onBlockStateChanged(BlockPos pos, BlockState oldState, BlockState newState) {
      Optional optional = PointOfInterestTypes.getTypeForState(oldState);
      Optional optional2 = PointOfInterestTypes.getTypeForState(newState);
      if (!Objects.equals(optional, optional2)) {
         BlockPos blockPos = pos.toImmutable();
         optional.ifPresent((oldPoiType) -> {
            this.getServer().execute(() -> {
               this.getPointOfInterestStorage().remove(blockPos);
               DebugInfoSender.sendPoiRemoval(this, blockPos);
            });
         });
         optional2.ifPresent((newPoiType) -> {
            this.getServer().execute(() -> {
               this.getPointOfInterestStorage().add(blockPos, newPoiType);
               DebugInfoSender.sendPoiAddition(this, blockPos);
            });
         });
      }
   }

   public PointOfInterestStorage getPointOfInterestStorage() {
      return this.getChunkManager().getPointOfInterestStorage();
   }

   public boolean isNearOccupiedPointOfInterest(BlockPos pos) {
      return this.isNearOccupiedPointOfInterest(pos, 1);
   }

   public boolean isNearOccupiedPointOfInterest(ChunkSectionPos sectionPos) {
      return this.isNearOccupiedPointOfInterest(sectionPos.getCenterPos());
   }

   public boolean isNearOccupiedPointOfInterest(BlockPos pos, int maxDistance) {
      if (maxDistance > 6) {
         return false;
      } else {
         return this.getOccupiedPointOfInterestDistance(ChunkSectionPos.from(pos)) <= maxDistance;
      }
   }

   public int getOccupiedPointOfInterestDistance(ChunkSectionPos pos) {
      return this.getPointOfInterestStorage().getDistanceFromNearestOccupied(pos);
   }

   public RaidManager getRaidManager() {
      return this.raidManager;
   }

   @Nullable
   public Raid getRaidAt(BlockPos pos) {
      return this.raidManager.getRaidAt(pos, 9216);
   }

   public boolean hasRaidAt(BlockPos pos) {
      return this.getRaidAt(pos) != null;
   }

   public void handleInteraction(EntityInteraction interaction, Entity entity, InteractionObserver observer) {
      observer.onInteractionWith(interaction, entity);
   }

   public void dump(Path path) throws IOException {
      ServerChunkLoadingManager serverChunkLoadingManager = this.getChunkManager().chunkLoadingManager;
      Writer writer = Files.newBufferedWriter(path.resolve("stats.txt"));

      try {
         writer.write(String.format(Locale.ROOT, "spawning_chunks: %d\n", serverChunkLoadingManager.getLevelManager().getTickedChunkCount()));
         SpawnHelper.Info info = this.getChunkManager().getSpawnInfo();
         if (info != null) {
            ObjectIterator var5 = info.getGroupToCount().object2IntEntrySet().iterator();

            while(var5.hasNext()) {
               Object2IntMap.Entry entry = (Object2IntMap.Entry)var5.next();
               writer.write(String.format(Locale.ROOT, "spawn_count.%s: %d\n", ((SpawnGroup)entry.getKey()).getName(), entry.getIntValue()));
            }
         }

         writer.write(String.format(Locale.ROOT, "entities: %s\n", this.entityManager.getDebugString()));
         writer.write(String.format(Locale.ROOT, "block_entity_tickers: %d\n", this.blockEntityTickers.size()));
         writer.write(String.format(Locale.ROOT, "block_ticks: %d\n", this.getBlockTickScheduler().getTickCount()));
         writer.write(String.format(Locale.ROOT, "fluid_ticks: %d\n", this.getFluidTickScheduler().getTickCount()));
         writer.write("distance_manager: " + serverChunkLoadingManager.getLevelManager().toDumpString() + "\n");
         writer.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getChunkManager().getPendingTasks()));
      } catch (Throwable var22) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var12) {
               var22.addSuppressed(var12);
            }
         }

         throw var22;
      }

      if (writer != null) {
         writer.close();
      }

      CrashReport crashReport = new CrashReport("Level dump", new Exception("dummy"));
      this.addDetailsToCrashReport(crashReport);
      Writer writer2 = Files.newBufferedWriter(path.resolve("example_crash.txt"));

      try {
         writer2.write(crashReport.asString(ReportType.MINECRAFT_TEST_REPORT));
      } catch (Throwable var17) {
         if (writer2 != null) {
            try {
               writer2.close();
            } catch (Throwable var11) {
               var17.addSuppressed(var11);
            }
         }

         throw var17;
      }

      if (writer2 != null) {
         writer2.close();
      }

      Path path2 = path.resolve("chunks.csv");
      Writer writer3 = Files.newBufferedWriter(path2);

      try {
         serverChunkLoadingManager.dump(writer3);
      } catch (Throwable var20) {
         if (writer3 != null) {
            try {
               writer3.close();
            } catch (Throwable var13) {
               var20.addSuppressed(var13);
            }
         }

         throw var20;
      }

      if (writer3 != null) {
         writer3.close();
      }

      Path path3 = path.resolve("entity_chunks.csv");
      Writer writer4 = Files.newBufferedWriter(path3);

      try {
         this.entityManager.dump(writer4);
      } catch (Throwable var18) {
         if (writer4 != null) {
            try {
               writer4.close();
            } catch (Throwable var15) {
               var18.addSuppressed(var15);
            }
         }

         throw var18;
      }

      if (writer4 != null) {
         writer4.close();
      }

      Path path4 = path.resolve("entities.csv");
      Writer writer5 = Files.newBufferedWriter(path4);

      try {
         dumpEntities(writer5, this.getEntityLookup().iterate());
      } catch (Throwable var21) {
         if (writer5 != null) {
            try {
               writer5.close();
            } catch (Throwable var16) {
               var21.addSuppressed(var16);
            }
         }

         throw var21;
      }

      if (writer5 != null) {
         writer5.close();
      }

      Path path5 = path.resolve("block_entities.csv");
      Writer writer6 = Files.newBufferedWriter(path5);

      try {
         this.dumpBlockEntities(writer6);
      } catch (Throwable var19) {
         if (writer6 != null) {
            try {
               writer6.close();
            } catch (Throwable var14) {
               var19.addSuppressed(var14);
            }
         }

         throw var19;
      }

      if (writer6 != null) {
         writer6.close();
      }

   }

   private static void dumpEntities(Writer writer, Iterable entities) throws IOException {
      CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").startBody(writer);
      Iterator var3 = entities.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         Text text = entity.getCustomName();
         Text text2 = entity.getDisplayName();
         csvWriter.printRow(entity.getX(), entity.getY(), entity.getZ(), entity.getUuid(), Registries.ENTITY_TYPE.getId(entity.getType()), entity.isAlive(), text2.getString(), text != null ? text.getString() : null);
      }

   }

   private void dumpBlockEntities(Writer writer) throws IOException {
      CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("type").startBody(writer);
      Iterator var3 = this.blockEntityTickers.iterator();

      while(var3.hasNext()) {
         BlockEntityTickInvoker blockEntityTickInvoker = (BlockEntityTickInvoker)var3.next();
         BlockPos blockPos = blockEntityTickInvoker.getPos();
         csvWriter.printRow(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockEntityTickInvoker.getName());
      }

   }

   @VisibleForTesting
   public void clearUpdatesInArea(BlockBox box) {
      this.syncedBlockEventQueue.removeIf((event) -> {
         return box.contains(event.pos());
      });
   }

   public float getBrightness(Direction direction, boolean shaded) {
      return 1.0F;
   }

   public Iterable iterateEntities() {
      return this.getEntityLookup().iterate();
   }

   public String toString() {
      return "ServerLevel[" + this.worldProperties.getLevelName() + "]";
   }

   public boolean isFlat() {
      return this.server.getSaveProperties().isFlatWorld();
   }

   public long getSeed() {
      return this.server.getSaveProperties().getGeneratorOptions().getSeed();
   }

   @Nullable
   public EnderDragonFight getEnderDragonFight() {
      return this.enderDragonFight;
   }

   public ServerWorld toServerWorld() {
      return this;
   }

   @VisibleForTesting
   public String getDebugString() {
      return String.format(Locale.ROOT, "players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entityManager.getDebugString(), getTopFive(this.entityManager.getLookup().iterate(), (entity) -> {
         return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
      }), this.blockEntityTickers.size(), getTopFive(this.blockEntityTickers, BlockEntityTickInvoker::getName), this.getBlockTickScheduler().getTickCount(), this.getFluidTickScheduler().getTickCount(), this.asString());
   }

   private static String getTopFive(Iterable items, Function classifier) {
      try {
         Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
         Iterator var3 = items.iterator();

         while(var3.hasNext()) {
            Object object = var3.next();
            String string = (String)classifier.apply(object);
            object2IntOpenHashMap.addTo(string, 1);
         }

         return (String)object2IntOpenHashMap.object2IntEntrySet().stream().sorted(Comparator.comparing(Object2IntMap.Entry::getIntValue).reversed()).limit(5L).map((entry) -> {
            String var10000 = (String)entry.getKey();
            return var10000 + ":" + entry.getIntValue();
         }).collect(Collectors.joining(","));
      } catch (Exception var6) {
         return "";
      }
   }

   protected EntityLookup getEntityLookup() {
      return this.entityManager.getLookup();
   }

   public void loadEntities(Stream entities) {
      this.entityManager.loadEntities(entities);
   }

   public void addEntities(Stream entities) {
      this.entityManager.addEntities(entities);
   }

   public void disableTickSchedulers(WorldChunk chunk) {
      chunk.disableTickSchedulers(this.getLevelProperties().getTime());
   }

   public void cacheStructures(Chunk chunk) {
      this.server.execute(() -> {
         this.structureLocator.cache(chunk.getPos(), chunk.getStructureStarts());
      });
   }

   public PathNodeTypeCache getPathNodeTypeCache() {
      return this.pathNodeTypeCache;
   }

   public void method_72079(ChunkPos chunkPos, int i) {
      List list = ChunkPos.stream(chunkPos, i).toList();
      this.chunkManager.addTicket(ChunkTicketType.UNKNOWN, chunkPos, i);
      list.forEach((chunkPosx) -> {
         this.getChunk(chunkPosx.x, chunkPosx.z);
      });
      this.server.runTasks(() -> {
         this.entityManager.loadChunks();
         Iterator var2 = list.iterator();

         ChunkPos chunkPos;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            chunkPos = (ChunkPos)var2.next();
         } while(this.isChunkLoaded(chunkPos.toLong()));

         return false;
      });
   }

   public void close() throws IOException {
      super.close();
      this.entityManager.close();
   }

   public String asString() {
      String var10000 = this.chunkManager.getDebugString();
      return "Chunks[S] W: " + var10000 + " E: " + this.entityManager.getDebugString();
   }

   public boolean isChunkLoaded(long chunkPos) {
      return this.entityManager.isLoaded(chunkPos);
   }

   public boolean isTickingFutureReady(long chunkPos) {
      return this.isChunkLoaded(chunkPos) && this.chunkManager.isTickingFutureReady(chunkPos);
   }

   public boolean shouldTickEntityAt(BlockPos pos) {
      return this.entityManager.shouldTick(pos) && this.chunkManager.chunkLoadingManager.getLevelManager().shouldTickEntities(ChunkPos.toLong(pos));
   }

   public boolean shouldTickTestAt(ChunkPos pos) {
      return this.entityManager.shouldTickTest(pos) && this.entityManager.isLoaded(pos.toLong());
   }

   public boolean shouldTickBlockAt(BlockPos pos) {
      return this.shouldTickChunkAt(new ChunkPos(pos));
   }

   public boolean shouldTickChunkAt(ChunkPos pos) {
      return this.chunkManager.chunkLoadingManager.shouldTick(pos);
   }

   public boolean canSpawnEntitiesAt(ChunkPos pos) {
      return this.entityManager.shouldTick(pos) && this.getWorldBorder().contains(pos);
   }

   public FeatureSet getEnabledFeatures() {
      return this.server.getSaveProperties().getEnabledFeatures();
   }

   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.server.getBrewingRecipeRegistry();
   }

   public FuelRegistry getFuelRegistry() {
      return this.server.getFuelRegistry();
   }

   public Random getOrCreateRandom(Identifier id) {
      return this.randomSequences.getOrCreate(id);
   }

   public RandomSequencesState getRandomSequences() {
      return this.randomSequences;
   }

   public GameRules getGameRules() {
      return this.worldProperties.getGameRules();
   }

   public CrashReportSection addDetailsToCrashReport(CrashReport report) {
      CrashReportSection crashReportSection = super.addDetailsToCrashReport(report);
      crashReportSection.add("Loaded entity count", () -> {
         return String.valueOf(this.entityManager.getIndexSize());
      });
      return crashReportSection;
   }

   public int getSeaLevel() {
      return this.chunkManager.getChunkGenerator().getSeaLevel();
   }

   // $FF: synthetic method
   public RecipeManager getRecipeManager() {
      return this.getRecipeManager();
   }

   // $FF: synthetic method
   public Scoreboard getScoreboard() {
      return this.getScoreboard();
   }

   // $FF: synthetic method
   public ChunkManager getChunkManager() {
      return this.getChunkManager();
   }

   // $FF: synthetic method
   public QueryableTickScheduler getFluidTickScheduler() {
      return this.getFluidTickScheduler();
   }

   // $FF: synthetic method
   public QueryableTickScheduler getBlockTickScheduler() {
      return this.getBlockTickScheduler();
   }

   private final class ServerEntityHandler implements EntityHandler {
      ServerEntityHandler() {
      }

      public void create(Entity entity) {
         if (entity instanceof ServerWaypoint serverWaypoint) {
            if (serverWaypoint.hasWaypoint()) {
               ServerWorld.this.getWaypointHandler().onTrack(serverWaypoint);
            }
         }

      }

      public void destroy(Entity entity) {
         if (entity instanceof ServerWaypoint serverWaypoint) {
            ServerWorld.this.getWaypointHandler().onUntrack(serverWaypoint);
         }

         ServerWorld.this.getScoreboard().clearDeadEntity(entity);
      }

      public void startTicking(Entity entity) {
         ServerWorld.this.entityList.add(entity);
      }

      public void stopTicking(Entity entity) {
         ServerWorld.this.entityList.remove(entity);
      }

      public void startTracking(Entity entity) {
         ServerWorld.this.getChunkManager().loadEntity(entity);
         if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            ServerWorld.this.players.add(serverPlayerEntity);
            if (serverPlayerEntity.canReceiveWaypoints()) {
               ServerWorld.this.getWaypointHandler().addPlayer(serverPlayerEntity);
            }

            ServerWorld.this.updateSleepingPlayers();
         }

         if (entity instanceof ServerWaypoint serverWaypoint) {
            if (serverWaypoint.hasWaypoint()) {
               ServerWorld.this.getWaypointHandler().onTrack(serverWaypoint);
            }
         }

         if (entity instanceof MobEntity mobEntity) {
            if (ServerWorld.this.duringListenerUpdate) {
               String string = "onTrackingStart called during navigation iteration";
               Util.logErrorOrPause("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }

            ServerWorld.this.loadedMobs.add(mobEntity);
         }

         if (entity instanceof EnderDragonEntity enderDragonEntity) {
            EnderDragonPart[] var10 = enderDragonEntity.getBodyParts();
            int var4 = var10.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               EnderDragonPart enderDragonPart = var10[var5];
               ServerWorld.this.enderDragonParts.put(enderDragonPart.getId(), enderDragonPart);
            }
         }

         entity.updateEventHandler(EntityGameEventHandler::onEntitySetPosCallback);
      }

      public void stopTracking(Entity entity) {
         ServerWorld.this.getChunkManager().unloadEntity(entity);
         if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            ServerWorld.this.players.remove(serverPlayerEntity);
            ServerWorld.this.getWaypointHandler().removePlayer(serverPlayerEntity);
            ServerWorld.this.updateSleepingPlayers();
         }

         if (entity instanceof MobEntity mobEntity) {
            if (ServerWorld.this.duringListenerUpdate) {
               String string = "onTrackingStart called during navigation iteration";
               Util.logErrorOrPause("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }

            ServerWorld.this.loadedMobs.remove(mobEntity);
         }

         if (entity instanceof EnderDragonEntity enderDragonEntity) {
            EnderDragonPart[] var9 = enderDragonEntity.getBodyParts();
            int var4 = var9.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               EnderDragonPart enderDragonPart = var9[var5];
               ServerWorld.this.enderDragonParts.remove(enderDragonPart.getId());
            }
         }

         entity.updateEventHandler(EntityGameEventHandler::onEntityRemoval);
      }

      public void updateLoadStatus(Entity entity) {
         entity.updateEventHandler(EntityGameEventHandler::onEntitySetPos);
      }

      // $FF: synthetic method
      public void updateLoadStatus(final Object entity) {
         this.updateLoadStatus((Entity)entity);
      }

      // $FF: synthetic method
      public void stopTracking(final Object entity) {
         this.stopTracking((Entity)entity);
      }

      // $FF: synthetic method
      public void startTracking(final Object entity) {
         this.startTracking((Entity)entity);
      }

      // $FF: synthetic method
      public void startTicking(final Object entity) {
         this.startTicking((Entity)entity);
      }

      // $FF: synthetic method
      public void destroy(final Object entity) {
         this.destroy((Entity)entity);
      }

      // $FF: synthetic method
      public void create(final Object entity) {
         this.create((Entity)entity);
      }
   }
}
