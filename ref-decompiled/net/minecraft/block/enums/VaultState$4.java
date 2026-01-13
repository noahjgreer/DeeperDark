/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.block.enums.VaultState;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

final class VaultState.4
extends VaultState {
    VaultState.4(String string2, VaultState.Light light) {
    }

    @Override
    protected void onChangedTo(ServerWorld world, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
        world.playSound(null, pos, SoundEvents.BLOCK_VAULT_OPEN_SHUTTER, SoundCategory.BLOCKS);
    }

    @Override
    protected void onChangedFrom(ServerWorld world, BlockPos pos, VaultConfig config, VaultSharedData sharedData) {
        world.playSound(null, pos, SoundEvents.BLOCK_VAULT_CLOSE_SHUTTER, SoundCategory.BLOCKS);
    }
}
