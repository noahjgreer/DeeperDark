package net.minecraft.data.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

public class SmithingTrimRecipeJsonBuilder {
   private final RecipeCategory category;
   private final Ingredient template;
   private final Ingredient base;
   private final Ingredient addition;
   private final RegistryEntry pattern;
   private final Map criteria = new LinkedHashMap();

   public SmithingTrimRecipeJsonBuilder(RecipeCategory category, Ingredient template, Ingredient base, Ingredient addition, RegistryEntry pattern) {
      this.category = category;
      this.template = template;
      this.base = base;
      this.addition = addition;
      this.pattern = pattern;
   }

   public static SmithingTrimRecipeJsonBuilder create(Ingredient template, Ingredient base, Ingredient addition, RegistryEntry pattern, RecipeCategory category) {
      return new SmithingTrimRecipeJsonBuilder(category, template, base, addition, pattern);
   }

   public SmithingTrimRecipeJsonBuilder criterion(String name, AdvancementCriterion criterion) {
      this.criteria.put(name, criterion);
      return this;
   }

   public void offerTo(RecipeExporter exporter, RegistryKey recipeKey) {
      this.validate(recipeKey);
      Advancement.Builder builder = exporter.getAdvancementBuilder().criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey)).rewards(AdvancementRewards.Builder.recipe(recipeKey)).criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
      Map var10000 = this.criteria;
      Objects.requireNonNull(builder);
      var10000.forEach(builder::criterion);
      SmithingTrimRecipe smithingTrimRecipe = new SmithingTrimRecipe(this.template, this.base, this.addition, this.pattern);
      exporter.accept(recipeKey, smithingTrimRecipe, builder.build(recipeKey.getValue().withPrefixedPath("recipes/" + this.category.getName() + "/")));
   }

   private void validate(RegistryKey recipeKey) {
      if (this.criteria.isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + String.valueOf(recipeKey.getValue()));
      }
   }
}
