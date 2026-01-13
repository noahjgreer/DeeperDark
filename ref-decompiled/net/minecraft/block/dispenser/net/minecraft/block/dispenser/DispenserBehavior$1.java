/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

static class DispenserBehavior.1
extends ItemDispenserBehavior {
    DispenserBehavior.1() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        EntityType<?> entityType = ((SpawnEggItem)stack.getItem()).getEntityType(stack);
        if (entityType == null) {
            return stack;
        }
        try {
            entityType.spawnFromItemStack(pointer.world(), stack, null, pointer.pos().offset(direction), SpawnReason.DISPENSER, direction != Direction.UP, false);
        }
        catch (Exception exception) {
            LOGGER.error("Error while dispensing spawn egg from dispenser at {}", (Object)pointer.pos(), (Object)exception);
            return ItemStack.EMPTY;
        }
        stack.decrement(1);
        pointer.world().emitGameEvent(null, GameEvent.ENTITY_PLACE, pointer.pos());
        return stack;
    }
}
