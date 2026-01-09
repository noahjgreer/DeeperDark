package net.minecraft.recipe.book;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum CookingRecipeCategory implements StringIdentifiable {
   FOOD(0, "food"),
   BLOCKS(1, "blocks"),
   MISC(2, "misc");

   private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction((category) -> {
      return category.id;
   }, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final Codec CODEC = StringIdentifiable.createCodec(CookingRecipeCategory::values);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(BY_ID, (category) -> {
      return category.id;
   });
   private final int id;
   private final String name;

   private CookingRecipeCategory(final int id, final String name) {
      this.id = id;
      this.name = name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static CookingRecipeCategory[] method_45439() {
      return new CookingRecipeCategory[]{FOOD, BLOCKS, MISC};
   }
}
