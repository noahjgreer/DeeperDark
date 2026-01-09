package net.minecraft.server.world;

import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkHolder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

public class ChunkHolder extends AbstractChunkHolder {
   public static final OptionalChunk UNLOADED_WORLD_CHUNK = OptionalChunk.of("Unloaded level chunk");
   private static final CompletableFuture UNLOADED_WORLD_CHUNK_FUTURE;
   private final HeightLimitView world;
   private volatile CompletableFuture accessibleFuture;
   private volatile CompletableFuture tickingFuture;
   private volatile CompletableFuture entityTickingFuture;
   private int lastTickLevel;
   private int level;
   private int completedLevel;
   private boolean pendingBlockUpdates;
   private final ShortSet[] blockUpdatesBySection;
   private final BitSet blockLightUpdateBits;
   private final BitSet skyLightUpdateBits;
   private final LightingProvider lightingProvider;
   private final LevelUpdateListener levelUpdateListener;
   private final PlayersWatchingChunkProvider playersWatchingChunkProvider;
   private boolean accessible;
   private CompletableFuture levelIncreaseFuture;
   private CompletableFuture postProcessingFuture;
   private CompletableFuture savingFuture;

   public ChunkHolder(ChunkPos pos, int level, HeightLimitView world, LightingProvider lightingProvider, LevelUpdateListener levelUpdateListener, PlayersWatchingChunkProvider playersWatchingChunkProvider) {
      super(pos);
      this.accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
      this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
      this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
      this.blockLightUpdateBits = new BitSet();
      this.skyLightUpdateBits = new BitSet();
      this.levelIncreaseFuture = CompletableFuture.completedFuture((Object)null);
      this.postProcessingFuture = CompletableFuture.completedFuture((Object)null);
      this.savingFuture = CompletableFuture.completedFuture((Object)null);
      this.world = world;
      this.lightingProvider = lightingProvider;
      this.levelUpdateListener = levelUpdateListener;
      this.playersWatchingChunkProvider = playersWatchingChunkProvider;
      this.lastTickLevel = ChunkLevels.INACCESSIBLE + 1;
      this.level = this.lastTickLevel;
      this.completedLevel = this.lastTickLevel;
      this.setLevel(level);
      this.blockUpdatesBySection = new ShortSet[world.countVerticalSections()];
   }

   public CompletableFuture getTickingFuture() {
      return this.tickingFuture;
   }

   public CompletableFuture getEntityTickingFuture() {
      return this.entityTickingFuture;
   }

   public CompletableFuture getAccessibleFuture() {
      return this.accessibleFuture;
   }

   @Nullable
   public WorldChunk getWorldChunk() {
      return (WorldChunk)((OptionalChunk)this.getTickingFuture().getNow(UNLOADED_WORLD_CHUNK)).orElse((Object)null);
   }

   @Nullable
   public WorldChunk getPostProcessedChunk() {
      return !this.postProcessingFuture.isDone() ? null : this.getWorldChunk();
   }

   public CompletableFuture getPostProcessingFuture() {
      return this.postProcessingFuture;
   }

   public void combinePostProcessingFuture(CompletableFuture postProcessingFuture) {
      if (this.postProcessingFuture.isDone()) {
         this.postProcessingFuture = postProcessingFuture;
      } else {
         this.postProcessingFuture = this.postProcessingFuture.thenCombine(postProcessingFuture, (object, object2) -> {
            return null;
         });
      }

   }

   public CompletableFuture getSavingFuture() {
      return this.savingFuture;
   }

   public boolean isSavable() {
      return this.savingFuture.isDone();
   }

   protected void combineSavingFuture(CompletableFuture savingFuture) {
      if (this.savingFuture.isDone()) {
         this.savingFuture = savingFuture;
      } else {
         this.savingFuture = this.savingFuture.thenCombine(savingFuture, (object, thenResult) -> {
            return null;
         });
      }

   }

   public boolean markForBlockUpdate(BlockPos pos) {
      WorldChunk worldChunk = this.getWorldChunk();
      if (worldChunk == null) {
         return false;
      } else {
         boolean bl = this.pendingBlockUpdates;
         int i = this.world.getSectionIndex(pos.getY());
         if (this.blockUpdatesBySection[i] == null) {
            this.pendingBlockUpdates = true;
            this.blockUpdatesBySection[i] = new ShortOpenHashSet();
         }

         this.blockUpdatesBySection[i].add(ChunkSectionPos.packLocal(pos));
         return !bl;
      }
   }

