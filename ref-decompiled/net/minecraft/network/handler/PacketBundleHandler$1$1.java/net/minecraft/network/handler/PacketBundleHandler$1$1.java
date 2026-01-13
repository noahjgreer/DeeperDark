/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.handler;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.handler.PacketBundleHandler;
import net.minecraft.network.packet.Packet;
import org.jspecify.annotations.Nullable;

class PacketBundleHandler.1
implements PacketBundleHandler.Bundler {
    private final List<Packet<? super T>> packets = new ArrayList();

    PacketBundleHandler.1() {
    }

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
}
