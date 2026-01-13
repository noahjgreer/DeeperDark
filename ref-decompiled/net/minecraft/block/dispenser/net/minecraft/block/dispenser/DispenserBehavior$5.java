/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import java.util.List;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

static class DispenserBehavior.5
extends FallibleItemDispenserBehavior {
    DispenserBehavior.5() {
    }

    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        BlockPos blockPos;
        ServerWorld serverWorld = pointer.world();
        List<Entity> list = serverWorld.getEntitiesByClass(ArmadilloEntity.class, new Box(blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING))), EntityPredicates.EXCEPT_SPECTATOR);
        if (list.isEmpty()) {
            this.setSuccess(false);
            return stack;
        }
        for (ArmadilloEntity armadilloEntity : list) {
            if (!armadilloEntity.brushScute(null, stack)) continue;
            stack.damage(16, serverWorld, null, item -> {});
            return stack;
        }
        this.setSuccess(false);
        return stack;
    }
}
