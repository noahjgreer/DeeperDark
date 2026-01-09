package net.minecraft.server;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.jtracy.DiscontinuousFrame;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.minecraft.SharedConstants;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FuelRegistry;
import net.minecraft.network.QueryableServer;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.test.TestManager;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.util.ModStatus;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.TickDurationMonitor;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.util.WinNativeModuleUtil;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.util.crash.SuppressedExceptionsTracker;
import net.minecraft.util.function.Finishable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.path.PathUtil;
import net.minecraft.util.profiler.DebugRecorder;
import net.minecraft.util.profiler.DummyRecorder;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.RecordDumper;
import net.minecraft.util.profiler.Recorder;
import net.minecraft.util.profiler.ServerSamplerSource;
import net.minecraft.util.profiler.ServerTickType;
import net.minecraft.util.profiler.log.DebugSampleLog;
import net.minecraft.util.profiler.log.DebugSampleType;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.MiscConfiguredFeatures;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import net.minecraft.world.storage.StorageKey;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class MinecraftServer extends ReentrantThreadExecutor implements QueryableServer, ChunkErrorHandler, CommandOutput {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String VANILLA = "vanilla";
   private static final float field_33212 = 0.8F;
   private static final int field_33213 = 100;
   private static final long OVERLOAD_THRESHOLD_NANOS;
   private static final int field_47144 = 20;
   private static final long OVERLOAD_WARNING_INTERVAL_NANOS;
   private static final int field_47146 = 100;
   private static final long PLAYER_SAMPLE_UPDATE_INTERVAL_NANOS;
   private static final long PREPARE_START_REGION_TICK_DELAY_NANOS;
   private static final int field_33218 = 12;
   private static final int field_48466 = 5;
   private static final int field_33220 = 6000;
   private static final int field_47149 = 100;
   private static final int field_33221 = 3;
   public static final int MAX_WORLD_BORDER_RADIUS = 29999984;
   public static final LevelInfo DEMO_LEVEL_INFO;
   public static final GameProfile ANONYMOUS_PLAYER_PROFILE;
   protected final LevelStorage.Session session;
   protected final PlayerSaveHandler saveHandler;
   private final List serverGuiTickables = Lists.newArrayList();
   private Recorder recorder;
   private Consumer recorderResultConsumer;
   private Consumer recorderDumpConsumer;
   private boolean needsRecorderSetup;
   @Nullable
   private DebugStart debugStart;
   private boolean needsDebugSetup;
   private final ServerNetworkIo networkIo;
   private final WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;
   @Nullable
   private ServerMetadata metadata;
   @Nullable
   private ServerMetadata.Favicon favicon;
   private final Random random;
   private final DataFixer dataFixer;
   private String serverIp;
   private int serverPort;
   private final CombinedDynamicRegistries combinedDynamicRegistries;
   private final Map worlds;
   private PlayerManager playerManager;
   private volatile boolean running;
   private boolean stopped;
   private int ticks;
   private int ticksUntilAutosave;
   protected final Proxy proxy;
   private boolean onlineMode;
   private boolean preventProxyConnections;
   private boolean pvpEnabled;
   private boolean flightEnabled;
   @Nullable
   private String motd;
   private int playerIdleTimeout;
   private final long[] tickTimes;
   private long recentTickTimesNanos;
   @Nullable
   private KeyPair keyPair;
   @Nullable
   private GameProfile hostProfile;
   private boolean demo;
   private volatile boolean loading;
   private long lastOverloadWarningNanos;
   protected final ApiServices apiServices;
   private long lastPlayerSampleUpdate;
   private final Thread serverThread;
   private long lastFullTickLogTime;
   private long tasksStartTime;
   private long waitTime;
   private long tickStartTimeNanos;
   private boolean waitingForNextTick;
   private long tickEndTimeNanos;
   private boolean hasJustExecutedTask;
   private final ResourcePackManager dataPackManager;
   private final ServerScoreboard scoreboard;
   @Nullable
   private DataCommandStorage dataCommandStorage;
   private final BossBarManager bossBarManager;
   private final CommandFunctionManager commandFunctionManager;
   private boolean enforceWhitelist;
   private float averageTickTime;
   private final Executor workerExecutor;
   @Nullable
   private String serverId;
   private ResourceManagerHolder resourceManagerHolder;
   private final StructureTemplateManager structureTemplateManager;
   private final ServerTickManager tickManager;
   protected final SaveProperties saveProperties;
   private final BrewingRecipeRegistry brewingRecipeRegistry;
   private FuelRegistry fuelRegistry;
   private int idleTickCount;
   private volatile boolean saving;
   private static final AtomicReference WORLD_GEN_EXCEPTION;
   private final SuppressedExceptionsTracker suppressedExceptionsTracker;
   private final DiscontinuousFrame discontinuousFrame;

   public static MinecraftServer startServer(Function serverFactory) {
      AtomicReference atomicReference = new AtomicReference();
      Thread thread = new Thread(() -> {
         ((MinecraftServer)atomicReference.get()).runServer();
      }, "Server thread");
      thread.setUncaughtExceptionHandler((threadx, throwable) -> {
         LOGGER.error("Uncaught exception in server thread", throwable);
      });
      if (Runtime.getRuntime().availableProcessors() > 4) {
         thread.setPriority(8);
      }

      MinecraftServer minecraftServer = (MinecraftServer)serverFactory.apply(thread);
      atomicReference.set(minecraftServer);
      thread.start();
      return minecraftServer;
   }

   public MinecraftServer(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory) {
      super("Server");
      this.recorder = DummyRecorder.INSTANCE;
      this.recorderResultConsumer = (profileResult) -> {
         this.resetRecorder();
      };
      this.recorderDumpConsumer = (path) -> {
      };
      this.random = Random.create();
      this.serverPort = -1;
      this.worlds = Maps.newLinkedHashMap();
      this.running = true;
      this.ticksUntilAutosave = 6000;
      this.tickTimes = new long[100];
      this.recentTickTimesNanos = 0L;
      this.lastFullTickLogTime = Util.getMeasuringTimeNano();
      this.tasksStartTime = Util.getMeasuringTimeNano();
      this.tickStartTimeNanos = Util.getMeasuringTimeNano();
      this.waitingForNextTick = false;
      this.scoreboard = new ServerScoreboard(this);
      this.bossBarManager = new BossBarManager();
      this.suppressedExceptionsTracker = new SuppressedExceptionsTracker();
      this.combinedDynamicRegistries = saveLoader.combinedDynamicRegistries();
      this.saveProperties = saveLoader.saveProperties();
      if (!this.combinedDynamicRegistries.getCombinedRegistryManager().getOrThrow(RegistryKeys.DIMENSION).contains(DimensionOptions.OVERWORLD)) {
         throw new IllegalStateException("Missing Overworld dimension data");
      } else {
         this.proxy = proxy;
         this.dataPackManager = dataPackManager;
         this.resourceManagerHolder = new ResourceManagerHolder(saveLoader.resourceManager(), saveLoader.dataPackContents());
         this.apiServices = apiServices;
         if (apiServices.userCache() != null) {
            apiServices.userCache().setExecutor(this);
         }

         this.networkIo = new ServerNetworkIo(this);
         this.tickManager = new ServerTickManager(this);
         this.worldGenerationProgressListenerFactory = worldGenerationProgressListenerFactory;
         this.session = session;
         this.saveHandler = session.createSaveHandler();
         this.dataFixer = dataFixer;
         this.commandFunctionManager = new CommandFunctionManager(this, this.resourceManagerHolder.dataPackContents.getFunctionLoader());
         RegistryEntryLookup registryEntryLookup = this.combinedDynamicRegistries.getCombinedRegistryManager().getOrThrow(RegistryKeys.BLOCK).withFeatureFilter(this.saveProperties.getEnabledFeatures());
         this.structureTemplateManager = new StructureTemplateManager(saveLoader.resourceManager(), session, dataFixer, registryEntryLookup);
         this.serverThread = serverThread;
         this.workerExecutor = Util.getMainWorkerExecutor();
         this.brewingRecipeRegistry = BrewingRecipeRegistry.create(this.saveProperties.getEnabledFeatures());
         this.resourceManagerHolder.dataPackContents.getRecipeManager().initialize(this.saveProperties.getEnabledFeatures());
         this.fuelRegistry = FuelRegistry.createDefault(this.combinedDynamicRegistries.getCombinedRegistryManager(), this.saveProperties.getEnabledFeatures());
         this.discontinuousFrame = TracyClient.createDiscontinuousFrame("Server Tick");
      }
   }

   private void initScoreboard(PersistentStateManager persistentStateManager) {
      persistentStateManager.getOrCreate(ServerScoreboard.STATE_TYPE);
   }

   protected abstract boolean setupServer() throws IOException;

   protected void loadWorld() {
      if (!FlightProfiler.INSTANCE.isProfiling()) {
      }

      boolean bl = false;
      Finishable finishable = FlightProfiler.INSTANCE.startWorldLoadProfiling();
      this.saveProperties.addServerBrand(this.getServerModName(), this.getModStatus().isModded());
      WorldGenerationProgressListener worldGenerationProgressListener = this.worldGenerationProgressListenerFactory.create(this.saveProperties.getGameRules().getInt(GameRules.SPAWN_CHUNK_RADIUS));
      this.createWorlds(worldGenerationProgressListener);
      this.updateDifficulty();
      this.prepareStartRegion(worldGenerationProgressListener);
      if (finishable != null) {
         finishable.finish(true);
      }

      if (bl) {
         try {
            FlightProfiler.INSTANCE.stop();
         } catch (Throwable var5) {
            LOGGER.warn("Failed to stop JFR profiling", var5);
         }
      }

   }

   protected void updateDifficulty() {
   }

   protected void createWorlds(WorldGenerationProgressListener worldGenerationProgressListener) {
      ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
      boolean bl = this.saveProperties.isDebugWorld();
      Registry registry = this.combinedDynamicRegistries.getCombinedRegistryManager().getOrThrow(RegistryKeys.DIMENSION);
      GeneratorOptions generatorOptions = this.saveProperties.getGeneratorOptions();
      long l = generatorOptions.getSeed();
      long m = BiomeAccess.hashSeed(l);
      List list = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverWorldProperties));
      DimensionOptions dimensionOptions = (DimensionOptions)registry.get(DimensionOptions.OVERWORLD);
      ServerWorld serverWorld = new ServerWorld(this, this.workerExecutor, this.session, serverWorldProperties, World.OVERWORLD, dimensionOptions, worldGenerationProgressListener, bl, m, list, true, (RandomSequencesState)null);
      this.worlds.put(World.OVERWORLD, serverWorld);
      PersistentStateManager persistentStateManager = serverWorld.getPersistentStateManager();
      this.initScoreboard(persistentStateManager);
      this.dataCommandStorage = new DataCommandStorage(persistentStateManager);
      WorldBorder worldBorder = serverWorld.getWorldBorder();
      if (!serverWorldProperties.isInitialized()) {
         try {
            setupSpawn(serverWorld, serverWorldProperties, generatorOptions.hasBonusChest(), bl);
            serverWorldProperties.setInitialized(true);
            if (bl) {
               this.setToDebugWorldProperties(this.saveProperties);
            }
         } catch (Throwable var23) {
            CrashReport crashReport = CrashReport.create(var23, "Exception initializing level");

            try {
               serverWorld.addDetailsToCrashReport(crashReport);
            } catch (Throwable var22) {
            }

            throw new CrashException(crashReport);
         }

         serverWorldProperties.setInitialized(true);
      }

      this.getPlayerManager().setMainWorld(serverWorld);
      if (this.saveProperties.getCustomBossEvents() != null) {
         this.getBossBarManager().readNbt(this.saveProperties.getCustomBossEvents(), this.getRegistryManager());
      }

      RandomSequencesState randomSequencesState = serverWorld.getRandomSequences();
      Iterator var24 = registry.getEntrySet().iterator();

      while(var24.hasNext()) {
         Map.Entry entry = (Map.Entry)var24.next();
         RegistryKey registryKey = (RegistryKey)entry.getKey();
         if (registryKey != DimensionOptions.OVERWORLD) {
            RegistryKey registryKey2 = RegistryKey.of(RegistryKeys.WORLD, registryKey.getValue());
            UnmodifiableLevelProperties unmodifiableLevelProperties = new UnmodifiableLevelProperties(this.saveProperties, serverWorldProperties);
            ServerWorld serverWorld2 = new ServerWorld(this, this.workerExecutor, this.session, unmodifiableLevelProperties, registryKey2, (DimensionOptions)entry.getValue(), worldGenerationProgressListener, bl, m, ImmutableList.of(), false, randomSequencesState);
            worldBorder.addListener(new WorldBorderListener.WorldBorderSyncer(serverWorld2.getWorldBorder()));
            this.worlds.put(registryKey2, serverWorld2);
         }
      }

      worldBorder.load(serverWorldProperties.getWorldBorder());
   }

   private static void setupSpawn(ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld) {
      if (debugWorld) {
         worldProperties.setSpawnPos(BlockPos.ORIGIN.up(80), 0.0F);
      } else {
         ServerChunkManager serverChunkManager = world.getChunkManager();
         ChunkPos chunkPos = new ChunkPos(serverChunkManager.getNoiseConfig().getMultiNoiseSampler().findBestSpawnPosition());
         int i = serverChunkManager.getChunkGenerator().getSpawnHeight(world);
         if (i < world.getBottomY()) {
            BlockPos blockPos = chunkPos.getStartPos();
            i = world.getTopY(Heightmap.Type.WORLD_SURFACE, blockPos.getX() + 8, blockPos.getZ() + 8);
         }

         worldProperties.setSpawnPos(chunkPos.getStartPos().add(8, i, 8), 0.0F);
         int j = 0;
         int k = 0;
         int l = 0;
         int m = -1;

         for(int n = 0; n < MathHelper.square(11); ++n) {
            if (j >= -5 && j <= 5 && k >= -5 && k <= 5) {
               BlockPos blockPos2 = SpawnLocating.findServerSpawnPoint(world, new ChunkPos(chunkPos.x + j, chunkPos.z + k));
               if (blockPos2 != null) {
                  worldProperties.setSpawnPos(blockPos2, 0.0F);
                  break;
               }
            }

            if (j == k || j < 0 && j == -k || j > 0 && j == 1 - k) {
               int o = l;
               l = -m;
               m = o;
            }

            j += l;
            k += m;
         }

         if (bonusChest) {
            world.getRegistryManager().getOptional(RegistryKeys.CONFIGURED_FEATURE).flatMap((featureRegistry) -> {
               return featureRegistry.getOptional(MiscConfiguredFeatures.BONUS_CHEST);
            }).ifPresent((feature) -> {
               ((ConfiguredFeature)feature.value()).generate(world, serverChunkManager.getChunkGenerator(), world.random, worldProperties.getSpawnPos());
            });
         }

      }
   }

   private void setToDebugWorldProperties(SaveProperties properties) {
      properties.setDifficulty(Difficulty.PEACEFUL);
      properties.setDifficultyLocked(true);
      ServerWorldProperties serverWorldProperties = properties.getMainWorldProperties();
      serverWorldProperties.setRaining(false);
      serverWorldProperties.setThundering(false);
      serverWorldProperties.setClearWeatherTime(1000000000);
      serverWorldProperties.setTimeOfDay(6000L);
      serverWorldProperties.setGameMode(GameMode.SPECTATOR);
   }

   private void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener) {
      ServerWorld serverWorld = this.getOverworld();
      LOGGER.info("Preparing start region for dimension {}", serverWorld.getRegistryKey().getValue());
      BlockPos blockPos = serverWorld.getSpawnPos();
      worldGenerationProgressListener.start(new ChunkPos(blockPos));
      ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
      this.tickStartTimeNanos = Util.getMeasuringTimeNano();
      serverWorld.setSpawnPos(blockPos, serverWorld.getSpawnAngle());
      int i = this.getGameRules().getInt(GameRules.SPAWN_CHUNK_RADIUS);
      int j = i > 0 ? MathHelper.square(WorldGenerationProgressListener.getStartRegionSize(i)) : 0;

      while(serverChunkManager.getTotalChunksLoadedCount() < j) {
         this.tickStartTimeNanos = Util.getMeasuringTimeNano() + PREPARE_START_REGION_TICK_DELAY_NANOS;
         this.runTasksTillTickEnd();
      }

      this.tickStartTimeNanos = Util.getMeasuringTimeNano() + PREPARE_START_REGION_TICK_DELAY_NANOS;
      this.runTasksTillTickEnd();
      Iterator var7 = this.worlds.values().iterator();

      while(var7.hasNext()) {
         ServerWorld serverWorld2 = (ServerWorld)var7.next();
         ChunkTicketManager chunkTicketManager = (ChunkTicketManager)serverWorld2.getPersistentStateManager().get(ChunkTicketManager.STATE_TYPE);
         if (chunkTicketManager != null) {
            chunkTicketManager.promoteToRealTickets();
         }
      }

      this.tickStartTimeNanos = Util.getMeasuringTimeNano() + PREPARE_START_REGION_TICK_DELAY_NANOS;
      this.runTasksTillTickEnd();
      worldGenerationProgressListener.stop();
      this.updateMobSpawnOptions();
   }

   public GameMode getDefaultGameMode() {
      return this.saveProperties.getGameMode();
   }

   public boolean isHardcore() {
      return this.saveProperties.isHardcore();
   }

   public abstract int getOpPermissionLevel();

   public abstract int getFunctionPermissionLevel();

   public abstract boolean shouldBroadcastRconToOps();

   public boolean save(boolean suppressLogs, boolean flush, boolean force) {
      boolean bl = false;

      for(Iterator var5 = this.getWorlds().iterator(); var5.hasNext(); bl = true) {
         ServerWorld serverWorld = (ServerWorld)var5.next();
         if (!suppressLogs) {
            LOGGER.info("Saving chunks for level '{}'/{}", serverWorld, serverWorld.getRegistryKey().getValue());
         }

         serverWorld.save((ProgressListener)null, flush, serverWorld.savingDisabled && !force);
      }

      ServerWorld serverWorld2 = this.getOverworld();
      ServerWorldProperties serverWorldProperties = this.saveProperties.getMainWorldProperties();
      serverWorldProperties.setWorldBorder(serverWorld2.getWorldBorder().write());
      this.saveProperties.setCustomBossEvents(this.getBossBarManager().toNbt(this.getRegistryManager()));
      this.session.backupLevelDataFile(this.getRegistryManager(), this.saveProperties, this.getPlayerManager().getUserData());
      if (flush) {
         Iterator var7 = this.getWorlds().iterator();

         while(var7.hasNext()) {
            ServerWorld serverWorld3 = (ServerWorld)var7.next();
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", serverWorld3.getChunkManager().chunkLoadingManager.getSaveDir());
         }

         LOGGER.info("ThreadedAnvilChunkStorage: All dimensions are saved");
      }

      return bl;
   }

   public boolean saveAll(boolean suppressLogs, boolean flush, boolean force) {
      boolean var4;
      try {
         this.saving = true;
         this.getPlayerManager().saveAllPlayerData();
         var4 = this.save(suppressLogs, flush, force);
      } finally {
         this.saving = false;
      }

      return var4;
   }

   public void close() {
      this.shutdown();
   }

   public void shutdown() {
      if (this.recorder.isActive()) {
         this.forceStopRecorder();
      }

      LOGGER.info("Stopping server");
      this.getNetworkIo().stop();
      this.saving = true;
      if (this.playerManager != null) {
         LOGGER.info("Saving players");
         this.playerManager.saveAllPlayerData();
         this.playerManager.disconnectAllPlayers();
      }

      LOGGER.info("Saving worlds");
      Iterator var1 = this.getWorlds().iterator();

      ServerWorld serverWorld;
      while(var1.hasNext()) {
         serverWorld = (ServerWorld)var1.next();
         if (serverWorld != null) {
            serverWorld.savingDisabled = false;
         }
      }

      while(this.worlds.values().stream().anyMatch((world) -> {
         return world.getChunkManager().chunkLoadingManager.shouldDelayShutdown();
      })) {
         this.tickStartTimeNanos = Util.getMeasuringTimeNano() + TimeHelper.MILLI_IN_NANOS;
         var1 = this.getWorlds().iterator();

         while(var1.hasNext()) {
            serverWorld = (ServerWorld)var1.next();
            serverWorld.getChunkManager().shutdown();
            serverWorld.getChunkManager().tick(() -> {
               return true;
            }, false);
         }

         this.runTasksTillTickEnd();
      }

      this.save(false, true, false);
      var1 = this.getWorlds().iterator();

      while(var1.hasNext()) {
         serverWorld = (ServerWorld)var1.next();
         if (serverWorld != null) {
            try {
               serverWorld.close();
            } catch (IOException var5) {
               LOGGER.error("Exception closing the level", var5);
            }
         }
      }

      this.saving = false;
      this.resourceManagerHolder.close();

      try {
         this.session.close();
      } catch (IOException var4) {
         LOGGER.error("Failed to unlock level {}", this.session.getDirectoryName(), var4);
      }

   }

   public String getServerIp() {
      return this.serverIp;
   }

   public void setServerIp(String serverIp) {
      this.serverIp = serverIp;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void stop(boolean waitForShutdown) {
      this.running = false;
      if (waitForShutdown) {
         try {
            this.serverThread.join();
         } catch (InterruptedException var3) {
            LOGGER.error("Error while shutting down", var3);
         }
      }

   }

   protected void runServer() {
      try {
         if (!this.setupServer()) {
            throw new IllegalStateException("Failed to initialize server");
         }

         this.tickStartTimeNanos = Util.getMeasuringTimeNano();
         this.favicon = (ServerMetadata.Favicon)this.loadFavicon().orElse((Object)null);
         this.metadata = this.createMetadata();

         while(this.running) {
            long l;
            if (!this.isPaused() && this.tickManager.isSprinting() && this.tickManager.sprint()) {
               l = 0L;
               this.tickStartTimeNanos = Util.getMeasuringTimeNano();
               this.lastOverloadWarningNanos = this.tickStartTimeNanos;
            } else {
               l = this.tickManager.getNanosPerTick();
               long m = Util.getMeasuringTimeNano() - this.tickStartTimeNanos;
               if (m > OVERLOAD_THRESHOLD_NANOS + 20L * l && this.tickStartTimeNanos - this.lastOverloadWarningNanos >= OVERLOAD_WARNING_INTERVAL_NANOS + 100L * l) {
                  long n = m / l;
                  LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", m / TimeHelper.MILLI_IN_NANOS, n);
                  this.tickStartTimeNanos += n * l;
                  this.lastOverloadWarningNanos = this.tickStartTimeNanos;
               }
            }

            boolean bl = l == 0L;
            if (this.needsDebugSetup) {
               this.needsDebugSetup = false;
               this.debugStart = new DebugStart(Util.getMeasuringTimeNano(), this.ticks);
            }

            this.tickStartTimeNanos += l;

            try {
               Profilers.Scoped scoped = Profilers.using(this.startTickMetrics());

               try {
                  Profiler profiler = Profilers.get();
                  profiler.push("tick");
                  this.discontinuousFrame.start();
                  this.tick(bl ? () -> {
                     return false;
                  } : this::shouldKeepTicking);
                  this.discontinuousFrame.end();
                  profiler.swap("nextTickWait");
                  this.hasJustExecutedTask = true;
                  this.tickEndTimeNanos = Math.max(Util.getMeasuringTimeNano() + l, this.tickStartTimeNanos);
                  this.startTaskPerformanceLog();
                  this.runTasksTillTickEnd();
                  this.pushPerformanceLogs();
                  if (bl) {
                     this.tickManager.updateSprintTime();
                  }

                  profiler.pop();
                  this.pushFullTickLog();
               } catch (Throwable var67) {
                  if (scoped != null) {
                     try {
                        scoped.close();
                     } catch (Throwable var65) {
                        var67.addSuppressed(var65);
                     }
                  }

                  throw var67;
               }

               if (scoped != null) {
                  scoped.close();
               }
            } finally {
               this.endTickMetrics();
            }

            this.loading = true;
            FlightProfiler.INSTANCE.onTick(this.averageTickTime);
         }
      } catch (Throwable var69) {
         LOGGER.error("Encountered an unexpected exception", var69);
         CrashReport crashReport = createCrashReport(var69);
         this.addSystemDetails(crashReport.getSystemDetailsSection());
         Path path = this.getRunDirectory().resolve("crash-reports").resolve("crash-" + Util.getFormattedCurrentTime() + "-server.txt");
         if (crashReport.writeToFile(path, ReportType.MINECRAFT_CRASH_REPORT)) {
            LOGGER.error("This crash report has been saved to: {}", path.toAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         this.setCrashReport(crashReport);
      } finally {
         try {
            this.stopped = true;
            this.shutdown();
         } catch (Throwable var64) {
            LOGGER.error("Exception stopping the server", var64);
         } finally {
            if (this.apiServices.userCache() != null) {
               this.apiServices.userCache().clearExecutor();
            }

            this.exit();
         }

      }

   }

   private void pushFullTickLog() {
      long l = Util.getMeasuringTimeNano();
      if (this.shouldPushTickTimeLog()) {
         this.getDebugSampleLog().push(l - this.lastFullTickLogTime);
      }

      this.lastFullTickLogTime = l;
   }

   private void startTaskPerformanceLog() {
      if (this.shouldPushTickTimeLog()) {
         this.tasksStartTime = Util.getMeasuringTimeNano();
         this.waitTime = 0L;
      }

   }

   private void pushPerformanceLogs() {
      if (this.shouldPushTickTimeLog()) {
         DebugSampleLog debugSampleLog = this.getDebugSampleLog();
         debugSampleLog.push(Util.getMeasuringTimeNano() - this.tasksStartTime - this.waitTime, ServerTickType.SCHEDULED_TASKS.ordinal());
         debugSampleLog.push(this.waitTime, ServerTickType.IDLE.ordinal());
      }

   }

   private static CrashReport createCrashReport(Throwable throwable) {
      CrashException crashException = null;

      for(Throwable throwable2 = throwable; throwable2 != null; throwable2 = throwable2.getCause()) {
         if (throwable2 instanceof CrashException crashException2) {
            crashException = crashException2;
         }
      }

      CrashReport crashReport;
      if (crashException != null) {
         crashReport = crashException.getReport();
         if (crashException != throwable) {
            crashReport.addElement("Wrapped in").add("Wrapping exception", throwable);
         }
      } else {
         crashReport = new CrashReport("Exception in server tick loop", throwable);
      }

      return crashReport;
   }

   private boolean shouldKeepTicking() {
      return this.hasRunningTasks() || Util.getMeasuringTimeNano() < (this.hasJustExecutedTask ? this.tickEndTimeNanos : this.tickStartTimeNanos);
   }

   public static boolean checkWorldGenException() {
      RuntimeException runtimeException = (RuntimeException)WORLD_GEN_EXCEPTION.get();
      if (runtimeException != null) {
         throw runtimeException;
      } else {
         return true;
      }
   }

   public static void setWorldGenException(RuntimeException exception) {
      WORLD_GEN_EXCEPTION.compareAndSet((Object)null, exception);
   }

   public void runTasks(BooleanSupplier stopCondition) {
      super.runTasks(() -> {
         return checkWorldGenException() && stopCondition.getAsBoolean();
      });
   }

   protected void runTasksTillTickEnd() {
      this.runTasks();
      this.waitingForNextTick = true;

      try {
         this.runTasks(() -> {
            return !this.shouldKeepTicking();
         });
      } finally {
         this.waitingForNextTick = false;
      }

   }

   public void waitForTasks() {
      boolean bl = this.shouldPushTickTimeLog();
      long l = bl ? Util.getMeasuringTimeNano() : 0L;
      long m = this.waitingForNextTick ? this.tickStartTimeNanos - Util.getMeasuringTimeNano() : 100000L;
      LockSupport.parkNanos("waiting for tasks", m);
      if (bl) {
         this.waitTime += Util.getMeasuringTimeNano() - l;
      }

   }

   public ServerTask createTask(Runnable runnable) {
      return new ServerTask(this.ticks, runnable);
   }

   protected boolean canExecute(ServerTask serverTask) {
      return serverTask.getCreationTicks() + 3 < this.ticks || this.shouldKeepTicking();
   }

   public boolean runTask() {
      boolean bl = this.runOneTask();
      this.hasJustExecutedTask = bl;
      return bl;
   }

   private boolean runOneTask() {
      if (super.runTask()) {
         return true;
      } else {
         if (this.tickManager.isSprinting() || this.shouldKeepTicking()) {
            Iterator var1 = this.getWorlds().iterator();

            while(var1.hasNext()) {
               ServerWorld serverWorld = (ServerWorld)var1.next();
               if (serverWorld.getChunkManager().executeQueuedTasks()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected void executeTask(ServerTask serverTask) {
      Profilers.get().visit("runTask");
      super.executeTask(serverTask);
   }

   private Optional loadFavicon() {
      Optional optional = Optional.of(this.getPath("server-icon.png")).filter((path) -> {
         return Files.isRegularFile(path, new LinkOption[0]);
      }).or(() -> {
         return this.session.getIconFile().filter((path) -> {
            return Files.isRegularFile(path, new LinkOption[0]);
         });
      });
      return optional.flatMap((path) -> {
         try {
            BufferedImage bufferedImage = ImageIO.read(path.toFile());
            Preconditions.checkState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
            Preconditions.checkState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
            return Optional.of(new ServerMetadata.Favicon(byteArrayOutputStream.toByteArray()));
         } catch (Exception var3) {
            LOGGER.error("Couldn't load server icon", var3);
            return Optional.empty();
         }
      });
   }

   public Optional getIconFile() {
      return this.session.getIconFile();
   }

   public Path getRunDirectory() {
      return Path.of("");
   }

   public void setCrashReport(CrashReport report) {
   }

   public void exit() {
   }

   public boolean isPaused() {
      return false;
   }

   public void tick(BooleanSupplier shouldKeepTicking) {
      long l = Util.getMeasuringTimeNano();
      int i = this.getPauseWhenEmptySeconds() * 20;
      if (i > 0) {
         if (this.playerManager.getCurrentPlayerCount() == 0 && !this.tickManager.isSprinting()) {
            ++this.idleTickCount;
         } else {
            this.idleTickCount = 0;
         }

         if (this.idleTickCount >= i) {
            if (this.idleTickCount == i) {
               LOGGER.info("Server empty for {} seconds, pausing", this.getPauseWhenEmptySeconds());
               this.runAutosave();
            }

            this.tickNetworkIo();
            return;
         }
      }

      ++this.ticks;
      this.tickManager.step();
      this.tickWorlds(shouldKeepTicking);
      if (l - this.lastPlayerSampleUpdate >= PLAYER_SAMPLE_UPDATE_INTERVAL_NANOS) {
         this.lastPlayerSampleUpdate = l;
         this.metadata = this.createMetadata();
      }

      --this.ticksUntilAutosave;
      if (this.ticksUntilAutosave <= 0) {
         this.runAutosave();
      }

      Profiler profiler = Profilers.get();
      profiler.push("tallying");
      long m = Util.getMeasuringTimeNano() - l;
      int j = this.ticks % 100;
      this.recentTickTimesNanos -= this.tickTimes[j];
      this.recentTickTimesNanos += m;
      this.tickTimes[j] = m;
      this.averageTickTime = this.averageTickTime * 0.8F + (float)m / (float)TimeHelper.MILLI_IN_NANOS * 0.19999999F;
      this.pushTickLog(l);
      profiler.pop();
   }

   private void runAutosave() {
      this.ticksUntilAutosave = this.getAutosaveInterval();
      LOGGER.debug("Autosave started");
      Profiler profiler = Profilers.get();
      profiler.push("save");
      this.saveAll(true, false, false);
      profiler.pop();
      LOGGER.debug("Autosave finished");
   }

   private void pushTickLog(long tickStartTime) {
      if (this.shouldPushTickTimeLog()) {
         this.getDebugSampleLog().push(Util.getMeasuringTimeNano() - tickStartTime, ServerTickType.TICK_SERVER_METHOD.ordinal());
      }

   }

   private int getAutosaveInterval() {
      float f;
      if (this.tickManager.isSprinting()) {
         long l = this.getAverageNanosPerTick() + 1L;
         f = (float)TimeHelper.SECOND_IN_NANOS / (float)l;
      } else {
         f = this.tickManager.getTickRate();
      }

      int i = true;
      return Math.max(100, (int)(f * 300.0F));
   }

   public void updateAutosaveTicks() {
      int i = this.getAutosaveInterval();
      if (i < this.ticksUntilAutosave) {
         this.ticksUntilAutosave = i;
      }

   }

   protected abstract DebugSampleLog getDebugSampleLog();

   public abstract boolean shouldPushTickTimeLog();

   private ServerMetadata createMetadata() {
      ServerMetadata.Players players = this.createMetadataPlayers();
      return new ServerMetadata(Text.of(this.motd), Optional.of(players), Optional.of(ServerMetadata.Version.create()), Optional.ofNullable(this.favicon), this.shouldEnforceSecureProfile());
   }

   private ServerMetadata.Players createMetadataPlayers() {
      List list = this.playerManager.getPlayerList();
      int i = this.getMaxPlayerCount();
      if (this.hideOnlinePlayers()) {
         return new ServerMetadata.Players(i, list.size(), List.of());
      } else {
         int j = Math.min(list.size(), 12);
         ObjectArrayList objectArrayList = new ObjectArrayList(j);
         int k = MathHelper.nextInt(this.random, 0, list.size() - j);

         for(int l = 0; l < j; ++l) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)list.get(k + l);
            objectArrayList.add(serverPlayerEntity.allowsServerListing() ? serverPlayerEntity.getGameProfile() : ANONYMOUS_PLAYER_PROFILE);
         }

         Util.shuffle((List)objectArrayList, this.random);
         return new ServerMetadata.Players(i, list.size(), objectArrayList);
      }
   }

   protected void tickWorlds(BooleanSupplier shouldKeepTicking) {
      Profiler profiler = Profilers.get();
      this.getPlayerManager().getPlayerList().forEach((player) -> {
         player.networkHandler.disableFlush();
      });
      profiler.push("commandFunctions");
      this.getCommandFunctionManager().tick();
      profiler.swap("levels");
      Iterator var3 = this.getWorlds().iterator();

      while(var3.hasNext()) {
         ServerWorld serverWorld = (ServerWorld)var3.next();
         profiler.push(() -> {
            String var10000 = String.valueOf(serverWorld);
            return var10000 + " " + String.valueOf(serverWorld.getRegistryKey().getValue());
         });
         if (this.ticks % 20 == 0) {
            profiler.push("timeSync");
            this.sendTimeUpdatePackets(serverWorld);
            profiler.pop();
         }

         profiler.push("tick");

         try {
            serverWorld.tick(shouldKeepTicking);
         } catch (Throwable var7) {
            CrashReport crashReport = CrashReport.create(var7, "Exception ticking world");
            serverWorld.addDetailsToCrashReport(crashReport);
            throw new CrashException(crashReport);
         }

         profiler.pop();
         profiler.pop();
      }

      profiler.swap("connection");
      this.tickNetworkIo();
      profiler.swap("players");
      this.playerManager.updatePlayerLatency();
      if (this.tickManager.shouldTick()) {
         TestManager.INSTANCE.tick();
      }

      profiler.swap("server gui refresh");

      for(int i = 0; i < this.serverGuiTickables.size(); ++i) {
         ((Runnable)this.serverGuiTickables.get(i)).run();
      }

      profiler.swap("send chunks");
      var3 = this.playerManager.getPlayerList().iterator();

      while(var3.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
         serverPlayerEntity.networkHandler.chunkDataSender.sendChunkBatches(serverPlayerEntity);
         serverPlayerEntity.networkHandler.enableFlush();
      }

      profiler.pop();
   }

   public void tickNetworkIo() {
      this.getNetworkIo().tick();
   }

   private void sendTimeUpdatePackets(ServerWorld world) {
      this.playerManager.sendToDimension(new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(), world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)), world.getRegistryKey());
   }

   public void sendTimeUpdatePackets() {
      Profiler profiler = Profilers.get();
      profiler.push("timeSync");
      Iterator var2 = this.getWorlds().iterator();

      while(var2.hasNext()) {
         ServerWorld serverWorld = (ServerWorld)var2.next();
         this.sendTimeUpdatePackets(serverWorld);
      }

      profiler.pop();
   }

   public boolean isWorldAllowed(World world) {
      return true;
   }

   public void addServerGuiTickable(Runnable tickable) {
      this.serverGuiTickables.add(tickable);
   }

   protected void setServerId(String serverId) {
      this.serverId = serverId;
   }

   public boolean isStopping() {
      return !this.serverThread.isAlive();
   }

   public Path getPath(String path) {
      return this.getRunDirectory().resolve(path);
   }

   public final ServerWorld getOverworld() {
      return (ServerWorld)this.worlds.get(World.OVERWORLD);
   }

   @Nullable
   public ServerWorld getWorld(RegistryKey key) {
      return (ServerWorld)this.worlds.get(key);
   }

   public Set getWorldRegistryKeys() {
      return this.worlds.keySet();
   }

   public Iterable getWorlds() {
      return this.worlds.values();
   }

   public String getVersion() {
      return SharedConstants.getGameVersion().name();
   }

   public int getCurrentPlayerCount() {
      return this.playerManager.getCurrentPlayerCount();
   }

   public int getMaxPlayerCount() {
      return this.playerManager.getMaxPlayerCount();
   }

   public String[] getPlayerNames() {
      return this.playerManager.getPlayerNames();
   }

   @DontObfuscate
   public String getServerModName() {
      return "vanilla";
   }

   public SystemDetails addSystemDetails(SystemDetails details) {
      details.addSection("Server Running", () -> {
         return Boolean.toString(this.running);
      });
      if (this.playerManager != null) {
         details.addSection("Player Count", () -> {
            int var10000 = this.playerManager.getCurrentPlayerCount();
            return "" + var10000 + " / " + this.playerManager.getMaxPlayerCount() + "; " + String.valueOf(this.playerManager.getPlayerList());
         });
      }

      details.addSection("Active Data Packs", () -> {
         return ResourcePackManager.listPacks(this.dataPackManager.getEnabledProfiles());
      });
      details.addSection("Available Data Packs", () -> {
         return ResourcePackManager.listPacks(this.dataPackManager.getProfiles());
      });
      details.addSection("Enabled Feature Flags", () -> {
         return (String)FeatureFlags.FEATURE_MANAGER.toId(this.saveProperties.getEnabledFeatures()).stream().map(Identifier::toString).collect(Collectors.joining(", "));
      });
      details.addSection("World Generation", () -> {
         return this.saveProperties.getLifecycle().toString();
      });
      details.addSection("World Seed", () -> {
         return String.valueOf(this.saveProperties.getGeneratorOptions().getSeed());
      });
      SuppressedExceptionsTracker var10002 = this.suppressedExceptionsTracker;
      Objects.requireNonNull(var10002);
      details.addSection("Suppressed Exceptions", var10002::collect);
      if (this.serverId != null) {
         details.addSection("Server Id", () -> {
            return this.serverId;
         });
      }

      return this.addExtraSystemDetails(details);
   }

   public abstract SystemDetails addExtraSystemDetails(SystemDetails details);

   public ModStatus getModStatus() {
      return ModStatus.check("vanilla", this::getServerModName, "Server", MinecraftServer.class);
   }

   public void sendMessage(Text message) {
      LOGGER.info(message.getString());
   }

   public KeyPair getKeyPair() {
      return this.keyPair;
   }

   public int getServerPort() {
      return this.serverPort;
   }

   public void setServerPort(int serverPort) {
      this.serverPort = serverPort;
   }

   @Nullable
   public GameProfile getHostProfile() {
      return this.hostProfile;
   }

   public void setHostProfile(@Nullable GameProfile hostProfile) {
      this.hostProfile = hostProfile;
   }

   public boolean isSingleplayer() {
      return this.hostProfile != null;
   }

   protected void generateKeyPair() {
      LOGGER.info("Generating keypair");

      try {
         this.keyPair = NetworkEncryptionUtils.generateServerKeyPair();
      } catch (NetworkEncryptionException var2) {
         throw new IllegalStateException("Failed to generate key pair", var2);
      }
   }

   public void setDifficulty(Difficulty difficulty, boolean forceUpdate) {
      if (forceUpdate || !this.saveProperties.isDifficultyLocked()) {
         this.saveProperties.setDifficulty(this.saveProperties.isHardcore() ? Difficulty.HARD : difficulty);
         this.updateMobSpawnOptions();
         this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
      }
   }

   public int adjustTrackingDistance(int initialDistance) {
      return initialDistance;
   }

   private void updateMobSpawnOptions() {
      Iterator var1 = this.getWorlds().iterator();

      while(var1.hasNext()) {
         ServerWorld serverWorld = (ServerWorld)var1.next();
         serverWorld.setMobSpawnOptions(this.isMonsterSpawningEnabled());
      }

   }

   public void setDifficultyLocked(boolean locked) {
      this.saveProperties.setDifficultyLocked(locked);
      this.getPlayerManager().getPlayerList().forEach(this::sendDifficulty);
   }

   private void sendDifficulty(ServerPlayerEntity player) {
      WorldProperties worldProperties = player.getWorld().getLevelProperties();
      player.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));
   }

   public boolean isMonsterSpawningEnabled() {
      return this.saveProperties.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isDemo() {
      return this.demo;
   }

   public void setDemo(boolean demo) {
      this.demo = demo;
   }

   public Optional getResourcePackProperties() {
      return Optional.empty();
   }

   public boolean requireResourcePack() {
      return this.getResourcePackProperties().filter(ServerResourcePackProperties::isRequired).isPresent();
   }

   public abstract boolean isDedicated();

   public abstract int getRateLimit();

   public boolean isOnlineMode() {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean onlineMode) {
      this.onlineMode = onlineMode;
   }

   public boolean shouldPreventProxyConnections() {
      return this.preventProxyConnections;
   }

   public void setPreventProxyConnections(boolean preventProxyConnections) {
      this.preventProxyConnections = preventProxyConnections;
   }

   public abstract boolean isUsingNativeTransport();

   public boolean isPvpEnabled() {
      return this.pvpEnabled;
   }

   public void setPvpEnabled(boolean pvpEnabled) {
      this.pvpEnabled = pvpEnabled;
   }

   public boolean isFlightEnabled() {
      return this.flightEnabled;
   }

   public void setFlightEnabled(boolean flightEnabled) {
      this.flightEnabled = flightEnabled;
   }

   public abstract boolean areCommandBlocksEnabled();

   public String getServerMotd() {
      return this.motd;
   }

   public void setMotd(String motd) {
      this.motd = motd;
   }

   public boolean isStopped() {
      return this.stopped;
   }

   public PlayerManager getPlayerManager() {
      return this.playerManager;
   }

   public void setPlayerManager(PlayerManager playerManager) {
      this.playerManager = playerManager;
   }

   public abstract boolean isRemote();

   public void setDefaultGameMode(GameMode gameMode) {
      this.saveProperties.setGameMode(gameMode);
   }

   public ServerNetworkIo getNetworkIo() {
      return this.networkIo;
   }

   public boolean isLoading() {
      return this.loading;
   }

   public boolean hasGui() {
      return false;
   }

   public boolean openToLan(@Nullable GameMode gameMode, boolean cheatsAllowed, int port) {
      return false;
   }

   public int getTicks() {
      return this.ticks;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isSpawnProtected(ServerWorld world, BlockPos pos, PlayerEntity player) {
      return false;
   }

   public boolean acceptsStatusQuery() {
      return true;
   }

   public boolean hideOnlinePlayers() {
      return false;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public int getPlayerIdleTimeout() {
      return this.playerIdleTimeout;
   }

   public void setPlayerIdleTimeout(int playerIdleTimeout) {
      this.playerIdleTimeout = playerIdleTimeout;
   }

   public MinecraftSessionService getSessionService() {
      return this.apiServices.sessionService();
   }

   @Nullable
   public SignatureVerifier getServicesSignatureVerifier() {
      return this.apiServices.serviceSignatureVerifier();
   }

   public GameProfileRepository getGameProfileRepo() {
      return this.apiServices.profileRepository();
   }

   @Nullable
   public UserCache getUserCache() {
      return this.apiServices.userCache();
   }

   @Nullable
   public ServerMetadata getServerMetadata() {
      return this.metadata;
   }

   public void forcePlayerSampleUpdate() {
      this.lastPlayerSampleUpdate = 0L;
   }

   public int getMaxWorldBorderRadius() {
      return 29999984;
   }

   public boolean shouldExecuteAsync() {
      return super.shouldExecuteAsync() && !this.isStopped();
   }

   public void executeSync(Runnable runnable) {
      if (this.isStopped()) {
         throw new RejectedExecutionException("Server already shutting down");
      } else {
         super.executeSync(runnable);
      }
   }

   public Thread getThread() {
      return this.serverThread;
   }

   public int getNetworkCompressionThreshold() {
      return 256;
   }

   public boolean shouldEnforceSecureProfile() {
      return false;
   }

   public long getTimeReference() {
      return this.tickStartTimeNanos;
   }

   public DataFixer getDataFixer() {
      return this.dataFixer;
   }

   public int getSpawnRadius(@Nullable ServerWorld world) {
      return world != null ? world.getGameRules().getInt(GameRules.SPAWN_RADIUS) : 10;
   }

   public ServerAdvancementLoader getAdvancementLoader() {
      return this.resourceManagerHolder.dataPackContents.getServerAdvancementLoader();
   }

   public CommandFunctionManager getCommandFunctionManager() {
      return this.commandFunctionManager;
   }

   public CompletableFuture reloadResources(Collection dataPacks) {
      CompletableFuture completableFuture = CompletableFuture.supplyAsync(() -> {
         Stream var10000 = dataPacks.stream();
         ResourcePackManager var10001 = this.dataPackManager;
         Objects.requireNonNull(var10001);
         return (ImmutableList)var10000.map(var10001::getProfile).filter(Objects::nonNull).map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
      }, this).thenCompose((resourcePacks) -> {
         LifecycledResourceManager lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, resourcePacks);
         List list = TagGroupLoader.startReload(lifecycledResourceManager, (DynamicRegistryManager)this.combinedDynamicRegistries.getCombinedRegistryManager());
         return DataPackContents.reload(lifecycledResourceManager, this.combinedDynamicRegistries, list, this.saveProperties.getEnabledFeatures(), this.isDedicated() ? CommandManager.RegistrationEnvironment.DEDICATED : CommandManager.RegistrationEnvironment.INTEGRATED, this.getFunctionPermissionLevel(), this.workerExecutor, this).whenComplete((dataPackContents, throwable) -> {
            if (throwable != null) {
               lifecycledResourceManager.close();
            }

         }).thenApply((dataPackContents) -> {
            return new ResourceManagerHolder(lifecycledResourceManager, dataPackContents);
         });
      }).thenAcceptAsync((resourceManagerHolder) -> {
         this.resourceManagerHolder.close();
         this.resourceManagerHolder = resourceManagerHolder;
         this.dataPackManager.setEnabledProfiles(dataPacks);
         DataConfiguration dataConfiguration = new DataConfiguration(createDataPackSettings(this.dataPackManager, true), this.saveProperties.getEnabledFeatures());
         this.saveProperties.updateLevelInfo(dataConfiguration);
         this.resourceManagerHolder.dataPackContents.applyPendingTagLoads();
         this.resourceManagerHolder.dataPackContents.getRecipeManager().initialize(this.saveProperties.getEnabledFeatures());
         this.getPlayerManager().saveAllPlayerData();
         this.getPlayerManager().onDataPacksReloaded();
         this.commandFunctionManager.setFunctions(this.resourceManagerHolder.dataPackContents.getFunctionLoader());
         this.structureTemplateManager.setResourceManager(this.resourceManagerHolder.resourceManager);
         this.fuelRegistry = FuelRegistry.createDefault(this.combinedDynamicRegistries.getCombinedRegistryManager(), this.saveProperties.getEnabledFeatures());
      }, this);
      if (this.isOnThread()) {
         Objects.requireNonNull(completableFuture);
         this.runTasks(completableFuture::isDone);
      }

      return completableFuture;
   }

   public static DataConfiguration loadDataPacks(ResourcePackManager resourcePackManager, DataConfiguration dataConfiguration, boolean initMode, boolean safeMode) {
      DataPackSettings dataPackSettings = dataConfiguration.dataPacks();
      FeatureSet featureSet = initMode ? FeatureSet.empty() : dataConfiguration.enabledFeatures();
      FeatureSet featureSet2 = initMode ? FeatureFlags.FEATURE_MANAGER.getFeatureSet() : dataConfiguration.enabledFeatures();
      resourcePackManager.scanPacks();
      if (safeMode) {
         return loadDataPacks(resourcePackManager, List.of("vanilla"), featureSet, false);
      } else {
         Set set = Sets.newLinkedHashSet();
         Iterator var8 = dataPackSettings.getEnabled().iterator();

         while(var8.hasNext()) {
            String string = (String)var8.next();
            if (resourcePackManager.hasProfile(string)) {
               set.add(string);
            } else {
               LOGGER.warn("Missing data pack {}", string);
            }
         }

         var8 = resourcePackManager.getProfiles().iterator();

         while(var8.hasNext()) {
            ResourcePackProfile resourcePackProfile = (ResourcePackProfile)var8.next();
            String string2 = resourcePackProfile.getId();
            if (!dataPackSettings.getDisabled().contains(string2)) {
               FeatureSet featureSet3 = resourcePackProfile.getRequestedFeatures();
               boolean bl = set.contains(string2);
               if (!bl && resourcePackProfile.getSource().canBeEnabledLater()) {
                  if (featureSet3.isSubsetOf(featureSet2)) {
                     LOGGER.info("Found new data pack {}, loading it automatically", string2);
                     set.add(string2);
                  } else {
                     LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", string2, FeatureFlags.printMissingFlags(featureSet2, featureSet3));
                  }
               }

               if (bl && !featureSet3.isSubsetOf(featureSet2)) {
                  LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", string2, FeatureFlags.printMissingFlags(featureSet2, featureSet3));
                  set.remove(string2);
               }
            }
         }

         if (set.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            set.add("vanilla");
         }

         return loadDataPacks(resourcePackManager, set, featureSet, true);
      }
   }

   private static DataConfiguration loadDataPacks(ResourcePackManager resourcePackManager, Collection enabledProfiles, FeatureSet enabledFeatures, boolean allowEnabling) {
      resourcePackManager.setEnabledProfiles(enabledProfiles);
      forceEnableRequestedFeatures(resourcePackManager, enabledFeatures);
      DataPackSettings dataPackSettings = createDataPackSettings(resourcePackManager, allowEnabling);
      FeatureSet featureSet = resourcePackManager.getRequestedFeatures().combine(enabledFeatures);
      return new DataConfiguration(dataPackSettings, featureSet);
   }

   private static void forceEnableRequestedFeatures(ResourcePackManager resourcePackManager, FeatureSet enabledFeatures) {
      FeatureSet featureSet = resourcePackManager.getRequestedFeatures();
      FeatureSet featureSet2 = enabledFeatures.subtract(featureSet);
      if (!featureSet2.isEmpty()) {
         Set set = new ObjectArraySet(resourcePackManager.getEnabledIds());
         Iterator var5 = resourcePackManager.getProfiles().iterator();

         while(var5.hasNext()) {
            ResourcePackProfile resourcePackProfile = (ResourcePackProfile)var5.next();
            if (featureSet2.isEmpty()) {
               break;
            }

            if (resourcePackProfile.getSource() == ResourcePackSource.FEATURE) {
               String string = resourcePackProfile.getId();
               FeatureSet featureSet3 = resourcePackProfile.getRequestedFeatures();
               if (!featureSet3.isEmpty() && featureSet3.intersects(featureSet2) && featureSet3.isSubsetOf(enabledFeatures)) {
                  if (!set.add(string)) {
                     throw new IllegalStateException("Tried to force '" + string + "', but it was already enabled");
                  }

                  LOGGER.info("Found feature pack ('{}') for requested feature, forcing to enabled", string);
                  featureSet2 = featureSet2.subtract(featureSet3);
               }
            }
         }

         resourcePackManager.setEnabledProfiles(set);
      }
   }

   private static DataPackSettings createDataPackSettings(ResourcePackManager dataPackManager, boolean allowEnabling) {
      Collection collection = dataPackManager.getEnabledIds();
      List list = ImmutableList.copyOf(collection);
      List list2 = allowEnabling ? dataPackManager.getIds().stream().filter((name) -> {
         return !collection.contains(name);
      }).toList() : List.of();
      return new DataPackSettings(list, list2);
   }

   public void kickNonWhitelistedPlayers(ServerCommandSource source) {
      if (this.isEnforceWhitelist()) {
         PlayerManager playerManager = source.getServer().getPlayerManager();
         Whitelist whitelist = playerManager.getWhitelist();
         List list = Lists.newArrayList(playerManager.getPlayerList());
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var5.next();
            if (!whitelist.isAllowed(serverPlayerEntity.getGameProfile())) {
               serverPlayerEntity.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.not_whitelisted"));
            }
         }

      }
   }

   public ResourcePackManager getDataPackManager() {
      return this.dataPackManager;
   }

   public CommandManager getCommandManager() {
      return this.resourceManagerHolder.dataPackContents.getCommandManager();
   }

   public ServerCommandSource getCommandSource() {
      ServerWorld serverWorld = this.getOverworld();
      return new ServerCommandSource(this, serverWorld == null ? Vec3d.ZERO : Vec3d.of(serverWorld.getSpawnPos()), Vec2f.ZERO, serverWorld, 4, "Server", Text.literal("Server"), this, (Entity)null);
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldTrackOutput() {
      return true;
   }

   public abstract boolean shouldBroadcastConsoleToOps();

   public ServerRecipeManager getRecipeManager() {
      return this.resourceManagerHolder.dataPackContents.getRecipeManager();
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public DataCommandStorage getDataCommandStorage() {
      if (this.dataCommandStorage == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.dataCommandStorage;
      }
   }

   public GameRules getGameRules() {
      return this.getOverworld().getGameRules();
   }

   public BossBarManager getBossBarManager() {
      return this.bossBarManager;
   }

   public boolean isEnforceWhitelist() {
      return this.enforceWhitelist;
   }

   public void setEnforceWhitelist(boolean enforceWhitelist) {
      this.enforceWhitelist = enforceWhitelist;
   }

   public float getAverageTickTime() {
      return this.averageTickTime;
   }

   public ServerTickManager getTickManager() {
      return this.tickManager;
   }

   public long getAverageNanosPerTick() {
      return this.recentTickTimesNanos / (long)Math.min(100, Math.max(this.ticks, 1));
   }

   public long[] getTickTimes() {
      return this.tickTimes;
   }

   public int getPermissionLevel(GameProfile profile) {
      if (this.getPlayerManager().isOperator(profile)) {
         OperatorEntry operatorEntry = (OperatorEntry)this.getPlayerManager().getOpList().get(profile);
         if (operatorEntry != null) {
            return operatorEntry.getPermissionLevel();
         } else if (this.isHost(profile)) {
            return 4;
         } else if (this.isSingleplayer()) {
            return this.getPlayerManager().areCheatsAllowed() ? 4 : 0;
         } else {
            return this.getOpPermissionLevel();
         }
      } else {
         return 0;
      }
   }

   public abstract boolean isHost(GameProfile profile);

   public void dumpProperties(Path file) throws IOException {
   }

   private void dump(Path path) {
      Path path2 = path.resolve("levels");

      try {
         Iterator var3 = this.worlds.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry entry = (Map.Entry)var3.next();
            Identifier identifier = ((RegistryKey)entry.getKey()).getValue();
            Path path3 = path2.resolve(identifier.getNamespace()).resolve(identifier.getPath());
            Files.createDirectories(path3);
            ((ServerWorld)entry.getValue()).dump(path3);
         }

         this.dumpGamerules(path.resolve("gamerules.txt"));
         this.dumpClasspath(path.resolve("classpath.txt"));
         this.dumpStats(path.resolve("stats.txt"));
         this.dumpThreads(path.resolve("threads.txt"));
         this.dumpProperties(path.resolve("server.properties.txt"));
         this.dumpNativeModules(path.resolve("modules.txt"));
      } catch (IOException var7) {
         LOGGER.warn("Failed to save debug report", var7);
      }

   }

   private void dumpStats(Path path) throws IOException {
      Writer writer = Files.newBufferedWriter(path);

      try {
         writer.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getTaskCount()));
         writer.write(String.format(Locale.ROOT, "average_tick_time: %f\n", this.getAverageTickTime()));
         writer.write(String.format(Locale.ROOT, "tick_times: %s\n", Arrays.toString(this.tickTimes)));
         writer.write(String.format(Locale.ROOT, "queue: %s\n", Util.getMainWorkerExecutor()));
      } catch (Throwable var6) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpGamerules(Path path) throws IOException {
      Writer writer = Files.newBufferedWriter(path);

      try {
         final List list = Lists.newArrayList();
         final GameRules gameRules = this.getGameRules();
         gameRules.accept(new GameRules.Visitor(this) {
            public void visit(GameRules.Key key, GameRules.Type type) {
               list.add(String.format(Locale.ROOT, "%s=%s\n", key.getName(), gameRules.get(key)));
            }
         });
         Iterator var5 = list.iterator();

         while(var5.hasNext()) {
            String string = (String)var5.next();
            writer.write(string);
         }
      } catch (Throwable var8) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpClasspath(Path path) throws IOException {
      Writer writer = Files.newBufferedWriter(path);

      try {
         String string = System.getProperty("java.class.path");
         String string2 = System.getProperty("path.separator");
         Iterator var5 = Splitter.on(string2).split(string).iterator();

         while(var5.hasNext()) {
            String string3 = (String)var5.next();
            writer.write(string3);
            writer.write("\n");
         }
      } catch (Throwable var8) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpThreads(Path path) throws IOException {
      ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
      ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
      Arrays.sort(threadInfos, Comparator.comparing(ThreadInfo::getThreadName));
      Writer writer = Files.newBufferedWriter(path);

      try {
         ThreadInfo[] var5 = threadInfos;
         int var6 = threadInfos.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ThreadInfo threadInfo = var5[var7];
            writer.write(threadInfo.toString());
            writer.write(10);
         }
      } catch (Throwable var10) {
         if (writer != null) {
            try {
               writer.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private void dumpNativeModules(Path path) throws IOException {
      Writer writer = Files.newBufferedWriter(path);

      label50: {
         try {
            label51: {
               ArrayList list;
               try {
                  list = Lists.newArrayList(WinNativeModuleUtil.collectNativeModules());
               } catch (Throwable var7) {
                  LOGGER.warn("Failed to list native modules", var7);
                  break label51;
               }

               list.sort(Comparator.comparing((module) -> {
                  return module.path;
               }));
               Iterator var4 = list.iterator();

               while(true) {
                  if (!var4.hasNext()) {
                     break label50;
                  }

                  WinNativeModuleUtil.NativeModule nativeModule = (WinNativeModuleUtil.NativeModule)var4.next();
                  writer.write(nativeModule.toString());
                  writer.write(10);
               }
            }
         } catch (Throwable var8) {
            if (writer != null) {
               try {
                  writer.close();
               } catch (Throwable var6) {
                  var8.addSuppressed(var6);
               }
            }

            throw var8;
         }

         if (writer != null) {
            writer.close();
         }

         return;
      }

      if (writer != null) {
         writer.close();
      }

   }

   private Profiler startTickMetrics() {
      if (this.needsRecorderSetup) {
         this.recorder = DebugRecorder.of(new ServerSamplerSource(Util.nanoTimeSupplier, this.isDedicated()), Util.nanoTimeSupplier, Util.getIoWorkerExecutor(), new RecordDumper("server"), this.recorderResultConsumer, (path) -> {
            this.submitAndJoin(() -> {
               this.dump(path.resolve("server"));
            });
            this.recorderDumpConsumer.accept(path);
         });
         this.needsRecorderSetup = false;
      }

      this.recorder.startTick();
      return TickDurationMonitor.tickProfiler(this.recorder.getProfiler(), TickDurationMonitor.create("Server"));
   }

   public void endTickMetrics() {
      this.recorder.endTick();
   }

   public boolean isRecorderActive() {
      return this.recorder.isActive();
   }

   public void setupRecorder(Consumer resultConsumer, Consumer dumpConsumer) {
      this.recorderResultConsumer = (result) -> {
         this.resetRecorder();
         resultConsumer.accept(result);
      };
      this.recorderDumpConsumer = dumpConsumer;
      this.needsRecorderSetup = true;
   }

   public void resetRecorder() {
      this.recorder = DummyRecorder.INSTANCE;
   }

   public void stopRecorder() {
      this.recorder.stop();
   }

   public void forceStopRecorder() {
      this.recorder.forceStop();
   }

   public Path getSavePath(WorldSavePath worldSavePath) {
      return this.session.getDirectory(worldSavePath);
   }

   public boolean syncChunkWrites() {
      return true;
   }

   public StructureTemplateManager getStructureTemplateManager() {
      return this.structureTemplateManager;
   }

   public SaveProperties getSaveProperties() {
      return this.saveProperties;
   }

   public DynamicRegistryManager.Immutable getRegistryManager() {
      return this.combinedDynamicRegistries.getCombinedRegistryManager();
   }

   public CombinedDynamicRegistries getCombinedDynamicRegistries() {
      return this.combinedDynamicRegistries;
   }

   public ReloadableRegistries.Lookup getReloadableRegistries() {
      return this.resourceManagerHolder.dataPackContents.getReloadableRegistries();
   }

   public TextStream createFilterer(ServerPlayerEntity player) {
      return TextStream.UNFILTERED;
   }

   public ServerPlayerInteractionManager getPlayerInteractionManager(ServerPlayerEntity player) {
      return (ServerPlayerInteractionManager)(this.isDemo() ? new DemoServerPlayerInteractionManager(player) : new ServerPlayerInteractionManager(player));
   }

   @Nullable
   public GameMode getForcedGameMode() {
      return null;
   }

   public ResourceManager getResourceManager() {
      return this.resourceManagerHolder.resourceManager;
   }

   public boolean isSaving() {
      return this.saving;
   }

   public boolean isDebugRunning() {
      return this.needsDebugSetup || this.debugStart != null;
   }

   public void startDebug() {
      this.needsDebugSetup = true;
   }

   public ProfileResult stopDebug() {
      if (this.debugStart == null) {
         return EmptyProfileResult.INSTANCE;
      } else {
         ProfileResult profileResult = this.debugStart.end(Util.getMeasuringTimeNano(), this.ticks);
         this.debugStart = null;
         return profileResult;
      }
   }

   public int getMaxChainedNeighborUpdates() {
      return 1000000;
   }

   public void logChatMessage(Text message, MessageType.Parameters params, @Nullable String prefix) {
      String string = params.applyChatDecoration(message).getString();
      if (prefix != null) {
         LOGGER.info("[{}] {}", prefix, string);
      } else {
         LOGGER.info("{}", string);
      }

   }

   public MessageDecorator getMessageDecorator() {
      return MessageDecorator.NOOP;
   }

   public boolean shouldLogIps() {
      return true;
   }

   public void subscribeToDebugSample(ServerPlayerEntity player, DebugSampleType type) {
   }

   public void handleCustomClickAction(Identifier id, Optional payload) {
      LOGGER.debug("Received custom click action {} with payload {}", id, payload.orElse((Object)null));
   }

   public boolean acceptsTransfers() {
      return false;
   }

   private void writeChunkIoReport(CrashReport report, ChunkPos pos, StorageKey key) {
      Util.getIoWorkerExecutor().execute(() -> {
         try {
            Path path = this.getPath("debug");
            PathUtil.createDirectories(path);
            String string = PathUtil.replaceInvalidChars(key.level());
            Path path2 = path.resolve("chunk-" + string + "-" + Util.getFormattedCurrentTime() + "-server.txt");
            FileStore fileStore = Files.getFileStore(path);
            long l = fileStore.getUsableSpace();
            if (l < 8192L) {
               LOGGER.warn("Not storing chunk IO report due to low space on drive {}", fileStore.name());
               return;
            }

            CrashReportSection crashReportSection = report.addElement("Chunk Info");
            Objects.requireNonNull(key);
            crashReportSection.add("Level", key::level);
            crashReportSection.add("Dimension", () -> {
               return key.dimension().getValue().toString();
            });
            Objects.requireNonNull(key);
            crashReportSection.add("Storage", key::type);
            Objects.requireNonNull(pos);
            crashReportSection.add("Position", pos::toString);
            report.writeToFile(path2, ReportType.MINECRAFT_CHUNK_IO_ERROR_REPORT);
            LOGGER.info("Saved details to {}", report.getFile());
         } catch (Exception var11) {
            LOGGER.warn("Failed to store chunk IO exception", var11);
         }

      });
   }

   public void onChunkLoadFailure(Throwable exception, StorageKey key, ChunkPos chunkPos) {
      LOGGER.error("Failed to load chunk {},{}", new Object[]{chunkPos.x, chunkPos.z, exception});
      this.suppressedExceptionsTracker.onSuppressedException("chunk/load", exception);
      this.writeChunkIoReport(CrashReport.create(exception, "Chunk load failure"), chunkPos, key);
   }

   public void onChunkSaveFailure(Throwable exception, StorageKey key, ChunkPos chunkPos) {
      LOGGER.error("Failed to save chunk {},{}", new Object[]{chunkPos.x, chunkPos.z, exception});
      this.suppressedExceptionsTracker.onSuppressedException("chunk/save", exception);
      this.writeChunkIoReport(CrashReport.create(exception, "Chunk save failure"), chunkPos, key);
   }

   public void onPacketException(Throwable exception, PacketType type) {
      this.suppressedExceptionsTracker.onSuppressedException("packet/" + type.toString(), exception);
   }

   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.brewingRecipeRegistry;
   }

   public FuelRegistry getFuelRegistry() {
      return this.fuelRegistry;
   }

   public ServerLinks getServerLinks() {
      return ServerLinks.EMPTY;
   }

   protected int getPauseWhenEmptySeconds() {
      return 0;
   }

   // $FF: synthetic method
   public void executeTask(final Runnable task) {
      this.executeTask((ServerTask)task);
   }

   // $FF: synthetic method
   public boolean canExecute(final Runnable task) {
      return this.canExecute((ServerTask)task);
   }

   // $FF: synthetic method
   public Runnable createTask(final Runnable runnable) {
      return this.createTask(runnable);
   }

   static {
      OVERLOAD_THRESHOLD_NANOS = 20L * TimeHelper.SECOND_IN_NANOS / 20L;
      OVERLOAD_WARNING_INTERVAL_NANOS = 10L * TimeHelper.SECOND_IN_NANOS;
      PLAYER_SAMPLE_UPDATE_INTERVAL_NANOS = 5L * TimeHelper.SECOND_IN_NANOS;
      PREPARE_START_REGION_TICK_DELAY_NANOS = 10L * TimeHelper.MILLI_IN_NANOS;
      DEMO_LEVEL_INFO = new LevelInfo("Demo World", GameMode.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(FeatureFlags.DEFAULT_ENABLED_FEATURES), DataConfiguration.SAFE_MODE);
      ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
      WORLD_GEN_EXCEPTION = new AtomicReference();
   }

   private static record ResourceManagerHolder(LifecycledResourceManager resourceManager, DataPackContents dataPackContents) implements AutoCloseable {
      final LifecycledResourceManager resourceManager;
      final DataPackContents dataPackContents;

      ResourceManagerHolder(LifecycledResourceManager lifecycledResourceManager, DataPackContents dataPackContents) {
         this.resourceManager = lifecycledResourceManager;
         this.dataPackContents = dataPackContents;
      }

      public void close() {
         this.resourceManager.close();
      }

      public LifecycledResourceManager resourceManager() {
         return this.resourceManager;
      }

      public DataPackContents dataPackContents() {
         return this.dataPackContents;
      }
   }

   static class DebugStart {
      final long time;
      final int tick;

      DebugStart(long time, int tick) {
         this.time = time;
         this.tick = tick;
      }

      ProfileResult end(final long endTime, final int endTick) {
         return new ProfileResult() {
            public List getTimings(String parentPath) {
               return Collections.emptyList();
            }

            public boolean save(Path path) {
               return false;
            }

            public long getStartTime() {
               return DebugStart.this.time;
            }

            public int getStartTick() {
               return DebugStart.this.tick;
            }

            public long getEndTime() {
               return endTime;
            }

            public int getEndTick() {
               return endTick;
            }

            public String getRootTimings() {
               return "";
            }
         };
      }
   }

   public static record ServerResourcePackProperties(UUID id, String url, String hash, boolean isRequired, @Nullable Text prompt) {
      public ServerResourcePackProperties(UUID uUID, String string, String string2, boolean bl, @Nullable Text text) {
         this.id = uUID;
         this.url = string;
         this.hash = string2;
         this.isRequired = bl;
         this.prompt = text;
      }

      public UUID id() {
         return this.id;
      }

      public String url() {
         return this.url;
      }

      public String hash() {
         return this.hash;
      }

      public boolean isRequired() {
         return this.isRequired;
      }

      @Nullable
      public Text prompt() {
         return this.prompt;
      }
   }
}
