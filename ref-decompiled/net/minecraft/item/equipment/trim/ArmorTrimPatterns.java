package net.minecraft.item.equipment.trim;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ArmorTrimPatterns {
   public static final RegistryKey SENTRY = of("sentry");
   public static final RegistryKey DUNE = of("dune");
   public static final RegistryKey COAST = of("coast");
   public static final RegistryKey WILD = of("wild");
   public static final RegistryKey WARD = of("ward");
   public static final RegistryKey EYE = of("eye");
   public static final RegistryKey VEX = of("vex");
   public static final RegistryKey TIDE = of("tide");
   public static final RegistryKey SNOUT = of("snout");
   public static final RegistryKey RIB = of("rib");
   public static final RegistryKey SPIRE = of("spire");
   public static final RegistryKey WAYFINDER = of("wayfinder");
   public static final RegistryKey SHAPER = of("shaper");
   public static final RegistryKey SILENCE = of("silence");
   public static final RegistryKey RAISER = of("raiser");
   public static final RegistryKey HOST = of("host");
   public static final RegistryKey FLOW = of("flow");
   public static final RegistryKey BOLT = of("bolt");

   public static void bootstrap(Registerable registry) {
      register(registry, SENTRY);
      register(registry, DUNE);
      register(registry, COAST);
      register(registry, WILD);
      register(registry, WARD);
      register(registry, EYE);
      register(registry, VEX);
      register(registry, TIDE);
      register(registry, SNOUT);
      register(registry, RIB);
      register(registry, SPIRE);
      register(registry, WAYFINDER);
      register(registry, SHAPER);
      register(registry, SILENCE);
      register(registry, RAISER);
      register(registry, HOST);
      register(registry, FLOW);
      register(registry, BOLT);
   }

   public static void register(Registerable registry, RegistryKey key) {
      ArmorTrimPattern armorTrimPattern = new ArmorTrimPattern(getId(key), Text.translatable(Util.createTranslationKey("trim_pattern", key.getValue())), false);
      registry.register(key, armorTrimPattern);
   }

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.TRIM_PATTERN, Identifier.ofVanilla(id));
   }

   public static Identifier getId(RegistryKey key) {
      return key.getValue();
   }
}
