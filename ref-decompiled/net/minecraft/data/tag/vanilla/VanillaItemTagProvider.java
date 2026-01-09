package net.minecraft.data.tag.vanilla;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.data.tag.ValueLookupTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

public class VanillaItemTagProvider extends ValueLookupTagProvider {
   public VanillaItemTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.ITEM, registriesFuture, (value) -> {
         return value.getRegistryEntry().registryKey();
      });
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      (new VanillaBlockItemTags() {
         protected ProvidedTagBuilder builder(TagKey blockTag, TagKey itemTag) {
            return new ItemTagBuilder(VanillaItemTagProvider.this.builder(itemTag));
         }
      }).configure();
      this.builder(ItemTags.BANNERS).add((Object[])(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER));
      this.builder(ItemTags.BOATS).add((Object[])(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT, Items.PALE_OAK_BOAT, Items.MANGROVE_BOAT, Items.BAMBOO_RAFT, Items.CHERRY_BOAT)).addTag(ItemTags.CHEST_BOATS);
      this.builder(ItemTags.BUNDLES).add((Object[])(Items.BUNDLE, Items.BLACK_BUNDLE, Items.BLUE_BUNDLE, Items.BROWN_BUNDLE, Items.CYAN_BUNDLE, Items.GRAY_BUNDLE, Items.GREEN_BUNDLE, Items.LIGHT_BLUE_BUNDLE, Items.LIGHT_GRAY_BUNDLE, Items.LIME_BUNDLE, Items.MAGENTA_BUNDLE, Items.ORANGE_BUNDLE, Items.PINK_BUNDLE, Items.PURPLE_BUNDLE, Items.RED_BUNDLE, Items.YELLOW_BUNDLE, Items.WHITE_BUNDLE));
      this.builder(ItemTags.CHEST_BOATS).add((Object[])(Items.OAK_CHEST_BOAT, Items.SPRUCE_CHEST_BOAT, Items.BIRCH_CHEST_BOAT, Items.JUNGLE_CHEST_BOAT, Items.ACACIA_CHEST_BOAT, Items.DARK_OAK_CHEST_BOAT, Items.PALE_OAK_CHEST_BOAT, Items.MANGROVE_CHEST_BOAT, Items.BAMBOO_CHEST_RAFT, Items.CHERRY_CHEST_BOAT));
      this.builder(ItemTags.EGGS).add((Object[])(Items.EGG, Items.BLUE_EGG, Items.BROWN_EGG));
      this.builder(ItemTags.FISHES).add((Object[])(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH));
      this.builder(ItemTags.CREEPER_DROP_MUSIC_DISCS).add((Object[])(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT));
      this.builder(ItemTags.COALS).add((Object[])(Items.COAL, Items.CHARCOAL));
      this.builder(ItemTags.ARROWS).add((Object[])(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW));
      this.builder(ItemTags.LECTERN_BOOKS).add((Object[])(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK));
      this.builder(ItemTags.BEACON_PAYMENT_ITEMS).add((Object[])(Items.NETHERITE_INGOT, Items.EMERALD, Items.DIAMOND, Items.GOLD_INGOT, Items.IRON_INGOT));
      this.builder(ItemTags.PIGLIN_REPELLENTS).add((Object)Items.SOUL_TORCH).add((Object)Items.SOUL_LANTERN).add((Object)Items.SOUL_CAMPFIRE);
      this.builder(ItemTags.PIGLIN_LOVED).addTag(ItemTags.GOLD_ORES).add((Object[])(Items.GOLD_BLOCK, Items.GILDED_BLACKSTONE, Items.LIGHT_WEIGHTED_PRESSURE_PLATE, Items.GOLD_INGOT, Items.BELL, Items.CLOCK, Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_HORSE_ARMOR, Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.RAW_GOLD, Items.RAW_GOLD_BLOCK));
      this.builder(ItemTags.IGNORED_BY_PIGLIN_BABIES).add((Object)Items.LEATHER);
      this.builder(ItemTags.PIGLIN_FOOD).add((Object[])(Items.PORKCHOP, Items.COOKED_PORKCHOP));
      this.builder(ItemTags.PIGLIN_SAFE_ARMOR).add((Object[])(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS));
      this.builder(ItemTags.FOX_FOOD).add((Object[])(Items.SWEET_BERRIES, Items.GLOW_BERRIES));
      this.builder(ItemTags.DUPLICATES_ALLAYS).add((Object)Items.AMETHYST_SHARD);
      this.builder(ItemTags.BREWING_FUEL).add((Object)Items.BLAZE_POWDER);
      this.builder(ItemTags.NON_FLAMMABLE_WOOD).add((Object[])(Items.WARPED_STEM, Items.STRIPPED_WARPED_STEM, Items.WARPED_HYPHAE, Items.STRIPPED_WARPED_HYPHAE, Items.CRIMSON_STEM, Items.STRIPPED_CRIMSON_STEM, Items.CRIMSON_HYPHAE, Items.STRIPPED_CRIMSON_HYPHAE, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS, Items.CRIMSON_SLAB, Items.WARPED_SLAB, Items.CRIMSON_PRESSURE_PLATE, Items.WARPED_PRESSURE_PLATE, Items.CRIMSON_FENCE, Items.WARPED_FENCE, Items.CRIMSON_TRAPDOOR, Items.WARPED_TRAPDOOR, Items.CRIMSON_FENCE_GATE, Items.WARPED_FENCE_GATE, Items.CRIMSON_STAIRS, Items.WARPED_STAIRS, Items.CRIMSON_BUTTON, Items.WARPED_BUTTON, Items.CRIMSON_DOOR, Items.WARPED_DOOR, Items.CRIMSON_SIGN, Items.WARPED_SIGN, Items.WARPED_HANGING_SIGN, Items.CRIMSON_HANGING_SIGN));
      this.builder(ItemTags.WOODEN_TOOL_MATERIALS).addTag(ItemTags.PLANKS);
      this.builder(ItemTags.STONE_TOOL_MATERIALS).add((Object[])(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE));
      this.builder(ItemTags.IRON_TOOL_MATERIALS).add((Object)Items.IRON_INGOT);
      this.builder(ItemTags.GOLD_TOOL_MATERIALS).add((Object)Items.GOLD_INGOT);
      this.builder(ItemTags.DIAMOND_TOOL_MATERIALS).add((Object)Items.DIAMOND);
      this.builder(ItemTags.NETHERITE_TOOL_MATERIALS).add((Object)Items.NETHERITE_INGOT);
      this.builder(ItemTags.REPAIRS_LEATHER_ARMOR).add((Object)Items.LEATHER);
      this.builder(ItemTags.REPAIRS_CHAIN_ARMOR).add((Object)Items.IRON_INGOT);
      this.builder(ItemTags.REPAIRS_IRON_ARMOR).add((Object)Items.IRON_INGOT);
      this.builder(ItemTags.REPAIRS_GOLD_ARMOR).add((Object)Items.GOLD_INGOT);
      this.builder(ItemTags.REPAIRS_DIAMOND_ARMOR).add((Object)Items.DIAMOND);
      this.builder(ItemTags.REPAIRS_NETHERITE_ARMOR).add((Object)Items.NETHERITE_INGOT);
      this.builder(ItemTags.REPAIRS_TURTLE_HELMET).add((Object)Items.TURTLE_SCUTE);
      this.builder(ItemTags.REPAIRS_WOLF_ARMOR).add((Object)Items.ARMADILLO_SCUTE);
      this.builder(ItemTags.STONE_CRAFTING_MATERIALS).add((Object[])(Items.COBBLESTONE, Items.BLACKSTONE, Items.COBBLED_DEEPSLATE));
      this.builder(ItemTags.FREEZE_IMMUNE_WEARABLES).add((Object[])(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_HORSE_ARMOR));
      this.builder(ItemTags.AXOLOTL_FOOD).add((Object)Items.TROPICAL_FISH_BUCKET);
      this.builder(ItemTags.CLUSTER_MAX_HARVESTABLES).add((Object[])(Items.DIAMOND_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.NETHERITE_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE));
      this.builder(ItemTags.COMPASSES).add((Object)Items.COMPASS).add((Object)Items.RECOVERY_COMPASS);
      this.builder(ItemTags.CREEPER_IGNITERS).add((Object)Items.FLINT_AND_STEEL).add((Object)Items.FIRE_CHARGE);
      this.builder(ItemTags.SWORDS).add((Object)Items.DIAMOND_SWORD).add((Object)Items.STONE_SWORD).add((Object)Items.GOLDEN_SWORD).add((Object)Items.NETHERITE_SWORD).add((Object)Items.WOODEN_SWORD).add((Object)Items.IRON_SWORD);
      this.builder(ItemTags.AXES).add((Object)Items.DIAMOND_AXE).add((Object)Items.STONE_AXE).add((Object)Items.GOLDEN_AXE).add((Object)Items.NETHERITE_AXE).add((Object)Items.WOODEN_AXE).add((Object)Items.IRON_AXE);
      this.builder(ItemTags.PICKAXES).add((Object)Items.DIAMOND_PICKAXE).add((Object)Items.STONE_PICKAXE).add((Object)Items.GOLDEN_PICKAXE).add((Object)Items.NETHERITE_PICKAXE).add((Object)Items.WOODEN_PICKAXE).add((Object)Items.IRON_PICKAXE);
      this.builder(ItemTags.SHOVELS).add((Object)Items.DIAMOND_SHOVEL).add((Object)Items.STONE_SHOVEL).add((Object)Items.GOLDEN_SHOVEL).add((Object)Items.NETHERITE_SHOVEL).add((Object)Items.WOODEN_SHOVEL).add((Object)Items.IRON_SHOVEL);
      this.builder(ItemTags.HOES).add((Object)Items.DIAMOND_HOE).add((Object)Items.STONE_HOE).add((Object)Items.GOLDEN_HOE).add((Object)Items.NETHERITE_HOE).add((Object)Items.WOODEN_HOE).add((Object)Items.IRON_HOE);
      this.builder(ItemTags.BREAKS_DECORATED_POTS).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add((Object)Items.TRIDENT).add((Object)Items.MACE);
      this.builder(ItemTags.SKELETON_PREFERRED_WEAPONS).add((Object)Items.BOW);
      this.builder(ItemTags.DROWNED_PREFERRED_WEAPONS).add((Object)Items.TRIDENT);
      this.builder(ItemTags.PIGLIN_PREFERRED_WEAPONS).add((Object)Items.CROSSBOW);
      this.builder(ItemTags.PILLAGER_PREFERRED_WEAPONS).add((Object)Items.CROSSBOW);
      this.builder(ItemTags.WITHER_SKELETON_DISLIKED_WEAPONS).add((Object)Items.BOW).add((Object)Items.CROSSBOW);
      this.builder(ItemTags.DECORATED_POT_SHERDS).add((Object[])(Items.ANGLER_POTTERY_SHERD, Items.ARCHER_POTTERY_SHERD, Items.ARMS_UP_POTTERY_SHERD, Items.BLADE_POTTERY_SHERD, Items.BREWER_POTTERY_SHERD, Items.BURN_POTTERY_SHERD, Items.DANGER_POTTERY_SHERD, Items.EXPLORER_POTTERY_SHERD, Items.FRIEND_POTTERY_SHERD, Items.HEART_POTTERY_SHERD, Items.HEARTBREAK_POTTERY_SHERD, Items.HOWL_POTTERY_SHERD, Items.MINER_POTTERY_SHERD, Items.MOURNER_POTTERY_SHERD, Items.PLENTY_POTTERY_SHERD, Items.PRIZE_POTTERY_SHERD, Items.SHEAF_POTTERY_SHERD, Items.SHELTER_POTTERY_SHERD, Items.SKULL_POTTERY_SHERD, Items.SNORT_POTTERY_SHERD, Items.FLOW_POTTERY_SHERD, Items.GUSTER_POTTERY_SHERD, Items.SCRAPE_POTTERY_SHERD));
      this.builder(ItemTags.DECORATED_POT_INGREDIENTS).add((Object)Items.BRICK).addTag(ItemTags.DECORATED_POT_SHERDS);
      this.builder(ItemTags.FOOT_ARMOR).add((Object[])(Items.LEATHER_BOOTS, Items.CHAINMAIL_BOOTS, Items.GOLDEN_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS, Items.NETHERITE_BOOTS));
      this.builder(ItemTags.LEG_ARMOR).add((Object[])(Items.LEATHER_LEGGINGS, Items.CHAINMAIL_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.IRON_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS));
      this.builder(ItemTags.CHEST_ARMOR).add((Object[])(Items.LEATHER_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.IRON_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE));
      this.builder(ItemTags.HEAD_ARMOR).add((Object[])(Items.LEATHER_HELMET, Items.CHAINMAIL_HELMET, Items.GOLDEN_HELMET, Items.IRON_HELMET, Items.DIAMOND_HELMET, Items.NETHERITE_HELMET, Items.TURTLE_HELMET));
      this.builder(ItemTags.SKULLS).add((Object[])(Items.PLAYER_HEAD, Items.CREEPER_HEAD, Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.DRAGON_HEAD, Items.PIGLIN_HEAD));
      this.builder(ItemTags.TRIMMABLE_ARMOR).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR);
      this.builder(ItemTags.TRIM_MATERIALS).add(registries.getOrThrow(RegistryKeys.ITEM).streamEntries().filter((entry) -> {
         return ((Item)entry.value()).getComponents().contains(DataComponentTypes.PROVIDES_TRIM_MATERIAL);
      }).sorted(Comparator.comparing((entry) -> {
         return entry.registryKey().getValue();
      })).map(RegistryEntry.Reference::value));
      this.builder(ItemTags.BOOKSHELF_BOOKS).add((Object[])(Items.BOOK, Items.WRITTEN_BOOK, Items.ENCHANTED_BOOK, Items.WRITABLE_BOOK, Items.KNOWLEDGE_BOOK));
      this.builder(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS).add((Object[])(Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD));
      this.builder(ItemTags.SNIFFER_FOOD).add((Object)Items.TORCHFLOWER_SEEDS);
      this.builder(ItemTags.VILLAGER_PLANTABLE_SEEDS).add((Object[])(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD));
      this.builder(ItemTags.VILLAGER_PICKS_UP).addTag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add((Object[])(Items.BREAD, Items.WHEAT, Items.BEETROOT));
      this.builder(ItemTags.BOOK_CLONING_TARGET).add((Object)Items.WRITABLE_BOOK);
      this.builder(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR);
      this.builder(ItemTags.LEG_ARMOR_ENCHANTABLE).addTag(ItemTags.LEG_ARMOR);
      this.builder(ItemTags.CHEST_ARMOR_ENCHANTABLE).addTag(ItemTags.CHEST_ARMOR);
      this.builder(ItemTags.HEAD_ARMOR_ENCHANTABLE).addTag(ItemTags.HEAD_ARMOR);
      this.builder(ItemTags.ARMOR_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE).addTag(ItemTags.LEG_ARMOR_ENCHANTABLE).addTag(ItemTags.CHEST_ARMOR_ENCHANTABLE).addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
      this.builder(ItemTags.SWORD_ENCHANTABLE).addTag(ItemTags.SWORDS);
      this.builder(ItemTags.FIRE_ASPECT_ENCHANTABLE).addTag(ItemTags.SWORD_ENCHANTABLE).add((Object)Items.MACE);
      this.builder(ItemTags.SHARP_WEAPON_ENCHANTABLE).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES);
      this.builder(ItemTags.WEAPON_ENCHANTABLE).addTag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add((Object)Items.MACE);
      this.builder(ItemTags.MACE_ENCHANTABLE).add((Object)Items.MACE);
      this.builder(ItemTags.MINING_ENCHANTABLE).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add((Object)Items.SHEARS);
      this.builder(ItemTags.MINING_LOOT_ENCHANTABLE).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES);
      this.builder(ItemTags.FISHING_ENCHANTABLE).add((Object)Items.FISHING_ROD);
      this.builder(ItemTags.TRIDENT_ENCHANTABLE).add((Object)Items.TRIDENT);
      this.builder(ItemTags.DURABILITY_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).add((Object)Items.ELYTRA).add((Object)Items.SHIELD).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add((Object)Items.BOW).add((Object)Items.CROSSBOW).add((Object)Items.TRIDENT).add((Object)Items.FLINT_AND_STEEL).add((Object)Items.SHEARS).add((Object)Items.BRUSH).add((Object)Items.FISHING_ROD).add((Object[])(Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK)).add((Object)Items.MACE);
      this.builder(ItemTags.BOW_ENCHANTABLE).add((Object)Items.BOW);
      this.builder(ItemTags.EQUIPPABLE_ENCHANTABLE).addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).add((Object)Items.ELYTRA).addTag(ItemTags.SKULLS).add((Object)Items.CARVED_PUMPKIN);
      this.builder(ItemTags.CROSSBOW_ENCHANTABLE).add((Object)Items.CROSSBOW);
      this.builder(ItemTags.VANISHING_ENCHANTABLE).addTag(ItemTags.DURABILITY_ENCHANTABLE).add((Object)Items.COMPASS).add((Object)Items.CARVED_PUMPKIN).addTag(ItemTags.SKULLS);
      this.builder(ItemTags.DYEABLE).add((Object[])(Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.LEATHER_HORSE_ARMOR, Items.WOLF_ARMOR));
      this.builder(ItemTags.FURNACE_MINECART_FUEL).add((Object[])(Items.COAL, Items.CHARCOAL));
      this.builder(ItemTags.MEAT).add((Object[])(Items.BEEF, Items.CHICKEN, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.MUTTON, Items.PORKCHOP, Items.RABBIT, Items.ROTTEN_FLESH));
      this.builder(ItemTags.WOLF_FOOD).addTag(ItemTags.MEAT).add((Object[])(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.RABBIT_STEW));
      this.builder(ItemTags.OCELOT_FOOD).add((Object[])(Items.COD, Items.SALMON));
      this.builder(ItemTags.CAT_FOOD).add((Object[])(Items.COD, Items.SALMON));
      this.builder(ItemTags.HORSE_FOOD).add((Object[])(Items.WHEAT, Items.SUGAR, Items.HAY_BLOCK, Items.APPLE, Items.CARROT, Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE));
      this.builder(ItemTags.HORSE_TEMPT_ITEMS).add((Object[])(Items.GOLDEN_CARROT, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE));
      this.builder(ItemTags.HARNESSES).add((Object[])(Items.WHITE_HARNESS, Items.ORANGE_HARNESS, Items.MAGENTA_HARNESS, Items.LIGHT_BLUE_HARNESS, Items.YELLOW_HARNESS, Items.LIME_HARNESS, Items.PINK_HARNESS, Items.GRAY_HARNESS, Items.LIGHT_GRAY_HARNESS, Items.CYAN_HARNESS, Items.PURPLE_HARNESS, Items.BLUE_HARNESS, Items.BROWN_HARNESS, Items.GREEN_HARNESS, Items.RED_HARNESS, Items.BLACK_HARNESS));
      this.builder(ItemTags.HAPPY_GHAST_FOOD).add((Object)Items.SNOWBALL);
      this.builder(ItemTags.HAPPY_GHAST_TEMPT_ITEMS).addTag(ItemTags.HAPPY_GHAST_FOOD).addTag(ItemTags.HARNESSES);
      this.builder(ItemTags.CAMEL_FOOD).add((Object)Items.CACTUS);
      this.builder(ItemTags.ARMADILLO_FOOD).add((Object)Items.SPIDER_EYE);
      this.builder(ItemTags.CHICKEN_FOOD).add((Object[])(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD));
      this.builder(ItemTags.FROG_FOOD).add((Object)Items.SLIME_BALL);
      this.builder(ItemTags.HOGLIN_FOOD).add((Object)Items.CRIMSON_FUNGUS);
      this.builder(ItemTags.LLAMA_FOOD).add((Object[])(Items.WHEAT, Items.HAY_BLOCK));
      this.builder(ItemTags.LLAMA_TEMPT_ITEMS).add((Object)Items.HAY_BLOCK);
      this.builder(ItemTags.PANDA_FOOD).add((Object)Items.BAMBOO);
      this.builder(ItemTags.PANDA_EATS_FROM_GROUND).addTag(ItemTags.PANDA_FOOD).add((Object)Items.CAKE);
      this.builder(ItemTags.PIG_FOOD).add((Object[])(Items.CARROT, Items.POTATO, Items.BEETROOT));
      this.builder(ItemTags.RABBIT_FOOD).add((Object[])(Items.CARROT, Items.GOLDEN_CARROT, Items.DANDELION));
      this.builder(ItemTags.STRIDER_FOOD).add((Object)Items.WARPED_FUNGUS);
      this.builder(ItemTags.STRIDER_TEMPT_ITEMS).addTag(ItemTags.STRIDER_FOOD).add((Object)Items.WARPED_FUNGUS_ON_A_STICK);
      this.builder(ItemTags.TURTLE_FOOD).add((Object)Items.SEAGRASS);
      this.builder(ItemTags.PARROT_FOOD).add((Object[])(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD));
      this.builder(ItemTags.PARROT_POISONOUS_FOOD).add((Object)Items.COOKIE);
      this.builder(ItemTags.COW_FOOD).add((Object)Items.WHEAT);
      this.builder(ItemTags.SHEEP_FOOD).add((Object)Items.WHEAT);
      this.builder(ItemTags.GOAT_FOOD).add((Object)Items.WHEAT);
      this.builder(ItemTags.MAP_INVISIBILITY_EQUIPMENT).add((Object)Items.CARVED_PUMPKIN);
      this.builder(ItemTags.GAZE_DISGUISE_EQUIPMENT).add((Object)Items.CARVED_PUMPKIN);
   }

   static class ItemTagBuilder implements ProvidedTagBuilder {
      private final ProvidedTagBuilder parent;

      public ItemTagBuilder(ProvidedTagBuilder parent) {
         this.parent = parent;
      }

      public ProvidedTagBuilder add(Block block) {
         this.parent.add((Object)((Item)Objects.requireNonNull(block.asItem())));
         return this;
      }

      public ProvidedTagBuilder addOptional(Block block) {
         this.parent.addOptional((Item)Objects.requireNonNull(block.asItem()));
         return this;
      }

      private static TagKey getItemTag(TagKey tag) {
         return TagKey.of(RegistryKeys.ITEM, tag.id());
      }

      public ProvidedTagBuilder addTag(TagKey tag) {
         this.parent.addTag(getItemTag(tag));
         return this;
      }

      public ProvidedTagBuilder addOptionalTag(TagKey tag) {
         this.parent.addOptionalTag(getItemTag(tag));
         return this;
      }
   }
}
