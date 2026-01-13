/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.world;

import java.util.concurrent.Executor;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ChunkLevelManager;
import net.minecraft.server.world.ChunkTicketManager;
import org.jspecify.annotations.Nullable;

class ServerChunkLoadingManager.LevelManager
extends ChunkLevelManager {
    protected ServerChunkLoadingManager.LevelManager(ChunkTicketManager ticketManager, Executor executor, Executor mainThreadExecutor) {
        super(ticketManager, executor, mainThreadExecutor);
    }

    @Override
    protected boolean isUnloaded(long pos) {
        return ServerChunkLoadingManager.this.unloadedChunks.contains(pos);
    }

    @Override
    protected @Nullable ChunkHolder getChunkHolder(long pos) {
        return ServerChunkLoadingManager.this.getCurrentChunkHolder(pos);
    }

    @Override
    protected @Nullable ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i) {
        return ServerChunkLoadingManager.this.setLevel(pos, level, holder, i);
    }
}
