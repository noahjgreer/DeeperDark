package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record ShapedCraftingRecipeDisplay(int width, int height, List ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.INT.fieldOf("width").forGetter(ShapedCraftingRecipeDisplay::width), Codec.INT.fieldOf("height").forGetter(ShapedCraftingRecipeDisplay::height), SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ShapedCraftingRecipeDisplay::ingredients), SlotDisplay.CODEC.fieldOf("result").forGetter(ShapedCraftingRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ShapedCraftingRecipeDisplay::craftingStation)).apply(instance, ShapedCraftingRecipeDisplay::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final RecipeDisplay.Serializer SERIALIZER;

   public ShapedCraftingRecipeDisplay(int i, int j, List list, SlotDisplay slotDisplay, SlotDisplay slotDisplay2) {
      if (list.size() != i * j) {
         throw new IllegalArgumentException("Invalid shaped recipe display contents");
      } else {
         this.width = i;
         this.height = j;
         this.ingredients = list;
         this.result = slotDisplay;
         this.craftingStation = slotDisplay2;
      }
   }

   public RecipeDisplay.Serializer serializer() {
      return SERIALIZER;
   }

   public boolean isEnabled(FeatureSet features) {
      return this.ingredients.stream().allMatch((ingredient) -> {
         return ingredient.isEnabled(features);
      }) && RecipeDisplay.super.isEnabled(features);
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
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
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, ShapedCraftingRecipeDisplay::width, PacketCodecs.VAR_INT, ShapedCraftingRecipeDisplay::height, SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()), ShapedCraftingRecipeDisplay::ingredients, SlotDisplay.PACKET_CODEC, ShapedCraftingRecipeDisplay::result, SlotDisplay.PACKET_CODEC, ShapedCraftingRecipeDisplay::craftingStation, ShapedCraftingRecipeDisplay::new);
      SERIALIZER = new RecipeDisplay.Serializer(CODEC, PACKET_CODEC);
   }
}
