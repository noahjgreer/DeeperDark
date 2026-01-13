/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

static class DispenserBehavior.7
extends ItemDispenserBehavior {
    private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

    DispenserBehavior.7() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        PotionContentsComponent potionContentsComponent = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        if (!potionContentsComponent.matches(Potions.WATER)) {
            return this.fallbackBehavior.dispense(pointer, stack);
        }
        ServerWorld serverWorld = pointer.world();
        BlockPos blockPos = pointer.pos();
        BlockPos blockPos2 = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        if (serverWorld.getBlockState(blockPos2).isIn(BlockTags.CONVERTABLE_TO_MUD)) {
            if (!serverWorld.isClient()) {
                for (int i = 0; i < 5; ++i) {
                    serverWorld.spawnParticles(ParticleTypes.SPLASH, (double)blockPos.getX() + serverWorld.random.nextDouble(), blockPos.getY() + 1, (double)blockPos.getZ() + serverWorld.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
                }
            }
            serverWorld.playSound(null, blockPos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            serverWorld.emitGameEvent(null, GameEvent.FLUID_PLACE, blockPos);
            serverWorld.setBlockState(blockPos2, Blocks.MUD.getDefaultState());
            return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.GLASS_BOTTLE));
        }
        return this.fallbackBehavior.dispense(pointer, stack);
    }
}
