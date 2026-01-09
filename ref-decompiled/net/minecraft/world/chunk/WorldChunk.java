package net.minecraft.world.chunk;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.event.listener.GameEventDispatcher;
import net.minecraft.world.event.listener.GameEventListener;
import net.minecraft.world.event.listener.SimpleGameEventDispatcher;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import net.minecraft.world.tick.BasicTickScheduler;
import net.minecraft.world.tick.ChunkTickScheduler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class WorldChunk extends Chunk {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final BlockEntityTickInvoker EMPTY_BLOCK_ENTITY_TICKER = new BlockEntityTickInvoker() {
      public void tick() {
      }

      public boolean isRemoved() {
         return true;
      }

      public BlockPos getPos() {
         return BlockPos.ORIGIN;
      }

      public String getName() {
         return "<null>";
      }
   };
   private final Map blockEntityTickers;
   private boolean loadedToWorld;
   final World world;
   @Nullable
   private Supplier levelTypeProvider;
   @Nullable
   private EntityLoader entityLoader;
   private final Int2ObjectMap gameEventDispatchers;
   private final ChunkTickScheduler blockTickScheduler;
   private final ChunkTickScheduler fluidTickScheduler;
   private UnsavedListener unsavedListener;

   public WorldChunk(World world, ChunkPos pos) {
      this(world, pos, UpgradeData.NO_UPGRADE_DATA, new ChunkTickScheduler(), new ChunkTickScheduler(), 0L, (ChunkSection[])null, (EntityLoader)null, (BlendingData)null);
   }

   public WorldChunk(World world, ChunkPos pos, UpgradeData upgradeData, ChunkTickScheduler blockTickScheduler, ChunkTickScheduler fluidTickScheduler, long inhabitedTime, @Nullable ChunkSection[] sectionArrayInitializer, @Nullable EntityLoader entityLoader, @Nullable BlendingData blendingData) {
      super(pos, upgradeData, world, world.getRegistryManager().getOrThrow(RegistryKeys.BIOME), inhabitedTime, sectionArrayInitializer, blendingData);
      this.blockEntityTickers = Maps.newHashMap();
      this.unsavedListener = (posx) -> {
      };
      this.world = world;
      this.gameEventDispatchers = new Int2ObjectOpenHashMap();
      Heightmap.Type[] var11 = Heightmap.Type.values();
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         Heightmap.Type type = var11[var13];
         if (ChunkStatus.FULL.getHeightmapTypes().contains(type)) {
            this.heightmaps.put(type, new Heightmap(this, type));
         }
      }

      this.entityLoader = entityLoader;
      this.blockTickScheduler = blockTickScheduler;
      this.fluidTickScheduler = fluidTickScheduler;
   }

   public WorldChunk(ServerWorld world, ProtoChunk protoChunk, @Nullable EntityLoader entityLoader) {
      this(world, protoChunk.getPos(), protoChunk.getUpgradeData(), protoChunk.getBlockProtoTickScheduler(), protoChunk.getFluidProtoTickScheduler(), protoChunk.getInhabitedTime(), protoChunk.getSectionArray(), entityLoader, protoChunk.getBlendingData());
      if (!Collections.disjoint(protoChunk.blockEntityNbts.keySet(), protoChunk.blockEntities.keySet())) {
         LOGGER.error("Chunk at {} contains duplicated block entities", protoChunk.getPos());
      }

      Iterator var4 = protoChunk.getBlockEntities().values().iterator();

      while(var4.hasNext()) {
         BlockEntity blockEntity = (BlockEntity)var4.next();
         this.setBlockEntity(blockEntity);
      }

      this.blockEntityNbts.putAll(protoChunk.getBlockEntityNbts());

      for(int i = 0; i < protoChunk.getPostProcessingLists().length; ++i) {
         this.postProcessingLists[i] = protoChunk.getPostProcessingLists()[i];
      }

      this.setStructureStarts(protoChunk.getStructureStarts());
      this.setStructureReferences(protoChunk.getStructureReferences());
      var4 = protoChunk.getHeightmaps().iterator();

      while(var4.hasNext()) {
         Map.Entry entry = (Map.Entry)var4.next();
         if (ChunkStatus.FULL.getHeightmapTypes().contains(entry.getKey())) {
            this.setHeightmap((Heightmap.Type)entry.getKey(), ((Heightmap)entry.getValue()).asLongArray());
         }
      }

      this.chunkSkyLight = protoChunk.chunkSkyLight;
      this.setLightOn(protoChunk.isLightOn());
      this.markNeedsSaving();
   }

   public void setUnsavedListener(UnsavedListener unsavedListener) {
      this.unsavedListener = unsavedListener;
      if (this.needsSaving()) {
         unsavedListener.setUnsaved(this.pos);
      }

   }

   public void markNeedsSaving() {
      boolean bl = this.needsSaving();
      super.markNeedsSaving();
      if (!bl) {
         this.unsavedListener.setUnsaved(this.pos);
      }

   }

   public BasicTickScheduler getBlockTickScheduler() {
      return this.blockTickScheduler;
   }

   public BasicTickScheduler getFluidTickScheduler() {
      return this.fluidTickScheduler;
   }

   public Chunk.TickSchedulers getTickSchedulers(long time) {
      return new Chunk.TickSchedulers(this.blockTickScheduler.collectTicks(time), this.fluidTickScheduler.collectTicks(time));
   }

   public GameEventDispatcher getGameEventDispatcher(int ySectionCoord) {
      World var3 = this.world;
      if (var3 instanceof ServerWorld serverWorld) {
         return (GameEventDispatcher)this.gameEventDispatchers.computeIfAbsent(ySectionCoord, (sectionCoord) -> {
            return new SimpleGameEventDispatcher(serverWorld, ySectionCoord, this::removeGameEventDispatcher);
         });
      } else {
         return super.getGameEventDispatcher(ySectionCoord);
      }
   }

   public BlockState getBlockState(BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      if (this.world.isDebugWorld()) {
         BlockState blockState = null;
         if (j == 60) {
            blockState = Blocks.BARRIER.getDefaultState();
         }

         if (j == 70) {
            blockState = DebugChunkGenerator.getBlockState(i, k);
         }

         return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
      } else {
         try {
            int l = this.getSectionIndex(j);
            if (l >= 0 && l < this.sectionArray.length) {
               ChunkSection chunkSection = this.sectionArray[l];
               if (!chunkSection.isEmpty()) {
                  return chunkSection.getBlockState(i & 15, j & 15, k & 15);
               }
            }

            return Blocks.AIR.getDefaultState();
         } catch (Throwable var8) {
            CrashReport crashReport = CrashReport.create(var8, "Getting block state");
            CrashReportSection crashReportSection = crashReport.addElement("Block being got");
            crashReportSection.add("Location", () -> {
               return CrashReportSection.createPositionString(this, i, j, k);
            });
            throw new CrashException(crashReport);
         }
      }
   }

   public FluidState getFluidState(BlockPos pos) {
      return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
   }

   public FluidState getFluidState(int x, int y, int z) {
      try {
         int i = this.getSectionIndex(y);
         if (i >= 0 && i < this.sectionArray.length) {
            ChunkSection chunkSection = this.sectionArray[i];
            if (!chunkSection.isEmpty()) {
               return chunkSection.getFluidState(x & 15, y & 15, z & 15);
            }
         }

         return Fluids.EMPTY.getDefaultState();
      } catch (Throwable var7) {
         CrashReport crashReport = CrashReport.create(var7, "Getting fluid state");
         CrashReportSection crashReportSection = crashReport.addElement("Block being got");
         crashReportSection.add("Location", () -> {
            return CrashReportSection.createPositionString(this, x, y, z);
         });
         throw new CrashException(crashReport);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos pos, BlockState state, int flags) {
      int i = pos.getY();
      ChunkSection chunkSection = this.getSection(this.getSectionIndex(i));
      boolean bl = chunkSection.isEmpty();
      if (bl && state.isAir()) {
         return null;
      } else {
         int j = pos.getX() & 15;
         int k = i & 15;
         int l = pos.getZ() & 15;
         BlockState blockState = chunkSection.setBlockState(j, k, l, state);
         if (blockState == state) {
            return null;
         } else {
            Block block = state.getBlock();
            ((Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING)).trackUpdate(j, i, l, state);
            ((Heightmap)this.heightmaps.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).trackUpdate(j, i, l, state);
            ((Heightmap)this.heightmaps.get(Heightmap.Type.OCEAN_FLOOR)).trackUpdate(j, i, l, state);
            ((Heightmap)this.heightmaps.get(Heightmap.Type.WORLD_SURFACE)).trackUpdate(j, i, l, state);
            boolean bl2 = chunkSection.isEmpty();
            if (bl != bl2) {
               this.world.getChunkManager().getLightingProvider().setSectionStatus(pos, bl2);
               this.world.getChunkManager().onSectionStatusChanged(this.pos.x, ChunkSectionPos.getSectionCoord(i), this.pos.z, bl2);
            }

            if (ChunkLightProvider.needsLightUpdate(blockState, state)) {
               Profiler profiler = Profilers.get();
               profiler.push("updateSkyLightSources");
               this.chunkSkyLight.isSkyLightAccessible(this, j, i, l);
               profiler.swap("queueCheckLight");
               this.world.getChunkManager().getLightingProvider().checkBlock(pos);
               profiler.pop();
            }

            boolean bl3 = !blockState.isOf(block);
            boolean bl4 = (flags & 64) != 0;
            boolean bl5 = (flags & 256) == 0;
            BlockEntity blockEntity;
            if (bl3 && blockState.hasBlockEntity()) {
               if (!this.world.isClient && bl5) {
                  blockEntity = this.world.getBlockEntity(pos);
                  if (blockEntity != null) {
                     blockEntity.onBlockReplaced(pos, blockState);
                  }
               }

               this.removeBlockEntity(pos);
            }

            if (bl3 || block instanceof AbstractRailBlock) {
               World var17 = this.world;
               if (var17 instanceof ServerWorld) {
                  ServerWorld serverWorld = (ServerWorld)var17;
                  if ((flags & 1) != 0 || bl4) {
                     blockState.onStateReplaced(serverWorld, pos, bl4);
                  }
               }
            }

            if (!chunkSection.getBlockState(j, k, l).isOf(block)) {
               return null;
            } else {
               if (!this.world.isClient && (flags & 512) == 0) {
                  state.onBlockAdded(this.world, pos, blockState, bl4);
               }

               if (state.hasBlockEntity()) {
                  blockEntity = this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
                  if (blockEntity != null && !blockEntity.supports(state)) {
                     LOGGER.warn("Found mismatched block entity @ {}: type = {}, state = {}", new Object[]{pos, blockEntity.getType().getRegistryEntry().registryKey().getValue(), state});
                     this.removeBlockEntity(pos);
                     blockEntity = null;
                  }

                  if (blockEntity == null) {
                     blockEntity = ((BlockEntityProvider)block).createBlockEntity(pos, state);
                     if (blockEntity != null) {
                        this.addBlockEntity(blockEntity);
                     }
                  } else {
                     blockEntity.setCachedState(state);
                     this.updateTicker(blockEntity);
                  }
               }

               this.markNeedsSaving();
               return blockState;
            }
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public void addEntity(Entity entity) {
   }

   @Nullable
   private BlockEntity createBlockEntity(BlockPos pos) {
      BlockState blockState = this.getBlockState(pos);
      return !blockState.hasBlockEntity() ? null : ((BlockEntityProvider)blockState.getBlock()).createBlockEntity(pos, blockState);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos) {
      return this.getBlockEntity(pos, WorldChunk.CreationType.CHECK);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos pos, CreationType creationType) {
      BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(pos);
      if (blockEntity == null) {
         NbtCompound nbtCompound = (NbtCompound)this.blockEntityNbts.remove(pos);
         if (nbtCompound != null) {
            BlockEntity blockEntity2 = this.loadBlockEntity(pos, nbtCompound);
            if (blockEntity2 != null) {
               return blockEntity2;
            }
         }
      }

      if (blockEntity == null) {
         if (creationType == WorldChunk.CreationType.IMMEDIATE) {
            blockEntity = this.createBlockEntity(pos);
            if (blockEntity != null) {
               this.addBlockEntity(blockEntity);
            }
         }
      } else if (blockEntity.isRemoved()) {
         this.blockEntities.remove(pos);
         return null;
      }

      return blockEntity;
   }

   public void addBlockEntity(BlockEntity blockEntity) {
      this.setBlockEntity(blockEntity);
      if (this.canTickBlockEntities()) {
         World var3 = this.world;
         if (var3 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var3;
            this.updateGameEventListener(blockEntity, serverWorld);
         }

         this.world.loadBlockEntity(blockEntity);
         this.updateTicker(blockEntity);
      }

   }

   private boolean canTickBlockEntities() {
      return this.loadedToWorld || this.world.isClient();
   }

   boolean canTickBlockEntity(BlockPos pos) {
      if (!this.world.getWorldBorder().contains(pos)) {
         return false;
      } else {
         World var3 = this.world;
         if (!(var3 instanceof ServerWorld)) {
            return true;
         } else {
            ServerWorld serverWorld = (ServerWorld)var3;
            return this.getLevelType().isAfter(ChunkLevelType.BLOCK_TICKING) && serverWorld.isChunkLoaded(ChunkPos.toLong(pos));
         }
      }
   }

   public void setBlockEntity(BlockEntity blockEntity) {
      BlockPos blockPos = blockEntity.getPos();
      BlockState blockState = this.getBlockState(blockPos);
      if (!blockState.hasBlockEntity()) {
         LOGGER.warn("Trying to set block entity {} at position {}, but state {} does not allow it", new Object[]{blockEntity, blockPos, blockState});
      } else {
         BlockState blockState2 = blockEntity.getCachedState();
         if (blockState != blockState2) {
            if (!blockEntity.getType().supports(blockState)) {
               LOGGER.warn("Trying to set block entity {} at position {}, but state {} does not allow it", new Object[]{blockEntity, blockPos, blockState});
               return;
            }

            if (blockState.getBlock() != blockState2.getBlock()) {
               LOGGER.warn("Block state mismatch on block entity {} in position {}, {} != {}, updating", new Object[]{blockEntity, blockPos, blockState, blockState2});
            }

            blockEntity.setCachedState(blockState);
         }

         blockEntity.setWorld(this.world);
         blockEntity.cancelRemoval();
         BlockEntity blockEntity2 = (BlockEntity)this.blockEntities.put(blockPos.toImmutable(), blockEntity);
         if (blockEntity2 != null && blockEntity2 != blockEntity) {
            blockEntity2.markRemoved();
         }

      }
   }

   @Nullable
   public NbtCompound getPackedBlockEntityNbt(BlockPos pos, RegistryWrapper.WrapperLookup registries) {
      BlockEntity blockEntity = this.getBlockEntity(pos);
      NbtCompound nbtCompound;
      if (blockEntity != null && !blockEntity.isRemoved()) {
         nbtCompound = blockEntity.createNbtWithIdentifyingData(this.world.getRegistryManager());
         nbtCompound.putBoolean("keepPacked", false);
         return nbtCompound;
      } else {
         nbtCompound = (NbtCompound)this.blockEntityNbts.get(pos);
         if (nbtCompound != null) {
            nbtCompound = nbtCompound.copy();
            nbtCompound.putBoolean("keepPacked", true);
         }

         return nbtCompound;
      }
   }

   public void removeBlockEntity(BlockPos pos) {
      if (this.canTickBlockEntities()) {
         BlockEntity blockEntity = (BlockEntity)this.blockEntities.remove(pos);
         if (blockEntity != null) {
            World var4 = this.world;
            if (var4 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var4;
               this.removeGameEventListener(blockEntity, serverWorld);
            }

            blockEntity.markRemoved();
         }
      }

      this.removeBlockEntityTicker(pos);
   }

   private void removeGameEventListener(BlockEntity blockEntity, ServerWorld world) {
      Block block = blockEntity.getCachedState().getBlock();
      if (block instanceof BlockEntityProvider) {
         GameEventListener gameEventListener = ((BlockEntityProvider)block).getGameEventListener(world, blockEntity);
         if (gameEventListener != null) {
            int i = ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY());
            GameEventDispatcher gameEventDispatcher = this.getGameEventDispatcher(i);
            gameEventDispatcher.removeListener(gameEventListener);
         }
      }

   }

   private void removeGameEventDispatcher(int ySectionCoord) {
      this.gameEventDispatchers.remove(ySectionCoord);
   }

   private void removeBlockEntityTicker(BlockPos pos) {
      WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker = (WrappedBlockEntityTickInvoker)this.blockEntityTickers.remove(pos);
      if (wrappedBlockEntityTickInvoker != null) {
         wrappedBlockEntityTickInvoker.setWrapped(EMPTY_BLOCK_ENTITY_TICKER);
      }

   }

   public void loadEntities() {
      if (this.entityLoader != null) {
         this.entityLoader.run(this);
         this.entityLoader = null;
      }

   }

   public boolean isEmpty() {
      return false;
   }

   public void loadFromPacket(PacketByteBuf buf, Map heightmaps, Consumer blockEntityVisitorConsumer) {
      this.clear();
      ChunkSection[] var4 = this.sectionArray;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ChunkSection chunkSection = var4[var6];
         chunkSection.readDataPacket(buf);
      }

      heightmaps.forEach(this::setHeightmap);
      this.refreshSurfaceY();
      ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getErrorReporterContext(), LOGGER);

      try {
         blockEntityVisitorConsumer.accept((pos, blockEntityType, nbt) -> {
            BlockEntity blockEntity = this.getBlockEntity(pos, WorldChunk.CreationType.IMMEDIATE);
            if (blockEntity != null && nbt != null && blockEntity.getType() == blockEntityType) {
               blockEntity.read(NbtReadView.create(logging.makeChild(blockEntity.getReporterContext()), this.world.getRegistryManager(), nbt));
            }

         });
      } catch (Throwable var9) {
         try {
            logging.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      logging.close();
   }

   public void loadBiomeFromPacket(PacketByteBuf buf) {
      ChunkSection[] var2 = this.sectionArray;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChunkSection chunkSection = var2[var4];
         chunkSection.readBiomePacket(buf);
      }

   }

   public void setLoadedToWorld(boolean loadedToWorld) {
      this.loadedToWorld = loadedToWorld;
   }

   public World getWorld() {
      return this.world;
   }

   public Map getBlockEntities() {
      return this.blockEntities;
   }

   public void runPostProcessing(ServerWorld world) {
      ChunkPos chunkPos = this.getPos();

      for(int i = 0; i < this.postProcessingLists.length; ++i) {
         if (this.postProcessingLists[i] != null) {
            ShortListIterator var4 = this.postProcessingLists[i].iterator();

            while(var4.hasNext()) {
               Short short_ = (Short)var4.next();
               BlockPos blockPos = ProtoChunk.joinBlockPos(short_, this.sectionIndexToCoord(i), chunkPos);
               BlockState blockState = this.getBlockState(blockPos);
               FluidState fluidState = blockState.getFluidState();
               if (!fluidState.isEmpty()) {
                  fluidState.onScheduledTick(world, blockPos, blockState);
               }

               if (!(blockState.getBlock() instanceof FluidBlock)) {
                  BlockState blockState2 = Block.postProcessState(blockState, world, blockPos);
                  if (blockState2 != blockState) {
                     world.setBlockState(blockPos, blockState2, 276);
                  }
               }
            }

            this.postProcessingLists[i].clear();
         }
      }

      UnmodifiableIterator var10 = ImmutableList.copyOf(this.blockEntityNbts.keySet()).iterator();

      while(var10.hasNext()) {
         BlockPos blockPos2 = (BlockPos)var10.next();
         this.getBlockEntity(blockPos2);
      }

      this.blockEntityNbts.clear();
      this.upgradeData.upgrade(this);
   }

   @Nullable
   private BlockEntity loadBlockEntity(BlockPos pos, NbtCompound nbt) {
      BlockState blockState = this.getBlockState(pos);
      BlockEntity blockEntity;
      if ("DUMMY".equals(nbt.getString("id", ""))) {
         if (blockState.hasBlockEntity()) {
            blockEntity = ((BlockEntityProvider)blockState.getBlock()).createBlockEntity(pos, blockState);
         } else {
            blockEntity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", pos, blockState);
         }
      } else {
         blockEntity = BlockEntity.createFromNbt(pos, blockState, nbt, this.world.getRegistryManager());
      }

      if (blockEntity != null) {
         blockEntity.setWorld(this.world);
         this.addBlockEntity(blockEntity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", blockState, pos);
      }

      return blockEntity;
   }

   public void disableTickSchedulers(long time) {
      this.blockTickScheduler.disable(time);
      this.fluidTickScheduler.disable(time);
   }

   public void addChunkTickSchedulers(ServerWorld world) {
      world.getBlockTickScheduler().addChunkTickScheduler(this.pos, this.blockTickScheduler);
      world.getFluidTickScheduler().addChunkTickScheduler(this.pos, this.fluidTickScheduler);
   }

   public void removeChunkTickSchedulers(ServerWorld world) {
      world.getBlockTickScheduler().removeChunkTickScheduler(this.pos);
      world.getFluidTickScheduler().removeChunkTickScheduler(this.pos);
   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkLevelType getLevelType() {
      return this.levelTypeProvider == null ? ChunkLevelType.FULL : (ChunkLevelType)this.levelTypeProvider.get();
   }

   public void setLevelTypeProvider(Supplier levelTypeProvider) {
      this.levelTypeProvider = levelTypeProvider;
   }

   public void clear() {
      this.blockEntities.values().forEach(BlockEntity::markRemoved);
      this.blockEntities.clear();
      this.blockEntityTickers.values().forEach((ticker) -> {
         ticker.setWrapped(EMPTY_BLOCK_ENTITY_TICKER);
      });
      this.blockEntityTickers.clear();
   }

   public void updateAllBlockEntities() {
      this.blockEntities.values().forEach((blockEntity) -> {
         World world = this.world;
         if (world instanceof ServerWorld serverWorld) {
            this.updateGameEventListener(blockEntity, serverWorld);
         }

         this.world.loadBlockEntity(blockEntity);
         this.updateTicker(blockEntity);
      });
   }

   private void updateGameEventListener(BlockEntity blockEntity, ServerWorld world) {
      Block block = blockEntity.getCachedState().getBlock();
      if (block instanceof BlockEntityProvider) {
         GameEventListener gameEventListener = ((BlockEntityProvider)block).getGameEventListener(world, blockEntity);
         if (gameEventListener != null) {
            this.getGameEventDispatcher(ChunkSectionPos.getSectionCoord(blockEntity.getPos().getY())).addListener(gameEventListener);
         }
      }

   }

   private void updateTicker(BlockEntity blockEntity) {
      BlockState blockState = blockEntity.getCachedState();
      BlockEntityTicker blockEntityTicker = blockState.getBlockEntityTicker(this.world, blockEntity.getType());
      if (blockEntityTicker == null) {
         this.removeBlockEntityTicker(blockEntity.getPos());
      } else {
         this.blockEntityTickers.compute(blockEntity.getPos(), (pos, ticker) -> {
            BlockEntityTickInvoker blockEntityTickInvoker = this.wrapTicker(blockEntity, blockEntityTicker);
            if (ticker != null) {
               ticker.setWrapped(blockEntityTickInvoker);
               return ticker;
            } else if (this.canTickBlockEntities()) {
               WrappedBlockEntityTickInvoker wrappedBlockEntityTickInvoker = new WrappedBlockEntityTickInvoker(blockEntityTickInvoker);
               this.world.addBlockEntityTicker(wrappedBlockEntityTickInvoker);
               return wrappedBlockEntityTickInvoker;
            } else {
               return null;
            }
         });
      }

   }

   private BlockEntityTickInvoker wrapTicker(BlockEntity blockEntity, BlockEntityTicker blockEntityTicker) {
      return new DirectBlockEntityTickInvoker(blockEntity, blockEntityTicker);
   }

   @FunctionalInterface
   public interface EntityLoader {
      void run(WorldChunk chunk);
   }

   @FunctionalInterface
   public interface UnsavedListener {
      void setUnsaved(ChunkPos pos);
   }

   public static enum CreationType {
      IMMEDIATE,
      QUEUED,
      CHECK;

      // $FF: synthetic method
      private static CreationType[] method_36742() {
         return new CreationType[]{IMMEDIATE, QUEUED, CHECK};
      }
   }

   static class WrappedBlockEntityTickInvoker implements BlockEntityTickInvoker {
      private BlockEntityTickInvoker wrapped;

      WrappedBlockEntityTickInvoker(BlockEntityTickInvoker wrapped) {
         this.wrapped = wrapped;
      }

      void setWrapped(BlockEntityTickInvoker wrapped) {
         this.wrapped = wrapped;
      }

      public void tick() {
         this.wrapped.tick();
      }

      public boolean isRemoved() {
         return this.wrapped.isRemoved();
      }

      public BlockPos getPos() {
         return this.wrapped.getPos();
      }

      public String getName() {
         return this.wrapped.getName();
      }

      public String toString() {
         return String.valueOf(this.wrapped) + " <wrapped>";
      }
   }

   class DirectBlockEntityTickInvoker implements BlockEntityTickInvoker {
      private final BlockEntity blockEntity;
      private final BlockEntityTicker ticker;
      private boolean hasWarned;

      DirectBlockEntityTickInvoker(final BlockEntity blockEntity, final BlockEntityTicker ticker) {
         this.blockEntity = blockEntity;
         this.ticker = ticker;
      }

      public void tick() {
         if (!this.blockEntity.isRemoved() && this.blockEntity.hasWorld()) {
            BlockPos blockPos = this.blockEntity.getPos();
            if (WorldChunk.this.canTickBlockEntity(blockPos)) {
               try {
                  Profiler profiler = Profilers.get();
                  profiler.push(this::getName);
                  BlockState blockState = WorldChunk.this.getBlockState(blockPos);
                  if (this.blockEntity.getType().supports(blockState)) {
                     this.ticker.tick(WorldChunk.this.world, this.blockEntity.getPos(), blockState, this.blockEntity);
                     this.hasWarned = false;
                  } else if (!this.hasWarned) {
                     this.hasWarned = true;
                     WorldChunk.LOGGER.warn("Block entity {} @ {} state {} invalid for ticking:", new Object[]{LogUtils.defer(this::getName), LogUtils.defer(this::getPos), blockState});
                  }

                  profiler.pop();
               } catch (Throwable var5) {
                  CrashReport crashReport = CrashReport.create(var5, "Ticking block entity");
                  CrashReportSection crashReportSection = crashReport.addElement("Block entity being ticked");
                  this.blockEntity.populateCrashReport(crashReportSection);
                  throw new CrashException(crashReport);
               }
            }
         }

      }

      public boolean isRemoved() {
         return this.blockEntity.isRemoved();
      }

      public BlockPos getPos() {
         return this.blockEntity.getPos();
      }

      public String getName() {
         return BlockEntityType.getId(this.blockEntity.getType()).toString();
      }

      public String toString() {
         String var10000 = this.getName();
         return "Level ticker for " + var10000 + "@" + String.valueOf(this.getPos());
      }
   }
}
