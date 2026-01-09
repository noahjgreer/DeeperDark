package net.minecraft.screen.slot;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

class ArmorSlot extends Slot {
   private final LivingEntity entity;
   private final EquipmentSlot equipmentSlot;
   @Nullable
   private final Identifier backgroundSprite;

   public ArmorSlot(Inventory inventory, LivingEntity entity, EquipmentSlot equipmentSlot, int index, int x, int y, @Nullable Identifier backgroundSprite) {
      super(inventory, index, x, y);
      this.entity = entity;
      this.equipmentSlot = equipmentSlot;
      this.backgroundSprite = backgroundSprite;
   }

   public void setStack(ItemStack stack, ItemStack previousStack) {
      this.entity.onEquipStack(this.equipmentSlot, previousStack, stack);
      super.setStack(stack, previousStack);
   }

   public int getMaxItemCount() {
      return 1;
   }

   public boolean canInsert(ItemStack stack) {
      return this.entity.canEquip(stack, this.equipmentSlot);
   }

   public boolean isEnabled() {
      return this.entity.canUseSlot(this.equipmentSlot);
   }

   public boolean canTakeItems(PlayerEntity playerEntity) {
      ItemStack itemStack = this.getStack();
      return !itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) ? false : super.canTakeItems(playerEntity);
   }

   @Nullable
   public Identifier getBackgroundSprite() {
      return this.backgroundSprite;
   }
}
