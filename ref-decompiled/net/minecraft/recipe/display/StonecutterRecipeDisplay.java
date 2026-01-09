package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;

public record StonecutterRecipeDisplay(SlotDisplay input, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SlotDisplay.CODEC.fieldOf("input").forGetter(StonecutterRecipeDisplay::input), SlotDisplay.CODEC.fieldOf("result").forGetter(StonecutterRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(StonecutterRecipeDisplay::craftingStation)).apply(instance, StonecutterRecipeDisplay::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final RecipeDisplay.Serializer SERIALIZER;

   public StonecutterRecipeDisplay(SlotDisplay slotDisplay, SlotDisplay slotDisplay2, SlotDisplay slotDisplay3) {
      this.input = slotDisplay;
      this.result = slotDisplay2;
      this.craftingStation = slotDisplay3;
   }

   public RecipeDisplay.Serializer serializer() {
      return SERIALIZER;
   }

   public SlotDisplay input() {
      return this.input;
   }

   public SlotDisplay result() {
      return this.result;
   }

   public SlotDisplay craftingStation() {
      return this.craftingStation;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC, StonecutterRecipeDisplay::input, SlotDisplay.PACKET_CODEC, StonecutterRecipeDisplay::result, SlotDisplay.PACKET_CODEC, StonecutterRecipeDisplay::craftingStation, StonecutterRecipeDisplay::new);
      SERIALIZER = new RecipeDisplay.Serializer(CODEC, PACKET_CODEC);
   }
}
