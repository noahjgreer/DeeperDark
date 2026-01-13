/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientChunkLoadProgress;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
record ClientChunkLoadProgress.LoadChunks(ClientPlayerEntity player, ClientWorld world, WorldRenderer worldRenderer, long timeoutAfter) implements ClientChunkLoadProgress.State
{
    @Override
    public ClientChunkLoadProgress.State next() {
        return this.isReady() ? new ClientChunkLoadProgress.Wait(Util.getMeasuringTimeMs()) : this;
    }

    private boolean isReady() {
        if (Util.getMeasuringTimeMs() > this.timeoutAfter) {
            LOGGER.warn("Timed out while waiting for the client to load chunks, letting the player into the world anyway");
            return true;
        }
        BlockPos blockPos = this.player.getBlockPos();
        if (this.world.isOutOfHeightLimit(blockPos.getY()) || this.player.isSpectator() || !this.player.isAlive()) {
            return true;
        }
        return this.worldRenderer.isRenderingReady(blockPos);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientChunkLoadProgress.LoadChunks.class, "player;level;levelRenderer;timeoutAfter", "player", "world", "worldRenderer", "timeoutAfter"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientChunkLoadProgress.LoadChunks.class, "player;level;levelRenderer;timeoutAfter", "player", "world", "worldRenderer", "timeoutAfter"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientChunkLoadProgress.LoadChunks.class, "player;level;levelRenderer;timeoutAfter", "player", "world", "worldRenderer", "timeoutAfter"}, this, object);
    }
}
