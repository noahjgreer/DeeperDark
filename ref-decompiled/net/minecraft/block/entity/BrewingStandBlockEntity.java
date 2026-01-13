/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BrewingStandBlock
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.BrewingStandBlockEntity
 *  net.minecraft.block.entity.LockableContainerBlockEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SidedInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.recipe.BrewingRecipeRegistry
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.screen.BrewingStandScreenHandler
 *  net.minecraft.screen.PropertyDelegate
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.Arrays;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BrewingStandBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory {
    private static final int INPUT_SLOT_INDEX = 3;
    private static final int FUEL_SLOT_INDEX = 4;
    private static final int[] TOP_SLOTS = new int[]{3};
    private static final int[] BOTTOM_SLOTS = new int[]{0, 1, 2, 3};
    private static final int[] SIDE_SLOTS = new int[]{0, 1, 2, 4};
    public static final int MAX_FUEL_USES = 20;
    public static final int BREW_TIME_PROPERTY_INDEX = 0;
    public static final int FUEL_PROPERTY_INDEX = 1;
    public static final int PROPERTY_COUNT = 2;
    private static final short DEFAULT_BREW_TIME = 0;
    private static final byte DEFAULT_FUEL = 0;
    private static final Text CONTAINER_NAME_TEXT = Text.translatable((String)"container.brewing");
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize((int)5, (Object)ItemStack.EMPTY);
    int brewTime;
    private boolean[] slotsEmptyLastTick;
    private Item itemBrewing;
    int fuel;
    protected final PropertyDelegate propertyDelegate = new /* Unavailable Anonymous Inner Class!! */;

    public BrewingStandBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.BREWING_STAND, pos, state);
    }

    protected Text getContainerName() {
        return CONTAINER_NAME_TEXT;
    }

    public int size() {
        return this.inventory.size();
    }

    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    public static void tick(World world, BlockPos pos, BlockState state, BrewingStandBlockEntity blockEntity) {
        ItemStack itemStack = (ItemStack)blockEntity.inventory.get(4);
        if (blockEntity.fuel <= 0 && itemStack.isIn(ItemTags.BREWING_FUEL)) {
            blockEntity.fuel = 20;
            itemStack.decrement(1);
            BrewingStandBlockEntity.markDirty((World)world, (BlockPos)pos, (BlockState)state);
        }
        boolean bl = BrewingStandBlockEntity.canCraft((BrewingRecipeRegistry)world.getBrewingRecipeRegistry(), (DefaultedList)blockEntity.inventory);
        boolean bl2 = blockEntity.brewTime > 0;
        ItemStack itemStack2 = (ItemStack)blockEntity.inventory.get(3);
        if (bl2) {
            boolean bl3;
            --blockEntity.brewTime;
            boolean bl4 = bl3 = blockEntity.brewTime == 0;
            if (bl3 && bl) {
                BrewingStandBlockEntity.craft((World)world, (BlockPos)pos, (DefaultedList)blockEntity.inventory);
            } else if (!bl || !itemStack2.isOf(blockEntity.itemBrewing)) {
                blockEntity.brewTime = 0;
            }
            BrewingStandBlockEntity.markDirty((World)world, (BlockPos)pos, (BlockState)state);
        } else if (bl && blockEntity.fuel > 0) {
            --blockEntity.fuel;
            blockEntity.brewTime = 400;
            blockEntity.itemBrewing = itemStack2.getItem();
            BrewingStandBlockEntity.markDirty((World)world, (BlockPos)pos, (BlockState)state);
        }
        boolean[] bls = blockEntity.getSlotsEmpty();
        if (!Arrays.equals(bls, blockEntity.slotsEmptyLastTick)) {
            blockEntity.slotsEmptyLastTick = bls;
            BlockState blockState = state;
            if (!(blockState.getBlock() instanceof BrewingStandBlock)) {
                return;
            }
            for (int i = 0; i < BrewingStandBlock.BOTTLE_PROPERTIES.length; ++i) {
                blockState = (BlockState)blockState.with((Property)BrewingStandBlock.BOTTLE_PROPERTIES[i], (Comparable)Boolean.valueOf(bls[i]));
            }
            world.setBlockState(pos, blockState, 2);
        }
    }

    private boolean[] getSlotsEmpty() {
        boolean[] bls = new boolean[3];
        for (int i = 0; i < 3; ++i) {
            if (((ItemStack)this.inventory.get(i)).isEmpty()) continue;
            bls[i] = true;
        }
        return bls;
    }

    private static boolean canCraft(BrewingRecipeRegistry brewingRecipeRegistry, DefaultedList<ItemStack> slots) {
        ItemStack itemStack = (ItemStack)slots.get(3);
        if (itemStack.isEmpty()) {
            return false;
        }
        if (!brewingRecipeRegistry.isValidIngredient(itemStack)) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            ItemStack itemStack2 = (ItemStack)slots.get(i);
            if (itemStack2.isEmpty() || !brewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) continue;
            return true;
        }
        return false;
    }

    private static void craft(World world, BlockPos pos, DefaultedList<ItemStack> slots) {
        ItemStack itemStack = (ItemStack)slots.get(3);
        BrewingRecipeRegistry brewingRecipeRegistry = world.getBrewingRecipeRegistry();
        for (int i = 0; i < 3; ++i) {
            slots.set(i, (Object)brewingRecipeRegistry.craft(itemStack, (ItemStack)slots.get(i)));
        }
        itemStack.decrement(1);
        ItemStack itemStack2 = itemStack.getItem().getRecipeRemainder();
        if (!itemStack2.isEmpty()) {
            if (itemStack.isEmpty()) {
                itemStack = itemStack2;
            } else {
                ItemScatterer.spawn((World)world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (ItemStack)itemStack2);
            }
        }
        slots.set(3, (Object)itemStack);
        world.syncWorldEvent(1035, pos, 0);
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize((int)this.size(), (Object)ItemStack.EMPTY);
        Inventories.readData((ReadView)view, (DefaultedList)this.inventory);
        this.brewTime = view.getShort("BrewTime", (short)0);
        if (this.brewTime > 0) {
            this.itemBrewing = ((ItemStack)this.inventory.get(3)).getItem();
        }
        this.fuel = view.getByte("Fuel", (byte)0);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putShort("BrewTime", (short)this.brewTime);
        Inventories.writeData((WriteView)view, (DefaultedList)this.inventory);
        view.putByte("Fuel", (byte)this.fuel);
    }

    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 3) {
            BrewingRecipeRegistry brewingRecipeRegistry = this.world != null ? this.world.getBrewingRecipeRegistry() : BrewingRecipeRegistry.EMPTY;
            return brewingRecipeRegistry.isValidIngredient(stack);
        }
        if (slot == 4) {
            return stack.isIn(ItemTags.BREWING_FUEL);
        }
        return (stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(Items.GLASS_BOTTLE)) && this.getStack(slot).isEmpty();
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        return SIDE_SLOTS;
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 3) {
            return stack.isOf(Items.GLASS_BOTTLE);
        }
        return true;
    }

    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BrewingStandScreenHandler(syncId, playerInventory, (Inventory)this, this.propertyDelegate);
    }
}

