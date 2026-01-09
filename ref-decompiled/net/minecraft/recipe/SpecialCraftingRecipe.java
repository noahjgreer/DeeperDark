package net.minecraft.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.book.CraftingRecipeCategory;

public abstract class SpecialCraftingRecipe implements CraftingRecipe {
   private final CraftingRecipeCategory category;

   public SpecialCraftingRecipe(CraftingRecipeCategory category) {
      this.category = category;
   }

   public boolean isIgnoredInRecipeBook() {
      return true;
   }

   public CraftingRecipeCategory getCategory() {
      return this.category;
   }

   public IngredientPlacement getIngredientPlacement() {
      return IngredientPlacement.NONE;
   }

   public abstract RecipeSerializer getSerializer();

   public static class SpecialRecipeSerializer implements RecipeSerializer {
      private final MapCodec codec;
      private final PacketCodec packetCodec;

      public SpecialRecipeSerializer(Factory factory) {
         this.codec = RecordCodecBuilder.mapCodec((instance) -> {
            Products.P1 var10000 = instance.group(CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter(CraftingRecipe::getCategory));
            Objects.requireNonNull(factory);
            return var10000.apply(instance, factory::create);
         });
         PacketCodec var10001 = CraftingRecipeCategory.PACKET_CODEC;
         Function var10002 = CraftingRecipe::getCategory;
         Objects.requireNonNull(factory);
         this.packetCodec = PacketCodec.tuple(var10001, var10002, factory::create);
      }

      public MapCodec codec() {
         return this.codec;
      }

      public PacketCodec packetCodec() {
         return this.packetCodec;
      }

      @FunctionalInterface
      public interface Factory {
         CraftingRecipe create(CraftingRecipeCategory category);
      }
   }
}
