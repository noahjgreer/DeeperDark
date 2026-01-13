/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.world;

import java.util.List;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

public static interface ChunkHolder.PlayersWatchingChunkProvider {
    public List<ServerPlayerEntity> getPlayersWatchingChunk(ChunkPos var1, boolean var2);
}
