/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.packet.BundlePacket;
import net.minecraft.network.packet.BundleSplitterPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import org.jspecify.annotations.Nullable;

static class PacketBundleHandler.1
implements PacketBundleHandler {
    final /* synthetic */ PacketType field_48610;
    final /* synthetic */ BundleSplitterPacket field_48611;
    final /* synthetic */ Function field_48612;

    PacketBundleHandler.1() {
        this.field_48610 = packetType;
        this.field_48611 = bundleSplitterPacket;
        this.field_48612 = function;
    }

    @Override
    public void forEachPacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
        if (packet.getPacketType() == this.field_48610) {
            BundlePacket bundlePacket = (BundlePacket)packet;
            consumer.accept(this.field_48611);
            bundlePacket.getPackets().forEach(consumer);
            consumer.accept(this.field_48611);
        } else {
            consumer.accept(packet);
        }
    }

    @Override
    public @Nullable PacketBundleHandler.Bundler createBundler(Packet<?> splitter) {
        if (splitter == this.field_48611) {
            return new PacketBundleHandler.Bundler(){
                private final List<Packet<? super T>> packets = new ArrayList();

                @Override
                public @Nullable Packet<?> add(Packet<?> packet) {
                    if (packet == field_48611) {
                        return (Packet)field_48612.apply(this.packets);
                    }
                    Packet<?> packet2 = packet;
                    if (this.packets.size() >= 4096) {
                        throw new IllegalStateException("Too many packets in a bundle");
                    }
                    this.packets.add(packet2);
                    return null;
                }
            };
        }
        return null;
    }
}
