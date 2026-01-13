/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WitherSkullBlock;
import net.minecraft.block.dispenser.EquippableDispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.event.GameEvent;

static class DispenserBehavior.15
extends FallibleItemDispenserBehavior {
    DispenserBehavior.15() {
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld world = pointer.world();
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        BlockPos blockPos = pointer.pos().offset(direction);
        if (world.isAir(blockPos) && WitherSkullBlock.canDispense(world, blockPos, stack)) {
            world.setBlockState(blockPos, (BlockState)Blocks.WITHER_SKELETON_SKULL.getDefaultState().with(SkullBlock.ROTATION, RotationPropertyHelper.fromDirection(direction)), 3);
            world.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof SkullBlockEntity) {
                WitherSkullBlock.onPlaced(world, blockPos, (SkullBlockEntity)blockEntity);
            }
            stack.decrement(1);
            this.setSuccess(true);
        } else {
            this.setSuccess(EquippableDispenserBehavior.dispense(pointer, stack));
        }
        return stack;
    }
}
