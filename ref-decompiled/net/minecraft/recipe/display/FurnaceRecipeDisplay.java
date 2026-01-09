package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record FurnaceRecipeDisplay(SlotDisplay ingredient, SlotDisplay fuel, SlotDisplay result, SlotDisplay craftingStation, int duration, float experience) implements RecipeDisplay {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SlotDisplay.CODEC.fieldOf("ingredient").forGetter(FurnaceRecipeDisplay::ingredient), SlotDisplay.CODEC.fieldOf("fuel").forGetter(FurnaceRecipeDisplay::fuel), SlotDisplay.CODEC.fieldOf("result").forGetter(FurnaceRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FurnaceRecipeDisplay::craftingStation), Codec.INT.fieldOf("duration").forGetter(FurnaceRecipeDisplay::duration), Codec.FLOAT.fieldOf("experience").forGetter(FurnaceRecipeDisplay::experience)).apply(instance, FurnaceRecipeDisplay::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final RecipeDisplay.Serializer SERIALIZER;

   public FurnaceRecipeDisplay(SlotDisplay slotDisplay, SlotDisplay slotDisplay2, SlotDisplay slotDisplay3, SlotDisplay slotDisplay4, int i, float f) {
      this.ingredient = slotDisplay;
      this.fuel = slotDisplay2;
      this.result = slotDisplay3;
      this.craftingStation = slotDisplay4;
      this.duration = i;
      this.experience = f;
   }

   public RecipeDisplay.Serializer serializer() {
      return SERIALIZER;
   }

   public boolean isEnabled(FeatureSet features) {
      return this.ingredient.isEnabled(features) && this.fuel().isEnabled(features) && RecipeDisplay.super.isEnabled(features);
   }

   public SlotDisplay ingredient() {
      return this.ingredient;
   }

   public SlotDisplay fuel() {
      return this.fuel;
   }

   public SlotDisplay result() {
      return this.result;
   }

   public SlotDisplay craftingStation() {
      return this.craftingStation;
   }

   public int duration() {
      return this.duration;
   }

   public float experience() {
      return this.experience;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC, FurnaceRecipeDisplay::ingredient, SlotDisplay.PACKET_CODEC, FurnaceRecipeDisplay::fuel, SlotDisplay.PACKET_CODEC, FurnaceRecipeDisplay::result, SlotDisplay.PACKET_CODEC, FurnaceRecipeDisplay::craftingStation, PacketCodecs.VAR_INT, FurnaceRecipeDisplay::duration, PacketCodecs.FLOAT, FurnaceRecipeDisplay::experience, FurnaceRecipeDisplay::new);
      SERIALIZER = new RecipeDisplay.Serializer(CODEC, PACKET_CODEC);
   }
}
