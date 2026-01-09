package net.minecraft.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;

public class ScreenHandlerType implements ToggleableFeature {
   public static final ScreenHandlerType GENERIC_9X1 = register("generic_9x1", GenericContainerScreenHandler::createGeneric9x1);
   public static final ScreenHandlerType GENERIC_9X2 = register("generic_9x2", GenericContainerScreenHandler::createGeneric9x2);
   public static final ScreenHandlerType GENERIC_9X3 = register("generic_9x3", GenericContainerScreenHandler::createGeneric9x3);
   public static final ScreenHandlerType GENERIC_9X4 = register("generic_9x4", GenericContainerScreenHandler::createGeneric9x4);
   public static final ScreenHandlerType GENERIC_9X5 = register("generic_9x5", GenericContainerScreenHandler::createGeneric9x5);
   public static final ScreenHandlerType GENERIC_9X6 = register("generic_9x6", GenericContainerScreenHandler::createGeneric9x6);
   public static final ScreenHandlerType GENERIC_3X3 = register("generic_3x3", Generic3x3ContainerScreenHandler::new);
   public static final ScreenHandlerType CRAFTER_3X3 = register("crafter_3x3", CrafterScreenHandler::new);
   public static final ScreenHandlerType ANVIL = register("anvil", AnvilScreenHandler::new);
   public static final ScreenHandlerType BEACON = register("beacon", BeaconScreenHandler::new);
   public static final ScreenHandlerType BLAST_FURNACE = register("blast_furnace", BlastFurnaceScreenHandler::new);
   public static final ScreenHandlerType BREWING_STAND = register("brewing_stand", BrewingStandScreenHandler::new);
   public static final ScreenHandlerType CRAFTING = register("crafting", CraftingScreenHandler::new);
   public static final ScreenHandlerType ENCHANTMENT = register("enchantment", EnchantmentScreenHandler::new);
   public static final ScreenHandlerType FURNACE = register("furnace", FurnaceScreenHandler::new);
   public static final ScreenHandlerType GRINDSTONE = register("grindstone", GrindstoneScreenHandler::new);
   public static final ScreenHandlerType HOPPER = register("hopper", HopperScreenHandler::new);
   public static final ScreenHandlerType LECTERN = register("lectern", (syncId, playerInventory) -> {
      return new LecternScreenHandler(syncId);
   });
   public static final ScreenHandlerType LOOM = register("loom", LoomScreenHandler::new);
   public static final ScreenHandlerType MERCHANT = register("merchant", MerchantScreenHandler::new);
   public static final ScreenHandlerType SHULKER_BOX = register("shulker_box", ShulkerBoxScreenHandler::new);
   public static final ScreenHandlerType SMITHING = register("smithing", SmithingScreenHandler::new);
   public static final ScreenHandlerType SMOKER = register("smoker", SmokerScreenHandler::new);
   public static final ScreenHandlerType CARTOGRAPHY_TABLE = register("cartography_table", CartographyTableScreenHandler::new);
   public static final ScreenHandlerType STONECUTTER = register("stonecutter", StonecutterScreenHandler::new);
   private final FeatureSet requiredFeatures;
   private final Factory factory;

   private static ScreenHandlerType register(String id, Factory factory) {
      return (ScreenHandlerType)Registry.register(Registries.SCREEN_HANDLER, (String)id, new ScreenHandlerType(factory, FeatureFlags.VANILLA_FEATURES));
   }

   private static ScreenHandlerType register(String id, Factory factory, FeatureFlag... requiredFeatures) {
      return (ScreenHandlerType)Registry.register(Registries.SCREEN_HANDLER, (String)id, new ScreenHandlerType(factory, FeatureFlags.FEATURE_MANAGER.featureSetOf(requiredFeatures)));
   }

   public ScreenHandlerType(Factory factory, FeatureSet requiredFeatures) {
      this.factory = factory;
      this.requiredFeatures = requiredFeatures;
   }

   public ScreenHandler create(int syncId, PlayerInventory playerInventory) {
      return this.factory.create(syncId, playerInventory);
   }

   public FeatureSet getRequiredFeatures() {
      return this.requiredFeatures;
   }

   public interface Factory {
      ScreenHandler create(int syncId, PlayerInventory playerInventory);
   }
}
