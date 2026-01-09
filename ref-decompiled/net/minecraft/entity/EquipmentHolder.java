package net.minecraft.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

public interface EquipmentHolder {
   void equipStack(EquipmentSlot slot, ItemStack stack);

   ItemStack getEquippedStack(EquipmentSlot slot);

   void setEquipmentDropChance(EquipmentSlot slot, float dropChance);

   default void setEquipmentFromTable(EquipmentTable equipmentTable, LootWorldContext parameters) {
      this.setEquipmentFromTable(equipmentTable.lootTable(), parameters, equipmentTable.slotDropChances());
   }

   default void setEquipmentFromTable(RegistryKey lootTable, LootWorldContext parameters, Map slotDropChances) {
      this.setEquipmentFromTable(lootTable, parameters, 0L, slotDropChances);
   }

   default void setEquipmentFromTable(RegistryKey lootTable, LootWorldContext parameters, long seed, Map slotDropChances) {
      LootTable lootTable2 = parameters.getWorld().getServer().getReloadableRegistries().getLootTable(lootTable);
      if (lootTable2 != LootTable.EMPTY) {
         List list = lootTable2.generateLoot(parameters, seed);
         List list2 = new ArrayList();
         Iterator var9 = list.iterator();

         while(var9.hasNext()) {
            ItemStack itemStack = (ItemStack)var9.next();
            EquipmentSlot equipmentSlot = this.getSlotForStack(itemStack, list2);
            if (equipmentSlot != null) {
               ItemStack itemStack2 = equipmentSlot.split(itemStack);
               this.equipStack(equipmentSlot, itemStack2);
               Float float_ = (Float)slotDropChances.get(equipmentSlot);
               if (float_ != null) {
                  this.setEquipmentDropChance(equipmentSlot, float_);
               }

               list2.add(equipmentSlot);
            }
         }

      }
   }

   @Nullable
   default EquipmentSlot getSlotForStack(ItemStack stack, List slotBlacklist) {
      if (stack.isEmpty()) {
         return null;
      } else {
         EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
         if (equippableComponent != null) {
            EquipmentSlot equipmentSlot = equippableComponent.slot();
            if (!slotBlacklist.contains(equipmentSlot)) {
               return equipmentSlot;
            }
         } else if (!slotBlacklist.contains(EquipmentSlot.MAINHAND)) {
            return EquipmentSlot.MAINHAND;
         }

         return null;
      }
   }
}
