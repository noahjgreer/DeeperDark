/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.LockableContainerBlockEntity
 *  net.minecraft.block.entity.LootableContainerBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ContainerLootComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.LootableInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.LootTable
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerLootComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

public abstract class LootableContainerBlockEntity
extends LockableContainerBlockEntity
implements LootableInventory {
    protected @Nullable RegistryKey<LootTable> lootTable;
    protected long lootTableSeed = 0L;

    protected LootableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public @Nullable RegistryKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    public boolean isEmpty() {
        this.generateLoot(null);
        return super.isEmpty();
    }

    public ItemStack getStack(int slot) {
        this.generateLoot(null);
        return super.getStack(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        this.generateLoot(null);
        return super.removeStack(slot, amount);
    }

    public ItemStack removeStack(int slot) {
        this.generateLoot(null);
        return super.removeStack(slot);
    }

    public void setStack(int slot, ItemStack stack) {
        this.generateLoot(null);
        super.setStack(slot, stack);
    }

    public boolean checkUnlocked(PlayerEntity player) {
        return super.checkUnlocked(player) && (this.lootTable == null || !player.isSpectator());
    }

    public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            this.generateLoot(playerInventory.player);
            return this.createScreenHandler(i, playerInventory);
        }
        LockableContainerBlockEntity.handleLocked((Vec3d)this.getPos().toCenterPos(), (PlayerEntity)playerEntity, (Text)this.getDisplayName());
        return null;
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        ContainerLootComponent containerLootComponent = (ContainerLootComponent)components.get(DataComponentTypes.CONTAINER_LOOT);
        if (containerLootComponent != null) {
            this.lootTable = containerLootComponent.lootTable();
            this.lootTableSeed = containerLootComponent.seed();
        }
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        if (this.lootTable != null) {
            builder.add(DataComponentTypes.CONTAINER_LOOT, (Object)new ContainerLootComponent(this.lootTable, this.lootTableSeed));
        }
    }

    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove("LootTable");
        view.remove("LootTableSeed");
    }
}

