package net.minecraft.enchantment;

import java.util.function.Consumer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record EnchantmentEffectContext(ItemStack stack, @Nullable EquipmentSlot slot, @Nullable LivingEntity owner, Consumer breakCallback) {
   public EnchantmentEffectContext(ItemStack stack, EquipmentSlot slot, LivingEntity owner) {
      this(stack, slot, owner, (item) -> {
         owner.sendEquipmentBreakStatus(item, slot);
      });
   }

   public EnchantmentEffectContext(ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable LivingEntity livingEntity, Consumer consumer) {
      this.stack = itemStack;
      this.slot = equipmentSlot;
      this.owner = livingEntity;
      this.breakCallback = consumer;
   }

   public ItemStack stack() {
      return this.stack;
   }

   @Nullable
   public EquipmentSlot slot() {
      return this.slot;
   }

   @Nullable
   public LivingEntity owner() {
      return this.owner;
   }

   public Consumer breakCallback() {
      return this.breakCallback;
   }
}
