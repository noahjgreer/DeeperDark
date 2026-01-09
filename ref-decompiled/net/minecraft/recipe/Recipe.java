package net.minecraft.recipe;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public interface Recipe {
   Codec CODEC = Registries.RECIPE_SERIALIZER.getCodec().dispatch(Recipe::getSerializer, RecipeSerializer::codec);
   Codec KEY_CODEC = RegistryKey.createCodec(RegistryKeys.RECIPE);
   PacketCodec PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.RECIPE_SERIALIZER).dispatch(Recipe::getSerializer, RecipeSerializer::packetCodec);

   boolean matches(RecipeInput input, World world);

   ItemStack craft(RecipeInput input, RegistryWrapper.WrapperLookup registries);

   default boolean isIgnoredInRecipeBook() {
      return false;
   }

   default boolean showNotification() {
      return true;
   }

   default String getGroup() {
      return "";
   }

   RecipeSerializer getSerializer();

   RecipeType getType();

   IngredientPlacement getIngredientPlacement();

   default List getDisplays() {
      return List.of();
   }

   RecipeBookCategory getRecipeBookCategory();
}
