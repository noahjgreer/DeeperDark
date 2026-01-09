package net.minecraft.entity.decoration.painting;

import java.util.Optional;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class PaintingVariants {
   public static final RegistryKey KEBAB = of("kebab");
   public static final RegistryKey AZTEC = of("aztec");
   public static final RegistryKey ALBAN = of("alban");
   public static final RegistryKey AZTEC2 = of("aztec2");
   public static final RegistryKey BOMB = of("bomb");
   public static final RegistryKey PLANT = of("plant");
   public static final RegistryKey WASTELAND = of("wasteland");
   public static final RegistryKey POOL = of("pool");
   public static final RegistryKey COURBET = of("courbet");
   public static final RegistryKey SEA = of("sea");
   public static final RegistryKey SUNSET = of("sunset");
   public static final RegistryKey CREEBET = of("creebet");
   public static final RegistryKey WANDERER = of("wanderer");
   public static final RegistryKey GRAHAM = of("graham");
   public static final RegistryKey MATCH = of("match");
   public static final RegistryKey BUST = of("bust");
   public static final RegistryKey STAGE = of("stage");
   public static final RegistryKey VOID = of("void");
   public static final RegistryKey SKULL_AND_ROSES = of("skull_and_roses");
   public static final RegistryKey WITHER = of("wither");
   public static final RegistryKey FIGHTERS = of("fighters");
   public static final RegistryKey POINTER = of("pointer");
   public static final RegistryKey PIGSCENE = of("pigscene");
   public static final RegistryKey BURNING_SKULL = of("burning_skull");
   public static final RegistryKey SKELETON = of("skeleton");
   public static final RegistryKey DONKEY_KONG = of("donkey_kong");
   public static final RegistryKey EARTH = of("earth");
   public static final RegistryKey WIND = of("wind");
   public static final RegistryKey WATER = of("water");
   public static final RegistryKey FIRE = of("fire");
   public static final RegistryKey BAROQUE = of("baroque");
   public static final RegistryKey HUMBLE = of("humble");
   public static final RegistryKey MEDITATIVE = of("meditative");
   public static final RegistryKey PRAIRIE_RIDE = of("prairie_ride");
   public static final RegistryKey UNPACKED = of("unpacked");
   public static final RegistryKey BACKYARD = of("backyard");
   public static final RegistryKey BOUQUET = of("bouquet");
   public static final RegistryKey CAVEBIRD = of("cavebird");
   public static final RegistryKey CHANGING = of("changing");
   public static final RegistryKey COTAN = of("cotan");
   public static final RegistryKey ENDBOSS = of("endboss");
   public static final RegistryKey FERN = of("fern");
   public static final RegistryKey FINDING = of("finding");
   public static final RegistryKey LOWMIST = of("lowmist");
   public static final RegistryKey ORB = of("orb");
   public static final RegistryKey OWLEMONS = of("owlemons");
   public static final RegistryKey PASSAGE = of("passage");
   public static final RegistryKey POND = of("pond");
   public static final RegistryKey SUNFLOWERS = of("sunflowers");
   public static final RegistryKey TIDES = of("tides");
   public static final RegistryKey DENNIS = of("dennis");

   public static void bootstrap(Registerable registry) {
      register(registry, KEBAB, 1, 1);
      register(registry, AZTEC, 1, 1);
      register(registry, ALBAN, 1, 1);
      register(registry, AZTEC2, 1, 1);
      register(registry, BOMB, 1, 1);
      register(registry, PLANT, 1, 1);
      register(registry, WASTELAND, 1, 1);
      register(registry, POOL, 2, 1);
      register(registry, COURBET, 2, 1);
      register(registry, SEA, 2, 1);
      register(registry, SUNSET, 2, 1);
      register(registry, CREEBET, 2, 1);
      register(registry, WANDERER, 1, 2);
      register(registry, GRAHAM, 1, 2);
      register(registry, MATCH, 2, 2);
      register(registry, BUST, 2, 2);
      register(registry, STAGE, 2, 2);
      register(registry, VOID, 2, 2);
      register(registry, SKULL_AND_ROSES, 2, 2);
      register(registry, WITHER, 2, 2, false);
      register(registry, FIGHTERS, 4, 2);
      register(registry, POINTER, 4, 4);
      register(registry, PIGSCENE, 4, 4);
      register(registry, BURNING_SKULL, 4, 4);
      register(registry, SKELETON, 4, 3);
      register(registry, EARTH, 2, 2, false);
      register(registry, WIND, 2, 2, false);
      register(registry, WATER, 2, 2, false);
      register(registry, FIRE, 2, 2, false);
      register(registry, DONKEY_KONG, 4, 3);
      register(registry, BAROQUE, 2, 2);
      register(registry, HUMBLE, 2, 2);
      register(registry, MEDITATIVE, 1, 1);
      register(registry, PRAIRIE_RIDE, 1, 2);
      register(registry, UNPACKED, 4, 4);
      register(registry, BACKYARD, 3, 4);
      register(registry, BOUQUET, 3, 3);
      register(registry, CAVEBIRD, 3, 3);
      register(registry, CHANGING, 4, 2);
      register(registry, COTAN, 3, 3);
      register(registry, ENDBOSS, 3, 3);
      register(registry, FERN, 3, 3);
      register(registry, FINDING, 4, 2);
      register(registry, LOWMIST, 4, 2);
      register(registry, ORB, 4, 4);
      register(registry, OWLEMONS, 3, 3);
      register(registry, PASSAGE, 4, 2);
      register(registry, POND, 3, 4);
      register(registry, SUNFLOWERS, 3, 3);
      register(registry, TIDES, 3, 3);
      register(registry, DENNIS, 3, 3);
   }

   private static void register(Registerable registry, RegistryKey key, int width, int height) {
      register(registry, key, width, height, true);
   }

   private static void register(Registerable registry, RegistryKey key, int width, int height, boolean hasAuthor) {
      registry.register(key, new PaintingVariant(width, height, key.getValue(), Optional.of(Text.translatable(key.getValue().toTranslationKey("painting", "title")).formatted(Formatting.YELLOW)), hasAuthor ? Optional.of(Text.translatable(key.getValue().toTranslationKey("painting", "author")).formatted(Formatting.GRAY)) : Optional.empty()));
   }

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.PAINTING_VARIANT, Identifier.ofVanilla(id));
   }
}
