package net.minecraft.world.entity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.slf4j.Logger;

public class ClientEntityManager {
   static final Logger LOGGER = LogUtils.getLogger();
   final EntityHandler handler;
   final EntityIndex index = new EntityIndex();
   final SectionedEntityCache cache;
   private final LongSet tickingChunkSections = new LongOpenHashSet();
   private final EntityLookup lookup;

   public ClientEntityManager(Class entityClass, EntityHandler handler) {
      this.cache = new SectionedEntityCache(entityClass, (pos) -> {
         return this.tickingChunkSections.contains(pos) ? EntityTrackingStatus.TICKING : EntityTrackingStatus.TRACKED;
      });
      this.handler = handler;
      this.lookup = new SimpleEntityLookup(this.index, this.cache);
   }

   public void startTicking(ChunkPos pos) {
      long l = pos.toLong();
      this.tickingChunkSections.add(l);
      this.cache.getTrackingSections(l).forEach((sections) -> {
         EntityTrackingStatus entityTrackingStatus = sections.swapStatus(EntityTrackingStatus.TICKING);
         if (!entityTrackingStatus.shouldTick()) {
            Stream var10000 = sections.stream().filter((e) -> {
               return !e.isPlayer();
            });
            EntityHandler var10001 = this.handler;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::startTicking);
         }

      });
   }

   public void stopTicking(ChunkPos pos) {
      long l = pos.toLong();
      this.tickingChunkSections.remove(l);
      this.cache.getTrackingSections(l).forEach((sections) -> {
         EntityTrackingStatus entityTrackingStatus = sections.swapStatus(EntityTrackingStatus.TRACKED);
         if (entityTrackingStatus.shouldTick()) {
            Stream var10000 = sections.stream().filter((e) -> {
               return !e.isPlayer();
            });
            EntityHandler var10001 = this.handler;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::stopTicking);
         }

      });
   }

   public EntityLookup getLookup() {
      return this.lookup;
   }

   public void addEntity(EntityLike entity) {
      this.index.add(entity);
      long l = ChunkSectionPos.toLong(entity.getBlockPos());
      EntityTrackingSection entityTrackingSection = this.cache.getTrackingSection(l);
      entityTrackingSection.add(entity);
      entity.setChangeListener(new Listener(entity, l, entityTrackingSection));
      this.handler.create(entity);
      this.handler.startTracking(entity);
      if (entity.isPlayer() || entityTrackingSection.getStatus().shouldTick()) {
         this.handler.startTicking(entity);
      }

   }

   @Debug
   public int getEntityCount() {
      return this.index.size();
   }

   void removeIfEmpty(long packedChunkSection, EntityTrackingSection entities) {
      if (entities.isEmpty()) {
         this.cache.removeSection(packedChunkSection);
      }

   }

   @Debug
   public String getDebugString() {
      int var10000 = this.index.size();
      return "" + var10000 + "," + this.cache.sectionCount() + "," + this.tickingChunkSections.size();
   }

   private class Listener implements EntityChangeListener {
      private final EntityLike entity;
      private long lastSectionPos;
      private EntityTrackingSection section;

      Listener(final EntityLike entity, final long pos, final EntityTrackingSection section) {
         this.entity = entity;
         this.lastSectionPos = pos;
         this.section = section;
      }

      public void updateEntityPosition() {
         BlockPos blockPos = this.entity.getBlockPos();
         long l = ChunkSectionPos.toLong(blockPos);
         if (l != this.lastSectionPos) {
            EntityTrackingStatus entityTrackingStatus = this.section.getStatus();
            if (!this.section.remove(this.entity)) {
               ClientEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", new Object[]{this.entity, ChunkSectionPos.from(this.lastSectionPos), l});
            }

            ClientEntityManager.this.removeIfEmpty(this.lastSectionPos, this.section);
            EntityTrackingSection entityTrackingSection = ClientEntityManager.this.cache.getTrackingSection(l);
            entityTrackingSection.add(this.entity);
            this.section = entityTrackingSection;
            this.lastSectionPos = l;
            ClientEntityManager.this.handler.updateLoadStatus(this.entity);
            if (!this.entity.isPlayer()) {
               boolean bl = entityTrackingStatus.shouldTick();
               boolean bl2 = entityTrackingSection.getStatus().shouldTick();
               if (bl && !bl2) {
                  ClientEntityManager.this.handler.stopTicking(this.entity);
               } else if (!bl && bl2) {
                  ClientEntityManager.this.handler.startTicking(this.entity);
               }
            }
         }

      }

      public void remove(Entity.RemovalReason reason) {
         if (!this.section.remove(this.entity)) {
            ClientEntityManager.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, ChunkSectionPos.from(this.lastSectionPos), reason});
         }

         EntityTrackingStatus entityTrackingStatus = this.section.getStatus();
         if (entityTrackingStatus.shouldTick() || this.entity.isPlayer()) {
            ClientEntityManager.this.handler.stopTicking(this.entity);
         }

         ClientEntityManager.this.handler.stopTracking(this.entity);
         ClientEntityManager.this.handler.destroy(this.entity);
         ClientEntityManager.this.index.remove(this.entity);
         this.entity.setChangeListener(NONE);
         ClientEntityManager.this.removeIfEmpty(this.lastSectionPos, this.section);
      }
   }
}
