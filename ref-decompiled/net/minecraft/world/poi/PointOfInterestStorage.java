package net.minecraft.world.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.server.world.ChunkErrorHandler;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.SectionDistanceLevelPropagator;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.storage.ChunkPosKeyedStorage;
import net.minecraft.world.storage.SerializingRegionBasedStorage;
import net.minecraft.world.storage.StorageKey;

public class PointOfInterestStorage extends SerializingRegionBasedStorage {
   public static final int field_30265 = 6;
   public static final int field_30266 = 1;
   private final PointOfInterestDistanceTracker pointOfInterestDistanceTracker = new PointOfInterestDistanceTracker();
   private final LongSet preloadedChunks = new LongOpenHashSet();

   public PointOfInterestStorage(StorageKey storageKey, Path directory, DataFixer dataFixer, boolean dsync, DynamicRegistryManager registryManager, ChunkErrorHandler errorHandler, HeightLimitView world) {
      super(new ChunkPosKeyedStorage(storageKey, directory, dataFixer, dsync, DataFixTypes.POI_CHUNK), PointOfInterestSet.Serialized.CODEC, PointOfInterestSet::toSerialized, PointOfInterestSet.Serialized::toPointOfInterestSet, PointOfInterestSet::new, registryManager, errorHandler, world);
   }

   public void add(BlockPos pos, RegistryEntry type) {
      ((PointOfInterestSet)this.getOrCreate(ChunkSectionPos.toLong(pos))).add(pos, type);
   }

   public void remove(BlockPos pos) {
      this.get(ChunkSectionPos.toLong(pos)).ifPresent((poiSet) -> {
         poiSet.remove(pos);
      });
   }

   public long count(Predicate typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getInCircle(typePredicate, pos, radius, occupationStatus).count();
   }

   public boolean hasTypeAt(RegistryKey type, BlockPos pos) {
      return this.test(pos, (entry) -> {
         return entry.matchesKey(type);
      });
   }

   public Stream getInSquare(Predicate typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      int i = Math.floorDiv(radius, 16) + 1;
      return ChunkPos.stream(new ChunkPos(pos), i).flatMap((chunkPos) -> {
         return this.getInChunk(typePredicate, chunkPos, occupationStatus);
      }).filter((poi) -> {
         BlockPos blockPos2 = poi.getPos();
         return Math.abs(blockPos2.getX() - pos.getX()) <= radius && Math.abs(blockPos2.getZ() - pos.getZ()) <= radius;
      });
   }

   public Stream getInCircle(Predicate typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      int i = radius * radius;
      return this.getInSquare(typePredicate, pos, radius, occupationStatus).filter((poi) -> {
         return poi.getPos().getSquaredDistance(pos) <= (double)i;
      });
   }

   @Debug
   public Stream getInChunk(Predicate typePredicate, ChunkPos chunkPos, OccupationStatus occupationStatus) {
      return IntStream.rangeClosed(this.world.getBottomSectionCoord(), this.world.getTopSectionCoord()).boxed().map((coord) -> {
         return this.get(ChunkSectionPos.from(chunkPos, coord).asLong());
      }).filter(Optional::isPresent).flatMap((poiSet) -> {
         return ((PointOfInterestSet)poiSet.get()).get(typePredicate, occupationStatus);
      });
   }

   public Stream getPositions(Predicate typePredicate, Predicate posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getInCircle(typePredicate, pos, radius, occupationStatus).map(PointOfInterest::getPos).filter(posPredicate);
   }

   public Stream getTypesAndPositions(Predicate typePredicate, Predicate posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getInCircle(typePredicate, pos, radius, occupationStatus).filter((poi) -> {
         return posPredicate.test(poi.getPos());
      }).map((poi) -> {
         return Pair.of(poi.getType(), poi.getPos());
      });
   }

   public Stream getSortedTypesAndPositions(Predicate typePredicate, Predicate posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getTypesAndPositions(typePredicate, posPredicate, pos, radius, occupationStatus).sorted(Comparator.comparingDouble((pair) -> {
         return ((BlockPos)pair.getSecond()).getSquaredDistance(pos);
      }));
   }

