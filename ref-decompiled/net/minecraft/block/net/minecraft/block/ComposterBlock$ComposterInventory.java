/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

static class ComposterBlock.ComposterInventory
extends SimpleInventory
implements SidedInventory {
    private final BlockState state;
    private final WorldAccess world;
    private final BlockPos pos;
    private boolean dirty;

    public ComposterBlock.ComposterInventory(BlockState state, WorldAccess world, BlockPos pos) {
        super(1);
        this.state = state;
        this.world = world;
        this.pos = pos;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] nArray;
        if (side == Direction.UP) {
            int[] nArray2 = new int[1];
            nArray = nArray2;
            nArray2[0] = 0;
        } else {
            nArray = new int[]{};
        }
        return nArray;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !this.dirty && dir == Direction.UP && ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey((Object)stack.getItem());
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public void markDirty() {
        ItemStack itemStack = this.getStack(0);
        if (!itemStack.isEmpty()) {
            this.dirty = true;
            BlockState blockState = ComposterBlock.addToComposter(null, this.state, this.world, this.pos, itemStack);
            this.world.syncWorldEvent(1500, this.pos, blockState != this.state ? 1 : 0);
            this.removeStack(0);
        }
    }
}
