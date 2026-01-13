/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.network.handler;

import net.minecraft.network.packet.Packet;
import org.jspecify.annotations.Nullable;

public static interface PacketBundleHandler.Bundler {
    public @Nullable Packet<?> add(Packet<?> var1);
}
