package net.minecraft.entity;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public record EquipmentDropChances(Map byEquipment) {
   public static final float DEFAULT_CHANCE = 0.085F;
   public static final float UNHARMED_DROP_THRESHOLD = 1.0F;
   public static final int GUARANTEED_DROP_CHANCE = 2;
   public static final EquipmentDropChances DEFAULT = new EquipmentDropChances(Util.mapEnum(EquipmentSlot.class, (slot) -> {
      return 0.085F;
   }));
   public static final Codec CODEC;

   public EquipmentDropChances(Map map) {
      this.byEquipment = map;
   }

   private static Map getWithoutDefaultChances(Map byEquipment) {
      Map map = new HashMap(byEquipment);
      map.values().removeIf((chance) -> {
         return chance == 0.085F;
      });
      return map;
   }

   private static Map getWithDefaultChances(Map byEquipment) {
      return Util.mapEnum(EquipmentSlot.class, (slot) -> {
         return (Float)byEquipment.getOrDefault(slot, 0.085F);
      });
   }

   public EquipmentDropChances withGuaranteed(EquipmentSlot slot) {
      return this.withChance(slot, 2.0F);
   }

   public EquipmentDropChances withChance(EquipmentSlot slot, float chance) {
      if (chance < 0.0F) {
         throw new IllegalArgumentException("Tried to set invalid equipment chance " + chance + " for " + String.valueOf(slot));
      } else {
         return this.get(slot) == chance ? this : new EquipmentDropChances(Util.mapEnum(EquipmentSlot.class, (slotx) -> {
            return slotx == slot ? chance : this.get(slotx);
         }));
      }
   }

   public float get(EquipmentSlot slot) {
      return (Float)this.byEquipment.getOrDefault(slot, 0.085F);
   }

   public boolean dropsExactly(EquipmentSlot slot) {
      return this.get(slot) > 1.0F;
   }

   public Map byEquipment() {
      return this.byEquipment;
   }

   static {
      CODEC = Codec.unboundedMap(EquipmentSlot.CODEC, Codecs.NON_NEGATIVE_FLOAT).xmap(EquipmentDropChances::getWithDefaultChances, EquipmentDropChances::getWithoutDefaultChances).xmap(EquipmentDropChances::new, EquipmentDropChances::byEquipment);
   }
}