   public Optional getPosition(Predicate typePredicate, Predicate posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getPositions(typePredicate, posPredicate, pos, radius, occupationStatus).findFirst();
   }

   public Optional getNearestPosition(Predicate typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getInCircle(typePredicate, pos, radius, occupationStatus).map(PointOfInterest::getPos).min(Comparator.comparingDouble((poiPos) -> {
         return poiPos.getSquaredDistance(pos);
      }));
   }

   public Optional getNearestTypeAndPosition(Predicate typePredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getInCircle(typePredicate, pos, radius, occupationStatus).min(Comparator.comparingDouble((poi) -> {
         return poi.getPos().getSquaredDistance(pos);
      })).map((poi) -> {
         return Pair.of(poi.getType(), poi.getPos());
      });
   }

   public Optional getNearestPosition(Predicate typePredicate, Predicate posPredicate, BlockPos pos, int radius, OccupationStatus occupationStatus) {
      return this.getInCircle(typePredicate, pos, radius, occupationStatus).map(PointOfInterest::getPos).filter(posPredicate).min(Comparator.comparingDouble((poiPos) -> {
         return poiPos.getSquaredDistance(pos);
      }));
   }

   public Optional getPosition(Predicate typePredicate, BiPredicate posPredicate, BlockPos pos, int radius) {
      return this.getInCircle(typePredicate, pos, radius, PointOfInterestStorage.OccupationStatus.HAS_SPACE).filter((poi) -> {
         return posPredicate.test(poi.getType(), poi.getPos());
      }).findFirst().map((poi) -> {
         poi.reserveTicket();
         return poi.getPos();
      });
   }

   public Optional getPosition(Predicate typePredicate, Predicate positionPredicate, OccupationStatus occupationStatus, BlockPos pos, int radius, Random random) {
      List list = Util.copyShuffled(this.getInCircle(typePredicate, pos, radius, occupationStatus), random);
      return list.stream().filter((poi) -> {
         return positionPredicate.test(poi.getPos());
      }).findFirst().map(PointOfInterest::getPos);
   }

   public boolean releaseTicket(BlockPos pos) {
      return (Boolean)this.get(ChunkSectionPos.toLong(pos)).map((poiSet) -> {
         return poiSet.releaseTicket(pos);
      }).orElseThrow(() -> {
         return (IllegalStateException)Util.getFatalOrPause(new IllegalStateException("POI never registered at " + String.valueOf(pos)));
      });
   }

   public boolean test(BlockPos pos, Predicate predicate) {
      return (Boolean)this.get(ChunkSectionPos.toLong(pos)).map((poiSet) -> {
         return poiSet.test(pos, predicate);
      }).orElse(false);
   }

   public Optional getType(BlockPos pos) {
      return this.get(ChunkSectionPos.toLong(pos)).flatMap((poiSet) -> {
         return poiSet.getType(pos);
      });
   }

   /** @deprecated */
   @Deprecated
   @Debug
   public int getFreeTickets(BlockPos pos) {
      return (Integer)this.get(ChunkSectionPos.toLong(pos)).map((poiSet) -> {
         return poiSet.getFreeTickets(pos);
      }).orElse(0);
   }

   public int getDistanceFromNearestOccupied(ChunkSectionPos pos) {
      this.pointOfInterestDistanceTracker.update();
      return this.pointOfInterestDistanceTracker.getLevel(pos.asLong());
   }

   boolean isOccupied(long pos) {
      Optional optional = this.getIfLoaded(pos);
      return optional == null ? false : (Boolean)optional.map((poiSet) -> {
         return poiSet.get((entry) -> {
            return entry.isIn(PointOfInterestTypeTags.VILLAGE);
         }, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).findAny().isPresent();
      }).orElse(false);
   }

   public void tick(BooleanSupplier shouldKeepTicking) {
      super.tick(shouldKeepTicking);
      this.pointOfInterestDistanceTracker.update();
   }

