package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.light.ChunkSkyLight;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.EmptyTickSchedulers;
import org.jetbrains.annotations.Nullable;

public class WrapperProtoChunk extends ProtoChunk {
   private final WorldChunk wrapped;
   private final boolean propagateToWrapped;

   public WrapperProtoChunk(WorldChunk wrapped, boolean propagateToWrapped) {
      super(wrapped.getPos(), UpgradeData.NO_UPGRADE_DATA, wrapped.heightLimitView, wrapped.getWorld().getRegistryManager().getOrThrow(RegistryKeys.BIOME), wrapped.getBlendingData());
      this.wrapped = wrapped;
      this.propagateToWrapped = propagateToWrapped;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      return this.wrapped.getBlockEntity(pos);
   }

   public BlockState getBlockState(BlockPos pos) {
      return this.wrapped.getBlockState(pos);
   }

   public FluidState getFluidState(BlockPos pos) {
      return this.wrapped.getFluidState(pos);
   }

   public ChunkSection getSection(int yIndex) {
      return this.propagateToWrapped ? this.wrapped.getSection(yIndex) : super.getSection(yIndex);
   }

   @Nullable
   public BlockState setBlockState(BlockPos pos, BlockState state, int flags) {
      return this.propagateToWrapped ? this.wrapped.setBlockState(pos, state, flags) : null;
   }

   public void setBlockEntity(BlockEntity blockEntity) {
      if (this.propagateToWrapped) {
         this.wrapped.setBlockEntity(blockEntity);
      }

   }

   public void addEntity(Entity entity) {
      if (this.propagateToWrapped) {
         this.wrapped.addEntity(entity);
      }

   }

   public void setStatus(ChunkStatus status) {
      if (this.propagateToWrapped) {
         super.setStatus(status);
      }

   }

   public ChunkSection[] getSectionArray() {
      return this.wrapped.getSectionArray();
   }

   public void setHeightmap(Heightmap.Type type, long[] heightmap) {
   }

   private Heightmap.Type transformHeightmapType(Heightmap.Type type) {
      if (type == Heightmap.Type.WORLD_SURFACE_WG) {
         return Heightmap.Type.WORLD_SURFACE;
      } else {
         return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
      }
   }

   public Heightmap getHeightmap(Heightmap.Type type) {
      return this.wrapped.getHeightmap(type);
   }

   public int sampleHeightmap(Heightmap.Type type, int x, int z) {
      return this.wrapped.sampleHeightmap(this.transformHeightmapType(type), x, z);
   }

   public RegistryEntry getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
      return this.wrapped.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
   }

   public ChunkPos getPos() {
      return this.wrapped.getPos();
   }

   @Nullable
   public StructureStart getStructureStart(Structure structure) {
      return this.wrapped.getStructureStart(structure);
   }

   public void setStructureStart(Structure structure, StructureStart start) {
   }

   public Map getStructureStarts() {
      return this.wrapped.getStructureStarts();
   }

   public void setStructureStarts(Map structureStarts) {
   }

   public LongSet getStructureReferences(Structure structure) {
      return this.wrapped.getStructureReferences(structure);
   }

   public void addStructureReference(Structure structure, long reference) {
   }

   public Map getStructureReferences() {
      return this.wrapped.getStructureReferences();
   }

   public void setStructureReferences(Map structureReferences) {
   }

   public void markNeedsSaving() {
      this.wrapped.markNeedsSaving();
   }

   public boolean isSerializable() {
      return false;
   }

   public boolean tryMarkSaved() {
      return false;
   }

   public boolean needsSaving() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.wrapped.getStatus();
   }

   public void removeBlockEntity(BlockPos pos) {
   }

   public void markBlockForPostProcessing(BlockPos pos) {
   }

   public void addPendingBlockEntityNbt(NbtCompound nbt) {
   }

   @Nullable
   public NbtCompound getBlockEntityNbt(BlockPos pos) {
      return this.wrapped.getBlockEntityNbt(pos);
   }

   @Nullable
   public NbtCompound getPackedBlockEntityNbt(BlockPos pos, RegistryWrapper.WrapperLookup registries) {
      return this.wrapped.getPackedBlockEntityNbt(pos, registries);
   }

   public void forEachBlockMatchingPredicate(Predicate predicate, BiConsumer consumer) {
      this.wrapped.forEachBlockMatchingPredicate(predicate, consumer);
   }

   public BasicTickScheduler getBlockTickScheduler() {
      return this.propagateToWrapped ? this.wrapped.getBlockTickScheduler() : EmptyTickSchedulers.getReadOnlyTickScheduler();
   }

   public BasicTickScheduler getFluidTickScheduler() {
      return this.propagateToWrapped ? this.wrapped.getFluidTickScheduler() : EmptyTickSchedulers.getReadOnlyTickScheduler();
   }

   public Chunk.TickSchedulers getTickSchedulers(long time) {
      return this.wrapped.getTickSchedulers(time);
   }

   @Nullable
   public BlendingData getBlendingData() {
      return this.wrapped.getBlendingData();
   }

   public CarvingMask getCarvingMask() {
      if (this.propagateToWrapped) {
         return super.getCarvingMask();
      } else {
         throw (UnsupportedOperationException)Util.getFatalOrPause(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   public CarvingMask getOrCreateCarvingMask() {
      if (this.propagateToWrapped) {
         return super.getOrCreateCarvingMask();
      } else {
         throw (UnsupportedOperationException)Util.getFatalOrPause(new UnsupportedOperationException("Meaningless in this context"));
      }
   }

   public WorldChunk getWrappedChunk() {
      return this.wrapped;
   }

   public boolean isLightOn() {
      return this.wrapped.isLightOn();
   }

   public void setLightOn(boolean lightOn) {
      this.wrapped.setLightOn(lightOn);
   }

   public void populateBiomes(BiomeSupplier biomeSupplier, MultiNoiseUtil.MultiNoiseSampler sampler) {
      if (this.propagateToWrapped) {
         this.wrapped.populateBiomes(biomeSupplier, sampler);
      }

   }

   public void refreshSurfaceY() {
      this.wrapped.refreshSurfaceY();
   }

   public ChunkSkyLight getChunkSkyLight() {
      return this.wrapped.getChunkSkyLight();
   }
}
