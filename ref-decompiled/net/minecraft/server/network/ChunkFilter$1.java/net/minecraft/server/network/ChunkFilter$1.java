/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import java.util.function.Consumer;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.math.ChunkPos;

class ChunkFilter.1
implements ChunkFilter {
    ChunkFilter.1() {
    }

    @Override
    public boolean isWithinDistance(int x, int z, boolean includeEdge) {
        return false;
    }

    @Override
    public void forEach(Consumer<ChunkPos> consumer) {
    }
}
