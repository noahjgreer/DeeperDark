package net.minecraft.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;

public abstract class AbstractCookingRecipe extends SingleStackRecipe {
   private final CookingRecipeCategory category;
   private final float experience;
   private final int cookingTime;

   public AbstractCookingRecipe(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
      super(group, ingredient, result);
      this.category = category;
      this.experience = experience;
      this.cookingTime = cookingTime;
   }

   public abstract RecipeSerializer getSerializer();

   public abstract RecipeType getType();

   public float getExperience() {
      return this.experience;
   }

   public int getCookingTime() {
      return this.cookingTime;
   }

   public CookingRecipeCategory getCategory() {
      return this.category;
   }

   protected abstract Item getCookerItem();

   public List getDisplays() {
      return List.of(new FurnaceRecipeDisplay(this.ingredient().toDisplay(), SlotDisplay.AnyFuelSlotDisplay.INSTANCE, new SlotDisplay.StackSlotDisplay(this.result()), new SlotDisplay.ItemSlotDisplay(this.getCookerItem()), this.cookingTime, this.experience));
   }

   @FunctionalInterface
   public interface RecipeFactory {
      AbstractCookingRecipe create(String group, CookingRecipeCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
   }

   public static class Serializer implements RecipeSerializer {
      private final MapCodec codec;
      private final PacketCodec packetCodec;

      public Serializer(RecipeFactory factory, int defaultCookingTime) {
         this.codec = RecordCodecBuilder.mapCodec((instance) -> {
            Products.P6 var10000 = instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter(SingleStackRecipe::getGroup), CookingRecipeCategory.CODEC.fieldOf("category").orElse(CookingRecipeCategory.MISC).forGetter(AbstractCookingRecipe::getCategory), Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleStackRecipe::ingredient), ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(SingleStackRecipe::result), Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::getExperience), Codec.INT.fieldOf("cookingtime").orElse(defaultCookingTime).forGetter(AbstractCookingRecipe::getCookingTime));
            Objects.requireNonNull(factory);
            return var10000.apply(instance, factory::create);
         });
         PacketCodec var10001 = PacketCodecs.STRING;
         Function var10002 = SingleStackRecipe::getGroup;
         PacketCodec var10003 = CookingRecipeCategory.PACKET_CODEC;
         Function var10004 = AbstractCookingRecipe::getCategory;
         PacketCodec var10005 = Ingredient.PACKET_CODEC;
         Function var10006 = SingleStackRecipe::ingredient;
         PacketCodec var10007 = ItemStack.PACKET_CODEC;
         Function var10008 = SingleStackRecipe::result;
         PacketCodec var10009 = PacketCodecs.FLOAT;
         Function var10010 = AbstractCookingRecipe::getExperience;
         PacketCodec var10011 = PacketCodecs.INTEGER;
         Function var10012 = AbstractCookingRecipe::getCookingTime;
         Objects.requireNonNull(factory);
         this.packetCodec = PacketCodec.tuple(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011, var10012, factory::create);
      }

      public MapCodec codec() {
         return this.codec;
      }

      public PacketCodec packetCodec() {
         return this.packetCodec;
      }
   }
}
