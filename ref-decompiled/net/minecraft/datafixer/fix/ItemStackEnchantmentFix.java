package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class ItemStackEnchantmentFix extends DataFix {
   private static final Int2ObjectMap ID_TO_ENCHANTMENTS_MAP = (Int2ObjectMap)DataFixUtils.make(new Int2ObjectOpenHashMap(), (map) -> {
      map.put(0, "minecraft:protection");
      map.put(1, "minecraft:fire_protection");
      map.put(2, "minecraft:feather_falling");
      map.put(3, "minecraft:blast_protection");
      map.put(4, "minecraft:projectile_protection");
      map.put(5, "minecraft:respiration");
      map.put(6, "minecraft:aqua_affinity");
      map.put(7, "minecraft:thorns");
      map.put(8, "minecraft:depth_strider");
      map.put(9, "minecraft:frost_walker");
      map.put(10, "minecraft:binding_curse");
      map.put(16, "minecraft:sharpness");
      map.put(17, "minecraft:smite");
      map.put(18, "minecraft:bane_of_arthropods");
      map.put(19, "minecraft:knockback");
      map.put(20, "minecraft:fire_aspect");
      map.put(21, "minecraft:looting");
      map.put(22, "minecraft:sweeping");
      map.put(32, "minecraft:efficiency");
      map.put(33, "minecraft:silk_touch");
      map.put(34, "minecraft:unbreaking");
      map.put(35, "minecraft:fortune");
      map.put(48, "minecraft:power");
      map.put(49, "minecraft:punch");
      map.put(50, "minecraft:flame");
      map.put(51, "minecraft:infinity");
      map.put(61, "minecraft:luck_of_the_sea");
      map.put(62, "minecraft:lure");
      map.put(65, "minecraft:loyalty");
      map.put(66, "minecraft:impaling");
      map.put(67, "minecraft:riptide");
      map.put(68, "minecraft:channeling");
      map.put(70, "minecraft:mending");
      map.put(71, "minecraft:vanishing_curse");
   });

   public ItemStackEnchantmentFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder opticFinder = type.findField("tag");
      return this.fixTypeEverywhereTyped("ItemStackEnchantmentFix", type, (itemStackTyped) -> {
         return itemStackTyped.updateTyped(opticFinder, (tagTyped) -> {
            return tagTyped.update(DSL.remainderFinder(), this::fixEnchantments);
         });
      });
   }

   private Dynamic fixEnchantments(Dynamic tagDynamic) {
      DataResult var10000 = tagDynamic.get("ench").asStreamOpt().map((enchantments) -> {
         return enchantments.map((enchantment) -> {
            return enchantment.set("id", enchantment.createString((String)ID_TO_ENCHANTMENTS_MAP.getOrDefault(enchantment.get("id").asInt(0), "null")));
         });
      });
      Objects.requireNonNull(tagDynamic);
      Optional optional = var10000.map(tagDynamic::createList).result();
      if (optional.isPresent()) {
         tagDynamic = tagDynamic.remove("ench").set("Enchantments", (Dynamic)optional.get());
      }

      return tagDynamic.update("StoredEnchantments", (storedEnchantmentsDynamic) -> {
         DataResult var10000 = storedEnchantmentsDynamic.asStreamOpt().map((storedEnchantments) -> {
            return storedEnchantments.map((storedEnchantment) -> {
               return storedEnchantment.set("id", storedEnchantment.createString((String)ID_TO_ENCHANTMENTS_MAP.getOrDefault(storedEnchantment.get("id").asInt(0), "null")));
            });
         });
         Objects.requireNonNull(storedEnchantmentsDynamic);
         return (Dynamic)DataFixUtils.orElse(var10000.map(storedEnchantmentsDynamic::createList).result(), storedEnchantmentsDynamic);
      });
   }
}
