/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

static class DispenserBehavior.2
extends FallibleItemDispenserBehavior {
    DispenserBehavior.2() {
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld world = pointer.world();
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
        if (world.isAir(blockPos) && carvedPumpkinBlock.canDispense(world, blockPos)) {
            if (!world.isClient()) {
                world.setBlockState(blockPos, carvedPumpkinBlock.getDefaultState(), 3);
                world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
            }
            stack.decrement(1);
            this.setSuccess(true);
        } else {
            this.setSuccess(EquippableDispenserBehavior.dispense(pointer, stack));
        }
        return stack;
    }
}
