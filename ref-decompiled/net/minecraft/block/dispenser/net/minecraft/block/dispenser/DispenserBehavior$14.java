/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;

static class DispenserBehavior.14
extends FallibleItemDispenserBehavior {
    DispenserBehavior.14() {
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld serverWorld = pointer.world();
        if (!serverWorld.getGameRules().getValue(GameRules.TNT_EXPLODES).booleanValue()) {
            this.setSuccess(false);
            return stack;
        }
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        TntEntity tntEntity = new TntEntity(serverWorld, (double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, null);
        serverWorld.spawnEntity(tntEntity);
        serverWorld.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        serverWorld.emitGameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
        stack.decrement(1);
        this.setSuccess(true);
        return stack;
    }
}
