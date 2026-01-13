/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk$Task
 *  net.minecraft.client.render.chunk.ChunkRenderTaskScheduler
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderTaskScheduler {
    private static final int field_53953 = 2;
    private int remainingPrioritizableTasks = 2;
    private final List<ChunkBuilder.BuiltChunk.Task> queue = new ObjectArrayList();

    public synchronized void enqueue(ChunkBuilder.BuiltChunk.Task task) {
        this.queue.add(task);
    }

    public synchronized // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkBuilder.BuiltChunk.Task dequeueNearest(Vec3d pos) {
        boolean bl2;
        int i = -1;
        int j = -1;
        double d = Double.MAX_VALUE;
        double e = Double.MAX_VALUE;
        ListIterator listIterator = this.queue.listIterator();
        while (listIterator.hasNext()) {
            int k = listIterator.nextIndex();
            ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)listIterator.next();
            if (task.cancelled.get()) {
                listIterator.remove();
                continue;
            }
            double f = task.getOrigin().getSquaredDistance((Position)pos);
            if (!task.isPrioritized() && f < d) {
                d = f;
                i = k;
            }
            if (!task.isPrioritized() || !(f < e)) continue;
            e = f;
            j = k;
        }
        boolean bl = j >= 0;
        boolean bl3 = bl2 = i >= 0;
        if (bl && (!bl2 || this.remainingPrioritizableTasks > 0 && e < d)) {
            --this.remainingPrioritizableTasks;
            return this.remove(j);
        }
        this.remainingPrioritizableTasks = 2;
        return this.remove(i);
    }

    public int size() {
        return this.queue.size();
    }

    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkBuilder.BuiltChunk.Task remove(int index) {
        if (index >= 0) {
            return (ChunkBuilder.BuiltChunk.Task)this.queue.remove(index);
        }
        return null;
    }

    public synchronized void cancelAll() {
        for (ChunkBuilder.BuiltChunk.Task task : this.queue) {
            task.cancel();
        }
        this.queue.clear();
    }
}

