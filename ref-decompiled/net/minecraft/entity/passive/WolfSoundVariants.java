package net.minecraft.entity.passive;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class WolfSoundVariants {
   public static final RegistryKey CLASSIC;
   public static final RegistryKey PUGLIN;
   public static final RegistryKey SAD;
   public static final RegistryKey ANGRY;
   public static final RegistryKey GRUMPY;
   public static final RegistryKey BIG;
   public static final RegistryKey CUTE;

   private static RegistryKey of(Type type) {
      return RegistryKey.of(RegistryKeys.WOLF_SOUND_VARIANT, Identifier.ofVanilla(type.getId()));
   }

   public static void bootstrap(Registerable registry) {
      register(registry, CLASSIC, WolfSoundVariants.Type.CLASSIC);
      register(registry, PUGLIN, WolfSoundVariants.Type.PUGLIN);
      register(registry, SAD, WolfSoundVariants.Type.SAD);
      register(registry, ANGRY, WolfSoundVariants.Type.ANGRY);
      register(registry, GRUMPY, WolfSoundVariants.Type.GRUMPY);
      register(registry, BIG, WolfSoundVariants.Type.BIG);
      register(registry, CUTE, WolfSoundVariants.Type.CUTE);
   }

   private static void register(Registerable registry, RegistryKey key, Type type) {
      registry.register(key, (WolfSoundVariant)SoundEvents.WOLF_SOUNDS.get(type));
   }

   public static RegistryEntry select(DynamicRegistryManager registries, Random random) {
      return (RegistryEntry)registries.getOrThrow(RegistryKeys.WOLF_SOUND_VARIANT).getRandom(random).orElseThrow();
   }

   static {
      CLASSIC = of(WolfSoundVariants.Type.CLASSIC);
      PUGLIN = of(WolfSoundVariants.Type.PUGLIN);
      SAD = of(WolfSoundVariants.Type.SAD);
      ANGRY = of(WolfSoundVariants.Type.ANGRY);
      GRUMPY = of(WolfSoundVariants.Type.GRUMPY);
      BIG = of(WolfSoundVariants.Type.BIG);
      CUTE = of(WolfSoundVariants.Type.CUTE);
   }

   public static enum Type {
      CLASSIC("classic", ""),
      PUGLIN("puglin", "_puglin"),
      SAD("sad", "_sad"),
      ANGRY("angry", "_angry"),
      GRUMPY("grumpy", "_grumpy"),
      BIG("big", "_big"),
      CUTE("cute", "_cute");

      private final String id;
      private final String suffix;

      private Type(final String id, final String suffix) {
         this.id = id;
         this.suffix = suffix;
      }

      public String getId() {
         return this.id;
      }

      public String getSoundEventSuffix() {
         return this.suffix;
      }

      // $FF: synthetic method
      private static Type[] method_68141() {
         return new Type[]{CLASSIC, PUGLIN, SAD, ANGRY, GRUMPY, BIG, CUTE};
      }
   }
}
