/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

static class DispenserBehavior.12
extends FallibleItemDispenserBehavior {
    DispenserBehavior.12() {
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld serverWorld = pointer.world();
        this.setSuccess(true);
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        BlockPos blockPos = pointer.pos().offset(direction);
        BlockState blockState = serverWorld.getBlockState(blockPos);
        if (AbstractFireBlock.canPlaceAt(serverWorld, blockPos, direction)) {
            serverWorld.setBlockState(blockPos, AbstractFireBlock.getState(serverWorld, blockPos));
            serverWorld.emitGameEvent(null, GameEvent.BLOCK_PLACE, blockPos);
        } else if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
            serverWorld.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, true));
            serverWorld.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
        } else if (blockState.getBlock() instanceof TntBlock) {
            if (TntBlock.primeTnt(serverWorld, blockPos)) {
                serverWorld.removeBlock(blockPos, false);
            } else {
                this.setSuccess(false);
            }
        } else {
            this.setSuccess(false);
        }
        if (this.isSuccess()) {
            stack.damage(1, serverWorld, null, item -> {});
        }
        return stack;
    }
}
