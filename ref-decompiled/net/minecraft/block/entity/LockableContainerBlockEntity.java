/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.LockableContainerBlockEntity
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ContainerComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.ContainerLock
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextCodecs
 *  net.minecraft.util.Nameable
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class LockableContainerBlockEntity
extends BlockEntity
implements Inventory,
NamedScreenHandlerFactory,
Nameable {
    private ContainerLock lock = ContainerLock.EMPTY;
    private @Nullable Text customName;

    protected LockableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.lock = ContainerLock.read((ReadView)view);
        this.customName = LockableContainerBlockEntity.tryParseCustomName((ReadView)view, (String)"CustomName");
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        this.lock.write(view);
        view.putNullable("CustomName", TextCodecs.CODEC, (Object)this.customName);
    }

    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getContainerName();
    }

    public Text getDisplayName() {
        return this.getName();
    }

    public @Nullable Text getCustomName() {
        return this.customName;
    }

    protected abstract Text getContainerName();

    public boolean checkUnlocked(PlayerEntity player) {
        return this.lock.checkUnlocked(player);
    }

    public static void handleLocked(Vec3d containerPos, PlayerEntity player, Text name) {
        World world = player.getEntityWorld();
        player.sendMessage((Text)Text.translatable((String)"container.isLocked", (Object[])new Object[]{name}), true);
        if (!world.isClient()) {
            world.playSound(null, containerPos.getX(), containerPos.getY(), containerPos.getZ(), SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    public boolean isLocked() {
        return !this.lock.equals((Object)ContainerLock.EMPTY);
    }

    protected abstract DefaultedList<ItemStack> getHeldStacks();

    protected abstract void setHeldStacks(DefaultedList<ItemStack> var1);

    public boolean isEmpty() {
        for (ItemStack itemStack : this.getHeldStacks()) {
            if (itemStack.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public ItemStack getStack(int slot) {
        return (ItemStack)this.getHeldStacks().get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack((List)this.getHeldStacks(), (int)slot, (int)amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack((List)this.getHeldStacks(), (int)slot);
    }

    public void setStack(int slot, ItemStack stack) {
        this.getHeldStacks().set(slot, (Object)stack);
        stack.capCount(this.getMaxCount(stack));
        this.markDirty();
    }

    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse((BlockEntity)this, (PlayerEntity)player);
    }

    public void clear() {
        this.getHeldStacks().clear();
    }

    public @Nullable ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.checkUnlocked(playerEntity)) {
            return this.createScreenHandler(i, playerInventory);
        }
        LockableContainerBlockEntity.handleLocked((Vec3d)this.getPos().toCenterPos(), (PlayerEntity)playerEntity, (Text)this.getDisplayName());
        return null;
    }

    protected abstract ScreenHandler createScreenHandler(int var1, PlayerInventory var2);

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.customName = (Text)components.get(DataComponentTypes.CUSTOM_NAME);
        this.lock = (ContainerLock)components.getOrDefault(DataComponentTypes.LOCK, (Object)ContainerLock.EMPTY);
        ((ContainerComponent)components.getOrDefault(DataComponentTypes.CONTAINER, (Object)ContainerComponent.DEFAULT)).copyTo(this.getHeldStacks());
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CUSTOM_NAME, (Object)this.customName);
        if (this.isLocked()) {
            builder.add(DataComponentTypes.LOCK, (Object)this.lock);
        }
        builder.add(DataComponentTypes.CONTAINER, (Object)ContainerComponent.fromStacks((List)this.getHeldStacks()));
    }

    public void removeFromCopiedStackData(WriteView view) {
        view.remove("CustomName");
        view.remove("lock");
        view.remove("Items");
    }
}

