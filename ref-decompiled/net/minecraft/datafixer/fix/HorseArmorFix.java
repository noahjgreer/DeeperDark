package net.minecraft.datafixer.fix;

import com.google.common.collect.Streams;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class HorseArmorFix extends ChoiceWriteReadFix {
   private final String oldNbtKey;
   private final boolean removeOldArmor;

   public HorseArmorFix(Schema outputSchema, String entityId, String oldNbtKey, boolean removeOldArmor) {
      super(outputSchema, true, "Horse armor fix for " + entityId, TypeReferences.ENTITY, entityId);
      this.oldNbtKey = oldNbtKey;
      this.removeOldArmor = removeOldArmor;
   }

   protected Dynamic transform(Dynamic data) {
      Optional optional = data.get(this.oldNbtKey).result();
      if (optional.isPresent()) {
         Dynamic dynamic = (Dynamic)optional.get();
         Dynamic dynamic2 = data.remove(this.oldNbtKey);
         if (this.removeOldArmor) {
            dynamic2 = dynamic2.update("ArmorItems", (armorItemsDynamic) -> {
               return armorItemsDynamic.createList(Streams.mapWithIndex(armorItemsDynamic.asStream(), (itemDynamic, slot) -> {
                  return slot == 2L ? itemDynamic.emptyMap() : itemDynamic;
               }));
            });
            dynamic2 = dynamic2.update("ArmorDropChances", (armorDropChancesDynamic) -> {
               return armorDropChancesDynamic.createList(Streams.mapWithIndex(armorDropChancesDynamic.asStream(), (dropChanceDynamic, slot) -> {
                  return slot == 2L ? dropChanceDynamic.createFloat(0.085F) : dropChanceDynamic;
               }));
            });
         }

         dynamic2 = dynamic2.set("body_armor_item", dynamic);
         dynamic2 = dynamic2.set("body_armor_drop_chance", data.createFloat(2.0F));
         return dynamic2;
      } else {
         return data;
      }
   }
}
