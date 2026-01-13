/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 */
package net.minecraft.block.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.enums.VaultState;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public static final class VaultBlockEntity.Server {
    private static final int UNLOCK_TIME = 14;
    private static final int DISPLAY_UPDATE_INTERVAL = 20;
    private static final int FAILED_UNLOCK_COOLDOWN = 15;

    public static void tick(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData) {
        VaultState vaultState = state.get(VaultBlock.VAULT_STATE);
        if (VaultBlockEntity.Server.shouldUpdateDisplayItem(world.getTime(), vaultState)) {
            VaultBlockEntity.Server.updateDisplayItem(world, vaultState, config, sharedData, pos);
        }
        BlockState blockState = state;
        if (world.getTime() >= serverData.getStateUpdatingResumeTime() && state != (blockState = (BlockState)blockState.with(VaultBlock.VAULT_STATE, vaultState.update(world, pos, config, serverData, sharedData)))) {
            VaultBlockEntity.Server.changeVaultState(world, pos, state, blockState, config, sharedData);
        }
        if (serverData.dirty || sharedData.dirty) {
            VaultBlockEntity.markDirty(world, pos, state);
            if (sharedData.dirty) {
                world.updateListeners(pos, state, blockState, 2);
            }
            serverData.dirty = false;
            sharedData.dirty = false;
        }
    }

    public static void tryUnlock(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, PlayerEntity player, ItemStack stack) {
        VaultState vaultState = state.get(VaultBlock.VAULT_STATE);
        if (!VaultBlockEntity.Server.canBeUnlocked(config, vaultState)) {
            return;
        }
        if (!VaultBlockEntity.Server.isValidKey(config, stack)) {
            VaultBlockEntity.Server.playFailedUnlockSound(world, serverData, pos, SoundEvents.BLOCK_VAULT_INSERT_ITEM_FAIL);
            return;
        }
        if (serverData.hasRewardedPlayer(player)) {
            VaultBlockEntity.Server.playFailedUnlockSound(world, serverData, pos, SoundEvents.BLOCK_VAULT_REJECT_REWARDED_PLAYER);
            return;
        }
        List<ItemStack> list = VaultBlockEntity.Server.generateLoot(world, config, pos, player, stack);
        if (list.isEmpty()) {
            return;
        }
        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        stack.decrementUnlessCreative(config.keyItem().getCount(), player);
        VaultBlockEntity.Server.unlock(world, state, pos, config, serverData, sharedData, list);
        serverData.markPlayerAsRewarded(player);
        sharedData.updateConnectedPlayers(world, pos, serverData, config, config.deactivationRange());
    }

    static void changeVaultState(ServerWorld world, BlockPos pos, BlockState oldState, BlockState newState, VaultConfig config, VaultSharedData sharedData) {
        VaultState vaultState = oldState.get(VaultBlock.VAULT_STATE);
        VaultState vaultState2 = newState.get(VaultBlock.VAULT_STATE);
        world.setBlockState(pos, newState, 3);
        vaultState.onStateChange(world, pos, vaultState2, config, sharedData, newState.get(VaultBlock.OMINOUS));
    }

    static void updateDisplayItem(ServerWorld world, VaultState state, VaultConfig config, VaultSharedData sharedData, BlockPos pos) {
        if (!VaultBlockEntity.Server.canBeUnlocked(config, state)) {
            sharedData.setDisplayItem(ItemStack.EMPTY);
            return;
        }
        ItemStack itemStack = VaultBlockEntity.Server.generateDisplayItem(world, pos, config.overrideLootTableToDisplay().orElse(config.lootTable()));
        sharedData.setDisplayItem(itemStack);
    }

    private static ItemStack generateDisplayItem(ServerWorld world, BlockPos pos, RegistryKey<LootTable> lootTable) {
        LootWorldContext lootWorldContext;
        LootTable lootTable2 = world.getServer().getReloadableRegistries().getLootTable(lootTable);
        ObjectArrayList<ItemStack> list = lootTable2.generateLoot(lootWorldContext = new LootWorldContext.Builder(world).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).build(LootContextTypes.VAULT), world.getRandom());
        if (list.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return Util.getRandom(list, world.getRandom());
    }

    private static void unlock(ServerWorld world, BlockState state, BlockPos pos, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, List<ItemStack> itemsToEject) {
        serverData.setItemsToEject(itemsToEject);
        sharedData.setDisplayItem(serverData.getItemToDisplay());
        serverData.setStateUpdatingResumeTime(world.getTime() + 14L);
        VaultBlockEntity.Server.changeVaultState(world, pos, state, (BlockState)state.with(VaultBlock.VAULT_STATE, VaultState.UNLOCKING), config, sharedData);
    }

    private static List<ItemStack> generateLoot(ServerWorld world, VaultConfig config, BlockPos pos, PlayerEntity player, ItemStack key) {
        LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(config.lootTable());
        LootWorldContext lootWorldContext = new LootWorldContext.Builder(world).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos)).luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player).add(LootContextParameters.TOOL, key).build(LootContextTypes.VAULT);
        return lootTable.generateLoot(lootWorldContext);
    }

    private static boolean canBeUnlocked(VaultConfig config, VaultState state) {
        return !config.keyItem().isEmpty() && state != VaultState.INACTIVE;
    }

    private static boolean isValidKey(VaultConfig config, ItemStack stack) {
        return ItemStack.areItemsAndComponentsEqual(stack, config.keyItem()) && stack.getCount() >= config.keyItem().getCount();
    }

    private static boolean shouldUpdateDisplayItem(long time, VaultState state) {
        return time % 20L == 0L && state == VaultState.ACTIVE;
    }

    private static void playFailedUnlockSound(ServerWorld world, VaultServerData serverData, BlockPos pos, SoundEvent sound) {
        if (world.getTime() >= serverData.getLastFailedUnlockTime() + 15L) {
            world.playSound(null, pos, sound, SoundCategory.BLOCKS);
            serverData.setLastFailedUnlockTime(world.getTime());
        }
    }
}
