/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.LootableContainerBlockEntity
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity$AnimationStage
 *  net.minecraft.block.piston.PistonBehavior
 *  net.minecraft.entity.ContainerUser
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.MovementType
 *  net.minecraft.entity.mob.ShulkerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SidedInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ShulkerBoxScreenHandler
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.event.GameEvent
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ContainerUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class ShulkerBoxBlockEntity
extends LootableContainerBlockEntity
implements SidedInventory {
    public static final int field_31354 = 9;
    public static final int field_31355 = 3;
    public static final int INVENTORY_SIZE = 27;
    public static final int field_31357 = 1;
    public static final int field_31358 = 10;
    public static final float field_31359 = 0.5f;
    public static final float field_31360 = 270.0f;
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.shulkerBox");
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize((int)27, (Object)ItemStack.EMPTY);
    private int viewerCount;
    private AnimationStage animationStage = AnimationStage.CLOSED;
    private float animationProgress;
    private float lastAnimationProgress;
    private final @Nullable DyeColor cachedColor;

    public ShulkerBoxBlockEntity(@Nullable DyeColor color, BlockPos pos, BlockState state) {
        super(BlockEntityType.SHULKER_BOX, pos, state);
        this.cachedColor = color;
    }

    public ShulkerBoxBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.SHULKER_BOX, pos, state);
        DyeColor dyeColor;
        Block block = state.getBlock();
        if (block instanceof ShulkerBoxBlock) {
            ShulkerBoxBlock shulkerBoxBlock = (ShulkerBoxBlock)block;
            dyeColor = shulkerBoxBlock.getColor();
        } else {
            dyeColor = null;
        }
        this.cachedColor = dyeColor;
    }

    public static void tick(World world, BlockPos pos, BlockState state, ShulkerBoxBlockEntity blockEntity) {
        blockEntity.updateAnimation(world, pos, state);
    }

    private void updateAnimation(World world, BlockPos pos, BlockState state) {
        this.lastAnimationProgress = this.animationProgress;
        switch (this.animationStage.ordinal()) {
            case 0: {
                this.animationProgress = 0.0f;
                break;
            }
            case 1: {
                this.animationProgress += 0.1f;
                if (this.lastAnimationProgress == 0.0f) {
                    ShulkerBoxBlockEntity.updateNeighborStates((World)world, (BlockPos)pos, (BlockState)state);
                }
                if (this.animationProgress >= 1.0f) {
                    this.animationStage = AnimationStage.OPENED;
                    this.animationProgress = 1.0f;
                    ShulkerBoxBlockEntity.updateNeighborStates((World)world, (BlockPos)pos, (BlockState)state);
                }
                this.pushEntities(world, pos, state);
                break;
            }
            case 3: {
                this.animationProgress -= 0.1f;
                if (this.lastAnimationProgress == 1.0f) {
                    ShulkerBoxBlockEntity.updateNeighborStates((World)world, (BlockPos)pos, (BlockState)state);
                }
                if (!(this.animationProgress <= 0.0f)) break;
                this.animationStage = AnimationStage.CLOSED;
                this.animationProgress = 0.0f;
                ShulkerBoxBlockEntity.updateNeighborStates((World)world, (BlockPos)pos, (BlockState)state);
                break;
            }
            case 2: {
                this.animationProgress = 1.0f;
            }
        }
    }

    public AnimationStage getAnimationStage() {
        return this.animationStage;
    }

    public Box getBoundingBox(BlockState state) {
        Vec3d vec3d = new Vec3d(0.5, 0.0, 0.5);
        return ShulkerEntity.calculateBoundingBox((float)1.0f, (Direction)((Direction)state.get((Property)ShulkerBoxBlock.FACING)), (float)(0.5f * this.getAnimationProgress(1.0f)), (Vec3d)vec3d);
    }

    private void pushEntities(World world, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        Direction direction = (Direction)state.get((Property)ShulkerBoxBlock.FACING);
        Box box = ShulkerEntity.calculateBoundingBox((float)1.0f, (Direction)direction, (float)this.lastAnimationProgress, (float)this.animationProgress, (Vec3d)pos.toBottomCenterPos());
        List list = world.getOtherEntities(null, box);
        if (list.isEmpty()) {
            return;
        }
        for (Entity entity : list) {
            if (entity.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            entity.move(MovementType.SHULKER_BOX, new Vec3d((box.getLengthX() + 0.01) * (double)direction.getOffsetX(), (box.getLengthY() + 0.01) * (double)direction.getOffsetY(), (box.getLengthZ() + 0.01) * (double)direction.getOffsetZ()));
        }
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.viewerCount = data;
            if (data == 0) {
                this.animationStage = AnimationStage.CLOSING;
            }
            if (data == 1) {
                this.animationStage = AnimationStage.OPENING;
            }
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    private static void updateNeighborStates(World world, BlockPos pos, BlockState state) {
        state.updateNeighbors((WorldAccess)world, pos, 3);
        world.updateNeighbors(pos, state.getBlock());
    }

    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
    }

    public void onOpen(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount == 1) {
                this.world.emitGameEvent((Entity)user.asLivingEntity(), (RegistryEntry)GameEvent.CONTAINER_OPEN, this.pos);
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    public void onClose(ContainerUser user) {
        if (!this.removed && !user.asLivingEntity().isSpectator()) {
            --this.viewerCount;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount <= 0) {
                this.world.emitGameEvent((Entity)user.asLivingEntity(), (RegistryEntry)GameEvent.CONTAINER_CLOSE, this.pos);
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    protected Text getContainerName() {
        return CONTAINER_NAME_TEXT;
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.readInventoryNbt(view);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        if (!this.writeLootTable(view)) {
            Inventories.writeData((WriteView)view, (DefaultedList)this.inventory, (boolean)false);
        }
    }

    public void readInventoryNbt(ReadView readView) {
        this.inventory = DefaultedList.ofSize((int)this.size(), (Object)ItemStack.EMPTY);
        if (!this.readLootTable(readView)) {
            Inventories.readData((ReadView)readView, (DefaultedList)this.inventory);
        }
    }

    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !(Block.getBlockFromItem((Item)stack.getItem()) instanceof ShulkerBoxBlock);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public float getAnimationProgress(float tickProgress) {
        return MathHelper.lerp((float)tickProgress, (float)this.lastAnimationProgress, (float)this.animationProgress);
    }

    public @Nullable DyeColor getColor() {
        return this.cachedColor;
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ShulkerBoxScreenHandler(syncId, playerInventory, (Inventory)this);
    }

    public boolean suffocates() {
        return this.animationStage == AnimationStage.CLOSED;
    }
}

