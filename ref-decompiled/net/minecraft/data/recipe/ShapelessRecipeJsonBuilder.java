package net.minecraft.data.recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipeJsonBuilder implements CraftingRecipeJsonBuilder {
   private final RegistryEntryLookup registryLookup;
   private final RecipeCategory category;
   private final ItemStack output;
   private final List inputs = new ArrayList();
   private final Map advancementBuilder = new LinkedHashMap();
   @Nullable
   private String group;

   private ShapelessRecipeJsonBuilder(RegistryEntryLookup registryLookup, RecipeCategory category, ItemStack output) {
      this.registryLookup = registryLookup;
      this.category = category;
      this.output = output;
   }

   public static ShapelessRecipeJsonBuilder create(RegistryEntryLookup registryLookup, RecipeCategory category, ItemStack output) {
      return new ShapelessRecipeJsonBuilder(registryLookup, category, output);
   }

   public static ShapelessRecipeJsonBuilder create(RegistryEntryLookup registryLookup, RecipeCategory category, ItemConvertible output) {
      return create(registryLookup, category, output, 1);
   }

   public static ShapelessRecipeJsonBuilder create(RegistryEntryLookup registryLookup, RecipeCategory category, ItemConvertible output, int count) {
      return new ShapelessRecipeJsonBuilder(registryLookup, category, output.asItem().getDefaultStack().copyWithCount(count));
   }

   public ShapelessRecipeJsonBuilder input(TagKey tag) {
      return this.input(Ingredient.ofTag(this.registryLookup.getOrThrow(tag)));
   }

   public ShapelessRecipeJsonBuilder input(ItemConvertible item) {
      return this.input((ItemConvertible)item, 1);
   }

   public ShapelessRecipeJsonBuilder input(ItemConvertible item, int amount) {
      for(int i = 0; i < amount; ++i) {
         this.input(Ingredient.ofItem(item));
      }

      return this;
   }

   public ShapelessRecipeJsonBuilder input(Ingredient ingredient) {
      return this.input((Ingredient)ingredient, 1);
   }

   public ShapelessRecipeJsonBuilder input(Ingredient ingredient, int amount) {
      for(int i = 0; i < amount; ++i) {
         this.inputs.add(ingredient);
      }

      return this;
   }

   public ShapelessRecipeJsonBuilder criterion(String string, AdvancementCriterion advancementCriterion) {
      this.advancementBuilder.put(string, advancementCriterion);
      return this;
   }

   public ShapelessRecipeJsonBuilder group(@Nullable String string) {
      this.group = string;
      return this;
   }

   public Item getOutputItem() {
      return this.output.getItem();
   }

   public void offerTo(RecipeExporter exporter, RegistryKey recipeKey) {
      this.validate(recipeKey);
      Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey)).rewards(AdvancementRewards.Builder.recipe(recipeKey)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
      Map var10000 = this.advancementBuilder;
      Objects.requireNonNull(builder);
      var10000.forEach(builder::criterion);
      ShapelessRecipe shapelessRecipe = new ShapelessRecipe((String)Objects.requireNonNullElse(this.group, ""), CraftingRecipeJsonBuilder.toCraftingCategory(this.category), this.output, this.inputs);
      exporter.accept(recipeKey, shapelessRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
   }

   private void validate(RegistryKey recipeKey) {
      if (this.advancementBuilder.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(recipeKey.getValue()));
      }
   }

   // $FF: synthetic method
   public CraftingRecipeJsonBuilder group(@Nullable final String group) {
      return this.group(group);
   }

   // $FF: synthetic method
   public CraftingRecipeJsonBuilder criterion(final String name, final AdvancementCriterion criterion) {
      return this.criterion(name, criterion);
   }
}
