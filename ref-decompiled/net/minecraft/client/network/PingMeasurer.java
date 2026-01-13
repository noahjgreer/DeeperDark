/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.network.PingMeasurer
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.query.QueryPingC2SPacket
 *  net.minecraft.network.packet.s2c.query.PingResultS2CPacket
 *  net.minecraft.util.Util
 *  net.minecraft.util.profiler.MultiValueDebugSampleLogImpl
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;

@Environment(value=EnvType.CLIENT)
public class PingMeasurer {
    private final ClientPlayNetworkHandler handler;
    private final MultiValueDebugSampleLogImpl log;

    public PingMeasurer(ClientPlayNetworkHandler handler, MultiValueDebugSampleLogImpl log) {
        this.handler = handler;
        this.log = log;
    }

    public void ping() {
        this.handler.sendPacket((Packet)new QueryPingC2SPacket(Util.getMeasuringTimeMs()));
    }

    public void onPingResult(PingResultS2CPacket packet) {
        this.log.push(Util.getMeasuringTimeMs() - packet.startTime());
    }
}

