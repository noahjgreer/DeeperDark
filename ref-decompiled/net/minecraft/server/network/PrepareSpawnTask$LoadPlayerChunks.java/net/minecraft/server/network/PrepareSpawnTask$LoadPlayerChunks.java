/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server.network;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.network.PrepareSpawnTask;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkLoadProgress;
import net.minecraft.world.chunk.ChunkLoadingCounter;
import org.jspecify.annotations.Nullable;

final class PrepareSpawnTask.LoadPlayerChunks
implements PrepareSpawnTask.Stage {
    private final ServerWorld world;
    private final CompletableFuture<Vec3d> spawnPos;
    private final Vec2f rotation;
    private @Nullable CompletableFuture<?> chunkLoadingFuture;
    private final ChunkLoadingCounter chunkCounter = new ChunkLoadingCounter();

    PrepareSpawnTask.LoadPlayerChunks(ServerWorld world, CompletableFuture<Vec3d> spawnPos, Vec2f rotation) {
        this.world = world;
        this.spawnPos = spawnPos;
        this.rotation = rotation;
    }

    public void cancel() {
        this.spawnPos.cancel(false);
    }

    public @Nullable PrepareSpawnTask.PlayerSpawn tryFinish() {
        if (!this.spawnPos.isDone()) {
            return null;
        }
        Vec3d vec3d = this.spawnPos.join();
        if (this.chunkLoadingFuture == null) {
            ChunkPos chunkPos = new ChunkPos(BlockPos.ofFloored(vec3d));
            this.chunkCounter.load(this.world, () -> {
                this.chunkLoadingFuture = this.world.getChunkManager().addChunkLoadingTicket(ChunkTicketType.PLAYER_SPAWN, chunkPos, 3);
            });
            PrepareSpawnTask.this.chunkLoadProgress.init(ChunkLoadProgress.Stage.LOAD_PLAYER_CHUNKS, this.chunkCounter.getTotalChunks());
            PrepareSpawnTask.this.chunkLoadProgress.initSpawnPos(this.world.getRegistryKey(), chunkPos);
        }
        PrepareSpawnTask.this.chunkLoadProgress.progress(ChunkLoadProgress.Stage.LOAD_PLAYER_CHUNKS, this.chunkCounter.getFullChunks(), this.chunkCounter.getTotalChunks());
        if (!this.chunkLoadingFuture.isDone()) {
            return null;
        }
        PrepareSpawnTask.this.chunkLoadProgress.finish(ChunkLoadProgress.Stage.LOAD_PLAYER_CHUNKS);
        return new PrepareSpawnTask.PlayerSpawn(PrepareSpawnTask.this, this.world, vec3d, this.rotation);
    }
}
