/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.debug;

import java.util.function.BiConsumer;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockValueDebugS2CPacket;
import net.minecraft.server.debug.TrackedSubscription;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

public static class TrackedSubscription.TrackedVillageSections
extends TrackedSubscription<Unit> {
    public TrackedSubscription.TrackedVillageSections() {
        super(DebugSubscriptionTypes.VILLAGE_SECTIONS);
    }

    @Override
    protected void sendInitial(ServerPlayerEntity player, ChunkPos chunkPos) {
        ServerWorld serverWorld = player.getEntityWorld();
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        pointOfInterestStorage.getInChunk(type -> true, chunkPos, PointOfInterestStorage.OccupationStatus.ANY).forEach(poi -> {
            ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(poi.getPos());
            TrackedSubscription.TrackedVillageSections.forEachSurrounding(serverWorld, chunkSectionPos, (sectionPos, nearOccupiedPoi) -> {
                BlockPos blockPos = sectionPos.getCenterPos();
                serverPlayerEntity.networkHandler.sendPacket(new BlockValueDebugS2CPacket(blockPos, this.type.optionalValueFor(nearOccupiedPoi != false ? Unit.INSTANCE : null)));
            });
        });
    }

    public void onPoiAdded(ServerWorld world, PointOfInterest poi) {
        this.handlePoiUpdate(world, poi.getPos());
    }

    public void onPoiRemoved(ServerWorld world, BlockPos pos) {
        this.handlePoiUpdate(world, pos);
    }

    private void handlePoiUpdate(ServerWorld world, BlockPos pos) {
        TrackedSubscription.TrackedVillageSections.forEachSurrounding(world, ChunkSectionPos.from(pos), (sectionPos, nearOccupiedPoi) -> {
            BlockPos blockPos = sectionPos.getCenterPos();
            if (nearOccupiedPoi.booleanValue()) {
                this.sendToTrackingPlayers(world, new ChunkPos(blockPos), (Packet<ClientPlayPacketListener>)new BlockValueDebugS2CPacket(blockPos, this.type.optionalValueFor(Unit.INSTANCE)));
            } else {
                this.sendToTrackingPlayers(world, new ChunkPos(blockPos), (Packet<ClientPlayPacketListener>)new BlockValueDebugS2CPacket(blockPos, this.type.optionalValueFor()));
            }
        });
    }

    private static void forEachSurrounding(ServerWorld world, ChunkSectionPos sectionPos, BiConsumer<ChunkSectionPos, Boolean> action) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    ChunkSectionPos chunkSectionPos = sectionPos.add(j, k, i);
                    if (world.isNearOccupiedPointOfInterest(chunkSectionPos.getCenterPos())) {
                        action.accept(chunkSectionPos, true);
                        continue;
                    }
                    action.accept(chunkSectionPos, false);
                }
            }
        }
    }
}
