/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.ChunkSectionPos;

static class ChunkSectionPos.1
extends Spliterators.AbstractSpliterator<ChunkSectionPos> {
    final CuboidBlockIterator iterator;
    final /* synthetic */ int field_19264;
    final /* synthetic */ int field_19265;
    final /* synthetic */ int field_19266;
    final /* synthetic */ int field_19267;
    final /* synthetic */ int field_19268;
    final /* synthetic */ int field_19269;

    ChunkSectionPos.1(long l, int i, int j, int k, int m, int n, int o, int p) {
        this.field_19264 = j;
        this.field_19265 = k;
        this.field_19266 = m;
        this.field_19267 = n;
        this.field_19268 = o;
        this.field_19269 = p;
        super(l, i);
        this.iterator = new CuboidBlockIterator(this.field_19264, this.field_19265, this.field_19266, this.field_19267, this.field_19268, this.field_19269);
    }

    @Override
    public boolean tryAdvance(Consumer<? super ChunkSectionPos> consumer) {
        if (this.iterator.step()) {
            consumer.accept(new ChunkSectionPos(this.iterator.getX(), this.iterator.getY(), this.iterator.getZ()));
            return true;
        }
        return false;
    }
}
