/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShelfBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ShelfBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.ListInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.NbtWriteView
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.ErrorReporter
 *  net.minecraft.util.ErrorReporter$Logging
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShelfBlock;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Property;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ShelfBlockEntity
extends BlockEntity
implements HeldItemContext,
ListInventory {
    public static final int SLOT_COUNT = 3;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ALIGN_ITEMS_TO_BOTTOM_KEY = "align_items_to_bottom";
    private final DefaultedList<ItemStack> heldStacks = DefaultedList.ofSize((int)3, (Object)ItemStack.EMPTY);
    private boolean alignItemsToBottom;

    public ShelfBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SHELF, pos, state);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.heldStacks.clear();
        Inventories.readData((ReadView)view, (DefaultedList)this.heldStacks);
        this.alignItemsToBottom = view.getBoolean(ALIGN_ITEMS_TO_BOTTOM_KEY, false);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData((WriteView)view, (DefaultedList)this.heldStacks, (boolean)true);
        view.putBoolean(ALIGN_ITEMS_TO_BOTTOM_KEY, this.alignItemsToBottom);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        try (ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), LOGGER);){
            NbtWriteView nbtWriteView = NbtWriteView.create((ErrorReporter)logging, (RegistryWrapper.WrapperLookup)registries);
            Inventories.writeData((WriteView)nbtWriteView, (DefaultedList)this.heldStacks, (boolean)true);
            nbtWriteView.putBoolean(ALIGN_ITEMS_TO_BOTTOM_KEY, this.alignItemsToBottom);
            NbtCompound nbtCompound = nbtWriteView.getNbt();
            return nbtCompound;
        }
    }

    public DefaultedList<ItemStack> getHeldStacks() {
        return this.heldStacks;
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse((BlockEntity)this, (PlayerEntity)player);
    }

    public ItemStack swapStackNoMarkDirty(int slot, ItemStack stack) {
        ItemStack itemStack = this.removeStack(slot);
        this.setStackNoMarkDirty(slot, stack);
        return itemStack;
    }

    public void markDirty(// Could not load outer class - annotation placement on inner may be incorrect
     @Nullable RegistryEntry.Reference<GameEvent> gameEvent) {
        super.markDirty();
        if (this.world != null) {
            if (gameEvent != null) {
                this.world.emitGameEvent(gameEvent, this.pos, GameEvent.Emitter.of((BlockState)this.getCachedState()));
            }
            this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        }
    }

    public void markDirty() {
        this.markDirty(GameEvent.BLOCK_ACTIVATE);
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

    public World getEntityWorld() {
        return this.world;
    }

    public Vec3d getEntityPos() {
        return this.getPos().toCenterPos();
    }

    public float getBodyYaw() {
        return ((Direction)this.getCachedState().get((Property)ShelfBlock.FACING)).getOpposite().getPositiveHorizontalDegrees();
    }

    public boolean shouldAlignItemsToBottom() {
        return this.alignItemsToBottom;
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }
}

