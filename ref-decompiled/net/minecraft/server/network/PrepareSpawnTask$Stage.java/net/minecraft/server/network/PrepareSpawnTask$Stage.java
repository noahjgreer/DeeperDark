/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import net.minecraft.server.network.PrepareSpawnTask;

static sealed interface PrepareSpawnTask.Stage
permits PrepareSpawnTask.LoadPlayerChunks, PrepareSpawnTask.PlayerSpawn {
}
