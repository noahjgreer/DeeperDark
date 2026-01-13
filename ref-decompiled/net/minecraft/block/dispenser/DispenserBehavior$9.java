/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.dispenser;

import java.util.List;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

static class DispenserBehavior.9
extends FallibleItemDispenserBehavior {
    DispenserBehavior.9() {
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        List<AbstractDonkeyEntity> list = pointer.world().getEntitiesByClass(AbstractDonkeyEntity.class, new Box(blockPos), donkey -> donkey.isAlive() && !donkey.hasChest());
        for (AbstractDonkeyEntity abstractDonkeyEntity : list) {
            StackReference stackReference;
            if (!abstractDonkeyEntity.isTame() || (stackReference = abstractDonkeyEntity.getStackReference(499)) == null || !stackReference.set(stack)) continue;
            stack.decrement(1);
            this.setSuccess(true);
            return stack;
        }
        return super.dispenseSilently(pointer, stack);
    }
}
