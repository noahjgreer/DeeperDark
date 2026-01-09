package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface RecipeSerializer {
   RecipeSerializer SHAPED = register("crafting_shaped", new ShapedRecipe.Serializer());
   RecipeSerializer SHAPELESS = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   RecipeSerializer ARMOR_DYE = register("crafting_special_armordye", new SpecialCraftingRecipe.SpecialRecipeSerializer(ArmorDyeRecipe::new));
   RecipeSerializer BOOK_CLONING = register("crafting_special_bookcloning", new SpecialCraftingRecipe.SpecialRecipeSerializer(BookCloningRecipe::new));
   RecipeSerializer MAP_CLONING = register("crafting_special_mapcloning", new SpecialCraftingRecipe.SpecialRecipeSerializer(MapCloningRecipe::new));
   RecipeSerializer MAP_EXTENDING = register("crafting_special_mapextending", new SpecialCraftingRecipe.SpecialRecipeSerializer(MapExtendingRecipe::new));
   RecipeSerializer FIREWORK_ROCKET = register("crafting_special_firework_rocket", new SpecialCraftingRecipe.SpecialRecipeSerializer(FireworkRocketRecipe::new));
   RecipeSerializer FIREWORK_STAR = register("crafting_special_firework_star", new SpecialCraftingRecipe.SpecialRecipeSerializer(FireworkStarRecipe::new));
   RecipeSerializer FIREWORK_STAR_FADE = register("crafting_special_firework_star_fade", new SpecialCraftingRecipe.SpecialRecipeSerializer(FireworkStarFadeRecipe::new));
   RecipeSerializer TIPPED_ARROW = register("crafting_special_tippedarrow", new SpecialCraftingRecipe.SpecialRecipeSerializer(TippedArrowRecipe::new));
   RecipeSerializer BANNER_DUPLICATE = register("crafting_special_bannerduplicate", new SpecialCraftingRecipe.SpecialRecipeSerializer(BannerDuplicateRecipe::new));
   RecipeSerializer SHIELD_DECORATION = register("crafting_special_shielddecoration", new SpecialCraftingRecipe.SpecialRecipeSerializer(ShieldDecorationRecipe::new));
   RecipeSerializer CRAFTING_TRANSMUTE = register("crafting_transmute", new TransmuteRecipe.Serializer());
   RecipeSerializer REPAIR_ITEM = register("crafting_special_repairitem", new SpecialCraftingRecipe.SpecialRecipeSerializer(RepairItemRecipe::new));
   RecipeSerializer SMELTING = register("smelting", new AbstractCookingRecipe.Serializer(SmeltingRecipe::new, 200));
   RecipeSerializer BLASTING = register("blasting", new AbstractCookingRecipe.Serializer(BlastingRecipe::new, 100));
   RecipeSerializer SMOKING = register("smoking", new AbstractCookingRecipe.Serializer(SmokingRecipe::new, 100));
   RecipeSerializer CAMPFIRE_COOKING = register("campfire_cooking", new AbstractCookingRecipe.Serializer(CampfireCookingRecipe::new, 100));
   RecipeSerializer STONECUTTING = register("stonecutting", new SingleStackRecipe.Serializer(StonecuttingRecipe::new));
   RecipeSerializer SMITHING_TRANSFORM = register("smithing_transform", new SmithingTransformRecipe.Serializer());
   RecipeSerializer SMITHING_TRIM = register("smithing_trim", new SmithingTrimRecipe.Serializer());
   RecipeSerializer CRAFTING_DECORATED_POT = register("crafting_decorated_pot", new SpecialCraftingRecipe.SpecialRecipeSerializer(CraftingDecoratedPotRecipe::new));

   MapCodec codec();

   /** @deprecated */
   @Deprecated
   PacketCodec packetCodec();

   static RecipeSerializer register(String id, RecipeSerializer serializer) {
      return (RecipeSerializer)Registry.register(Registries.RECIPE_SERIALIZER, (String)id, serializer);
   }
}
