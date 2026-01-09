package net.minecraft.recipe.display;

import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Ingredient;

public record CuttingRecipeDisplay(SlotDisplay optionDisplay, Optional recipe) {
   public CuttingRecipeDisplay(SlotDisplay slotDisplay, Optional optional) {
      this.optionDisplay = slotDisplay;
      this.recipe = optional;
   }

   public static PacketCodec codec() {
      return PacketCodec.tuple(SlotDisplay.PACKET_CODEC, CuttingRecipeDisplay::optionDisplay, (display) -> {
         return new CuttingRecipeDisplay(display, Optional.empty());
      });
   }

   public SlotDisplay optionDisplay() {
      return this.optionDisplay;
   }

   public Optional recipe() {
      return this.recipe;
   }

   public static record Grouping(List entries) {
      public Grouping(List list) {
         this.entries = list;
      }

      public static Grouping empty() {
         return new Grouping(List.of());
      }

      public static PacketCodec codec() {
         return PacketCodec.tuple(CuttingRecipeDisplay.GroupEntry.codec().collect(PacketCodecs.toList()), Grouping::entries, Grouping::new);
      }

      public boolean contains(ItemStack stack) {
         return this.entries.stream().anyMatch((entry) -> {
            return entry.input.test(stack);
         });
      }

      public Grouping filter(ItemStack stack) {
         return new Grouping(this.entries.stream().filter((entry) -> {
            return entry.input.test(stack);
         }).toList());
      }

      public boolean isEmpty() {
         return this.entries.isEmpty();
      }

      public int size() {
         return this.entries.size();
      }

      public List entries() {
         return this.entries;
      }
   }

   public static record GroupEntry(Ingredient input, CuttingRecipeDisplay recipe) {
      final Ingredient input;

      public GroupEntry(Ingredient ingredient, CuttingRecipeDisplay cuttingRecipeDisplay) {
         this.input = ingredient;
         this.recipe = cuttingRecipeDisplay;
      }

      public static PacketCodec codec() {
         return PacketCodec.tuple(Ingredient.PACKET_CODEC, GroupEntry::input, CuttingRecipeDisplay.codec(), GroupEntry::recipe, GroupEntry::new);
      }

      public Ingredient input() {
         return this.input;
      }

      public CuttingRecipeDisplay recipe() {
         return this.recipe;
      }
   }
}
