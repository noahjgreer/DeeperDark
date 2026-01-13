/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import java.util.Optional;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.PrepareSpawnTask;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

final class PrepareSpawnTask.PlayerSpawn
implements PrepareSpawnTask.Stage {
    private final ServerWorld world;
    private final Vec3d spawnPos;
    private final Vec2f rotation;

    PrepareSpawnTask.PlayerSpawn(ServerWorld world, Vec3d spawnPos, Vec2f rotation) {
        this.world = world;
        this.spawnPos = spawnPos;
        this.rotation = rotation;
    }

    public void tick() {
        this.world.getChunkManager().addTicket(ChunkTicketType.PLAYER_SPAWN, new ChunkPos(BlockPos.ofFloored(this.spawnPos)), 3);
    }

    public ServerPlayerEntity onReady(ClientConnection connection, ConnectedClientData clientData) {
        ChunkPos chunkPos = new ChunkPos(BlockPos.ofFloored(this.spawnPos));
        this.world.loadChunks(chunkPos, 3);
        ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(PrepareSpawnTask.this.server, this.world, clientData.gameProfile(), clientData.syncedOptions());
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(serverPlayerEntity.getErrorReporterContext(), LOGGER);){
            Optional<ReadView> optional = PrepareSpawnTask.this.server.getPlayerManager().loadPlayerData(PrepareSpawnTask.this.player).map(playerData -> NbtReadView.create(logging, PrepareSpawnTask.this.server.getRegistryManager(), playerData));
            optional.ifPresent(serverPlayerEntity::readData);
            serverPlayerEntity.refreshPositionAndAngles(this.spawnPos, this.rotation.x, this.rotation.y);
            PrepareSpawnTask.this.server.getPlayerManager().onPlayerConnect(connection, serverPlayerEntity, clientData);
            optional.ifPresent(playerData -> {
                serverPlayerEntity.readEnderPearls((ReadView)playerData);
                serverPlayerEntity.readRootVehicle((ReadView)playerData);
            });
            ServerPlayerEntity serverPlayerEntity2 = serverPlayerEntity;
            return serverPlayerEntity2;
        }
    }
}
