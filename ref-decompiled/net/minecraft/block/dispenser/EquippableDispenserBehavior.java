/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.dispenser.EquippableDispenserBehavior
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 */
package net.minecraft.block.dispenser;

import java.util.List;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

/*
 * Exception performing whole class analysis ignored.
 */
public class EquippableDispenserBehavior
extends ItemDispenserBehavior {
    public static final EquippableDispenserBehavior INSTANCE = new EquippableDispenserBehavior();

    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        return EquippableDispenserBehavior.dispense((BlockPointer)pointer, (ItemStack)stack) ? stack : super.dispenseSilently(pointer, stack);
    }

    public static boolean dispense(BlockPointer pointer, ItemStack stack) {
        BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get((Property)DispenserBlock.FACING));
        List list = pointer.world().getEntitiesByClass(LivingEntity.class, new Box(blockPos), entity -> entity.canEquipFromDispenser(stack));
        if (list.isEmpty()) {
            return false;
        }
        LivingEntity livingEntity = (LivingEntity)list.getFirst();
        EquipmentSlot equipmentSlot = livingEntity.getPreferredEquipmentSlot(stack);
        ItemStack itemStack = stack.split(1);
        livingEntity.equipStack(equipmentSlot, itemStack);
        if (livingEntity instanceof MobEntity) {
            MobEntity mobEntity = (MobEntity)livingEntity;
            mobEntity.setDropGuaranteed(equipmentSlot);
            mobEntity.setPersistent();
        }
        return true;
    }
}

