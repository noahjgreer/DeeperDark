/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import java.util.List;

public record LevelPrioritizedQueue.Entry(long chunkPos, List<Runnable> tasks) {
}
