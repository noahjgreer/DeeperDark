/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

static class DispenserBehavior.13
extends FallibleItemDispenserBehavior {
    DispenserBehavior.13() {
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(true);
        ServerWorld world = pointer.world();
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        if (BoneMealItem.useOnFertilizable(stack, world, blockPos) || BoneMealItem.useOnGround(stack, world, blockPos, null)) {
            if (!world.isClient()) {
                world.syncWorldEvent(1505, blockPos, 15);
            }
        } else {
            this.setSuccess(false);
        }
        return stack;
    }
}
