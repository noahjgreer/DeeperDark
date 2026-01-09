package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SmithingRecipeDisplay;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;

public class SmithingTransformRecipe implements SmithingRecipe {
   final Optional template;
   final Ingredient base;
   final Optional addition;
   final TransmuteRecipeResult result;
   @Nullable
   private IngredientPlacement ingredientPlacement;

   public SmithingTransformRecipe(Optional template, Ingredient base, Optional addition, TransmuteRecipeResult result) {
      this.template = template;
      this.base = base;
      this.addition = addition;
      this.result = result;
   }

   public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
      return this.result.apply(smithingRecipeInput.base());
   }

   public Optional template() {
      return this.template;
   }

   public Ingredient base() {
      return this.base;
   }

   public Optional addition() {
      return this.addition;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SMITHING_TRANSFORM;
   }

   public IngredientPlacement getIngredientPlacement() {
      if (this.ingredientPlacement == null) {
         this.ingredientPlacement = IngredientPlacement.forMultipleSlots(List.of(this.template, Optional.of(this.base), this.addition));
      }

      return this.ingredientPlacement;
   }

   public List getDisplays() {
      return List.of(new SmithingRecipeDisplay(Ingredient.toDisplay(this.template), this.base.toDisplay(), Ingredient.toDisplay(this.addition), this.result.createSlotDisplay(), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Ingredient.CODEC.optionalFieldOf("template").forGetter((recipe) -> {
            return recipe.template;
         }), Ingredient.CODEC.fieldOf("base").forGetter((recipe) -> {
            return recipe.base;
         }), Ingredient.CODEC.optionalFieldOf("addition").forGetter((recipe) -> {
            return recipe.addition;
         }), TransmuteRecipeResult.CODEC.fieldOf("result").forGetter((recipe) -> {
            return recipe.result;
         })).apply(instance, SmithingTransformRecipe::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public MapCodec codec() {
         return CODEC;
      }

      public PacketCodec packetCodec() {
         return PACKET_CODEC;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(Ingredient.OPTIONAL_PACKET_CODEC, (recipe) -> {
            return recipe.template;
         }, Ingredient.PACKET_CODEC, (recipe) -> {
            return recipe.base;
         }, Ingredient.OPTIONAL_PACKET_CODEC, (recipe) -> {
            return recipe.addition;
         }, TransmuteRecipeResult.PACKET_CODEC, (recipe) -> {
            return recipe.result;
         }, SmithingTransformRecipe::new);
      }
   }
}
