/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ChiseledBookshelfBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ChiseledBookshelfBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.ListInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ListInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public class ChiseledBookshelfBlockEntity
extends BlockEntity
implements ListInventory {
    public static final int MAX_BOOKS = 6;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_LAST_INTERACTED_SLOT = -1;
    private final DefaultedList<ItemStack> heldStacks = DefaultedList.ofSize((int)6, (Object)ItemStack.EMPTY);
    private int lastInteractedSlot = -1;

    public ChiseledBookshelfBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.CHISELED_BOOKSHELF, pos, state);
    }

    private void updateState(int interactedSlot) {
        if (interactedSlot < 0 || interactedSlot >= 6) {
            LOGGER.error("Expected slot 0-5, got {}", (Object)interactedSlot);
            return;
        }
        this.lastInteractedSlot = interactedSlot;
        BlockState blockState = this.getCachedState();
        for (int i = 0; i < ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); ++i) {
            boolean bl = !this.getStack(i).isEmpty();
            BooleanProperty booleanProperty = (BooleanProperty)ChiseledBookshelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i);
            blockState = (BlockState)blockState.with((Property)booleanProperty, (Comparable)Boolean.valueOf(bl));
        }
        Objects.requireNonNull(this.world).setBlockState(this.pos, blockState, 3);
        this.world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, this.pos, GameEvent.Emitter.of((BlockState)blockState));
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.heldStacks.clear();
        Inventories.readData((ReadView)view, (DefaultedList)this.heldStacks);
        this.lastInteractedSlot = view.getInt("last_interacted_slot", -1);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData((WriteView)view, (DefaultedList)this.heldStacks, (boolean)true);
        view.putInt("last_interacted_slot", this.lastInteractedSlot);
    }

    public int getMaxCountPerStack() {
        return 1;
    }

    public boolean canAccept(ItemStack stack) {
        return stack.isIn(ItemTags.BOOKSHELF_BOOKS);
    }

    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Objects.requireNonNullElse((ItemStack)this.getHeldStacks().get(slot), ItemStack.EMPTY);
        this.getHeldStacks().set(slot, (Object)ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.updateState(slot);
        }
        return itemStack;
    }

    public void setStack(int slot, ItemStack stack) {
        if (this.canAccept(stack)) {
            this.getHeldStacks().set(slot, (Object)stack);
            this.updateState(slot);
        } else if (stack.isEmpty()) {
            this.removeStack(slot, this.getMaxCountPerStack());
        }
    }

    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return hopperInventory.containsAny(itemStack2 -> {
            if (itemStack2.isEmpty()) {
                return true;
            }
            return ItemStack.areItemsAndComponentsEqual((ItemStack)stack, (ItemStack)itemStack2) && itemStack2.getCount() + stack.getCount() <= hopperInventory.getMaxCount(itemStack2);
        });
    }

    public DefaultedList<ItemStack> getHeldStacks() {
        return this.heldStacks;
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse((BlockEntity)this, (PlayerEntity)player);
    }

    public int getLastInteractedSlot() {
        return this.lastInteractedSlot;
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        ((ContainerComponent)components.getOrDefault(DataComponentTypes.CONTAINER, (Object)ContainerComponent.DEFAULT)).copyTo(this.heldStacks);
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CONTAINER, (Object)ContainerComponent.fromStacks((List)this.heldStacks));
    }

    public void removeFromCopiedStackData(WriteView view) {
        view.remove("Items");
    }
}

