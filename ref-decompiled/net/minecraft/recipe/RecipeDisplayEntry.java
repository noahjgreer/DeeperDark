package net.minecraft.recipe;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.context.ContextParameterMap;

public record RecipeDisplayEntry(NetworkRecipeId id, RecipeDisplay display, OptionalInt group, RecipeBookCategory category, Optional craftingRequirements) {
   public static final PacketCodec PACKET_CODEC;

   public RecipeDisplayEntry(NetworkRecipeId networkRecipeId, RecipeDisplay recipeDisplay, OptionalInt optionalInt, RecipeBookCategory recipeBookCategory, Optional optional) {
      this.id = networkRecipeId;
      this.display = recipeDisplay;
      this.group = optionalInt;
      this.category = recipeBookCategory;
      this.craftingRequirements = optional;
   }

   public List getStacks(ContextParameterMap context) {
      return this.display.result().getStacks(context);
   }

   public boolean isCraftable(RecipeFinder finder) {
      return this.craftingRequirements.isEmpty() ? false : finder.isCraftable((List)((List)this.craftingRequirements.get()), (RecipeMatcher.ItemCallback)null);
   }

   public NetworkRecipeId id() {
      return this.id;
   }

   public RecipeDisplay display() {
      return this.display;
   }

   public OptionalInt group() {
      return this.group;
   }

   public RecipeBookCategory category() {
      return this.category;
   }

   public Optional craftingRequirements() {
      return this.craftingRequirements;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(NetworkRecipeId.PACKET_CODEC, RecipeDisplayEntry::id, RecipeDisplay.PACKET_CODEC, RecipeDisplayEntry::display, PacketCodecs.OPTIONAL_INT, RecipeDisplayEntry::group, PacketCodecs.registryValue(RegistryKeys.RECIPE_BOOK_CATEGORY), RecipeDisplayEntry::category, Ingredient.PACKET_CODEC.collect(PacketCodecs.toList()).collect(PacketCodecs::optional), RecipeDisplayEntry::craftingRequirements, RecipeDisplayEntry::new);
   }
}
