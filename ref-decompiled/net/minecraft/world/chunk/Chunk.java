package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.light.ChunkSkyLight;
import net.minecraft.world.chunk.light.LightSourceView;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.tick.BasicTickScheduler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public abstract class Chunk implements BiomeAccess.Storage, LightSourceView, StructureHolder, AttachmentTarget {
   public static final int MISSING_SECTION = -1;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LongSet EMPTY_STRUCTURE_REFERENCES = new LongOpenHashSet();
   protected final ShortList[] postProcessingLists;
   private volatile boolean needsSaving;
   private volatile boolean lightOn;
   protected final ChunkPos pos;
   private long inhabitedTime;
   /** @deprecated */
   @Nullable
   @Deprecated
   private GenerationSettings generationSettings;
   @Nullable
   protected ChunkNoiseSampler chunkNoiseSampler;
   protected final UpgradeData upgradeData;
   @Nullable
   protected BlendingData blendingData;
   protected final Map heightmaps = Maps.newEnumMap(Heightmap.Type.class);
   protected ChunkSkyLight chunkSkyLight;
   private final Map structureStarts = Maps.newHashMap();
   private final Map structureReferences = Maps.newHashMap();
   protected final Map blockEntityNbts = Maps.newHashMap();
   protected final Map blockEntities = new Object2ObjectOpenHashMap();
   protected final HeightLimitView heightLimitView;
   protected final ChunkSection[] sectionArray;

   public Chunk(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
      this.pos = pos;
      this.upgradeData = upgradeData;
      this.heightLimitView = heightLimitView;
      this.sectionArray = new ChunkSection[heightLimitView.countVerticalSections()];
      this.inhabitedTime = inhabitedTime;
      this.postProcessingLists = new ShortList[heightLimitView.countVerticalSections()];
      this.blendingData = blendingData;
      this.chunkSkyLight = new ChunkSkyLight(heightLimitView);
      if (sectionArray != null) {
         if (this.sectionArray.length == sectionArray.length) {
            System.arraycopy(sectionArray, 0, this.sectionArray, 0, this.sectionArray.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", sectionArray.length, this.sectionArray.length);
         }
      }

      fillSectionArray(biomeRegistry, this.sectionArray);
   }

   private static void fillSectionArray(Registry biomeRegistry, ChunkSection[] sectionArray) {
      for(int i = 0; i < sectionArray.length; ++i) {
         if (sectionArray[i] == null) {
            sectionArray[i] = new ChunkSection(biomeRegistry);
         }
      }

   }

   public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
      return GameEventDispatcher.EMPTY;
   }

   @Nullable
   public BlockState setBlockState(BlockPos pos, BlockState state) {
      return this.setBlockState(pos, state, 3);
   }

   @Nullable
   public abstract BlockState setBlockState(BlockPos pos, BlockState state, int flags);

   public abstract void setBlockEntity(BlockEntity blockEntity);

   public abstract void addEntity(Entity entity);

   public int getHighestNonEmptySection() {
      ChunkSection[] chunkSections = this.getSectionArray();

      for(int i = chunkSections.length - 1; i >= 0; --i) {
         ChunkSection chunkSection = chunkSections[i];
         if (!chunkSection.isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public int getHighestNonEmptySectionYOffset() {
      int i = this.getHighestNonEmptySection();
      return i == -1 ? this.getBottomY() : ChunkSectionPos.getBlockCoord(this.sectionIndexToCoord(i));
   }

   public Set getBlockEntityPositions() {
      Set set = Sets.newHashSet(this.blockEntityNbts.keySet());
      set.addAll(this.blockEntities.keySet());
      return set;
   }

   public ChunkSection[] getSectionArray() {
      return this.sectionArray;
   }

   public ChunkSection getSection(int yIndex) {
      return this.getSectionArray()[yIndex];
   }

   public Collection getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Type type, long[] heightmap) {
      this.getHeightmap(type).setTo(this, type, heightmap);
   }

   public Heightmap getHeightmap(Heightmap.Type type) {
      return (Heightmap)this.heightmaps.computeIfAbsent(type, (type2) -> {
         return new Heightmap(this, type2);
      });
   }

   public boolean hasHeightmap(Heightmap.Type type) {
      return this.heightmaps.get(type) != null;
   }

   public int sampleHeightmap(Heightmap.Type type, int x, int z) {
      Heightmap heightmap = (Heightmap)this.heightmaps.get(type);
      if (heightmap == null) {
         if (SharedConstants.isDevelopment && this instanceof WorldChunk) {
            LOGGER.error("Unprimed heightmap: " + String.valueOf(type) + " " + x + " " + z);
         }

         Heightmap.populateHeightmaps(this, EnumSet.of(type));
         heightmap = (Heightmap)this.heightmaps.get(type);
      }

      return heightmap.get(x & 15, z & 15) - 1;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   @Nullable
   public StructureStart getStructureStart(Structure structure) {
      return (StructureStart)this.structureStarts.get(structure);
   }

   public void setStructureStart(Structure structure, StructureStart start) {
      this.structureStarts.put(structure, start);
      this.markNeedsSaving();
   }

   public Map getStructureStarts() {
      return Collections.unmodifiableMap(this.structureStarts);
   }

   public void setStructureStarts(Map structureStarts) {
      this.structureStarts.clear();
      this.structureStarts.putAll(structureStarts);
      this.markNeedsSaving();
   }

   public LongSet getStructureReferences(Structure structure) {
      return (LongSet)this.structureReferences.getOrDefault(structure, EMPTY_STRUCTURE_REFERENCES);
   }

   public void addStructureReference(Structure structure, long reference) {
      ((LongSet)this.structureReferences.computeIfAbsent(structure, (type2) -> {
         return new LongOpenHashSet();
      })).add(reference);
      this.markNeedsSaving();
   }

   public Map getStructureReferences() {
      return Collections.unmodifiableMap(this.structureReferences);
   }

   public void setStructureReferences(Map structureReferences) {
      this.structureReferences.clear();
      this.structureReferences.putAll(structureReferences);
      this.markNeedsSaving();
   }

   public boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
      if (lowerHeight < this.getBottomY()) {
         lowerHeight = this.getBottomY();
      }

      if (upperHeight > this.getTopYInclusive()) {
         upperHeight = this.getTopYInclusive();
      }

      for(int i = lowerHeight; i <= upperHeight; i += 16) {
         if (!this.getSection(this.getSectionIndex(i)).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public void markNeedsSaving() {
      this.needsSaving = true;
   }

   public boolean tryMarkSaved() {
      if (this.needsSaving) {
         this.needsSaving = false;
         return true;
      } else {
         return false;
      }
   }

   public boolean needsSaving() {
      return this.needsSaving;
   }

   public abstract ChunkStatus getStatus();

   public ChunkStatus getMaxStatus() {
      ChunkStatus chunkStatus = this.getStatus();
      BelowZeroRetrogen belowZeroRetrogen = this.getBelowZeroRetrogen();
      if (belowZeroRetrogen != null) {
         ChunkStatus chunkStatus2 = belowZeroRetrogen.getTargetStatus();
         return ChunkStatus.max(chunkStatus2, chunkStatus);
      } else {
         return chunkStatus;
      }
   }

   public abstract void removeBlockEntity(BlockPos pos);

   public void markBlockForPostProcessing(BlockPos pos) {
      LOGGER.warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", pos);
   }

   public ShortList[] getPostProcessingLists() {
      return this.postProcessingLists;
   }

   public void markBlocksForPostProcessing(ShortList packedPositions, int index) {
      getList(this.getPostProcessingLists(), index).addAll(packedPositions);
   }

   public void addPendingBlockEntityNbt(NbtCompound nbt) {
      BlockPos blockPos = BlockEntity.posFromNbt(this.pos, nbt);
      if (!this.blockEntities.containsKey(blockPos)) {
         this.blockEntityNbts.put(blockPos, nbt);
      }

   }

   @Nullable
   public NbtCompound getBlockEntityNbt(BlockPos pos) {
      return (NbtCompound)this.blockEntityNbts.get(pos);
   }

   @Nullable
   public abstract NbtCompound getPackedBlockEntityNbt(BlockPos pos, RegistryWrapper.WrapperLookup registries);

   public final void forEachLightSource(BiConsumer callback) {
      this.forEachBlockMatchingPredicate((blockState) -> {
         return blockState.getLuminance() != 0;
      }, callback);
   }

   public void forEachBlockMatchingPredicate(Predicate predicate, BiConsumer consumer) {
      BlockPos.Mutable mutable = new BlockPos.Mutable();

      for(int i = this.getBottomSectionCoord(); i <= this.getTopSectionCoord(); ++i) {
         ChunkSection chunkSection = this.getSection(this.sectionCoordToIndex(i));
         if (chunkSection.hasAny(predicate)) {
            BlockPos blockPos = ChunkSectionPos.from(this.pos, i).getMinPos();

            for(int j = 0; j < 16; ++j) {
               for(int k = 0; k < 16; ++k) {
                  for(int l = 0; l < 16; ++l) {
                     BlockState blockState = chunkSection.getBlockState(l, j, k);
                     if (predicate.test(blockState)) {
                        consumer.accept(mutable.set((Vec3i)blockPos, l, j, k), blockState);
                     }
                  }
               }
            }
         }
      }

   }

   public abstract BasicTickScheduler getBlockTickScheduler();

   public abstract BasicTickScheduler getFluidTickScheduler();

   public boolean isSerializable() {
      return true;
   }

   public abstract TickSchedulers getTickSchedulers(long time);

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public boolean usesOldNoise() {
      return this.blendingData != null;
   }

   @Nullable
   public BlendingData getBlendingData() {
      return this.blendingData;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void increaseInhabitedTime(long timeDelta) {
      this.inhabitedTime += timeDelta;
   }

   public void setInhabitedTime(long inhabitedTime) {
      this.inhabitedTime = inhabitedTime;
   }

   public static ShortList getList(ShortList[] lists, int index) {
      if (lists[index] == null) {
         lists[index] = new ShortArrayList();
      }

      return lists[index];
   }

   public boolean isLightOn() {
      return this.lightOn;
   }

   public void setLightOn(boolean lightOn) {
      this.lightOn = lightOn;
      this.markNeedsSaving();
   }

   public int getBottomY() {
      return this.heightLimitView.getBottomY();
   }

   public int getHeight() {
      return this.heightLimitView.getHeight();
   }

   public ChunkNoiseSampler getOrCreateChunkNoiseSampler(Function chunkNoiseSamplerCreator) {
      if (this.chunkNoiseSampler == null) {
         this.chunkNoiseSampler = (ChunkNoiseSampler)chunkNoiseSamplerCreator.apply(this);
      }

      return this.chunkNoiseSampler;
   }

   /** @deprecated */
   @Deprecated
   public GenerationSettings getOrCreateGenerationSettings(Supplier generationSettingsCreator) {
      if (this.generationSettings == null) {
         this.generationSettings = (GenerationSettings)generationSettingsCreator.get();
      }

      return this.generationSettings;
   }

   public RegistryEntry getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
      try {
         int i = BiomeCoords.fromBlock(this.getBottomY());
         int j = i + BiomeCoords.fromBlock(this.getHeight()) - 1;
         int k = MathHelper.clamp(biomeY, i, j);
         int l = this.getSectionIndex(BiomeCoords.toBlock(k));
         return this.sectionArray[l].getBiome(biomeX & 3, k & 3, biomeZ & 3);
      } catch (Throwable var8) {
         CrashReport crashReport = CrashReport.create(var8, "Getting biome");
         CrashReportSection crashReportSection = crashReport.addElement("Biome being got");
         crashReportSection.add("Location", () -> {
            return CrashReportSection.createPositionString(this, biomeX, biomeY, biomeZ);
         });
         throw new CrashException(crashReport);
      }
   }

   public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
      ChunkPos chunkPos = this.getPos();
      int i = BiomeCoords.fromBlock(chunkPos.getStartX());
      int j = BiomeCoords.fromBlock(chunkPos.getStartZ());
      HeightLimitView heightLimitView = this.getHeightLimitView();

      for(int k = heightLimitView.getBottomSectionCoord(); k <= heightLimitView.getTopSectionCoord(); ++k) {
         ChunkSection chunkSection = this.getSection(this.sectionCoordToIndex(k));
         int l = BiomeCoords.fromChunk(k);
         chunkSection.populateBiomes(biomeSupplier, sampler, i, l, j);
      }

   }

   public boolean hasStructureReferences() {
      return !this.getStructureReferences().isEmpty();
   }

   @Nullable
   public BelowZeroRetrogen getBelowZeroRetrogen() {
      return null;
   }

   public boolean hasBelowZeroRetrogen() {
      return this.getBelowZeroRetrogen() != null;
   }

   public HeightLimitView getHeightLimitView() {
      return this;
   }

   public void refreshSurfaceY() {
      this.chunkSkyLight.refreshSurfaceY(this);
   }

   public ChunkSkyLight getChunkSkyLight() {
      return this.chunkSkyLight;
   }

   public static ErrorReporter.Context createErrorReporterContext(ChunkPos pos) {
      return new ErrorReporterContext(pos);
   }

   public ErrorReporter.Context getErrorReporterContext() {
      return createErrorReporterContext(this.getPos());
   }

   static record ErrorReporterContext(ChunkPos pos) implements ErrorReporter.Context {
      ErrorReporterContext(ChunkPos chunkPos) {
         this.pos = chunkPos;
      }

      public String getName() {
         return "chunk@" + String.valueOf(this.pos);
      }

      public ChunkPos pos() {
         return this.pos;
      }
   }

   public static record TickSchedulers(List blocks, List fluids) {
      public TickSchedulers(List list, List list2) {
         this.blocks = list;
         this.fluids = list2;
      }

      public List blocks() {
         return this.blocks;
      }

      public List fluids() {
         return this.fluids;
      }
   }
}
