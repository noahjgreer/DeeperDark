/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.crash.CrashException;

record PacketApplyBatcher.Entry<T extends PacketListener>(T listener, Packet<T> packet) {
    public void apply() {
        if (this.listener.accepts(this.packet)) {
            try {
                this.packet.apply(this.listener);
            }
            catch (Exception exception) {
                CrashException crashException;
                if (exception instanceof CrashException && (crashException = (CrashException)exception).getCause() instanceof OutOfMemoryError) {
                    throw NetworkThreadUtils.createCrashException(exception, this.packet, this.listener);
                }
                this.listener.onPacketException(this.packet, exception);
            }
        } else {
            LOGGER.debug("Ignoring packet due to disconnection: {}", this.packet);
        }
    }
}
