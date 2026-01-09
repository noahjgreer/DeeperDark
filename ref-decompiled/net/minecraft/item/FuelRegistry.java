package net.minecraft.item;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.SequencedSet;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;

public class FuelRegistry {
   private final Object2IntSortedMap fuelValues;

   FuelRegistry(Object2IntSortedMap fuelValues) {
      this.fuelValues = fuelValues;
   }

   public boolean isFuel(ItemStack item) {
      return this.fuelValues.containsKey(item.getItem());
   }

   public SequencedSet getFuelItems() {
      return Collections.unmodifiableSequencedSet(this.fuelValues.keySet());
   }

   public int getFuelTicks(ItemStack item) {
      return item.isEmpty() ? 0 : this.fuelValues.getInt(item.getItem());
   }

   public static FuelRegistry createDefault(RegistryWrapper.WrapperLookup registries, FeatureSet enabledFeatures) {
      return createDefault(registries, enabledFeatures, 200);
   }

   public static FuelRegistry createDefault(RegistryWrapper.WrapperLookup registries, FeatureSet enabledFeatures, int itemSmeltTime) {
      return (new Builder(registries, enabledFeatures)).add((ItemConvertible)Items.LAVA_BUCKET, itemSmeltTime * 100).add((ItemConvertible)Blocks.COAL_BLOCK, itemSmeltTime * 8 * 10).add((ItemConvertible)Items.BLAZE_ROD, itemSmeltTime * 12).add((ItemConvertible)Items.COAL, itemSmeltTime * 8).add((ItemConvertible)Items.CHARCOAL, itemSmeltTime * 8).add(ItemTags.LOGS, itemSmeltTime * 3 / 2).add(ItemTags.BAMBOO_BLOCKS, itemSmeltTime * 3 / 2).add(ItemTags.PLANKS, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.BAMBOO_MOSAIC, itemSmeltTime * 3 / 2).add(ItemTags.WOODEN_STAIRS, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.BAMBOO_MOSAIC_STAIRS, itemSmeltTime * 3 / 2).add(ItemTags.WOODEN_SLABS, itemSmeltTime * 3 / 4).add((ItemConvertible)Blocks.BAMBOO_MOSAIC_SLAB, itemSmeltTime * 3 / 4).add(ItemTags.WOODEN_TRAPDOORS, itemSmeltTime * 3 / 2).add(ItemTags.WOODEN_PRESSURE_PLATES, itemSmeltTime * 3 / 2).add(ItemTags.WOODEN_FENCES, itemSmeltTime * 3 / 2).add(ItemTags.FENCE_GATES, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.NOTE_BLOCK, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.BOOKSHELF, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.CHISELED_BOOKSHELF, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.LECTERN, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.JUKEBOX, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.CHEST, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.TRAPPED_CHEST, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.CRAFTING_TABLE, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.DAYLIGHT_DETECTOR, itemSmeltTime * 3 / 2).add(ItemTags.BANNERS, itemSmeltTime * 3 / 2).add((ItemConvertible)Items.BOW, itemSmeltTime * 3 / 2).add((ItemConvertible)Items.FISHING_ROD, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.LADDER, itemSmeltTime * 3 / 2).add(ItemTags.SIGNS, itemSmeltTime).add(ItemTags.HANGING_SIGNS, itemSmeltTime * 4).add((ItemConvertible)Items.WOODEN_SHOVEL, itemSmeltTime).add((ItemConvertible)Items.WOODEN_SWORD, itemSmeltTime).add((ItemConvertible)Items.WOODEN_HOE, itemSmeltTime).add((ItemConvertible)Items.WOODEN_AXE, itemSmeltTime).add((ItemConvertible)Items.WOODEN_PICKAXE, itemSmeltTime).add(ItemTags.WOODEN_DOORS, itemSmeltTime).add(ItemTags.BOATS, itemSmeltTime * 6).add(ItemTags.WOOL, itemSmeltTime / 2).add(ItemTags.WOODEN_BUTTONS, itemSmeltTime / 2).add((ItemConvertible)Items.STICK, itemSmeltTime / 2).add(ItemTags.SAPLINGS, itemSmeltTime / 2).add((ItemConvertible)Items.BOWL, itemSmeltTime / 2).add(ItemTags.WOOL_CARPETS, 1 + itemSmeltTime / 3).add((ItemConvertible)Blocks.DRIED_KELP_BLOCK, 1 + itemSmeltTime * 20).add((ItemConvertible)Items.CROSSBOW, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.BAMBOO, itemSmeltTime / 4).add((ItemConvertible)Blocks.DEAD_BUSH, itemSmeltTime / 2).add((ItemConvertible)Blocks.SHORT_DRY_GRASS, itemSmeltTime / 2).add((ItemConvertible)Blocks.TALL_DRY_GRASS, itemSmeltTime / 2).add((ItemConvertible)Blocks.SCAFFOLDING, itemSmeltTime / 4).add((ItemConvertible)Blocks.LOOM, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.BARREL, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.CARTOGRAPHY_TABLE, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.FLETCHING_TABLE, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.SMITHING_TABLE, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.COMPOSTER, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.AZALEA, itemSmeltTime / 2).add((ItemConvertible)Blocks.FLOWERING_AZALEA, itemSmeltTime / 2).add((ItemConvertible)Blocks.MANGROVE_ROOTS, itemSmeltTime * 3 / 2).add((ItemConvertible)Blocks.LEAF_LITTER, itemSmeltTime / 2).remove(ItemTags.NON_FLAMMABLE_WOOD).build();
   }

   public static class Builder {
      private final RegistryWrapper itemLookup;
      private final FeatureSet enabledFeatures;
      private final Object2IntSortedMap fuelValues = new Object2IntLinkedOpenHashMap();

      public Builder(RegistryWrapper.WrapperLookup registries, FeatureSet enabledFeatures) {
         this.itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
         this.enabledFeatures = enabledFeatures;
      }

      public FuelRegistry build() {
         return new FuelRegistry(this.fuelValues);
      }

      public Builder remove(TagKey tag) {
         this.fuelValues.keySet().removeIf((item) -> {
            return item.getRegistryEntry().isIn(tag);
         });
         return this;
      }

      public Builder add(TagKey tag, int value) {
         this.itemLookup.getOptional(tag).ifPresent((tagx) -> {
            Iterator var3 = tagx.iterator();

            while(var3.hasNext()) {
               RegistryEntry registryEntry = (RegistryEntry)var3.next();
               this.add(value, (Item)registryEntry.value());
            }

         });
         return this;
      }

      public Builder add(ItemConvertible item, int value) {
         Item item2 = item.asItem();
         this.add(value, item2);
         return this;
      }

      private void add(int value, Item item) {
         if (item.isEnabled(this.enabledFeatures)) {
            this.fuelValues.put(item, value);
         }

      }
   }
}
