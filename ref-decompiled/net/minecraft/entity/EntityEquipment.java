package net.minecraft.entity;

import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import net.minecraft.item.ItemStack;

public class EntityEquipment {
   public static final Codec CODEC;
   private final EnumMap map;

   private EntityEquipment(EnumMap map) {
      this.map = map;
   }

   public EntityEquipment() {
      this(new EnumMap(EquipmentSlot.class));
   }

   public ItemStack put(EquipmentSlot slot, ItemStack stack) {
      stack.getItem().postProcessComponents(stack);
      return (ItemStack)Objects.requireNonNullElse((ItemStack)this.map.put(slot, stack), ItemStack.EMPTY);
   }

   public ItemStack get(EquipmentSlot slot) {
      return (ItemStack)this.map.getOrDefault(slot, ItemStack.EMPTY);
   }

   public boolean isEmpty() {
      Iterator var1 = this.map.values().iterator();

      ItemStack itemStack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemStack = (ItemStack)var1.next();
      } while(itemStack.isEmpty());

      return false;
   }

   public void tick(Entity entity) {
      Iterator var2 = this.map.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         ItemStack itemStack = (ItemStack)entry.getValue();
         if (!itemStack.isEmpty()) {
            itemStack.inventoryTick(entity.getWorld(), entity, (EquipmentSlot)entry.getKey());
         }
      }

   }

   public void copyFrom(EntityEquipment equipment) {
      this.map.clear();
      this.map.putAll(equipment.map);
   }

   public void dropAll(LivingEntity entity) {
      Iterator var2 = this.map.values().iterator();

      while(var2.hasNext()) {
         ItemStack itemStack = (ItemStack)var2.next();
         entity.dropItem(itemStack, true, false);
      }

      this.clear();
   }

   public void clear() {
      this.map.replaceAll((slot, stack) -> {
         return ItemStack.EMPTY;
      });
   }

   static {
      CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, ItemStack.CODEC).xmap((map) -> {
         EnumMap enumMap = new EnumMap(EquipmentSlot.class);
         enumMap.putAll(map);
         return new EntityEquipment(enumMap);
      }, (equipment) -> {
         Map map = new EnumMap(equipment.map);
         map.values().removeIf(ItemStack::isEmpty);
         return map;
      });
   }
}
