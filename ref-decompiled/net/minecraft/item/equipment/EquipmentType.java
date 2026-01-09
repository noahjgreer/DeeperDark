package net.minecraft.item.equipment;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.StringIdentifiable;

public enum EquipmentType implements StringIdentifiable {
   HELMET(EquipmentSlot.HEAD, 11, "helmet"),
   CHESTPLATE(EquipmentSlot.CHEST, 16, "chestplate"),
   LEGGINGS(EquipmentSlot.LEGS, 15, "leggings"),
   BOOTS(EquipmentSlot.FEET, 13, "boots"),
   BODY(EquipmentSlot.BODY, 16, "body");

   public static final Codec CODEC = StringIdentifiable.createBasicCodec(EquipmentType::values);
   private final EquipmentSlot equipmentSlot;
   private final String name;
   private final int baseMaxDamage;

   private EquipmentType(final EquipmentSlot equipmentSlot, final int baseMaxDamage, final String name) {
      this.equipmentSlot = equipmentSlot;
      this.name = name;
      this.baseMaxDamage = baseMaxDamage;
   }

   public int getMaxDamage(int multiplier) {
      return this.baseMaxDamage * multiplier;
   }

   public EquipmentSlot getEquipmentSlot() {
      return this.equipmentSlot;
   }

   public String getName() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static EquipmentType[] method_48401() {
      return new EquipmentType[]{HELMET, CHESTPLATE, LEGGINGS, BOOTS, BODY};
   }
}
