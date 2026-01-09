package net.minecraft.item.equipment;

import java.util.Map;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public interface EquipmentAssetKeys {
   RegistryKey REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset"));
   RegistryKey LEATHER = register("leather");
   RegistryKey CHAINMAIL = register("chainmail");
   RegistryKey IRON = register("iron");
   RegistryKey GOLD = register("gold");
   RegistryKey DIAMOND = register("diamond");
   RegistryKey TURTLE_SCUTE = register("turtle_scute");
   RegistryKey NETHERITE = register("netherite");
   RegistryKey ARMADILLO_SCUTE = register("armadillo_scute");
   RegistryKey ELYTRA = register("elytra");
   RegistryKey SADDLE = register("saddle");
   Map CARPET_FROM_COLOR = Util.mapEnum(DyeColor.class, (color) -> {
      return register(color.asString() + "_carpet");
   });
   RegistryKey TRADER_LLAMA = register("trader_llama");
   Map HARNESS_FROM_COLOR = Util.mapEnum(DyeColor.class, (color) -> {
      return register(color.asString() + "_harness");
   });

   static RegistryKey register(String name) {
      return RegistryKey.of(REGISTRY_KEY, Identifier.ofVanilla(name));
   }
}
