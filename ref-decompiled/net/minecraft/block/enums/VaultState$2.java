/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.enums.VaultState;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

final class VaultState.2
extends VaultState {
    VaultState.2(String string2, VaultState.Light light) {
    }

    @Override
    protected void onChangedTo(ServerWorld world, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
        if (!sharedData.hasDisplayItem()) {
            VaultBlockEntity.Server.updateDisplayItem(world, this, config, sharedData, pos);
        }
        world.syncWorldEvent(3015, pos, ominous ? 1 : 0);
    }
}
