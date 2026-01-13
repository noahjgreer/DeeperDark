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

final class VaultState.3
extends VaultState {
    VaultState.3(String string2, VaultState.Light light) {
    }

    @Override
    protected void onChangedTo(ServerWorld world, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
        world.playSound(null, pos, SoundEvents.BLOCK_VAULT_INSERT_ITEM, SoundCategory.BLOCKS);
    }
}
