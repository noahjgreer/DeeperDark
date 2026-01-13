/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.datafixer.fix;

import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.datafixer.fix.ChunkHeightAndBiomeFix;
import org.jspecify.annotations.Nullable;

public static final class ProtoChunkTickListFix.PalettedSection {
    private static final long MIN_UNIT_SIZE = 4L;
    private final List<? extends Dynamic<?>> palette;
    private final long[] data;
    private final int unitSize;
    private final long unitMask;
    private final int unitsPerLong;

    public ProtoChunkTickListFix.PalettedSection(List<? extends Dynamic<?>> palette, long[] data) {
        this.palette = palette;
        this.data = data;
        this.unitSize = Math.max(4, ChunkHeightAndBiomeFix.ceilLog2(palette.size()));
        this.unitMask = (1L << this.unitSize) - 1L;
        this.unitsPerLong = (char)(64 / this.unitSize);
    }

    public @Nullable Dynamic<?> get(int localX, int localY, int localZ) {
        int i = this.palette.size();
        if (i < 1) {
            return null;
        }
        if (i == 1) {
            return this.palette.getFirst();
        }
        int j = this.packLocalPos(localX, localY, localZ);
        int k = j / this.unitsPerLong;
        if (k < 0 || k >= this.data.length) {
            return null;
        }
        long l = this.data[k];
        int m = (j - k * this.unitsPerLong) * this.unitSize;
        int n = (int)(l >> m & this.unitMask);
        if (n < 0 || n >= i) {
            return null;
        }
        return this.palette.get(n);
    }

    private int packLocalPos(int localX, int localY, int localZ) {
        return (localY << 4 | localZ) << 4 | localX;
    }

    public List<? extends Dynamic<?>> getPalette() {
        return this.palette;
    }

    public long[] getData() {
        return this.data;
    }
}
