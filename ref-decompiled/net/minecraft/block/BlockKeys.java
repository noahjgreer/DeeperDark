package net.minecraft.block;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BlockKeys {
   public static final RegistryKey PUMPKIN = of("pumpkin");
   public static final RegistryKey PUMPKIN_STEM = of("pumpkin_stem");
   public static final RegistryKey ATTACHED_PUMPKIN_STEM = of("attached_pumpkin_stem");
   public static final RegistryKey MELON = of("melon");
   public static final RegistryKey MELON_STEM = of("melon_stem");
   public static final RegistryKey ATTACHED_MELON_STEM = of("attached_melon_stem");

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla(id));
   }
}
