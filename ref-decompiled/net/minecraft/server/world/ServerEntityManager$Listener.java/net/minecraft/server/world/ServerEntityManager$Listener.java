/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.EntityTrackingStatus;

class ServerEntityManager.Listener
implements EntityChangeListener {
    private final T entity;
    private long sectionPos;
    private EntityTrackingSection<T> section;
    final /* synthetic */ ServerEntityManager manager;

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    ServerEntityManager.Listener(T t, long section, EntityTrackingSection<T> entityTrackingSection) {
        void var3_3;
        void entity;
        this.manager = (ServerEntityManager)serverEntityManager;
        this.entity = entity;
        this.sectionPos = var3_3;
        this.section = (EntityTrackingSection)section;
    }

    @Override
    public void updateEntityPosition() {
        BlockPos blockPos = this.entity.getBlockPos();
        long l = ChunkSectionPos.toLong(blockPos);
        if (l != this.sectionPos) {
            EntityTrackingStatus entityTrackingStatus = this.section.getStatus();
            if (!this.section.remove(this.entity)) {
                LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", new Object[]{this.entity, ChunkSectionPos.from(this.sectionPos), l});
            }
            this.manager.entityLeftSection(this.sectionPos, this.section);
            EntityTrackingSection entityTrackingSection = this.manager.cache.getTrackingSection(l);
            entityTrackingSection.add(this.entity);
            this.section = entityTrackingSection;
            this.sectionPos = l;
            this.updateLoadStatus(entityTrackingStatus, entityTrackingSection.getStatus());
        }
    }

    private void updateLoadStatus(EntityTrackingStatus oldStatus, EntityTrackingStatus newStatus) {
        EntityTrackingStatus entityTrackingStatus2;
        EntityTrackingStatus entityTrackingStatus = ServerEntityManager.getNeededLoadStatus(this.entity, oldStatus);
        if (entityTrackingStatus == (entityTrackingStatus2 = ServerEntityManager.getNeededLoadStatus(this.entity, newStatus))) {
            if (entityTrackingStatus2.shouldTrack()) {
                this.manager.handler.updateLoadStatus(this.entity);
            }
            return;
        }
        boolean bl = entityTrackingStatus.shouldTrack();
        boolean bl2 = entityTrackingStatus2.shouldTrack();
        if (bl && !bl2) {
            this.manager.stopTracking(this.entity);
        } else if (!bl && bl2) {
            this.manager.startTracking(this.entity);
        }
        boolean bl3 = entityTrackingStatus.shouldTick();
        boolean bl4 = entityTrackingStatus2.shouldTick();
        if (bl3 && !bl4) {
            this.manager.stopTicking(this.entity);
        } else if (!bl3 && bl4) {
            this.manager.startTicking(this.entity);
        }
        if (bl2) {
            this.manager.handler.updateLoadStatus(this.entity);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        EntityTrackingStatus entityTrackingStatus;
        if (!this.section.remove(this.entity)) {
            LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, ChunkSectionPos.from(this.sectionPos), reason});
        }
        if ((entityTrackingStatus = ServerEntityManager.getNeededLoadStatus(this.entity, this.section.getStatus())).shouldTick()) {
            this.manager.stopTicking(this.entity);
        }
        if (entityTrackingStatus.shouldTrack()) {
            this.manager.stopTracking(this.entity);
        }
        if (reason.shouldDestroy()) {
            this.manager.handler.destroy(this.entity);
        }
        this.manager.entityUuids.remove(this.entity.getUuid());
        this.entity.setChangeListener(NONE);
        this.manager.entityLeftSection(this.sectionPos, this.section);
    }
}
