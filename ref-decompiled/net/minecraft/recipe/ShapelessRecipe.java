package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipe implements CraftingRecipe {
   final String group;
   final CraftingRecipeCategory category;
   final ItemStack result;
   final List ingredients;
   @Nullable
   private IngredientPlacement ingredientPlacement;

   public ShapelessRecipe(String group, CraftingRecipeCategory category, ItemStack result, List ingredients) {
      this.group = group;
      this.category = category;
      this.result = result;
      this.ingredients = ingredients;
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SHAPELESS;
   }

   public String getGroup() {
      return this.group;
   }

   public CraftingRecipeCategory getCategory() {
      return this.category;
   }

   public IngredientPlacement getIngredientPlacement() {
      if (this.ingredientPlacement == null) {
         this.ingredientPlacement = IngredientPlacement.forShapeless(this.ingredients);
      }

      return this.ingredientPlacement;
   }

   public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
      if (craftingRecipeInput.getStackCount() != this.ingredients.size()) {
         return false;
      } else {
         return craftingRecipeInput.size() == 1 && this.ingredients.size() == 1 ? ((Ingredient)this.ingredients.getFirst()).test(craftingRecipeInput.getStackInSlot(0)) : craftingRecipeInput.getRecipeMatcher().isCraftable((Recipe)this, (RecipeMatcher.ItemCallback)null);
      }
   }

   public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
      return this.result.copy();
   }

   public List getDisplays() {
      return List.of(new ShapelessCraftingRecipeDisplay(this.ingredients.stream().map(Ingredient::toDisplay).toList(), new SlotDisplay.StackSlotDisplay(this.result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter((recipe) -> {
            return recipe.group;
         }), CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter((recipe) -> {
            return recipe.category;
         }), ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter((recipe) -> {
            return recipe.result;
         }), Ingredient.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter((recipe) -> {
            return recipe.ingredients;
         })).apply(instance, ShapelessRecipe::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public MapCodec codec() {
         return CODEC;
      }

      public PacketCodec packetCodec() {
         return PACKET_CODEC;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, (recipe) -> {
            return recipe.group;
         }, CraftingRecipeCategory.PACKET_CODEC, (recipe) -> {
            return recipe.category;
         }, ItemStack.PACKET_CODEC, (recipe) -> {
            return recipe.result;
         }, Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()), (recipe) -> {
            return recipe.ingredients;
         }, ShapelessRecipe::new);
      }
   }
}
