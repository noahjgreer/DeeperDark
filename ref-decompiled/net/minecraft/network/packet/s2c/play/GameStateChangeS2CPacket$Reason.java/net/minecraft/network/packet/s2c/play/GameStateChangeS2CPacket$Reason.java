/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public static class GameStateChangeS2CPacket.Reason {
    static final Int2ObjectMap<GameStateChangeS2CPacket.Reason> REASONS = new Int2ObjectOpenHashMap();
    final int id;

    public GameStateChangeS2CPacket.Reason(int id) {
        this.id = id;
        REASONS.put(id, (Object)this);
    }
}
