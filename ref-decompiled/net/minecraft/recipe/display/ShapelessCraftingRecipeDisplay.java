package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record ShapelessCraftingRecipeDisplay(List ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ShapelessCraftingRecipeDisplay::ingredients), SlotDisplay.CODEC.fieldOf("result").forGetter(ShapelessCraftingRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ShapelessCraftingRecipeDisplay::craftingStation)).apply(instance, ShapelessCraftingRecipeDisplay::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final RecipeDisplay.Serializer SERIALIZER;

   public ShapelessCraftingRecipeDisplay(List list, SlotDisplay slotDisplay, SlotDisplay slotDisplay2) {
      this.ingredients = list;
      this.result = slotDisplay;
      this.craftingStation = slotDisplay2;
   }

   public RecipeDisplay.Serializer serializer() {
      return SERIALIZER;
   }

   public boolean isEnabled(FeatureSet features) {
      return this.ingredients.stream().allMatch((ingredient) -> {
         return ingredient.isEnabled(features);
      }) && RecipeDisplay.super.isEnabled(features);
   }

   public List ingredients() {
      return this.ingredients;
   }

   public SlotDisplay result() {
      return this.result;
   }

   public SlotDisplay craftingStation() {
      return this.craftingStation;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()), ShapelessCraftingRecipeDisplay::ingredients, SlotDisplay.PACKET_CODEC, ShapelessCraftingRecipeDisplay::result, SlotDisplay.PACKET_CODEC, ShapelessCraftingRecipeDisplay::craftingStation, ShapelessCraftingRecipeDisplay::new);
      SERIALIZER = new RecipeDisplay.Serializer(CODEC, PACKET_CODEC);
   }
}