   public boolean markForLightUpdate(LightType lightType, int y) {
      Chunk chunk = this.getOrNull(ChunkStatus.INITIALIZE_LIGHT);
      if (chunk == null) {
         return false;
      } else {
         chunk.markNeedsSaving();
         WorldChunk worldChunk = this.getWorldChunk();
         if (worldChunk == null) {
            return false;
         } else {
            int i = this.lightingProvider.getBottomY();
            int j = this.lightingProvider.getTopY();
            if (y >= i && y <= j) {
               BitSet bitSet = lightType == LightType.SKY ? this.skyLightUpdateBits : this.blockLightUpdateBits;
               int k = y - i;
               if (!bitSet.get(k)) {
                  bitSet.set(k);
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean hasPendingUpdates() {
      return this.pendingBlockUpdates || !this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty();
   }

   public void flushUpdates(WorldChunk chunk) {
      if (this.hasPendingUpdates()) {
         World world = chunk.getWorld();
         List list;
         if (!this.skyLightUpdateBits.isEmpty() || !this.blockLightUpdateBits.isEmpty()) {
            list = this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, true);
            if (!list.isEmpty()) {
               LightUpdateS2CPacket lightUpdateS2CPacket = new LightUpdateS2CPacket(chunk.getPos(), this.lightingProvider, this.skyLightUpdateBits, this.blockLightUpdateBits);
               this.sendPacketToPlayers(list, lightUpdateS2CPacket);
            }

            this.skyLightUpdateBits.clear();
            this.blockLightUpdateBits.clear();
         }

         if (this.pendingBlockUpdates) {
            list = this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, false);

            for(int i = 0; i < this.blockUpdatesBySection.length; ++i) {
               ShortSet shortSet = this.blockUpdatesBySection[i];
               if (shortSet != null) {
                  this.blockUpdatesBySection[i] = null;
                  if (!list.isEmpty()) {
                     int j = this.world.sectionIndexToCoord(i);
                     ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk.getPos(), j);
                     if (shortSet.size() == 1) {
                        BlockPos blockPos = chunkSectionPos.unpackBlockPos(shortSet.iterator().nextShort());
                        BlockState blockState = world.getBlockState(blockPos);
                        this.sendPacketToPlayers(list, new BlockUpdateS2CPacket(blockPos, blockState));
                        this.tryUpdateBlockEntityAt(list, world, blockPos, blockState);
                     } else {
                        ChunkSection chunkSection = chunk.getSection(i);
                        ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket = new ChunkDeltaUpdateS2CPacket(chunkSectionPos, shortSet, chunkSection);
                        this.sendPacketToPlayers(list, chunkDeltaUpdateS2CPacket);
                        chunkDeltaUpdateS2CPacket.visitUpdates((pos, state) -> {
                           this.tryUpdateBlockEntityAt(list, world, pos, state);
                        });
                     }
                  }
               }
            }

            this.pendingBlockUpdates = false;
         }
      }
   }

   private void tryUpdateBlockEntityAt(List players, World world, BlockPos pos, BlockState state) {
      if (state.hasBlockEntity()) {
         this.sendBlockEntityUpdatePacket(players, world, pos);
      }

   }

   private void sendBlockEntityUpdatePacket(List players, World world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity != null) {
         Packet packet = blockEntity.toUpdatePacket();
         if (packet != null) {
            this.sendPacketToPlayers(players, packet);
         }
      }

   }

   private void sendPacketToPlayers(List players, Packet packet) {
      players.forEach((player) -> {
         player.networkHandler.sendPacket(packet);
      });
   }

   public int getLevel() {
      return this.level;
   }

   public int getCompletedLevel() {
      return this.completedLevel;
   }

   private void setCompletedLevel(int level) {
      this.completedLevel = level;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   private void increaseLevel(ServerChunkLoadingManager chunkLoadingManager, CompletableFuture chunkFuture, Executor executor, ChunkLevelType target) {
      this.levelIncreaseFuture.cancel(false);
      CompletableFuture completableFuture = new CompletableFuture();
      completableFuture.thenRunAsync(() -> {
         chunkLoadingManager.onChunkStatusChange(this.pos, target);
      }, executor);
      this.levelIncreaseFuture = completableFuture;
      chunkFuture.thenAccept((optionalChunk) -> {
         optionalChunk.ifPresent((chunk) -> {
            completableFuture.complete((Object)null);
         });
      });
   }

   private void decreaseLevel(ServerChunkLoadingManager chunkLoadingManager, ChunkLevelType target) {
      this.levelIncreaseFuture.cancel(false);
      chunkLoadingManager.onChunkStatusChange(this.pos, target);
   }

   protected void updateFutures(ServerChunkLoadingManager chunkLoadingManager, Executor executor) {
      ChunkLevelType chunkLevelType = ChunkLevels.getType(this.lastTickLevel);
      ChunkLevelType chunkLevelType2 = ChunkLevels.getType(this.level);
      boolean bl = chunkLevelType.isAfter(ChunkLevelType.FULL);
      boolean bl2 = chunkLevelType2.isAfter(ChunkLevelType.FULL);
      this.accessible |= bl2;
      if (!bl && bl2) {
         this.accessibleFuture = chunkLoadingManager.makeChunkAccessible(this);
         this.increaseLevel(chunkLoadingManager, this.accessibleFuture, executor, ChunkLevelType.FULL);
         this.combineSavingFuture(this.accessibleFuture);
      }

      if (bl && !bl2) {
         this.accessibleFuture.complete(UNLOADED_WORLD_CHUNK);
         this.accessibleFuture = UNLOADED_WORLD_CHUNK_FUTURE;
      }

      boolean bl3 = chunkLevelType.isAfter(ChunkLevelType.BLOCK_TICKING);
      boolean bl4 = chunkLevelType2.isAfter(ChunkLevelType.BLOCK_TICKING);
      if (!bl3 && bl4) {
         this.tickingFuture = chunkLoadingManager.makeChunkTickable(this);
         this.increaseLevel(chunkLoadingManager, this.tickingFuture, executor, ChunkLevelType.BLOCK_TICKING);
         this.combineSavingFuture(this.tickingFuture);
      }

      if (bl3 && !bl4) {
         this.tickingFuture.complete(UNLOADED_WORLD_CHUNK);
         this.tickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
      }

      boolean bl5 = chunkLevelType.isAfter(ChunkLevelType.ENTITY_TICKING);
      boolean bl6 = chunkLevelType2.isAfter(ChunkLevelType.ENTITY_TICKING);
      if (!bl5 && bl6) {
         if (this.entityTickingFuture != UNLOADED_WORLD_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException());
         }

         this.entityTickingFuture = chunkLoadingManager.makeChunkEntitiesTickable(this);
         this.increaseLevel(chunkLoadingManager, this.entityTickingFuture, executor, ChunkLevelType.ENTITY_TICKING);
         this.combineSavingFuture(this.entityTickingFuture);
      }

      if (bl5 && !bl6) {
         this.entityTickingFuture.complete(UNLOADED_WORLD_CHUNK);
         this.entityTickingFuture = UNLOADED_WORLD_CHUNK_FUTURE;
      }

      if (!chunkLevelType2.isAfter(chunkLevelType)) {
         this.decreaseLevel(chunkLoadingManager, chunkLevelType2);
      }

      this.levelUpdateListener.updateLevel(this.pos, this::getCompletedLevel, this.level, this::setCompletedLevel);
      this.lastTickLevel = this.level;
   }

   public boolean isAccessible() {
      return this.accessible;
   }

   public void updateAccessibleStatus() {
      this.accessible = ChunkLevels.getType(this.level).isAfter(ChunkLevelType.FULL);
   }

   static {
      UNLOADED_WORLD_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_WORLD_CHUNK);
   }

   @FunctionalInterface
   public interface LevelUpdateListener {
      void updateLevel(ChunkPos pos, IntSupplier levelGetter, int targetLevel, IntConsumer levelSetter);
   }

   public interface PlayersWatchingChunkProvider {
      List getPlayersWatchingChunk(ChunkPos chunkPos, boolean onlyOnWatchDistanceEdge);
   }
}
