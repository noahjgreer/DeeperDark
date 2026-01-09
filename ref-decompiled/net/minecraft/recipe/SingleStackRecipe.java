package net.minecraft.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class SingleStackRecipe implements Recipe {
   private final Ingredient ingredient;
   private final ItemStack result;
   private final String group;
   @Nullable
   private IngredientPlacement ingredientPlacement;

   public SingleStackRecipe(String group, Ingredient ingredient, ItemStack result) {
      this.group = group;
      this.ingredient = ingredient;
      this.result = result;
   }

   public abstract RecipeSerializer getSerializer();

   public abstract RecipeType getType();

   public boolean matches(SingleStackRecipeInput singleStackRecipeInput, World world) {
      return this.ingredient.test(singleStackRecipeInput.item());
   }

   public String getGroup() {
      return this.group;
   }

   public Ingredient ingredient() {
      return this.ingredient;
   }

   protected ItemStack result() {
      return this.result;
   }

   public IngredientPlacement getIngredientPlacement() {
      if (this.ingredientPlacement == null) {
         this.ingredientPlacement = IngredientPlacement.forSingleSlot(this.ingredient);
      }

      return this.ingredientPlacement;
   }

   public ItemStack craft(SingleStackRecipeInput singleStackRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
      return this.result.copy();
   }

   @FunctionalInterface
   public interface RecipeFactory {
      SingleStackRecipe create(String group, Ingredient ingredient, ItemStack result);
   }

   public static class Serializer implements RecipeSerializer {
      private final MapCodec codec;
      private final PacketCodec packetCodec;

      protected Serializer(RecipeFactory recipeFactory) {
         this.codec = RecordCodecBuilder.mapCodec((instance) -> {
            Products.P3 var10000 = instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(SingleStackRecipe::getGroup), Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleStackRecipe::ingredient), ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter(SingleStackRecipe::result));
            Objects.requireNonNull(recipeFactory);
            return var10000.apply(instance, recipeFactory::create);
         });
         PacketCodec var10001 = PacketCodecs.STRING;
         Function var10002 = SingleStackRecipe::getGroup;
         PacketCodec var10003 = Ingredient.PACKET_CODEC;
         Function var10004 = SingleStackRecipe::ingredient;
         PacketCodec var10005 = ItemStack.PACKET_CODEC;
         Function var10006 = SingleStackRecipe::result;
         Objects.requireNonNull(recipeFactory);
         this.packetCodec = PacketCodec.tuple(var10001, var10002, var10003, var10004, var10005, var10006, recipeFactory::create);
      }

      public MapCodec codec() {
         return this.codec;
      }

      public PacketCodec packetCodec() {
         return this.packetCodec;
      }
   }
}
