/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientChunkLoadProgress;

@Environment(value=EnvType.CLIENT)
static sealed interface ClientChunkLoadProgress.State
permits ClientChunkLoadProgress.Start, ClientChunkLoadProgress.LoadChunks, ClientChunkLoadProgress.Wait {
    default public ClientChunkLoadProgress.State next() {
        return this;
    }

    default public ClientChunkLoadProgress.State initialChunksComing() {
        return this;
    }
}
