package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.slf4j.Logger;

public class PointOfInterestSet {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Short2ObjectMap pointsOfInterestByPos;
   private final Map pointsOfInterestByType;
   private final Runnable updateListener;
   private boolean valid;

   public PointOfInterestSet(Runnable updateListener) {
      this(updateListener, true, ImmutableList.of());
   }

   PointOfInterestSet(Runnable updateListener, boolean valid, List pois) {
      this.pointsOfInterestByPos = new Short2ObjectOpenHashMap();
      this.pointsOfInterestByType = Maps.newHashMap();
      this.updateListener = updateListener;
      this.valid = valid;
      pois.forEach(this::add);
   }

   public Serialized toSerialized() {
      return new Serialized(this.valid, this.pointsOfInterestByPos.values().stream().map(PointOfInterest::toSerialized).toList());
   }

   public Stream get(Predicate predicate, PointOfInterestStorage.OccupationStatus occupationStatus) {
      return this.pointsOfInterestByType.entrySet().stream().filter((entry) -> {
         return predicate.test((RegistryEntry)entry.getKey());
      }).flatMap((entry) -> {
         return ((Set)entry.getValue()).stream();
      }).filter(occupationStatus.getPredicate());
   }

   public void add(BlockPos pos, RegistryEntry type) {
      if (this.add(new PointOfInterest(pos, type, this.updateListener))) {
         LOGGER.debug("Added POI of type {} @ {}", type.getIdAsString(), pos);
         this.updateListener.run();
      }

   }

   private boolean add(PointOfInterest poi) {
      BlockPos blockPos = poi.getPos();
      RegistryEntry registryEntry = poi.getType();
      short s = ChunkSectionPos.packLocal(blockPos);
      PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(s);
      if (pointOfInterest != null) {
         if (registryEntry.equals(pointOfInterest.getType())) {
            return false;
         }

         Util.logErrorOrPause("POI data mismatch: already registered at " + String.valueOf(blockPos));
      }

      this.pointsOfInterestByPos.put(s, poi);
      ((Set)this.pointsOfInterestByType.computeIfAbsent(registryEntry, (type) -> {
         return Sets.newHashSet();
      })).add(poi);
      return true;
   }

   public void remove(BlockPos pos) {
      PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.remove(ChunkSectionPos.packLocal(pos));
      if (pointOfInterest == null) {
         LOGGER.error("POI data mismatch: never registered at {}", pos);
      } else {
         ((Set)this.pointsOfInterestByType.get(pointOfInterest.getType())).remove(pointOfInterest);
         Logger var10000 = LOGGER;
         Objects.requireNonNull(pointOfInterest);
         Object var10002 = LogUtils.defer(pointOfInterest::getType);
         Objects.requireNonNull(pointOfInterest);
         var10000.debug("Removed POI of type {} @ {}", var10002, LogUtils.defer(pointOfInterest::getPos));
         this.updateListener.run();
      }
   }

   /** @deprecated */
   @Deprecated
   @Debug
   public int getFreeTickets(BlockPos pos) {
      return (Integer)this.get(pos).map(PointOfInterest::getFreeTickets).orElse(0);
   }

   public boolean releaseTicket(BlockPos pos) {
      PointOfInterest pointOfInterest = (PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.packLocal(pos));
      if (pointOfInterest == null) {
         throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException("POI never registered at " + String.valueOf(pos)));
      } else {
         boolean bl = pointOfInterest.releaseTicket();
         this.updateListener.run();
         return bl;
      }
   }

   public boolean test(BlockPos pos, Predicate predicate) {
      return this.getType(pos).filter(predicate).isPresent();
   }

   public Optional getType(BlockPos pos) {
      return this.get(pos).map(PointOfInterest::getType);
   }

   private Optional get(BlockPos pos) {
      return Optional.ofNullable((PointOfInterest)this.pointsOfInterestByPos.get(ChunkSectionPos.packLocal(pos)));
   }

   public void updatePointsOfInterest(Consumer updater) {
      if (!this.valid) {
         Short2ObjectMap short2ObjectMap = new Short2ObjectOpenHashMap(this.pointsOfInterestByPos);
         this.clear();
         updater.accept((pos, poiEntry) -> {
            short s = ChunkSectionPos.packLocal(pos);
            PointOfInterest pointOfInterest = (PointOfInterest)short2ObjectMap.computeIfAbsent(s, (sx) -> {
               return new PointOfInterest(pos, poiEntry, this.updateListener);
            });
            this.add(pointOfInterest);
         });
         this.valid = true;
         this.updateListener.run();
      }

   }

   private void clear() {
      this.pointsOfInterestByPos.clear();
      this.pointsOfInterestByType.clear();
   }

   boolean isValid() {
      return this.valid;
   }

   public static record Serialized(boolean isValid, List records) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.BOOL.lenientOptionalFieldOf("Valid", false).forGetter(Serialized::isValid), PointOfInterest.Serialized.CODEC.listOf().fieldOf("Records").forGetter(Serialized::records)).apply(instance, Serialized::new);
      });

      public Serialized(boolean bl, List list) {
         this.isValid = bl;
         this.records = list;
      }

      public PointOfInterestSet toPointOfInterestSet(Runnable updateListener) {
         return new PointOfInterestSet(updateListener, this.isValid, this.records.stream().map((serialized) -> {
            return serialized.toPointOfInterest(updateListener);
         }).toList());
      }

      public boolean isValid() {
         return this.isValid;
      }

      public List records() {
         return this.records;
      }
   }
}
