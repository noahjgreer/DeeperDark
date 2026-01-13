/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import java.util.function.Consumer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

static class DispenserBehavior.8
extends ItemDispenserBehavior {
    DispenserBehavior.8() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        Consumer<ArmorStandEntity> consumer;
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        BlockPos blockPos = pointer.pos().offset(direction);
        ServerWorld serverWorld = pointer.world();
        ArmorStandEntity armorStandEntity = EntityType.ARMOR_STAND.spawn(serverWorld, consumer = EntityType.copier(armorStand -> armorStand.setYaw(direction.getPositiveHorizontalDegrees()), serverWorld, stack, null), blockPos, SpawnReason.DISPENSER, false, false);
        if (armorStandEntity != null) {
            stack.decrement(1);
        }
        return stack;
    }
}
