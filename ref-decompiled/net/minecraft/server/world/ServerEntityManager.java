package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.CsvWriter;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.EntityTrackingStatus;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.world.entity.SimpleEntityLookup;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkDataList;
import org.slf4j.Logger;

public class ServerEntityManager implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   final Set entityUuids = Sets.newHashSet();
   final EntityHandler handler;
   private final ChunkDataAccess dataAccess;
   private final EntityIndex index = new EntityIndex();
   final SectionedEntityCache cache;
   private final EntityLookup lookup;
   private final Long2ObjectMap trackingStatuses = new Long2ObjectOpenHashMap();
   private final Long2ObjectMap managedStatuses = new Long2ObjectOpenHashMap();
   private final LongSet pendingUnloads = new LongOpenHashSet();
   private final Queue loadingQueue = Queues.newConcurrentLinkedQueue();

   public ServerEntityManager(Class entityClass, EntityHandler handler, ChunkDataAccess dataAccess) {
      this.cache = new SectionedEntityCache(entityClass, this.trackingStatuses);
      this.trackingStatuses.defaultReturnValue(EntityTrackingStatus.HIDDEN);
      this.managedStatuses.defaultReturnValue(ServerEntityManager.Status.FRESH);
      this.handler = handler;
      this.dataAccess = dataAccess;
      this.lookup = new SimpleEntityLookup(this.index, this.cache);
   }

   void entityLeftSection(long sectionPos, EntityTrackingSection section) {
      if (section.isEmpty()) {
         this.cache.removeSection(sectionPos);
      }

   }

   private boolean addEntityUuid(EntityLike entity) {
      if (!this.entityUuids.add(entity.getUuid())) {
         LOGGER.warn("UUID of added entity already exists: {}", entity);
         return false;
      } else {
         return true;
      }
   }

   public boolean addEntity(EntityLike entity) {
      return this.addEntity(entity, false);
   }

   private boolean addEntity(EntityLike entity, boolean existing) {
      if (!this.addEntityUuid(entity)) {
         return false;
      } else {
         long l = ChunkSectionPos.toLong(entity.getBlockPos());
         EntityTrackingSection entityTrackingSection = this.cache.getTrackingSection(l);
         entityTrackingSection.add(entity);
         entity.setChangeListener(new Listener(entity, l, entityTrackingSection));
         if (!existing) {
            this.handler.create(entity);
         }

         EntityTrackingStatus entityTrackingStatus = getNeededLoadStatus(entity, entityTrackingSection.getStatus());
         if (entityTrackingStatus.shouldTrack()) {
            this.startTracking(entity);
         }

         if (entityTrackingStatus.shouldTick()) {
            this.startTicking(entity);
         }

         return true;
      }
   }

   static EntityTrackingStatus getNeededLoadStatus(EntityLike entity, EntityTrackingStatus current) {
      return entity.isPlayer() ? EntityTrackingStatus.TICKING : current;
   }

   public boolean shouldTickTest(ChunkPos pos) {
      return ((EntityTrackingStatus)this.trackingStatuses.get(pos.toLong())).shouldTick();
   }

   public void loadEntities(Stream entities) {
      entities.forEach((entity) -> {
         this.addEntity(entity, true);
      });
   }

   public void addEntities(Stream entities) {
      entities.forEach((entity) -> {
         this.addEntity(entity, false);
      });
   }

   void startTicking(EntityLike entity) {
      this.handler.startTicking(entity);
   }

   void stopTicking(EntityLike entity) {
      this.handler.stopTicking(entity);
   }

   void startTracking(EntityLike entity) {
      this.index.add(entity);
      this.handler.startTracking(entity);
   }

   void stopTracking(EntityLike entity) {
      this.handler.stopTracking(entity);
      this.index.remove(entity);
   }

   public void updateTrackingStatus(ChunkPos chunkPos, ChunkLevelType levelType) {
      EntityTrackingStatus entityTrackingStatus = EntityTrackingStatus.fromLevelType(levelType);
      this.updateTrackingStatus(chunkPos, entityTrackingStatus);
   }

   public void updateTrackingStatus(ChunkPos chunkPos, EntityTrackingStatus trackingStatus) {
      long l = chunkPos.toLong();
      if (trackingStatus == EntityTrackingStatus.HIDDEN) {
         this.trackingStatuses.remove(l);
         this.pendingUnloads.add(l);
      } else {
         this.trackingStatuses.put(l, trackingStatus);
         this.pendingUnloads.remove(l);
         this.readIfFresh(l);
      }

      this.cache.getTrackingSections(l).forEach((group) -> {
         EntityTrackingStatus entityTrackingStatus2 = group.swapStatus(trackingStatus);
         boolean bl = entityTrackingStatus2.shouldTrack();
         boolean bl2 = trackingStatus.shouldTrack();
         boolean bl3 = entityTrackingStatus2.shouldTick();
         boolean bl4 = trackingStatus.shouldTick();
         if (bl3 && !bl4) {
            group.stream().filter((entity) -> {
               return !entity.isPlayer();
            }).forEach(this::stopTicking);
         }

         if (bl && !bl2) {
            group.stream().filter((entity) -> {
               return !entity.isPlayer();
            }).forEach(this::stopTracking);
         } else if (!bl && bl2) {
            group.stream().filter((entity) -> {
               return !entity.isPlayer();
            }).forEach(this::startTracking);
         }

         if (!bl3 && bl4) {
            group.stream().filter((entity) -> {
               return !entity.isPlayer();
            }).forEach(this::startTicking);
         }

      });
   }

   private void readIfFresh(long chunkPos) {
      Status status = (Status)this.managedStatuses.get(chunkPos);
      if (status == ServerEntityManager.Status.FRESH) {
         this.scheduleRead(chunkPos);
      }

   }

   private boolean trySave(long chunkPos, Consumer action) {
      Status status = (Status)this.managedStatuses.get(chunkPos);
      if (status == ServerEntityManager.Status.PENDING) {
         return false;
      } else {
         List list = (List)this.cache.getTrackingSections(chunkPos).flatMap((section) -> {
            return section.stream().filter(EntityLike::shouldSave);
         }).collect(Collectors.toList());
         if (list.isEmpty()) {
            if (status == ServerEntityManager.Status.LOADED) {
               this.dataAccess.writeChunkData(new ChunkDataList(new ChunkPos(chunkPos), ImmutableList.of()));
            }

            return true;
         } else if (status == ServerEntityManager.Status.FRESH) {
            this.scheduleRead(chunkPos);
            return false;
         } else {
            this.dataAccess.writeChunkData(new ChunkDataList(new ChunkPos(chunkPos), list));
            list.forEach(action);
            return true;
         }
      }
   }

   private void scheduleRead(long chunkPos) {
      this.managedStatuses.put(chunkPos, ServerEntityManager.Status.PENDING);
      ChunkPos chunkPos2 = new ChunkPos(chunkPos);
      CompletableFuture var10000 = this.dataAccess.readChunkData(chunkPos2);
      Queue var10001 = this.loadingQueue;
      Objects.requireNonNull(var10001);
      var10000.thenAccept(var10001::add).exceptionally((throwable) -> {
         LOGGER.error("Failed to read chunk {}", chunkPos2, throwable);
         return null;
      });
   }

   private boolean unload(long chunkPos) {
      boolean bl = this.trySave(chunkPos, (entity) -> {
         entity.streamPassengersAndSelf().forEach(this::unload);
      });
      if (!bl) {
         return false;
      } else {
         this.managedStatuses.remove(chunkPos);
         return true;
      }
   }

   private void unload(EntityLike entity) {
      entity.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
      entity.setChangeListener(EntityChangeListener.NONE);
   }

   private void unloadChunks() {
      this.pendingUnloads.removeIf((pos) -> {
         return this.trackingStatuses.get(pos) != EntityTrackingStatus.HIDDEN ? true : this.unload(pos);
      });
   }

   public void loadChunks() {
      ChunkDataList chunkDataList;
      while((chunkDataList = (ChunkDataList)this.loadingQueue.poll()) != null) {
         chunkDataList.stream().forEach((entity) -> {
            this.addEntity(entity, true);
         });
         this.managedStatuses.put(chunkDataList.getChunkPos().toLong(), ServerEntityManager.Status.LOADED);
      }

   }

   public void tick() {
      this.loadChunks();
      this.unloadChunks();
   }

   private LongSet getLoadedChunks() {
      LongSet longSet = this.cache.getChunkPositions();
      ObjectIterator var2 = Long2ObjectMaps.fastIterable(this.managedStatuses).iterator();

      while(var2.hasNext()) {
         Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)var2.next();
         if (entry.getValue() == ServerEntityManager.Status.LOADED) {
            longSet.add(entry.getLongKey());
         }
      }

      return longSet;
   }

   public void save() {
      this.getLoadedChunks().forEach((pos) -> {
         boolean bl = this.trackingStatuses.get(pos) == EntityTrackingStatus.HIDDEN;
         if (bl) {
            this.unload(pos);
         } else {
            this.trySave(pos, (entity) -> {
            });
         }

      });
   }

   public void flush() {
      LongSet longSet = this.getLoadedChunks();

      while(!longSet.isEmpty()) {
         this.dataAccess.awaitAll(false);
         this.loadChunks();
         longSet.removeIf((pos) -> {
            boolean bl = this.trackingStatuses.get(pos) == EntityTrackingStatus.HIDDEN;
            return bl ? this.unload(pos) : this.trySave(pos, (entity) -> {
            });
         });
      }

      this.dataAccess.awaitAll(true);
   }

   public void close() throws IOException {
      this.flush();
      this.dataAccess.close();
   }

   public boolean has(UUID uuid) {
      return this.entityUuids.contains(uuid);
   }

   public EntityLookup getLookup() {
      return this.lookup;
   }

   public boolean shouldTick(BlockPos pos) {
      return ((EntityTrackingStatus)this.trackingStatuses.get(ChunkPos.toLong(pos))).shouldTick();
   }

   public boolean shouldTick(ChunkPos pos) {
      return ((EntityTrackingStatus)this.trackingStatuses.get(pos.toLong())).shouldTick();
   }

   public boolean isLoaded(long chunkPos) {
      return this.managedStatuses.get(chunkPos) == ServerEntityManager.Status.LOADED;
   }

   public void dump(Writer writer) throws IOException {
      CsvWriter csvWriter = CsvWriter.makeHeader().addColumn("x").addColumn("y").addColumn("z").addColumn("visibility").addColumn("load_status").addColumn("entity_count").startBody(writer);
      this.cache.getChunkPositions().forEach((chunkPos) -> {
         Status status = (Status)this.managedStatuses.get(chunkPos);
         this.cache.getSections(chunkPos).forEach((sectionPos) -> {
            EntityTrackingSection entityTrackingSection = this.cache.findTrackingSection(sectionPos);
            if (entityTrackingSection != null) {
               try {
                  csvWriter.printRow(ChunkSectionPos.unpackX(sectionPos), ChunkSectionPos.unpackY(sectionPos), ChunkSectionPos.unpackZ(sectionPos), entityTrackingSection.getStatus(), status, entityTrackingSection.size());
               } catch (IOException var7) {
                  throw new UncheckedIOException(var7);
               }
            }

         });
      });
   }

   @Debug
   public String getDebugString() {
      int var10000 = this.entityUuids.size();
      return "" + var10000 + "," + this.index.size() + "," + this.cache.sectionCount() + "," + this.managedStatuses.size() + "," + this.trackingStatuses.size() + "," + this.loadingQueue.size() + "," + this.pendingUnloads.size();
   }

   @Debug
   public int getIndexSize() {
      return this.index.size();
   }

   private static enum Status {
      FRESH,
      PENDING,
      LOADED;

      // $FF: synthetic method
      private static Status[] method_36746() {
         return new Status[]{FRESH, PENDING, LOADED};
      }
   }

   class Listener implements EntityChangeListener {
      private final EntityLike entity;
      private long sectionPos;
      private EntityTrackingSection section;

      Listener(final EntityLike entity, final long sectionPos, final EntityTrackingSection section) {
         this.entity = entity;
         this.sectionPos = sectionPos;
         this.section = section;
      }

      public void updateEntityPosition() {
         BlockPos blockPos = this.entity.getBlockPos();
         long l = ChunkSectionPos.toLong(blockPos);
         if (l != this.sectionPos) {
            EntityTrackingStatus entityTrackingStatus = this.section.getStatus();
            if (!this.section.remove(this.entity)) {
               ServerEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", new Object[]{this.entity, ChunkSectionPos.from(this.sectionPos), l});
            }

            ServerEntityManager.this.entityLeftSection(this.sectionPos, this.section);
            EntityTrackingSection entityTrackingSection = ServerEntityManager.this.cache.getTrackingSection(l);
            entityTrackingSection.add(this.entity);
            this.section = entityTrackingSection;
            this.sectionPos = l;
            this.updateLoadStatus(entityTrackingStatus, entityTrackingSection.getStatus());
         }

      }

      private void updateLoadStatus(EntityTrackingStatus oldStatus, EntityTrackingStatus newStatus) {
         EntityTrackingStatus entityTrackingStatus = ServerEntityManager.getNeededLoadStatus(this.entity, oldStatus);
         EntityTrackingStatus entityTrackingStatus2 = ServerEntityManager.getNeededLoadStatus(this.entity, newStatus);
         if (entityTrackingStatus == entityTrackingStatus2) {
            if (entityTrackingStatus2.shouldTrack()) {
               ServerEntityManager.this.handler.updateLoadStatus(this.entity);
            }

         } else {
            boolean bl = entityTrackingStatus.shouldTrack();
            boolean bl2 = entityTrackingStatus2.shouldTrack();
            if (bl && !bl2) {
               ServerEntityManager.this.stopTracking(this.entity);
            } else if (!bl && bl2) {
               ServerEntityManager.this.startTracking(this.entity);
            }

            boolean bl3 = entityTrackingStatus.shouldTick();
            boolean bl4 = entityTrackingStatus2.shouldTick();
            if (bl3 && !bl4) {
               ServerEntityManager.this.stopTicking(this.entity);
            } else if (!bl3 && bl4) {
               ServerEntityManager.this.startTicking(this.entity);
            }

            if (bl2) {
               ServerEntityManager.this.handler.updateLoadStatus(this.entity);
            }

         }
      }

      public void remove(Entity.RemovalReason reason) {
         if (!this.section.remove(this.entity)) {
            ServerEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, ChunkSectionPos.from(this.sectionPos), reason});
         }

         EntityTrackingStatus entityTrackingStatus = ServerEntityManager.getNeededLoadStatus(this.entity, this.section.getStatus());
         if (entityTrackingStatus.shouldTick()) {
            ServerEntityManager.this.stopTicking(this.entity);
         }

         if (entityTrackingStatus.shouldTrack()) {
            ServerEntityManager.this.stopTracking(this.entity);
         }

         if (reason.shouldDestroy()) {
            ServerEntityManager.this.handler.destroy(this.entity);
         }

         ServerEntityManager.this.entityUuids.remove(this.entity.getUuid());
         this.entity.setChangeListener(NONE);
         ServerEntityManager.this.entityLeftSection(this.sectionPos, this.section);
      }
   }
}