   protected void onUpdate(long pos) {
      super.onUpdate(pos);
      this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
   }

   protected void onLoad(long pos) {
      this.pointOfInterestDistanceTracker.update(pos, this.pointOfInterestDistanceTracker.getInitialLevel(pos), false);
   }

   public void initForPalette(ChunkSectionPos sectionPos, ChunkSection chunkSection) {
      Util.ifPresentOrElse(this.get(sectionPos.asLong()), (poiSet) -> {
         poiSet.updatePointsOfInterest((populator) -> {
            if (shouldScan(chunkSection)) {
               this.scanAndPopulate(chunkSection, sectionPos, populator);
            }

         });
      }, () -> {
         if (shouldScan(chunkSection)) {
            PointOfInterestSet pointOfInterestSet = (PointOfInterestSet)this.getOrCreate(sectionPos.asLong());
            Objects.requireNonNull(pointOfInterestSet);
            this.scanAndPopulate(chunkSection, sectionPos, pointOfInterestSet::add);
         }

      });
   }

   private static boolean shouldScan(ChunkSection chunkSection) {
      return chunkSection.hasAny(PointOfInterestTypes::isPointOfInterest);
   }

   private void scanAndPopulate(ChunkSection chunkSection, ChunkSectionPos sectionPos, BiConsumer populator) {
      sectionPos.streamBlocks().forEach((pos) -> {
         BlockState blockState = chunkSection.getBlockState(ChunkSectionPos.getLocalCoord(pos.getX()), ChunkSectionPos.getLocalCoord(pos.getY()), ChunkSectionPos.getLocalCoord(pos.getZ()));
         PointOfInterestTypes.getTypeForState(blockState).ifPresent((poiType) -> {
            populator.accept(pos, poiType);
         });
      });
   }

   public void preloadChunks(WorldView world, BlockPos pos, int radius) {
      ChunkSectionPos.stream(new ChunkPos(pos), Math.floorDiv(radius, 16), this.world.getBottomSectionCoord(), this.world.getTopSectionCoord()).map((sectionPos) -> {
         return Pair.of(sectionPos, this.get(sectionPos.asLong()));
      }).filter((pair) -> {
         return !(Boolean)((Optional)pair.getSecond()).map(PointOfInterestSet::isValid).orElse(false);
      }).map((pair) -> {
         return ((ChunkSectionPos)pair.getFirst()).toChunkPos();
      }).filter((chunkPos) -> {
         return this.preloadedChunks.add(chunkPos.toLong());
      }).forEach((chunkPos) -> {
         world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY);
      });
   }

   private final class PointOfInterestDistanceTracker extends SectionDistanceLevelPropagator {
      private final Long2ByteMap distances = new Long2ByteOpenHashMap();

      protected PointOfInterestDistanceTracker() {
         super(7, 16, 256);
         this.distances.defaultReturnValue((byte)7);
      }

      protected int getInitialLevel(long id) {
         return PointOfInterestStorage.this.isOccupied(id) ? 0 : 7;
      }

      protected int getLevel(long id) {
         return this.distances.get(id);
      }

      protected void setLevel(long id, int level) {
         if (level > 6) {
            this.distances.remove(id);
         } else {
            this.distances.put(id, (byte)level);
         }

      }

      public void update() {
         super.applyPendingUpdates(Integer.MAX_VALUE);
      }
   }

   public static enum OccupationStatus {
      HAS_SPACE(PointOfInterest::hasSpace),
      IS_OCCUPIED(PointOfInterest::isOccupied),
      ANY((poi) -> {
         return true;
      });

      private final Predicate predicate;

      private OccupationStatus(final Predicate predicate) {
         this.predicate = predicate;
      }

      public Predicate getPredicate() {
         return this.predicate;
      }

      // $FF: synthetic method
      private static OccupationStatus[] method_36629() {
         return new OccupationStatus[]{HAS_SPACE, IS_OCCUPIED, ANY};
      }
   }
}
