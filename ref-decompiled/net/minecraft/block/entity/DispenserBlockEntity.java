/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.DispenserBlockEntity
 *  net.minecraft.block.entity.LootableContainerBlockEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.Generic3x3ContainerScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class DispenserBlockEntity
extends LootableContainerBlockEntity {
    public static final int INVENTORY_SIZE = 9;
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.dispenser");
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize((int)9, (Object)ItemStack.EMPTY);

    protected DispenserBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public DispenserBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityType.DISPENSER, pos, state);
    }

    public int size() {
        return 9;
    }

    public int chooseNonEmptySlot(Random random) {
        this.generateLoot(null);
        int i = -1;
        int j = 1;
        for (int k = 0; k < this.inventory.size(); ++k) {
            if (((ItemStack)this.inventory.get(k)).isEmpty() || random.nextInt(j++) != 0) continue;
            i = k;
        }
        return i;
    }

    public ItemStack addToFirstFreeSlot(ItemStack stack) {
        int i = this.getMaxCount(stack);
        for (int j = 0; j < this.inventory.size(); ++j) {
            ItemStack itemStack = (ItemStack)this.inventory.get(j);
            if (!itemStack.isEmpty() && !ItemStack.areItemsAndComponentsEqual((ItemStack)stack, (ItemStack)itemStack)) continue;
            int k = Math.min(stack.getCount(), i - itemStack.getCount());
            if (k > 0) {
                if (itemStack.isEmpty()) {
                    this.setStack(j, stack.split(k));
                } else {
                    stack.decrement(k);
                    itemStack.increment(k);
                }
            }
            if (stack.isEmpty()) break;
        }
        return stack;
    }

    protected Text getContainerName() {
        return CONTAINER_NAME_TEXT;
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize((int)this.size(), (Object)ItemStack.EMPTY);
        if (!this.readLootTable(view)) {
            Inventories.readData((ReadView)view, (DefaultedList)this.inventory);
        }
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.writeLootTable(view)) {
            Inventories.writeData((WriteView)view, (DefaultedList)this.inventory);
        }
    }

    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new Generic3x3ContainerScreenHandler(syncId, playerInventory, (Inventory)this);
    }
}

