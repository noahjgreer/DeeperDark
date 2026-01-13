/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package net.minecraft.server.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import net.minecraft.server.network.ChunkFilter;
import net.minecraft.util.math.ChunkPos;

public record ChunkFilter.Cylindrical(ChunkPos center, int viewDistance) implements ChunkFilter
{
    int getLeft() {
        return this.center.x - this.viewDistance - 1;
    }

    int getBottom() {
        return this.center.z - this.viewDistance - 1;
    }

    int getRight() {
        return this.center.x + this.viewDistance + 1;
    }

    int getTop() {
        return this.center.z + this.viewDistance + 1;
    }

    @VisibleForTesting
    protected boolean overlaps(ChunkFilter.Cylindrical o) {
        return this.getLeft() <= o.getRight() && this.getRight() >= o.getLeft() && this.getBottom() <= o.getTop() && this.getTop() >= o.getBottom();
    }

    @Override
    public boolean isWithinDistance(int x, int z, boolean includeEdge) {
        return ChunkFilter.isWithinDistance(this.center.x, this.center.z, this.viewDistance, x, z, includeEdge);
    }

    @Override
    public void forEach(Consumer<ChunkPos> consumer) {
        for (int i = this.getLeft(); i <= this.getRight(); ++i) {
            for (int j = this.getBottom(); j <= this.getTop(); ++j) {
                if (!this.isWithinDistance(i, j)) continue;
                consumer.accept(new ChunkPos(i, j));
            }
        }
    }
}
