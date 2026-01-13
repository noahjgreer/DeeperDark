/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

import net.minecraft.block.enums.VaultState;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

final class VaultState.1
extends VaultState {
    VaultState.1(String string2, VaultState.Light light) {
    }

    @Override
    protected void onChangedTo(ServerWorld world, BlockPos pos, VaultConfig config, VaultSharedData sharedData, boolean ominous) {
        sharedData.setDisplayItem(ItemStack.EMPTY);
        world.syncWorldEvent(3016, pos, ominous ? 1 : 0);
    }
}
