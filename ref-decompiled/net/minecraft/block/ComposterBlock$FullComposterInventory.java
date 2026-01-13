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
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jspecify.annotations.Nullable;

static class ComposterBlock.FullComposterInventory
extends SimpleInventory
implements SidedInventory {
    private final BlockState state;
    private final WorldAccess world;
    private final BlockPos pos;
    private boolean dirty;

    public ComposterBlock.FullComposterInventory(BlockState state, WorldAccess world, BlockPos pos, ItemStack outputItem) {
        super(outputItem);
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
        if (side == Direction.DOWN) {
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
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !this.dirty && dir == Direction.DOWN && stack.isOf(Items.BONE_MEAL);
    }

    @Override
    public void markDirty() {
        ComposterBlock.emptyComposter(null, this.state, this.world, this.pos);
        this.dirty = true;
    }
}
