package net.minecraft.world.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.BedPart;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class PointOfInterestTypes {
   public static final RegistryKey ARMORER = of("armorer");
   public static final RegistryKey BUTCHER = of("butcher");
   public static final RegistryKey CARTOGRAPHER = of("cartographer");
   public static final RegistryKey CLERIC = of("cleric");
   public static final RegistryKey FARMER = of("farmer");
   public static final RegistryKey FISHERMAN = of("fisherman");
   public static final RegistryKey FLETCHER = of("fletcher");
   public static final RegistryKey LEATHERWORKER = of("leatherworker");
   public static final RegistryKey LIBRARIAN = of("librarian");
   public static final RegistryKey MASON = of("mason");
   public static final RegistryKey SHEPHERD = of("shepherd");
   public static final RegistryKey TOOLSMITH = of("toolsmith");
   public static final RegistryKey WEAPONSMITH = of("weaponsmith");
   public static final RegistryKey HOME = of("home");
   public static final RegistryKey MEETING = of("meeting");
   public static final RegistryKey BEEHIVE = of("beehive");
   public static final RegistryKey BEE_NEST = of("bee_nest");
   public static final RegistryKey NETHER_PORTAL = of("nether_portal");
   public static final RegistryKey LODESTONE = of("lodestone");
   public static final RegistryKey LIGHTNING_ROD = of("lightning_rod");
   public static final RegistryKey TEST_INSTANCE = of("test_instance");
   private static final Set BED_HEADS;
   private static final Set CAULDRONS;
   private static final Map POI_STATES_TO_TYPE;

   private static Set getStatesOfBlock(Block block) {
      return ImmutableSet.copyOf(block.getStateManager().getStates());
   }

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.ofVanilla(id));
   }

   private static PointOfInterestType register(Registry registry, RegistryKey key, Set states, int ticketCount, int searchDistance) {
      PointOfInterestType pointOfInterestType = new PointOfInterestType(states, ticketCount, searchDistance);
      Registry.register(registry, (RegistryKey)key, pointOfInterestType);
      registerStates(registry.getOrThrow(key), states);
      return pointOfInterestType;
   }

   private static void registerStates(RegistryEntry poiTypeEntry, Set states) {
      states.forEach((state) -> {
         RegistryEntry registryEntry2 = (RegistryEntry)POI_STATES_TO_TYPE.put(state, poiTypeEntry);
         if (registryEntry2 != null) {
            throw (IllegalStateException)Util.getFatalOrPause(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", state)));
         }
      });
   }

   public static Optional getTypeForState(BlockState state) {
      return Optional.ofNullable((RegistryEntry)POI_STATES_TO_TYPE.get(state));
   }

   public static boolean isPointOfInterest(BlockState state) {
      return POI_STATES_TO_TYPE.containsKey(state);
   }

   public static PointOfInterestType registerAndGetDefault(Registry registry) {
      register(registry, ARMORER, getStatesOfBlock(Blocks.BLAST_FURNACE), 1, 1);
      register(registry, BUTCHER, getStatesOfBlock(Blocks.SMOKER), 1, 1);
      register(registry, CARTOGRAPHER, getStatesOfBlock(Blocks.CARTOGRAPHY_TABLE), 1, 1);
      register(registry, CLERIC, getStatesOfBlock(Blocks.BREWING_STAND), 1, 1);
      register(registry, FARMER, getStatesOfBlock(Blocks.COMPOSTER), 1, 1);
      register(registry, FISHERMAN, getStatesOfBlock(Blocks.BARREL), 1, 1);
      register(registry, FLETCHER, getStatesOfBlock(Blocks.FLETCHING_TABLE), 1, 1);
      register(registry, LEATHERWORKER, CAULDRONS, 1, 1);
      register(registry, LIBRARIAN, getStatesOfBlock(Blocks.LECTERN), 1, 1);
      register(registry, MASON, getStatesOfBlock(Blocks.STONECUTTER), 1, 1);
      register(registry, SHEPHERD, getStatesOfBlock(Blocks.LOOM), 1, 1);
      register(registry, TOOLSMITH, getStatesOfBlock(Blocks.SMITHING_TABLE), 1, 1);
      register(registry, WEAPONSMITH, getStatesOfBlock(Blocks.GRINDSTONE), 1, 1);
      register(registry, HOME, BED_HEADS, 1, 1);
      register(registry, MEETING, getStatesOfBlock(Blocks.BELL), 32, 6);
      register(registry, BEEHIVE, getStatesOfBlock(Blocks.BEEHIVE), 0, 1);
      register(registry, BEE_NEST, getStatesOfBlock(Blocks.BEE_NEST), 0, 1);
      register(registry, NETHER_PORTAL, getStatesOfBlock(Blocks.NETHER_PORTAL), 0, 1);
      register(registry, LODESTONE, getStatesOfBlock(Blocks.LODESTONE), 0, 1);
      register(registry, TEST_INSTANCE, getStatesOfBlock(Blocks.TEST_INSTANCE_BLOCK), 0, 1);
      return register(registry, LIGHTNING_ROD, getStatesOfBlock(Blocks.LIGHTNING_ROD), 0, 1);
   }

   static {
      BED_HEADS = (Set)ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((block) -> {
         return block.getStateManager().getStates().stream();
      }).filter((state) -> {
         return state.get(BedBlock.PART) == BedPart.HEAD;
      }).collect(ImmutableSet.toImmutableSet());
      CAULDRONS = (Set)ImmutableList.of(Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON).stream().flatMap((block) -> {
         return block.getStateManager().getStates().stream();
      }).collect(ImmutableSet.toImmutableSet());
      POI_STATES_TO_TYPE = Maps.newHashMap();
   }
}
