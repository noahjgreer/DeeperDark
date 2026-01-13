/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.packet.s2c.play;

import io.netty.buffer.ByteBuf;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.function.ValueLists;
import net.minecraft.world.waypoint.TrackedWaypoint;
import net.minecraft.world.waypoint.TrackedWaypointHandler;
import net.minecraft.world.waypoint.WaypointHandler;

static final class WaypointS2CPacket.Operation
extends Enum<WaypointS2CPacket.Operation> {
    public static final /* enum */ WaypointS2CPacket.Operation TRACK = new WaypointS2CPacket.Operation(WaypointHandler::onTrack);
    public static final /* enum */ WaypointS2CPacket.Operation UNTRACK = new WaypointS2CPacket.Operation(WaypointHandler::onUntrack);
    public static final /* enum */ WaypointS2CPacket.Operation UPDATE = new WaypointS2CPacket.Operation(WaypointHandler::onUpdate);
    final BiConsumer<TrackedWaypointHandler, TrackedWaypoint> handler;
    public static final IntFunction<WaypointS2CPacket.Operation> BY_INDEX;
    public static final PacketCodec<ByteBuf, WaypointS2CPacket.Operation> PACKET_CODEC;
    private static final /* synthetic */ WaypointS2CPacket.Operation[] field_59619;

    public static WaypointS2CPacket.Operation[] values() {
        return (WaypointS2CPacket.Operation[])field_59619.clone();
    }

    public static WaypointS2CPacket.Operation valueOf(String string) {
        return Enum.valueOf(WaypointS2CPacket.Operation.class, string);
    }

    private WaypointS2CPacket.Operation(BiConsumer<TrackedWaypointHandler, TrackedWaypoint> handler) {
        this.handler = handler;
    }

    private static /* synthetic */ WaypointS2CPacket.Operation[] method_70591() {
        return new WaypointS2CPacket.Operation[]{TRACK, UNTRACK, UPDATE};
    }

    static {
        field_59619 = WaypointS2CPacket.Operation.method_70591();
        BY_INDEX = ValueLists.createIndexToValueFunction(Enum::ordinal, WaypointS2CPacket.Operation.values(), ValueLists.OutOfBoundsHandling.WRAP);
        PACKET_CODEC = PacketCodecs.indexed(BY_INDEX, Enum::ordinal);
    }
}
