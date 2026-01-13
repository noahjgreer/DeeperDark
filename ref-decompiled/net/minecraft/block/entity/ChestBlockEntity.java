/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.ChestLidAnimator
 *  net.minecraft.block.entity.LidOpenable
 *  net.minecraft.block.entity.LootableContainerBlockEntity
 *  net.minecraft.block.entity.ViewerCountManager
 *  net.minecraft.block.enums.ChestType
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
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 */
package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.block.enums.ChestType;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChestBlockEntity
extends LootableContainerBlockEntity
implements LidOpenable {
    private static final int VIEWER_COUNT_UPDATE_EVENT_TYPE = 1;
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.chest");
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize((int)27, (Object)ItemStack.EMPTY);
    private final ViewerCountManager stateManager = new /* Unavailable Anonymous Inner Class!! */;
    private final ChestLidAnimator lidAnimator = new ChestLidAnimator();

    protected ChestBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public ChestBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityType.CHEST, pos, state);
    }

    public int size() {
        return 27;
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

    public static void clientTick(World world, BlockPos pos, BlockState state, ChestBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        ChestType chestType = (ChestType)state.get((Property)ChestBlock.CHEST_TYPE);
        if (chestType == ChestType.LEFT) {
            return;
        }
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.5;
        double f = (double)pos.getZ() + 0.5;
        if (chestType == ChestType.RIGHT) {
            Direction direction = ChestBlock.getFacing((BlockState)state);
            d += (double)direction.getOffsetX() * 0.5;
            f += (double)direction.getOffsetZ() * 0.5;
        }
        world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
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

    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public float getAnimationProgress(float tickProgress) {
        return this.lidAnimator.getProgress(tickProgress);
    }

    public static int getPlayersLookingInChestCount(BlockView world, BlockPos pos) {
        BlockEntity blockEntity;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity() && (blockEntity = world.getBlockEntity(pos)) instanceof ChestBlockEntity) {
            return ((ChestBlockEntity)blockEntity).stateManager.getViewerCount();
        }
        return 0;
    }

    public static void copyInventory(ChestBlockEntity from, ChestBlockEntity to) {
        DefaultedList defaultedList = from.getHeldStacks();
        from.setHeldStacks(to.getHeldStacks());
        to.setHeldStacks(defaultedList);
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3((int)syncId, (PlayerInventory)playerInventory, (Inventory)this);
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        Block block = state.getBlock();
        world.addSyncedBlockEvent(pos, block, 1, newViewerCount);
    }
}

