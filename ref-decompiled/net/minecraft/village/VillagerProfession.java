package net.minecraft.village;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.PointOfInterestTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;

public record VillagerProfession(Text id, Predicate heldWorkstation, Predicate acquirableWorkstation, ImmutableSet gatherableItems, ImmutableSet secondaryJobSites, @Nullable SoundEvent workSound) {
   public static final Predicate IS_ACQUIRABLE_JOB_SITE = (poiType) -> {
      return poiType.isIn(PointOfInterestTypeTags.ACQUIRABLE_JOB_SITE);
   };
   public static final RegistryKey NONE = of("none");
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
   public static final RegistryKey NITWIT = of("nitwit");
   public static final RegistryKey SHEPHERD = of("shepherd");
   public static final RegistryKey TOOLSMITH = of("toolsmith");
   public static final RegistryKey WEAPONSMITH = of("weaponsmith");

   public VillagerProfession(Text text, Predicate predicate, Predicate predicate2, ImmutableSet immutableSet, ImmutableSet immutableSet2, @Nullable SoundEvent soundEvent) {
      this.id = text;
      this.heldWorkstation = predicate;
      this.acquirableWorkstation = predicate2;
      this.gatherableItems = immutableSet;
      this.secondaryJobSites = immutableSet2;
      this.workSound = soundEvent;
   }

   private static RegistryKey of(String id) {
      return RegistryKey.of(RegistryKeys.VILLAGER_PROFESSION, Identifier.ofVanilla(id));
   }

   private static VillagerProfession register(Registry registry, RegistryKey key, RegistryKey heldWorkstation, @Nullable SoundEvent workSound) {
      return register(registry, key, (entry) -> {
         return entry.matchesKey(heldWorkstation);
      }, (entry) -> {
         return entry.matchesKey(heldWorkstation);
      }, workSound);
   }

   private static VillagerProfession register(Registry registry, RegistryKey key, Predicate heldWorkstation, Predicate acquirableWorkstation, @Nullable SoundEvent workSound) {
      return register(registry, key, heldWorkstation, acquirableWorkstation, ImmutableSet.of(), ImmutableSet.of(), workSound);
   }

   private static VillagerProfession register(Registry registry, RegistryKey key, RegistryKey heldWorkstation, ImmutableSet gatherableItems, ImmutableSet secondaryJobSites, @Nullable SoundEvent workSound) {
      return register(registry, key, (entry) -> {
         return entry.matchesKey(heldWorkstation);
      }, (entry) -> {
         return entry.matchesKey(heldWorkstation);
      }, gatherableItems, secondaryJobSites, workSound);
   }

   private static VillagerProfession register(Registry registry, RegistryKey key, Predicate heldWorkstation, Predicate acquirableWorkstation, ImmutableSet gatherableItems, ImmutableSet secondaryJobSites, @Nullable SoundEvent workSound) {
      String var10004 = key.getValue().getNamespace();
      return (VillagerProfession)Registry.register(registry, (RegistryKey)key, new VillagerProfession(Text.translatable("entity." + var10004 + ".villager." + key.getValue().getPath()), heldWorkstation, acquirableWorkstation, gatherableItems, secondaryJobSites, workSound));
   }

   public static VillagerProfession registerAndGetDefault(Registry registry) {
      register(registry, NONE, PointOfInterestType.NONE, IS_ACQUIRABLE_JOB_SITE, (SoundEvent)null);
      register(registry, ARMORER, PointOfInterestTypes.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
      register(registry, BUTCHER, PointOfInterestTypes.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
      register(registry, CARTOGRAPHER, PointOfInterestTypes.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
      register(registry, CLERIC, PointOfInterestTypes.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
      register(registry, FARMER, PointOfInterestTypes.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
      register(registry, FISHERMAN, PointOfInterestTypes.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
      register(registry, FLETCHER, PointOfInterestTypes.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
      register(registry, LEATHERWORKER, PointOfInterestTypes.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER);
      register(registry, LIBRARIAN, PointOfInterestTypes.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
      register(registry, MASON, PointOfInterestTypes.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
      register(registry, NITWIT, PointOfInterestType.NONE, PointOfInterestType.NONE, (SoundEvent)null);
      register(registry, SHEPHERD, PointOfInterestTypes.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
      register(registry, TOOLSMITH, PointOfInterestTypes.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
      return register(registry, WEAPONSMITH, PointOfInterestTypes.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);
   }

   public Text id() {
      return this.id;
   }

   public Predicate heldWorkstation() {
      return this.heldWorkstation;
   }

   public Predicate acquirableWorkstation() {
      return this.acquirableWorkstation;
   }

   public ImmutableSet gatherableItems() {
      return this.gatherableItems;
   }

   public ImmutableSet secondaryJobSites() {
      return this.secondaryJobSites;
   }

   @Nullable
   public SoundEvent workSound() {
      return this.workSound;
   }
}
