package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChunkBiomeDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import net.minecraft.util.collection.BoundedRegionArray;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.thread.SimpleConsecutiveExecutor;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.ChunkLoadingManager;
import net.minecraft.world.GameRules;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkGenerationContext;
import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkLoader;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatusChangeListener;
import net.minecraft.world.chunk.ChunkType;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.SerializedChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.StorageKey;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerChunkLoadingManager extends VersionedChunkStorage implements ChunkHolder.PlayersWatchingChunkProvider, ChunkLoadingManager {
   private static final OptionalChunk UNLOADED_CHUNKS = OptionalChunk.of("Unloaded chunks found in range");
   private static final CompletableFuture UNLOADED_CHUNKS_FUTURE;
   private static final byte PROTO_CHUNK = -1;
   private static final byte UNMARKED_CHUNK = 0;
   private static final byte LEVEL_CHUNK = 1;
   private static final Logger LOGGER;
   private static final int field_29674 = 200;
   private static final int field_36291 = 20;
   private static final int field_36384 = 10000;
   private static final int field_54966 = 128;
   public static final int DEFAULT_VIEW_DISTANCE = 2;
   public static final int field_29669 = 32;
   public static final int FORCED_CHUNK_LEVEL;
   private final Long2ObjectLinkedOpenHashMap currentChunkHolders = new Long2ObjectLinkedOpenHashMap();
   private volatile Long2ObjectLinkedOpenHashMap chunkHolders;
   private final Long2ObjectLinkedOpenHashMap chunksToUnload;
   private final List loaders;
   final ServerWorld world;
   private final ServerLightingProvider lightingProvider;
   private final ThreadExecutor mainThreadExecutor;
   private final NoiseConfig noiseConfig;
   private final StructurePlacementCalculator structurePlacementCalculator;
   private final Supplier persistentStateManagerFactory;
   private final ChunkTicketManager ticketManager;
   private final PointOfInterestStorage pointOfInterestStorage;
   final LongSet unloadedChunks;
   private boolean chunkHolderListDirty;
   private final ChunkTaskScheduler worldGenScheduler;
   private final ChunkTaskScheduler lightScheduler;
   private final WorldGenerationProgressListener worldGenerationProgressListener;
   private final ChunkStatusChangeListener chunkStatusChangeListener;
   private final LevelManager levelManager;
   private final AtomicInteger totalChunksLoadedCount;
   private final String saveDir;
   private final PlayerChunkWatchingManager playerChunkWatchingManager;
   private final Int2ObjectMap entityTrackers;
   private final Long2ByteMap chunkToType;
   private final Long2LongMap chunkToNextSaveTimeMs;
   private final LongSet chunksToSave;
   private final Queue unloadTaskQueue;
   private final AtomicInteger chunksBeingSavedCount;
   private int watchDistance;
   private final ChunkGenerationContext generationContext;

   public ServerChunkLoadingManager(ServerWorld world, LevelStorage.Session session, DataFixer dataFixer, StructureTemplateManager structureTemplateManager, Executor executor, ThreadExecutor mainThreadExecutor, ChunkProvider chunkProvider, ChunkGenerator chunkGenerator, WorldGenerationProgressListener worldGenerationProgressListener, ChunkStatusChangeListener chunkStatusChangeListener, Supplier persistentStateManagerFactory, ChunkTicketManager ticketManager, int viewDistance, boolean dsync) {
      super(new StorageKey(session.getDirectoryName(), world.getRegistryKey(), "chunk"), session.getWorldDirectory(world.getRegistryKey()).resolve("region"), dataFixer, dsync);
      this.chunkHolders = this.currentChunkHolders.clone();
      this.chunksToUnload = new Long2ObjectLinkedOpenHashMap();
      this.loaders = new ArrayList();
      this.unloadedChunks = new LongOpenHashSet();
      this.totalChunksLoadedCount = new AtomicInteger();
      this.playerChunkWatchingManager = new PlayerChunkWatchingManager();
      this.entityTrackers = new Int2ObjectOpenHashMap();
      this.chunkToType = new Long2ByteOpenHashMap();
      this.chunkToNextSaveTimeMs = new Long2LongOpenHashMap();
      this.chunksToSave = new LongLinkedOpenHashSet();
      this.unloadTaskQueue = Queues.newConcurrentLinkedQueue();
      this.chunksBeingSavedCount = new AtomicInteger();
      Path path = session.getWorldDirectory(world.getRegistryKey());
      this.saveDir = path.getFileName().toString();
      this.world = world;
      DynamicRegistryManager dynamicRegistryManager = world.getRegistryManager();
      long l = world.getSeed();
      if (chunkGenerator instanceof NoiseChunkGenerator noiseChunkGenerator) {
         this.noiseConfig = NoiseConfig.create((ChunkGeneratorSettings)((ChunkGeneratorSettings)noiseChunkGenerator.getSettings().value()), (RegistryEntryLookup)dynamicRegistryManager.getOrThrow(RegistryKeys.NOISE_PARAMETERS), l);
      } else {
         this.noiseConfig = NoiseConfig.create((ChunkGeneratorSettings)ChunkGeneratorSettings.createMissingSettings(), (RegistryEntryLookup)dynamicRegistryManager.getOrThrow(RegistryKeys.NOISE_PARAMETERS), l);
      }

      this.structurePlacementCalculator = chunkGenerator.createStructurePlacementCalculator(dynamicRegistryManager.getOrThrow(RegistryKeys.STRUCTURE_SET), this.noiseConfig, l);
      this.mainThreadExecutor = mainThreadExecutor;
      SimpleConsecutiveExecutor simpleConsecutiveExecutor = new SimpleConsecutiveExecutor(executor, "worldgen");
      this.worldGenerationProgressListener = worldGenerationProgressListener;
      this.chunkStatusChangeListener = chunkStatusChangeListener;
      SimpleConsecutiveExecutor simpleConsecutiveExecutor2 = new SimpleConsecutiveExecutor(executor, "light");
      this.worldGenScheduler = new ChunkTaskScheduler(simpleConsecutiveExecutor, executor);
      this.lightScheduler = new ChunkTaskScheduler(simpleConsecutiveExecutor2, executor);
      this.lightingProvider = new ServerLightingProvider(chunkProvider, this, this.world.getDimension().hasSkyLight(), simpleConsecutiveExecutor2, this.lightScheduler);
      this.levelManager = new LevelManager(ticketManager, executor, mainThreadExecutor);
      this.persistentStateManagerFactory = persistentStateManagerFactory;
      this.ticketManager = ticketManager;
      this.pointOfInterestStorage = new PointOfInterestStorage(new StorageKey(session.getDirectoryName(), world.getRegistryKey(), "poi"), path.resolve("poi"), dataFixer, dsync, dynamicRegistryManager, world.getServer(), world);
      this.setViewDistance(viewDistance);
      this.generationContext = new ChunkGenerationContext(world, chunkGenerator, structureTemplateManager, this.lightingProvider, mainThreadExecutor, this::markChunkNeedsSaving);
   }

   private void markChunkNeedsSaving(ChunkPos pos) {
      this.chunksToSave.add(pos.toLong());
   }

   protected ChunkGenerator getChunkGenerator() {
      return this.generationContext.generator();
   }

   protected StructurePlacementCalculator getStructurePlacementCalculator() {
      return this.structurePlacementCalculator;
   }

   protected NoiseConfig getNoiseConfig() {
      return this.noiseConfig;
   }

   boolean isTracked(ServerPlayerEntity player, int chunkX, int chunkZ) {
      return player.getChunkFilter().isWithinDistance(chunkX, chunkZ) && !player.networkHandler.chunkDataSender.isInNextBatch(ChunkPos.toLong(chunkX, chunkZ));
   }

   private boolean isOnTrackEdge(ServerPlayerEntity player, int chunkX, int chunkZ) {
      if (!this.isTracked(player, chunkX, chunkZ)) {
         return false;
      } else {
         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               if ((i != 0 || j != 0) && !this.isTracked(player, chunkX + i, chunkZ + j)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   protected ServerLightingProvider getLightingProvider() {
      return this.lightingProvider;
   }

   @Nullable
   public ChunkHolder getCurrentChunkHolder(long pos) {
      return (ChunkHolder)this.currentChunkHolders.get(pos);
   }

   @Nullable
   protected ChunkHolder getChunkHolder(long pos) {
      return (ChunkHolder)this.chunkHolders.get(pos);
   }

   protected IntSupplier getCompletedLevelSupplier(long pos) {
      return () -> {
         ChunkHolder chunkHolder = this.getChunkHolder(pos);
         return chunkHolder == null ? LevelPrioritizedQueue.LEVEL_COUNT - 1 : Math.min(chunkHolder.getCompletedLevel(), LevelPrioritizedQueue.LEVEL_COUNT - 1);
      };
   }

   public String getChunkLoadingDebugInfo(ChunkPos chunkPos) {
      ChunkHolder chunkHolder = this.getChunkHolder(chunkPos.toLong());
      if (chunkHolder == null) {
         return "null";
      } else {
         String string = chunkHolder.getLevel() + "\n";
         ChunkStatus chunkStatus = chunkHolder.getLatestStatus();
         Chunk chunk = chunkHolder.getLatest();
         if (chunkStatus != null) {
            string = string + "St: §" + chunkStatus.getIndex() + String.valueOf(chunkStatus) + "§r\n";
         }

         if (chunk != null) {
            string = string + "Ch: §" + chunk.getStatus().getIndex() + String.valueOf(chunk.getStatus()) + "§r\n";
         }

         ChunkLevelType chunkLevelType = chunkHolder.getLevelType();
         string = string + String.valueOf('§') + chunkLevelType.ordinal() + String.valueOf(chunkLevelType);
         return string + "§r";
      }
   }

   private CompletableFuture getRegion(ChunkHolder centerChunk, int margin, IntFunction distanceToStatus) {
      if (margin == 0) {
         ChunkStatus chunkStatus = (ChunkStatus)distanceToStatus.apply(0);
         return centerChunk.load(chunkStatus, this).thenApply((chunk) -> {
            return chunk.map(List::of);
         });
      } else {
         int i = MathHelper.square(margin * 2 + 1);
         List list = new ArrayList(i);
         ChunkPos chunkPos = centerChunk.getPos();

         for(int j = -margin; j <= margin; ++j) {
            for(int k = -margin; k <= margin; ++k) {
               int l = Math.max(Math.abs(k), Math.abs(j));
               long m = ChunkPos.toLong(chunkPos.x + k, chunkPos.z + j);
               ChunkHolder chunkHolder = this.getCurrentChunkHolder(m);
               if (chunkHolder == null) {
                  return UNLOADED_CHUNKS_FUTURE;
               }

               ChunkStatus chunkStatus2 = (ChunkStatus)distanceToStatus.apply(l);
               list.add(chunkHolder.load(chunkStatus2, this));
            }
         }

         return Util.combineSafe(list).thenApply((chunks) -> {
            List list = new ArrayList(chunks.size());
            Iterator var3 = chunks.iterator();

            while(var3.hasNext()) {
               OptionalChunk optionalChunk = (OptionalChunk)var3.next();
               if (optionalChunk == null) {
                  throw this.crash(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
               }

               Chunk chunk = (Chunk)optionalChunk.orElse((Object)null);
               if (chunk == null) {
                  return UNLOADED_CHUNKS;
               }

               list.add(chunk);
            }

            return OptionalChunk.of((Object)list);
         });
      }
   }

   public CrashException crash(IllegalStateException exception, String details) {
      StringBuilder stringBuilder = new StringBuilder();
      Consumer consumer = (chunkHolder) -> {
         chunkHolder.enumerateFutures().forEach((pair) -> {
            ChunkStatus chunkStatus = (ChunkStatus)pair.getFirst();
            CompletableFuture completableFuture = (CompletableFuture)pair.getSecond();
            if (completableFuture != null && completableFuture.isDone() && completableFuture.join() == null) {
               stringBuilder.append(chunkHolder.getPos()).append(" - status: ").append(chunkStatus).append(" future: ").append(completableFuture).append(System.lineSeparator());
            }

         });
      };
      stringBuilder.append("Updating:").append(System.lineSeparator());
      this.currentChunkHolders.values().forEach(consumer);
      stringBuilder.append("Visible:").append(System.lineSeparator());
      this.chunkHolders.values().forEach(consumer);
      CrashReport crashReport = CrashReport.create(exception, "Chunk loading");
      CrashReportSection crashReportSection = crashReport.addElement("Chunk loading");
      crashReportSection.add("Details", (Object)details);
      crashReportSection.add("Futures", (Object)stringBuilder);
      return new CrashException(crashReport);
   }

   public CompletableFuture makeChunkEntitiesTickable(ChunkHolder holder) {
      return this.getRegion(holder, 2, (distance) -> {
         return ChunkStatus.FULL;
      }).thenApply((chunk) -> {
         return chunk.map((chunks) -> {
            return (WorldChunk)chunks.get(chunks.size() / 2);
         });
      });
   }

   @Nullable
   ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
      if (!ChunkLevels.isAccessible(i) && !ChunkLevels.isAccessible(level)) {
         return holder;
      } else {
         if (holder != null) {
            holder.setLevel(level);
         }

         if (holder != null) {
            if (!ChunkLevels.isAccessible(level)) {
               this.unloadedChunks.add(pos);
            } else {
               this.unloadedChunks.remove(pos);
            }
         }

         if (ChunkLevels.isAccessible(level) && holder == null) {
            holder = (ChunkHolder)this.chunksToUnload.remove(pos);
            if (holder != null) {
               holder.setLevel(level);
            } else {
               holder = new ChunkHolder(new ChunkPos(pos), level, this.world, this.lightingProvider, this::updateLevel, this);
            }

            this.currentChunkHolders.put(pos, holder);
            this.chunkHolderListDirty = true;
         }

         return holder;
      }
   }

   private void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter) {
      this.worldGenScheduler.updateLevel(pos, levelGetter, targetLevel, levelSetter);
      this.lightScheduler.updateLevel(pos, levelGetter, targetLevel, levelSetter);
   }

   public void close() throws IOException {
      try {
         this.worldGenScheduler.close();
         this.lightScheduler.close();
         this.pointOfInterestStorage.close();
      } finally {
         super.close();
      }

   }

   protected void save(boolean flush) {
      if (flush) {
         List list = this.chunkHolders.values().stream().filter(ChunkHolder::isAccessible).peek(ChunkHolder::updateAccessibleStatus).toList();
         MutableBoolean mutableBoolean = new MutableBoolean();

         do {
            mutableBoolean.setFalse();
            list.stream().map((holder) -> {
               ThreadExecutor var10000 = this.mainThreadExecutor;
               Objects.requireNonNull(holder);
               var10000.runTasks(holder::isSavable);
               return holder.getLatest();
            }).filter((chunk) -> {
               return chunk instanceof WrapperProtoChunk || chunk instanceof WorldChunk;
            }).filter(this::save).forEach((chunk) -> {
               mutableBoolean.setTrue();
            });
         } while(mutableBoolean.isTrue());

         this.pointOfInterestStorage.save();
         this.unloadChunks(() -> {
            return true;
         });
         this.completeAll();
      } else {
         this.chunkToNextSaveTimeMs.clear();
         long l = Util.getMeasuringTimeMs();
         ObjectIterator var4 = this.chunkHolders.values().iterator();

         while(var4.hasNext()) {
            ChunkHolder chunkHolder = (ChunkHolder)var4.next();
            this.save(chunkHolder, l);
         }
      }

   }

   protected void tick(BooleanSupplier shouldKeepTicking) {
      Profiler profiler = Profilers.get();
      profiler.push("poi");
      this.pointOfInterestStorage.tick(shouldKeepTicking);
      profiler.swap("chunk_unload");
      if (!this.world.isSavingDisabled()) {
         this.unloadChunks(shouldKeepTicking);
      }

      profiler.pop();
   }

   public boolean shouldDelayShutdown() {
      return this.lightingProvider.hasUpdates() || !this.chunksToUnload.isEmpty() || !this.currentChunkHolders.isEmpty() || this.pointOfInterestStorage.hasUnsavedElements() || !this.unloadedChunks.isEmpty() || !this.unloadTaskQueue.isEmpty() || this.worldGenScheduler.shouldDelayShutdown() || this.lightScheduler.shouldDelayShutdown() || this.levelManager.shouldDelayShutdown();
   }

   private void unloadChunks(BooleanSupplier shouldKeepTicking) {
      for(LongIterator longIterator = this.unloadedChunks.iterator(); longIterator.hasNext(); longIterator.remove()) {
         long l = longIterator.nextLong();
         ChunkHolder chunkHolder = (ChunkHolder)this.currentChunkHolders.get(l);
         if (chunkHolder != null) {
            this.currentChunkHolders.remove(l);
            this.chunksToUnload.put(l, chunkHolder);
            this.chunkHolderListDirty = true;
            this.tryUnloadChunk(l, chunkHolder);
         }
      }

      int i = Math.max(0, this.unloadTaskQueue.size() - 2000);

      Runnable runnable;
      while((i > 0 || shouldKeepTicking.getAsBoolean()) && (runnable = (Runnable)this.unloadTaskQueue.poll()) != null) {
         --i;
         runnable.run();
      }

      this.saveChunks(shouldKeepTicking);
   }

   private void saveChunks(BooleanSupplier shouldKeepTicking) {
      long l = Util.getMeasuringTimeMs();
      int i = 0;
      LongIterator longIterator = this.chunksToSave.iterator();

      while(i < 20 && this.chunksBeingSavedCount.get() < 128 && shouldKeepTicking.getAsBoolean() && longIterator.hasNext()) {
         long m = longIterator.nextLong();
         ChunkHolder chunkHolder = (ChunkHolder)this.chunkHolders.get(m);
         Chunk chunk = chunkHolder != null ? chunkHolder.getLatest() : null;
         if (chunk != null && chunk.needsSaving()) {
            if (this.save(chunkHolder, l)) {
               ++i;
               longIterator.remove();
            }
         } else {
            longIterator.remove();
         }
      }

   }

   private void tryUnloadChunk(long pos, ChunkHolder chunk) {
      CompletableFuture completableFuture = chunk.getSavingFuture();
      Runnable var10001 = () -> {
         CompletableFuture completableFuture2 = chunk.getSavingFuture();
         if (completableFuture2 != completableFuture) {
            this.tryUnloadChunk(pos, chunk);
         } else {
            Chunk chunkx = chunk.getLatest();
            if (this.chunksToUnload.remove(pos, chunk) && chunkx != null) {
               WorldChunk worldChunk;
               if (chunkx instanceof WorldChunk) {
                  worldChunk = (WorldChunk)chunkx;
                  worldChunk.setLoadedToWorld(false);
               }

               this.save(chunkx);
               if (chunkx instanceof WorldChunk) {
                  worldChunk = (WorldChunk)chunkx;
                  this.world.unloadEntities(worldChunk);
               }

               this.lightingProvider.updateChunkStatus(chunkx.getPos());
               this.lightingProvider.tick();
               this.worldGenerationProgressListener.setChunkStatus(chunkx.getPos(), (ChunkStatus)null);
               this.chunkToNextSaveTimeMs.remove(chunkx.getPos().toLong());
            }

         }
      };
      Queue var10002 = this.unloadTaskQueue;
      Objects.requireNonNull(var10002);
      completableFuture.thenRunAsync(var10001, var10002::add).whenComplete((void_, throwable) -> {
         if (throwable != null) {
            LOGGER.error("Failed to save chunk {}", chunk.getPos(), throwable);
         }

      });
   }

   protected boolean updateHolderMap() {
      if (!this.chunkHolderListDirty) {
         return false;
      } else {
         this.chunkHolders = this.currentChunkHolders.clone();
         this.chunkHolderListDirty = false;
         return true;
      }
   }

   private CompletableFuture loadChunk(ChunkPos pos) {
      CompletableFuture completableFuture = this.getUpdatedChunkNbt(pos).thenApplyAsync((optional) -> {
         return optional.map((nbtCompound) -> {
            SerializedChunk serializedChunk = SerializedChunk.fromNbt(this.world, this.world.getRegistryManager(), nbtCompound);
            if (serializedChunk == null) {
               LOGGER.error("Chunk file at {} is missing level data, skipping", pos);
            }

            return serializedChunk;
         });
      }, Util.getMainWorkerExecutor().named("parseChunk"));
      CompletableFuture completableFuture2 = this.pointOfInterestStorage.load(pos);
      return completableFuture.thenCombine(completableFuture2, (optional, object) -> {
         return optional;
      }).thenApplyAsync((nbt) -> {
         Profilers.get().visit("chunkLoad");
         if (nbt.isPresent()) {
            Chunk chunk = ((SerializedChunk)nbt.get()).convert(this.world, this.pointOfInterestStorage, this.getStorageKey(), pos);
            this.mark(pos, chunk.getStatus().getChunkType());
            return chunk;
         } else {
            return this.getProtoChunk(pos);
         }
      }, this.mainThreadExecutor).exceptionallyAsync((throwable) -> {
         return this.recoverFromException(throwable, pos);
      }, this.mainThreadExecutor);
   }

   private Chunk recoverFromException(Throwable throwable, ChunkPos chunkPos) {
      Throwable var10000;
      if (throwable instanceof CompletionException completionException) {
         var10000 = completionException.getCause();
      } else {
         var10000 = throwable;
      }

      Throwable throwable2 = var10000;
      if (throwable2 instanceof CrashException crashException) {
         var10000 = crashException.getCause();
      } else {
         var10000 = throwable2;
      }

      Throwable throwable3 = var10000;
      boolean bl = throwable3 instanceof Error;
      boolean bl2 = throwable3 instanceof IOException || throwable3 instanceof NbtException;
      if (!bl) {
         if (!bl2) {
         }

         this.world.getServer().onChunkLoadFailure(throwable3, this.getStorageKey(), chunkPos);
         return this.getProtoChunk(chunkPos);
      } else {
         CrashReport crashReport = CrashReport.create(throwable, "Exception loading chunk");
         CrashReportSection crashReportSection = crashReport.addElement("Chunk being loaded");
         crashReportSection.add("pos", (Object)chunkPos);
         this.markAsProtoChunk(chunkPos);
         throw new CrashException(crashReport);
      }
   }

   private Chunk getProtoChunk(ChunkPos chunkPos) {
      this.markAsProtoChunk(chunkPos);
      return new ProtoChunk(chunkPos, UpgradeData.NO_UPGRADE_DATA, this.world, this.world.getRegistryManager().getOrThrow(RegistryKeys.BIOME), (BlendingData)null);
   }

   private void markAsProtoChunk(ChunkPos pos) {
      this.chunkToType.put(pos.toLong(), (byte)-1);
   }

   private byte mark(ChunkPos pos, ChunkType type) {
      return this.chunkToType.put(pos.toLong(), (byte)(type == ChunkType.PROTOCHUNK ? -1 : 1));
   }

   public AbstractChunkHolder acquire(long pos) {
      ChunkHolder chunkHolder = (ChunkHolder)this.currentChunkHolders.get(pos);
      chunkHolder.incrementRefCount();
      return chunkHolder;
   }

   public void release(AbstractChunkHolder chunkHolder) {
      chunkHolder.decrementRefCount();
   }

   public CompletableFuture generate(AbstractChunkHolder chunkHolder, ChunkGenerationStep step, BoundedRegionArray chunks) {
      ChunkPos chunkPos = chunkHolder.getPos();
      if (step.targetStatus() == ChunkStatus.EMPTY) {
         return this.loadChunk(chunkPos);
      } else {
         try {
            AbstractChunkHolder abstractChunkHolder = (AbstractChunkHolder)chunks.get(chunkPos.x, chunkPos.z);
            Chunk chunk = abstractChunkHolder.getUncheckedOrNull(step.targetStatus().getPrevious());
            if (chunk == null) {
               throw new IllegalStateException("Parent chunk missing");
            } else {
               CompletableFuture completableFuture = step.run(this.generationContext, chunks, chunk);
               this.worldGenerationProgressListener.setChunkStatus(chunkPos, step.targetStatus());
               return completableFuture;
            }
         } catch (Exception var8) {
            var8.getStackTrace();
            CrashReport crashReport = CrashReport.create(var8, "Exception generating new chunk");
            CrashReportSection crashReportSection = crashReport.addElement("Chunk to be generated");
            crashReportSection.add("Status being generated", () -> {
               return step.targetStatus().getId();
            });
            crashReportSection.add("Location", (Object)String.format(Locale.ROOT, "%d,%d", chunkPos.x, chunkPos.z));
            crashReportSection.add("Position hash", (Object)ChunkPos.toLong(chunkPos.x, chunkPos.z));
            crashReportSection.add("Generator", (Object)this.getChunkGenerator());
            this.mainThreadExecutor.execute(() -> {
               throw new CrashException(crashReport);
            });
            throw new CrashException(crashReport);
         }
      }
   }

   public ChunkLoader createLoader(ChunkStatus requestedStatus, ChunkPos pos) {
      ChunkLoader chunkLoader = ChunkLoader.create(this, requestedStatus, pos);
      this.loaders.add(chunkLoader);
      return chunkLoader;
   }

   private void schedule(ChunkLoader loader) {
      AbstractChunkHolder abstractChunkHolder = loader.getHolder();
      ChunkTaskScheduler var10000 = this.worldGenScheduler;
      Runnable var10001 = () -> {
         CompletableFuture completableFuture = loader.run();
         if (completableFuture != null) {
            completableFuture.thenRun(() -> {
               this.schedule(loader);
            });
         }
      };
      long var10002 = abstractChunkHolder.getPos().toLong();
      Objects.requireNonNull(abstractChunkHolder);
      var10000.add(var10001, var10002, abstractChunkHolder::getCompletedLevel);
   }

   public void updateChunks() {
      this.loaders.forEach(this::schedule);
      this.loaders.clear();
   }

   public CompletableFuture makeChunkTickable(ChunkHolder holder) {
      CompletableFuture completableFuture = this.getRegion(holder, 1, (distance) -> {
         return ChunkStatus.FULL;
      });
      CompletableFuture completableFuture2 = completableFuture.thenApplyAsync((optionalChunk) -> {
         return optionalChunk.map((chunks) -> {
            WorldChunk worldChunk = (WorldChunk)chunks.get(chunks.size() / 2);
            worldChunk.runPostProcessing(this.world);
            this.world.disableTickSchedulers(worldChunk);
            CompletableFuture completableFuture = holder.getPostProcessingFuture();
            if (completableFuture.isDone()) {
               this.sendToPlayers(holder, worldChunk);
            } else {
               completableFuture.thenAcceptAsync((object) -> {
                  this.sendToPlayers(holder, worldChunk);
               }, this.mainThreadExecutor);
            }

            return worldChunk;
         });
      }, this.mainThreadExecutor);
      completableFuture2.handle((chunk, throwable) -> {
         this.totalChunksLoadedCount.getAndIncrement();
         return null;
      });
      return completableFuture2;
   }

   private void sendToPlayers(ChunkHolder chunkHolder, WorldChunk chunk) {
      ChunkPos chunkPos = chunk.getPos();
      Iterator var4 = this.playerChunkWatchingManager.getPlayersWatchingChunk().iterator();

      while(var4.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
         if (serverPlayerEntity.getChunkFilter().isWithinDistance(chunkPos)) {
            track(serverPlayerEntity, chunk);
         }
      }

      this.world.getChunkManager().markForUpdate(chunkHolder);
   }

   public CompletableFuture makeChunkAccessible(ChunkHolder holder) {
      return this.getRegion(holder, 1, ChunkLevels::getStatusForAdditionalLevel).thenApply((optionalChunks) -> {
         return optionalChunks.map((chunks) -> {
            return (WorldChunk)chunks.get(chunks.size() / 2);
         });
      });
   }

   public int getTotalChunksLoadedCount() {
      return this.totalChunksLoadedCount.get();
   }

   private boolean save(ChunkHolder chunkHolder, long currentTime) {
      if (chunkHolder.isAccessible() && chunkHolder.isSavable()) {
         Chunk chunk = chunkHolder.getLatest();
         if (!(chunk instanceof WrapperProtoChunk) && !(chunk instanceof WorldChunk)) {
            return false;
         } else if (!chunk.needsSaving()) {
            return false;
         } else {
            long l = chunk.getPos().toLong();
            long m = this.chunkToNextSaveTimeMs.getOrDefault(l, -1L);
            if (currentTime < m) {
               return false;
            } else {
               boolean bl = this.save(chunk);
               chunkHolder.updateAccessibleStatus();
               if (bl) {
                  this.chunkToNextSaveTimeMs.put(l, currentTime + 10000L);
               }

               return bl;
            }
         }
      } else {
         return false;
      }
   }

   private boolean save(Chunk chunk) {
      this.pointOfInterestStorage.saveChunk(chunk.getPos());
      if (!chunk.tryMarkSaved()) {
         return false;
      } else {
         ChunkPos chunkPos = chunk.getPos();

         try {
            ChunkStatus chunkStatus = chunk.getStatus();
            if (chunkStatus.getChunkType() != ChunkType.LEVELCHUNK) {
               if (this.isLevelChunk(chunkPos)) {
                  return false;
               }

               if (chunkStatus == ChunkStatus.EMPTY && chunk.getStructureStarts().values().stream().noneMatch(StructureStart::hasChildren)) {
                  return false;
               }
            }

            Profilers.get().visit("chunkSave");
            this.chunksBeingSavedCount.incrementAndGet();
            SerializedChunk serializedChunk = SerializedChunk.fromChunk(this.world, chunk);
            Objects.requireNonNull(serializedChunk);
            CompletableFuture completableFuture = CompletableFuture.supplyAsync(serializedChunk::serialize, Util.getMainWorkerExecutor());
            Objects.requireNonNull(completableFuture);
            this.setNbt(chunkPos, completableFuture::join).handle((void_, exception) -> {
               if (exception != null) {
                  this.world.getServer().onChunkSaveFailure(exception, this.getStorageKey(), chunkPos);
               }

               this.chunksBeingSavedCount.decrementAndGet();
               return null;
            });
            this.mark(chunkPos, chunkStatus.getChunkType());
            return true;
         } catch (Exception var6) {
            this.world.getServer().onChunkSaveFailure(var6, this.getStorageKey(), chunkPos);
            return false;
         }
      }
   }

   private boolean isLevelChunk(ChunkPos pos) {
      byte b = this.chunkToType.get(pos.toLong());
      if (b != 0) {
         return b == 1;
      } else {
         NbtCompound nbtCompound;
         try {
            nbtCompound = (NbtCompound)((Optional)this.getUpdatedChunkNbt(pos).join()).orElse((Object)null);
            if (nbtCompound == null) {
               this.markAsProtoChunk(pos);
               return false;
            }
         } catch (Exception var5) {
            LOGGER.error("Failed to read chunk {}", pos, var5);
            this.markAsProtoChunk(pos);
            return false;
         }

         ChunkType chunkType = SerializedChunk.getChunkStatus(nbtCompound).getChunkType();
         return this.mark(pos, chunkType) == 1;
      }
   }

   protected void setViewDistance(int watchDistance) {
      int i = MathHelper.clamp(watchDistance, 2, 32);
      if (i != this.watchDistance) {
         this.watchDistance = i;
         this.levelManager.setWatchDistance(this.watchDistance);
         Iterator var3 = this.playerChunkWatchingManager.getPlayersWatchingChunk().iterator();

         while(var3.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var3.next();
            this.sendWatchPackets(serverPlayerEntity);
         }
      }

   }

   int getViewDistance(ServerPlayerEntity player) {
      return MathHelper.clamp(player.getViewDistance(), 2, this.watchDistance);
   }

   private void track(ServerPlayerEntity player, ChunkPos pos) {
      WorldChunk worldChunk = this.getPostProcessedChunk(pos.toLong());
      if (worldChunk != null) {
         track(player, worldChunk);
      }

   }

   private static void track(ServerPlayerEntity player, WorldChunk chunk) {
      player.networkHandler.chunkDataSender.add(chunk);
   }

   private static void untrack(ServerPlayerEntity player, ChunkPos pos) {
      player.networkHandler.chunkDataSender.unload(player, pos);
   }

   @Nullable
   public WorldChunk getPostProcessedChunk(long pos) {
      ChunkHolder chunkHolder = this.getChunkHolder(pos);
      return chunkHolder == null ? null : chunkHolder.getPostProcessedChunk();
   }

   public int getLoadedChunkCount() {
      return this.chunkHolders.size();
   }

   public ChunkLevelManager getLevelManager() {
      return this.levelManager;
   }

   protected Iterable entryIterator() {
      return Iterables.unmodifiableIterable(this.chunkHolders.values());
   }

   void dump(Writer writer) throws IOException {
      CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("block_entity_count").addColumn("ticking_ticket").addColumn("ticking_level").addColumn("block_ticks").addColumn("fluid_ticks").startBody(writer);
      ObjectBidirectionalIterator var3 = this.chunkHolders.long2ObjectEntrySet().iterator();

      while(var3.hasNext()) {
         Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)var3.next();
         long l = entry.getLongKey();
         ChunkPos chunkPos = new ChunkPos(l);
         ChunkHolder chunkHolder = (ChunkHolder)entry.getValue();
         Optional optional = Optional.ofNullable(chunkHolder.getLatest());
         Optional optional2 = optional.flatMap((chunk) -> {
            return chunk instanceof WorldChunk ? Optional.of((WorldChunk)chunk) : Optional.empty();
         });
         csvWriter.printRow(chunkPos.x, chunkPos.z, chunkHolder.getLevel(), optional.isPresent(), optional.map(Chunk::getStatus).orElse((Object)null), optional2.map(WorldChunk::getLevelType).orElse((Object)null), getFutureStatus(chunkHolder.getAccessibleFuture()), getFutureStatus(chunkHolder.getTickingFuture()), getFutureStatus(chunkHolder.getEntityTickingFuture()), this.ticketManager.getDebugString(l, false), this.shouldTick(chunkPos), optional2.map((chunk) -> {
            return chunk.getBlockEntities().size();
         }).orElse(0), this.ticketManager.getDebugString(l, true), this.levelManager.getLevel(l, true), optional2.map((chunk) -> {
            return chunk.getBlockTickScheduler().getTickCount();
         }).orElse(0), optional2.map((chunk) -> {
            return chunk.getFluidTickScheduler().getTickCount();
         }).orElse(0));
      }

   }

   private static String getFutureStatus(CompletableFuture future) {
      try {
         OptionalChunk optionalChunk = (OptionalChunk)future.getNow((Object)null);
         if (optionalChunk != null) {
            return optionalChunk.isPresent() ? "done" : "unloaded";
         } else {
            return "not completed";
         }
      } catch (CompletionException var2) {
         return "failed " + var2.getCause().getMessage();
      } catch (CancellationException var3) {
         return "cancelled";
      }
   }

   private CompletableFuture getUpdatedChunkNbt(ChunkPos chunkPos) {
      return this.getNbt(chunkPos).thenApplyAsync((nbt) -> {
         return nbt.map(this::updateChunkNbt);
      }, Util.getMainWorkerExecutor().named("upgradeChunk"));
   }

   private NbtCompound updateChunkNbt(NbtCompound nbt) {
      return this.updateChunkNbt(this.world.getRegistryKey(), this.persistentStateManagerFactory, nbt, this.getChunkGenerator().getCodecKey());
   }

   void collectSpawningChunks(List chunks) {
      LongIterator longIterator = this.levelManager.iterateChunkPosToTick();

      while(longIterator.hasNext()) {
         ChunkHolder chunkHolder = (ChunkHolder)this.chunkHolders.get(longIterator.nextLong());
         if (chunkHolder != null) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk != null && this.isAnyPlayerTicking(chunkHolder.getPos())) {
               chunks.add(worldChunk);
            }
         }
      }

   }

   void forEachBlockTickingChunk(Consumer chunkConsumer) {
      this.levelManager.forEachBlockTickingChunk((chunkPos) -> {
         ChunkHolder chunkHolder = (ChunkHolder)this.chunkHolders.get(chunkPos);
         if (chunkHolder != null) {
            WorldChunk worldChunk = chunkHolder.getWorldChunk();
            if (worldChunk != null) {
               chunkConsumer.accept(worldChunk);
            }
         }
      });
   }

   boolean shouldTick(ChunkPos pos) {
      TriState triState = this.levelManager.shouldTick(pos.toLong());
      return triState == TriState.DEFAULT ? this.isAnyPlayerTicking(pos) : triState.asBoolean(true);
   }

   private boolean isAnyPlayerTicking(ChunkPos pos) {
      Iterator var2 = this.playerChunkWatchingManager.getPlayersWatchingChunk().iterator();

      ServerPlayerEntity serverPlayerEntity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         serverPlayerEntity = (ServerPlayerEntity)var2.next();
      } while(!this.canTickChunk(serverPlayerEntity, pos));

      return true;
   }

   public List getPlayersWatchingChunk(ChunkPos pos) {
      long l = pos.toLong();
      if (!this.levelManager.shouldTick(l).asBoolean(true)) {
         return List.of();
      } else {
         ImmutableList.Builder builder = ImmutableList.builder();
         Iterator var5 = this.playerChunkWatchingManager.getPlayersWatchingChunk().iterator();

         while(var5.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var5.next();
            if (this.canTickChunk(serverPlayerEntity, pos)) {
               builder.add(serverPlayerEntity);
            }
         }

         return builder.build();
      }
   }

   private boolean canTickChunk(ServerPlayerEntity player, ChunkPos pos) {
      if (player.isSpectator()) {
         return false;
      } else {
         double d = getSquaredDistance(pos, player.getPos());
         return d < 16384.0;
      }
   }

   private static double getSquaredDistance(ChunkPos pos, Vec3d vec3d) {
      double d = (double)ChunkSectionPos.getOffsetPos(pos.x, 8);
      double e = (double)ChunkSectionPos.getOffsetPos(pos.z, 8);
      double f = d - vec3d.x;
      double g = e - vec3d.z;
      return f * f + g * g;
   }

   private boolean doesNotGenerateChunks(ServerPlayerEntity player) {
      return player.isSpectator() && !this.world.getGameRules().getBoolean(GameRules.SPECTATORS_GENERATE_CHUNKS);
   }

   void handlePlayerAddedOrRemoved(ServerPlayerEntity player, boolean added) {
      boolean bl = this.doesNotGenerateChunks(player);
      boolean bl2 = this.playerChunkWatchingManager.isWatchInactive(player);
      if (added) {
         this.playerChunkWatchingManager.add(player, bl);
         this.updateWatchedSection(player);
         if (!bl) {
            this.levelManager.handleChunkEnter(ChunkSectionPos.from((EntityLike)player), player);
         }

         player.setChunkFilter(ChunkFilter.IGNORE_ALL);
         this.sendWatchPackets(player);
      } else {
         ChunkSectionPos chunkSectionPos = player.getWatchedSection();
         this.playerChunkWatchingManager.remove(player);
         if (!bl2) {
            this.levelManager.handleChunkLeave(chunkSectionPos, player);
         }

         this.sendWatchPackets(player, ChunkFilter.IGNORE_ALL);
      }

   }

   private void updateWatchedSection(ServerPlayerEntity player) {
      ChunkSectionPos chunkSectionPos = ChunkSectionPos.from((EntityLike)player);
      player.setWatchedSection(chunkSectionPos);
   }

   public void updatePosition(ServerPlayerEntity player) {
      ObjectIterator var2 = this.entityTrackers.values().iterator();

      while(var2.hasNext()) {
         EntityTracker entityTracker = (EntityTracker)var2.next();
         if (entityTracker.entity == player) {
            entityTracker.updateTrackedStatus(this.world.getPlayers());
         } else {
            entityTracker.updateTrackedStatus(player);
         }
      }

      ChunkSectionPos chunkSectionPos = player.getWatchedSection();
      ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from((EntityLike)player);
      boolean bl = this.playerChunkWatchingManager.isWatchDisabled(player);
      boolean bl2 = this.doesNotGenerateChunks(player);
      boolean bl3 = chunkSectionPos.asLong() != chunkSectionPos2.asLong();
      if (bl3 || bl != bl2) {
         this.updateWatchedSection(player);
         if (!bl) {
            this.levelManager.handleChunkLeave(chunkSectionPos, player);
         }

         if (!bl2) {
            this.levelManager.handleChunkEnter(chunkSectionPos2, player);
         }

         if (!bl && bl2) {
            this.playerChunkWatchingManager.disableWatch(player);
         }

         if (bl && !bl2) {
            this.playerChunkWatchingManager.enableWatch(player);
         }

         this.sendWatchPackets(player);
      }

   }

   private void sendWatchPackets(ServerPlayerEntity player) {
      ChunkPos chunkPos = player.getChunkPos();
      int i = this.getViewDistance(player);
      ChunkFilter var5 = player.getChunkFilter();
      if (var5 instanceof ChunkFilter.Cylindrical cylindrical) {
         if (cylindrical.center().equals(chunkPos) && cylindrical.viewDistance() == i) {
            return;
         }
      }

      this.sendWatchPackets(player, ChunkFilter.cylindrical(chunkPos, i));
   }

   private void sendWatchPackets(ServerPlayerEntity player, ChunkFilter chunkFilter) {
      if (player.getWorld() == this.world) {
         ChunkFilter chunkFilter2 = player.getChunkFilter();
         if (chunkFilter instanceof ChunkFilter.Cylindrical) {
            label15: {
               ChunkFilter.Cylindrical cylindrical = (ChunkFilter.Cylindrical)chunkFilter;
               if (chunkFilter2 instanceof ChunkFilter.Cylindrical) {
                  ChunkFilter.Cylindrical cylindrical2 = (ChunkFilter.Cylindrical)chunkFilter2;
                  if (cylindrical2.center().equals(cylindrical.center())) {
                     break label15;
                  }
               }

               player.networkHandler.sendPacket(new ChunkRenderDistanceCenterS2CPacket(cylindrical.center().x, cylindrical.center().z));
            }
         }

         ChunkFilter.forEachChangedChunk(chunkFilter2, chunkFilter, (chunkPos) -> {
            this.track(player, chunkPos);
         }, (chunkPos) -> {
            untrack(player, chunkPos);
         });
         player.setChunkFilter(chunkFilter);
      }
   }

   public List getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge) {
      Set set = this.playerChunkWatchingManager.getPlayersWatchingChunk();
      ImmutableList.Builder builder = ImmutableList.builder();
      Iterator var5 = set.iterator();

      while(true) {
         ServerPlayerEntity serverPlayerEntity;
         do {
            if (!var5.hasNext()) {
               return builder.build();
            }

            serverPlayerEntity = (ServerPlayerEntity)var5.next();
         } while((!onlyOnWatchDistanceEdge || !this.isOnTrackEdge(serverPlayerEntity, chunkPos.x, chunkPos.z)) && (onlyOnWatchDistanceEdge || !this.isTracked(serverPlayerEntity, chunkPos.x, chunkPos.z)));

         builder.add(serverPlayerEntity);
      }
   }

   protected void loadEntity(Entity entity) {
      if (!(entity instanceof EnderDragonPart)) {
         EntityType entityType = entity.getType();
         int i = entityType.getMaxTrackDistance() * 16;
         if (i != 0) {
            int j = entityType.getTrackTickInterval();
            if (this.entityTrackers.containsKey(entity.getId())) {
               throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException("Entity is already tracked!"));
            } else {
               EntityTracker entityTracker = new EntityTracker(entity, i, j, entityType.alwaysUpdateVelocity());
               this.entityTrackers.put(entity.getId(), entityTracker);
               entityTracker.updateTrackedStatus(this.world.getPlayers());
               if (entity instanceof ServerPlayerEntity) {
                  ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                  this.handlePlayerAddedOrRemoved(serverPlayerEntity, true);
                  ObjectIterator var7 = this.entityTrackers.values().iterator();

                  while(var7.hasNext()) {
                     EntityTracker entityTracker2 = (EntityTracker)var7.next();
                     if (entityTracker2.entity != serverPlayerEntity) {
                        entityTracker2.updateTrackedStatus(serverPlayerEntity);
                     }
                  }
               }

            }
         }
      }
   }

   protected void unloadEntity(Entity entity) {
      if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
         this.handlePlayerAddedOrRemoved(serverPlayerEntity, false);
         ObjectIterator var3 = this.entityTrackers.values().iterator();

         while(var3.hasNext()) {
            EntityTracker entityTracker = (EntityTracker)var3.next();
            entityTracker.stopTracking(serverPlayerEntity);
         }
      }

      EntityTracker entityTracker2 = (EntityTracker)this.entityTrackers.remove(entity.getId());
      if (entityTracker2 != null) {
         entityTracker2.stopTracking();
      }

   }

   protected void tickEntityMovement() {
      Iterator var1 = this.playerChunkWatchingManager.getPlayersWatchingChunk().iterator();

      while(var1.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var1.next();
         this.sendWatchPackets(serverPlayerEntity);
      }

      List list = Lists.newArrayList();
      List list2 = this.world.getPlayers();
      ObjectIterator var3 = this.entityTrackers.values().iterator();

      EntityTracker entityTracker;
      while(var3.hasNext()) {
         entityTracker = (EntityTracker)var3.next();
         ChunkSectionPos chunkSectionPos = entityTracker.trackedSection;
         ChunkSectionPos chunkSectionPos2 = ChunkSectionPos.from((EntityLike)entityTracker.entity);
         boolean bl = !Objects.equals(chunkSectionPos, chunkSectionPos2);
         if (bl) {
            entityTracker.updateTrackedStatus(list2);
            Entity entity = entityTracker.entity;
            if (entity instanceof ServerPlayerEntity) {
               list.add((ServerPlayerEntity)entity);
            }

            entityTracker.trackedSection = chunkSectionPos2;
         }

         if (bl || this.levelManager.shouldTickEntities(chunkSectionPos2.toChunkPos().toLong())) {
            entityTracker.entry.tick();
         }
      }

      if (!list.isEmpty()) {
         var3 = this.entityTrackers.values().iterator();

         while(var3.hasNext()) {
            entityTracker = (EntityTracker)var3.next();
            entityTracker.updateTrackedStatus((List)list);
         }
      }

   }

   public void sendToOtherNearbyPlayers(Entity entity, Packet packet) {
      EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getId());
      if (entityTracker != null) {
         entityTracker.sendToOtherNearbyPlayers(packet);
      }

   }

   protected void sendToNearbyPlayers(Entity entity, Packet packet) {
      EntityTracker entityTracker = (EntityTracker)this.entityTrackers.get(entity.getId());
      if (entityTracker != null) {
         entityTracker.sendToNearbyPlayers(packet);
      }

   }

   public void sendChunkBiomePackets(List chunks) {
      Map map = new HashMap();
      Iterator var3 = chunks.iterator();

      while(var3.hasNext()) {
         Chunk chunk = (Chunk)var3.next();
         ChunkPos chunkPos = chunk.getPos();
         WorldChunk worldChunk2;
         if (chunk instanceof WorldChunk worldChunk) {
            worldChunk2 = worldChunk;
         } else {
            worldChunk2 = this.world.getChunk(chunkPos.x, chunkPos.z);
         }

         Iterator var9 = this.getPlayersWatchingChunk(chunkPos, false).iterator();

         while(var9.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var9.next();
            ((List)map.computeIfAbsent(serverPlayerEntity, (player) -> {
               return new ArrayList();
            })).add(worldChunk2);
         }
      }

      map.forEach((player, chunksx) -> {
         player.networkHandler.sendPacket(ChunkBiomeDataS2CPacket.create(chunksx));
      });
   }

   protected PointOfInterestStorage getPointOfInterestStorage() {
      return this.pointOfInterestStorage;
   }

   public String getSaveDir() {
      return this.saveDir;
   }

   void onChunkStatusChange(ChunkPos chunkPos, ChunkLevelType levelType) {
      this.chunkStatusChangeListener.onChunkStatusChange(chunkPos, levelType);
   }

   public void forceLighting(ChunkPos centerPos, int radius) {
      int i = radius + 1;
      ChunkPos.stream(centerPos, i).forEach((pos) -> {
         ChunkHolder chunkHolder = this.getChunkHolder(pos.toLong());
         if (chunkHolder != null) {
            chunkHolder.combinePostProcessingFuture(this.lightingProvider.enqueue(pos.x, pos.z));
         }

      });
   }

   static {
      UNLOADED_CHUNKS_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNKS);
      LOGGER = LogUtils.getLogger();
      FORCED_CHUNK_LEVEL = ChunkLevels.getLevelFromType(ChunkLevelType.ENTITY_TICKING);
   }

   private class LevelManager extends ChunkLevelManager {
      protected LevelManager(final ChunkTicketManager ticketManager, final Executor executor, final Executor mainThreadExecutor) {
         super(ticketManager, executor, mainThreadExecutor);
      }

      protected boolean isUnloaded(long pos) {
         return ServerChunkLoadingManager.this.unloadedChunks.contains(pos);
      }

      @Nullable
      protected ChunkHolder getChunkHolder(long pos) {
         return ServerChunkLoadingManager.this.getCurrentChunkHolder(pos);
      }

      @Nullable
      protected ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
         return ServerChunkLoadingManager.this.setLevel(pos, level, holder, i);
      }
   }

   private class EntityTracker {
      final EntityTrackerEntry entry;
      final Entity entity;
      private final int maxDistance;
      ChunkSectionPos trackedSection;
      private final Set listeners = Sets.newIdentityHashSet();

      public EntityTracker(final Entity entity, final int maxDistance, final int tickInterval, final boolean alwaysUpdateVelocity) {
         this.entry = new EntityTrackerEntry(ServerChunkLoadingManager.this.world, entity, tickInterval, alwaysUpdateVelocity, this::sendToOtherNearbyPlayers, this::sendToOtherNearbyPlayers);
         this.entity = entity;
         this.maxDistance = maxDistance;
         this.trackedSection = ChunkSectionPos.from((EntityLike)entity);
      }

      public boolean equals(Object o) {
         if (o instanceof EntityTracker) {
            return ((EntityTracker)o).entity.getId() == this.entity.getId();
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.entity.getId();
      }

      public void sendToOtherNearbyPlayers(Packet packet) {
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler = (PlayerAssociatedNetworkHandler)var2.next();
            playerAssociatedNetworkHandler.sendPacket(packet);
         }

      }

      public void sendToOtherNearbyPlayers(Packet packet, List except) {
         Iterator var3 = this.listeners.iterator();

         while(var3.hasNext()) {
            PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler = (PlayerAssociatedNetworkHandler)var3.next();
            if (!except.contains(playerAssociatedNetworkHandler.getPlayer().getUuid())) {
               playerAssociatedNetworkHandler.sendPacket(packet);
            }
         }

      }

      public void sendToNearbyPlayers(Packet packet) {
         this.sendToOtherNearbyPlayers(packet);
         if (this.entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)this.entity).networkHandler.sendPacket(packet);
         }

      }

      public void stopTracking() {
         Iterator var1 = this.listeners.iterator();

         while(var1.hasNext()) {
            PlayerAssociatedNetworkHandler playerAssociatedNetworkHandler = (PlayerAssociatedNetworkHandler)var1.next();
            this.entry.stopTracking(playerAssociatedNetworkHandler.getPlayer());
         }

      }

      public void stopTracking(ServerPlayerEntity player) {
         if (this.listeners.remove(player.networkHandler)) {
            this.entry.stopTracking(player);
         }

      }

      public void updateTrackedStatus(ServerPlayerEntity player) {
         if (player != this.entity) {
            Vec3d vec3d = player.getPos().subtract(this.entity.getPos());
            int i = ServerChunkLoadingManager.this.getViewDistance(player);
            double d = (double)Math.min(this.getMaxTrackDistance(), i * 16);
            double e = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            double f = d * d;
            boolean bl = e <= f && this.entity.canBeSpectated(player) && ServerChunkLoadingManager.this.isTracked(player, this.entity.getChunkPos().x, this.entity.getChunkPos().z);
            if (bl) {
               if (this.listeners.add(player.networkHandler)) {
                  this.entry.startTracking(player);
               }
            } else if (this.listeners.remove(player.networkHandler)) {
               this.entry.stopTracking(player);
            }

         }
      }

      private int adjustTrackingDistance(int initialDistance) {
         return ServerChunkLoadingManager.this.world.getServer().adjustTrackingDistance(initialDistance);
      }

      private int getMaxTrackDistance() {
         int i = this.maxDistance;
         Iterator var2 = this.entity.getPassengersDeep().iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            int j = entity.getType().getMaxTrackDistance() * 16;
            if (j > i) {
               i = j;
            }
         }

         return this.adjustTrackingDistance(i);
      }

      public void updateTrackedStatus(List players) {
         Iterator var2 = players.iterator();

         while(var2.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var2.next();
            this.updateTrackedStatus(serverPlayerEntity);
         }

      }
   }
}
