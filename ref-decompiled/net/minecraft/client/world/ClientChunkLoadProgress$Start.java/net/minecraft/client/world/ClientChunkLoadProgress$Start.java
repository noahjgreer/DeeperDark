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

@Environment(value=EnvType.CLIENT)
record ClientChunkLoadProgress.Start(ClientPlayerEntity player, ClientWorld world, WorldRenderer worldRenderer, long timeoutAfter) implements ClientChunkLoadProgress.State
{
    @Override
    public ClientChunkLoadProgress.State initialChunksComing() {
        return new ClientChunkLoadProgress.LoadChunks(this.player, this.world, this.worldRenderer, this.timeoutAfter);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ClientChunkLoadProgress.Start.class, "player;level;levelRenderer;timeoutAfter", "player", "world", "worldRenderer", "timeoutAfter"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ClientChunkLoadProgress.Start.class, "player;level;levelRenderer;timeoutAfter", "player", "world", "worldRenderer", "timeoutAfter"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ClientChunkLoadProgress.Start.class, "player;level;levelRenderer;timeoutAfter", "player", "world", "worldRenderer", "timeoutAfter"}, this, object);
    }
}
