/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

static class DispenserBehavior.6
extends FallibleItemDispenserBehavior {
    DispenserBehavior.6() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        ServerWorld world = pointer.world();
        BlockState blockState = world.getBlockState(blockPos);
        Optional<BlockState> optional = HoneycombItem.getWaxedState(blockState);
        if (optional.isPresent()) {
            world.setBlockState(blockPos, optional.get());
            world.syncWorldEvent(3003, blockPos, 0);
            stack.decrement(1);
            this.setSuccess(true);
            return stack;
        }
        return super.dispenseSilently(pointer, stack);
    }
}
