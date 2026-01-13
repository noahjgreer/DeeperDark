/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BarrelBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BarrelBlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.LootableContainerBlockEntity
 *  net.minecraft.block.entity.ViewerCountManager
 *  net.minecraft.entity.ContainerUser
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.GenericContainerScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3i
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class BarrelBlockEntity
extends LootableContainerBlockEntity {
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.barrel");
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize((int)27, (Object)ItemStack.EMPTY);
    private final ViewerCountManager stateManager = new /* Unavailable Anonymous Inner Class!! */;

    public BarrelBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BARREL, pos, state);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.writeLootTable(view)) {
            Inventories.writeData((WriteView)view, (DefaultedList)this.inventory);
        }
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize((int)this.size(), (Object)ItemStack.EMPTY);
        if (!this.readLootTable(view)) {
            Inventories.readData((ReadView)view, (DefaultedList)this.inventory);
        }
    }

    public int size() {
        return 27;
    }

    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    protected Text getContainerName() {
        return CONTAINER_NAME_TEXT;
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3((int)syncId, (PlayerInventory)playerInventory, (Inventory)this);
    }

    public void onOpen(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            this.stateManager.openContainer(user.asLivingEntity(), this.getWorld(), this.getPos(), this.getCachedState(), user.getContainerInteractionRange());
        }
    }

    public void onClose(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            this.stateManager.closeContainer(user.asLivingEntity(), this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public List<ContainerUser> getViewingUsers() {
        return this.stateManager.getViewingUsers(this.getWorld(), this.getPos());
    }

    public void tick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), (BlockState)state.with((Property)BarrelBlock.OPEN, (Comparable)Boolean.valueOf(open)), 3);
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = ((Direction)state.get((Property)BarrelBlock.FACING)).getVector();
        double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }
}

