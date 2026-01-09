package net.minecraft.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SmithingRecipeDisplay;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

public class SmithingTrimRecipe implements SmithingRecipe {
   final Ingredient template;
   final Ingredient base;
   final Ingredient addition;
   final RegistryEntry pattern;
   @Nullable
   private IngredientPlacement ingredientPlacement;

   public SmithingTrimRecipe(Ingredient template, Ingredient base, Ingredient addition, RegistryEntry pattern) {
      this.template = template;
      this.base = base;
      this.addition = addition;
      this.pattern = pattern;
   }

   public ItemStack craft(SmithingRecipeInput smithingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
      return craft(wrapperLookup, smithingRecipeInput.base(), smithingRecipeInput.addition(), this.pattern);
   }

   public static ItemStack craft(RegistryWrapper.WrapperLookup registries, ItemStack base, ItemStack addition, RegistryEntry pattern) {
      Optional optional = ArmorTrimMaterials.get(registries, addition);
      if (optional.isPresent()) {
         ArmorTrim armorTrim = (ArmorTrim)base.get(DataComponentTypes.TRIM);
         ArmorTrim armorTrim2 = new ArmorTrim((RegistryEntry)optional.get(), pattern);
         if (Objects.equals(armorTrim, armorTrim2)) {
            return ItemStack.EMPTY;
         } else {
            ItemStack itemStack = base.copyWithCount(1);
            itemStack.set(DataComponentTypes.TRIM, armorTrim2);
            return itemStack;
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   public Optional template() {
      return Optional.of(this.template);
   }

   public Ingredient base() {
      return this.base;
   }

   public Optional addition() {
      return Optional.of(this.addition);
   }

   public RecipeSerializer getSerializer() {
      return RecipeSerializer.SMITHING_TRIM;
   }

   public IngredientPlacement getIngredientPlacement() {
      if (this.ingredientPlacement == null) {
         this.ingredientPlacement = IngredientPlacement.forShapeless(List.of(this.template, this.base, this.addition));
      }

      return this.ingredientPlacement;
   }

   public List getDisplays() {
      SlotDisplay slotDisplay = this.base.toDisplay();
      SlotDisplay slotDisplay2 = this.addition.toDisplay();
      SlotDisplay slotDisplay3 = this.template.toDisplay();
      return List.of(new SmithingRecipeDisplay(slotDisplay3, slotDisplay, slotDisplay2, new SlotDisplay.SmithingTrimSlotDisplay(slotDisplay, slotDisplay2, this.pattern), new SlotDisplay.ItemSlotDisplay(Items.SMITHING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer {
      private static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Ingredient.CODEC.fieldOf("template").forGetter((recipe) -> {
            return recipe.template;
         }), Ingredient.CODEC.fieldOf("base").forGetter((recipe) -> {
            return recipe.base;
         }), Ingredient.CODEC.fieldOf("addition").forGetter((recipe) -> {
            return recipe.addition;
         }), ArmorTrimPattern.ENTRY_CODEC.fieldOf("pattern").forGetter((recipe) -> {
            return recipe.pattern;
         })).apply(instance, SmithingTrimRecipe::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public MapCodec codec() {
         return CODEC;
      }

      public PacketCodec packetCodec() {
         return PACKET_CODEC;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(Ingredient.PACKET_CODEC, (recipe) -> {
            return recipe.template;
         }, Ingredient.PACKET_CODEC, (recipe) -> {
            return recipe.base;
         }, Ingredient.PACKET_CODEC, (recipe) -> {
            return recipe.addition;
         }, ArmorTrimPattern.ENTRY_PACKET_CODEC, (recipe) -> {
            return recipe.pattern;
         }, SmithingTrimRecipe::new);
      }
   }
}
