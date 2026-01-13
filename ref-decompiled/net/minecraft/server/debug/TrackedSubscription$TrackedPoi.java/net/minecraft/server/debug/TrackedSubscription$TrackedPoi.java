/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.debug;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockValueDebugS2CPacket;
import net.minecraft.server.debug.TrackedSubscription;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.data.PoiDebugData;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

public static class TrackedSubscription.TrackedPoi
extends TrackedSubscription<PoiDebugData> {
    public TrackedSubscription.TrackedPoi() {
        super(DebugSubscriptionTypes.POIS);
    }

    @Override
    protected void sendInitial(ServerPlayerEntity player, ChunkPos chunkPos) {
        ServerWorld serverWorld = player.getEntityWorld();
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        pointOfInterestStorage.getInChunk(type -> true, chunkPos, PointOfInterestStorage.OccupationStatus.ANY).forEach(poi -> serverPlayerEntity.networkHandler.sendPacket(new BlockValueDebugS2CPacket(poi.getPos(), this.type.optionalValueFor(new PoiDebugData((PointOfInterest)poi)))));
    }

    public void onPoiAdded(ServerWorld world, PointOfInterest poi) {
        this.sendToTrackingPlayers(world, new ChunkPos(poi.getPos()), (Packet<ClientPlayPacketListener>)new BlockValueDebugS2CPacket(poi.getPos(), this.type.optionalValueFor(new PoiDebugData(poi))));
    }

    public void onPoiRemoved(ServerWorld world, BlockPos pos) {
        this.sendToTrackingPlayers(world, new ChunkPos(pos), (Packet<ClientPlayPacketListener>)new BlockValueDebugS2CPacket(pos, this.type.optionalValueFor()));
    }

    public void onPoiUpdated(ServerWorld world, BlockPos pos) {
        this.sendToTrackingPlayers(world, new ChunkPos(pos), (Packet<ClientPlayPacketListener>)new BlockValueDebugS2CPacket(pos, this.type.optionalValueFor(world.getPointOfInterestStorage().getDebugData(pos))));
    }
}
