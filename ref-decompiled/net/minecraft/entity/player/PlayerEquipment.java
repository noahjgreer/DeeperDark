package net.minecraft.entity.player;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class PlayerEquipment extends EntityEquipment {
   private final PlayerEntity player;

   public PlayerEquipment(PlayerEntity player) {
      this.player = player;
   }

   public ItemStack put(EquipmentSlot slot, ItemStack stack) {
      return slot == EquipmentSlot.MAINHAND ? this.player.getInventory().setSelectedStack(stack) : super.put(slot, stack);
   }

   public ItemStack get(EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND ? this.player.getInventory().getSelectedStack() : super.get(slot);
   }

   public boolean isEmpty() {
      return this.player.getInventory().getSelectedStack().isEmpty() && super.isEmpty();
   }
}
