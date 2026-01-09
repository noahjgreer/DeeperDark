package net.minecraft.entity;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;

public record EquipmentTable(RegistryKey lootTable, Map slotDropChances) {
   public static final Codec SLOT_DROP_CHANCES_CODEC;
   public static final Codec CODEC;

   public EquipmentTable(RegistryKey lootTable, float slotDropChances) {
      this(lootTable, createSlotDropChances(slotDropChances));
   }

   public EquipmentTable(RegistryKey registryKey, Map map) {
      this.lootTable = registryKey;
      this.slotDropChances = map;
   }

   private static Map createSlotDropChances(float dropChance) {
      return createSlotDropChances(List.of(EquipmentSlot.values()), dropChance);
   }

   private static Map createSlotDropChances(List slots, float dropChance) {
      Map map = Maps.newHashMap();
      Iterator var3 = slots.iterator();

      while(var3.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var3.next();
         map.put(equipmentSlot, dropChance);
      }

      return map;
   }

   public RegistryKey lootTable() {
      return this.lootTable;
   }

   public Map slotDropChances() {
      return this.slotDropChances;
   }

   static {
      SLOT_DROP_CHANCES_CODEC = Codec.either(Codec.FLOAT, Codec.unboundedMap(EquipmentSlot.CODEC, Codec.FLOAT)).xmap((either) -> {
         return (Map)either.map(EquipmentTable::createSlotDropChances, Function.identity());
      }, (map) -> {
         boolean bl = map.values().stream().distinct().count() == 1L;
         boolean bl2 = map.keySet().containsAll(EquipmentSlot.VALUES);
         return bl && bl2 ? Either.left((Float)map.values().stream().findFirst().orElse(0.0F)) : Either.right(map);
      });
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(LootTable.TABLE_KEY.fieldOf("loot_table").forGetter(EquipmentTable::lootTable), SLOT_DROP_CHANCES_CODEC.optionalFieldOf("slot_drop_chances", Map.of()).forGetter(EquipmentTable::slotDropChances)).apply(instance, EquipmentTable::new);
      });
   }
}
