/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
static class BiomeColorCache.Colors {
    private final Int2ObjectArrayMap<int[]> colors = new Int2ObjectArrayMap(16);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final int XZ_COLORS_SIZE = MathHelper.square(16);
    private volatile boolean needsCacheRefresh;

    BiomeColorCache.Colors() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int[] get(int y2) {
        this.lock.readLock().lock();
        try {
            int[] is = (int[])this.colors.get(y2);
            if (is != null) {
                int[] nArray = is;
                return nArray;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            int[] nArray = (int[])this.colors.computeIfAbsent(y2, y -> this.createDefault());
            return nArray;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private int[] createDefault() {
        int[] is = new int[XZ_COLORS_SIZE];
        Arrays.fill(is, -1);
        return is;
    }

    public boolean needsCacheRefresh() {
        return this.needsCacheRefresh;
    }

    public void setNeedsCacheRefresh() {
        this.needsCacheRefresh = true;
    }
}
