package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShapedRecipe implements CraftingRecipe {
   final RawShapedRecipe raw;
   final ItemStack result;
   final String group;
   final CraftingRecipeCategory category;
   final boolean showNotification;
   @Nullable
   private IngredientPlacement ingredientPlacement;

   public ShapedRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification) {
      this.group = group;
      this.category = category;
      this.raw = raw;
      this.result = result;
      this.showNotification = showNotification;
   }

   public ShapedRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result) {
      this(group, category, raw, result, true);
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SHAPED;
   }

   public String getGroup() {
      return this.group;
   }

   public CraftingRecipeCategory getCategory() {
      return this.category;
   }

   @VisibleForTesting
   public List getIngredients() {
      return this.raw.getIngredients();
   }

   public IngredientPlacement getIngredientPlacement() {
      if (this.ingredientPlacement == null) {
         this.ingredientPlacement = IngredientPlacement.forMultipleSlots(this.raw.getIngredients());
      }

      return this.ingredientPlacement;
   }

   public boolean showNotification() {
      return this.showNotification;
   }

   public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
      return this.raw.matches(craftingRecipeInput);
   }

   public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
      return this.result.copy();
   }

   public int getWidth() {
      return this.raw.getWidth();
   }

   public int getHeight() {
      return this.raw.getHeight();
   }

   public List getDisplays() {
      return List.of(new ShapedCraftingRecipeDisplay(this.raw.getWidth(), this.raw.getHeight(), this.raw.getIngredients().stream().map((ingredient) -> {
         return (SlotDisplay)ingredient.map(Ingredient::toDisplay).orElse(SlotDisplay.EmptySlotDisplay.INSTANCE);
      }).toList(), new SlotDisplay.StackSlotDisplay(this.result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.STRING.optionalFieldOf("group", "").forGetter((recipe) -> {
            return recipe.group;
         }), CraftingRecipeCategory.CODEC.fieldOf("category").orElse(CraftingRecipeCategory.MISC).forGetter((recipe) -> {
            return recipe.category;
         }), RawShapedRecipe.CODEC.forGetter((recipe) -> {
            return recipe.raw;
         }), ItemStack.VALIDATED_CODEC.fieldOf("result").forGetter((recipe) -> {
            return recipe.result;
         }), Codec.BOOL.optionalFieldOf("show_notification", true).forGetter((recipe) -> {
            return recipe.showNotification;
         })).apply(instance, ShapedRecipe::new);
      });
      public static final PacketCodec PACKET_CODEC = PacketCodec.ofStatic(Serializer::write, Serializer::read);

      public MapCodec codec() {
         return CODEC;
      }

      public PacketCodec packetCodec() {
         return PACKET_CODEC;
      }

      private static ShapedRecipe read(RegistryByteBuf buf) {
         String string = buf.readString();
         CraftingRecipeCategory craftingRecipeCategory = (CraftingRecipeCategory)buf.readEnumConstant(CraftingRecipeCategory.class);
         RawShapedRecipe rawShapedRecipe = (RawShapedRecipe)RawShapedRecipe.PACKET_CODEC.decode(buf);
         ItemStack itemStack = (ItemStack)ItemStack.PACKET_CODEC.decode(buf);
         boolean bl = buf.readBoolean();
         return new ShapedRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
      }

      private static void write(RegistryByteBuf buf, ShapedRecipe recipe) {
         buf.writeString(recipe.group);
         buf.writeEnumConstant(recipe.category);
         RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.raw);
         ItemStack.PACKET_CODEC.encode(buf, recipe.result);
         buf.writeBoolean(recipe.showNotification);
      }
   }
}
