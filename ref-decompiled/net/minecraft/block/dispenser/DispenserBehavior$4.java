/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

static class DispenserBehavior.4
extends FallibleItemDispenserBehavior {
    DispenserBehavior.4() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        BlockPos blockPos = pointer.pos().offset(direction);
        ServerWorld world = pointer.world();
        BlockState blockState = world.getBlockState(blockPos);
        this.setSuccess(true);
        if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            if (blockState.get(RespawnAnchorBlock.CHARGES) != 4) {
                RespawnAnchorBlock.charge(null, world, blockPos, blockState);
                stack.decrement(1);
            } else {
                this.setSuccess(false);
            }
            return stack;
        }
        return super.dispenseSilently(pointer, stack);
    }
}
