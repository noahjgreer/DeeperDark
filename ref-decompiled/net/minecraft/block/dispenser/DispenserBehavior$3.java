/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

static class DispenserBehavior.3
extends FallibleItemDispenserBehavior {
    DispenserBehavior.3() {
    }

    private ItemStack pickUpFluid(BlockPointer pointer, ItemStack inputStack, ItemStack outputStack) {
        pointer.world().emitGameEvent(null, GameEvent.FLUID_PICKUP, pointer.pos());
        return this.decrementStackWithRemainder(pointer, inputStack, outputStack);
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        this.setSuccess(false);
        ServerWorld serverWorld = pointer.world();
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        BlockState blockState = serverWorld.getBlockState(blockPos);
        if (blockState.isIn(BlockTags.BEEHIVES, state -> state.contains(BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock) && blockState.get(BeehiveBlock.HONEY_LEVEL) >= 5) {
            ((BeehiveBlock)blockState.getBlock()).takeHoney(serverWorld, blockState, blockPos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
            this.setSuccess(true);
            return this.pickUpFluid(pointer, stack, new ItemStack(Items.HONEY_BOTTLE));
        }
        if (serverWorld.getFluidState(blockPos).isIn(FluidTags.WATER)) {
            this.setSuccess(true);
            return this.pickUpFluid(pointer, stack, PotionContentsComponent.createStack(Items.POTION, Potions.WATER));
        }
        return super.dispenseSilently(pointer, stack);
    }
}
