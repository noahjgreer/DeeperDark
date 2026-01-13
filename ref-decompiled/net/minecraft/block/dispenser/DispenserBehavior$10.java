/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

static class DispenserBehavior.10
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior fallback = new ItemDispenserBehavior();

    DispenserBehavior.10() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        FluidModificationItem fluidModificationItem = (FluidModificationItem)((Object)stack.getItem());
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        ServerWorld world = pointer.world();
        if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
            fluidModificationItem.onEmptied(null, world, stack, blockPos);
            return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.BUCKET));
        }
        return this.fallback.dispense(pointer, stack);
    }
}
