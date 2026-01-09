package net.minecraft.recipe.book;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public enum CraftingRecipeCategory implements StringIdentifiable {
   BUILDING("building", 0),
   REDSTONE("redstone", 1),
   EQUIPMENT("equipment", 2),
   MISC("misc", 3);

   public static final Codec CODEC = StringIdentifiable.createCodec(CraftingRecipeCategory::values);
   public static final IntFunction INDEX_TO_VALUE = ValueLists.createIndexToValueFunction(CraftingRecipeCategory::getIndex, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   public static final PacketCodec PACKET_CODEC = PacketCodecs.indexed(INDEX_TO_VALUE, CraftingRecipeCategory::getIndex);
   private final String id;
   private final int index;

   private CraftingRecipeCategory(final String id, final int index) {
      this.id = id;
      this.index = index;
   }

   public String asString() {
      return this.id;
   }

   private int getIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static CraftingRecipeCategory[] method_45440() {
      return new CraftingRecipeCategory[]{BUILDING, REDSTONE, EQUIPMENT, MISC};
   }
}
