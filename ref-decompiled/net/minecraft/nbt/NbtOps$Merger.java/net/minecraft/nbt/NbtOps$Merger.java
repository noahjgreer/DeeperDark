/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

import java.util.stream.Stream;
import net.minecraft.nbt.NbtElement;

static interface NbtOps.Merger {
    public NbtOps.Merger merge(NbtElement var1);

    default public NbtOps.Merger merge(Iterable<NbtElement> nbts) {
        NbtOps.Merger merger = this;
        for (NbtElement nbtElement : nbts) {
            merger = merger.merge(nbtElement);
        }
        return merger;
    }

    default public NbtOps.Merger merge(Stream<NbtElement> nbts) {
        return this.merge(nbts::iterator);
    }

    public NbtElement getResult();
}
