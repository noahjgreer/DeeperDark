/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.util.math.ChunkPos;

@FunctionalInterface
public static interface ChunkHolder.LevelUpdateListener {
    public void updateLevel(ChunkPos var1, IntSupplier var2, int var3, IntConsumer var4);
}
