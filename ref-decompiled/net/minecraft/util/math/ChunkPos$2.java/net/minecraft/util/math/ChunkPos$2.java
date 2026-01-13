/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import net.minecraft.util.math.ChunkPos;
import org.jspecify.annotations.Nullable;

static class ChunkPos.2
extends Spliterators.AbstractSpliterator<ChunkPos> {
    private @Nullable ChunkPos position;
    final /* synthetic */ ChunkPos field_18680;
    final /* synthetic */ ChunkPos field_18681;
    final /* synthetic */ int field_18682;
    final /* synthetic */ int field_18683;

    ChunkPos.2(long l, int i, ChunkPos chunkPos, ChunkPos chunkPos2, int j, int k) {
        this.field_18680 = chunkPos;
        this.field_18681 = chunkPos2;
        this.field_18682 = j;
        this.field_18683 = k;
        super(l, i);
    }

    @Override
    public boolean tryAdvance(Consumer<? super ChunkPos> consumer) {
        if (this.position == null) {
            this.position = this.field_18680;
        } else {
            int i = this.position.x;
            int j = this.position.z;
            if (i == this.field_18681.x) {
                if (j == this.field_18681.z) {
                    return false;
                }
                this.position = new ChunkPos(this.field_18680.x, j + this.field_18682);
            } else {
                this.position = new ChunkPos(i + this.field_18683, j);
            }
        }
        consumer.accept(this.position);
        return true;
    }
}
