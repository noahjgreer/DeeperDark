package net.minecraft.recipe.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;

public record SmithingRecipeDisplay(SlotDisplay template, SlotDisplay base, SlotDisplay addition, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SlotDisplay.CODEC.fieldOf("template").forGetter(SmithingRecipeDisplay::template), SlotDisplay.CODEC.fieldOf("base").forGetter(SmithingRecipeDisplay::base), SlotDisplay.CODEC.fieldOf("addition").forGetter(SmithingRecipeDisplay::addition), SlotDisplay.CODEC.fieldOf("result").forGetter(SmithingRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SmithingRecipeDisplay::craftingStation)).apply(instance, SmithingRecipeDisplay::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final RecipeDisplay.Serializer SERIALIZER;

   public SmithingRecipeDisplay(SlotDisplay slotDisplay, SlotDisplay slotDisplay2, SlotDisplay slotDisplay3, SlotDisplay slotDisplay4, SlotDisplay slotDisplay5) {
      this.template = slotDisplay;
      this.base = slotDisplay2;
      this.addition = slotDisplay3;
      this.result = slotDisplay4;
      this.craftingStation = slotDisplay5;
   }

   public RecipeDisplay.Serializer serializer() {
      return SERIALIZER;
   }

   public SlotDisplay template() {
      return this.template;
   }

   public SlotDisplay base() {
      return this.base;
   }

   public SlotDisplay addition() {
      return this.addition;
   }

   public SlotDisplay result() {
      return this.result;
   }

   public SlotDisplay craftingStation() {
      return this.craftingStation;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC, SmithingRecipeDisplay::template, SlotDisplay.PACKET_CODEC, SmithingRecipeDisplay::base, SlotDisplay.PACKET_CODEC, SmithingRecipeDisplay::addition, SlotDisplay.PACKET_CODEC, SmithingRecipeDisplay::result, SlotDisplay.PACKET_CODEC, SmithingRecipeDisplay::craftingStation, SmithingRecipeDisplay::new);
      SERIALIZER = new RecipeDisplay.Serializer(CODEC, PACKET_CODEC);
   }
}
