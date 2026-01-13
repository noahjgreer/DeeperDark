/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
static class PendingUpdateManager.PendingUpdate {
    final Vec3d playerPos;
    int sequence;
    BlockState blockState;

    PendingUpdateManager.PendingUpdate(int sequence, BlockState blockState, Vec3d playerPos) {
        this.sequence = sequence;
        this.blockState = blockState;
        this.playerPos = playerPos;
    }

    PendingUpdateManager.PendingUpdate withSequence(int sequence) {
        this.sequence = sequence;
        return this;
    }

    void setBlockState(BlockState state) {
        this.blockState = state;
    }
}
