package net.minecraft.block.dispenser;

import java.util.List;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class EquippableDispenserBehavior extends ItemDispenserBehavior {
   public static final EquippableDispenserBehavior INSTANCE = new EquippableDispenserBehavior();

   protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
      return dispense(pointer, stack) ? stack : super.dispenseSilently(pointer, stack);
   }

   public static boolean dispense(BlockPointer pointer, ItemStack stack) {
      BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get(DispenserBlock.FACING));
      List list = pointer.world().getEntitiesByClass(LivingEntity.class, new Box(blockPos), (entity) -> {
         return entity.canEquipFromDispenser(stack);
      });
      if (list.isEmpty()) {
         return false;
      } else {
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
}
