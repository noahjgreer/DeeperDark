/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

static class DispenserBehavior.11
extends ItemDispenserBehavior {
    DispenserBehavior.11() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ItemStack itemStack;
        BlockPos blockPos;
        ServerWorld worldAccess = pointer.world();
        BlockState blockState = worldAccess.getBlockState(blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING)));
        Block block = blockState.getBlock();
        if (block instanceof FluidDrainable) {
            FluidDrainable fluidDrainable = (FluidDrainable)((Object)block);
            itemStack = fluidDrainable.tryDrainFluid(null, worldAccess, blockPos, blockState);
            if (itemStack.isEmpty()) {
                return super.dispenseSilently(pointer, stack);
            }
        } else {
            return super.dispenseSilently(pointer, stack);
        }
        worldAccess.emitGameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
        Item item = itemStack.getItem();
        return this.decrementStackWithRemainder(pointer, stack, new ItemStack(item));
    }
}
