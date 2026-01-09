package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.recipe.v1.FabricServerRecipeManager;
import net.minecraft.item.Item;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ServerRecipeManager extends SinglePreparationResourceReloader implements RecipeManager, FabricServerRecipeManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map SOLE_INGREDIENT_GETTERS;
   private static final ResourceFinder FINDER;
   private final RegistryWrapper.WrapperLookup registries;
   private PreparedRecipes preparedRecipes;
   private Map propertySets;
   private CuttingRecipeDisplay.Grouping stonecutterRecipes;
   private List recipes;
   private Map recipesByKey;

   public ServerRecipeManager(RegistryWrapper.WrapperLookup registries) {
      this.preparedRecipes = PreparedRecipes.EMPTY;
      this.propertySets = Map.of();
      this.stonecutterRecipes = CuttingRecipeDisplay.Grouping.empty();
      this.recipes = List.of();
      this.recipesByKey = Map.of();
      this.registries = registries;
   }

   protected PreparedRecipes prepare(ResourceManager resourceManager, Profiler profiler) {
      SortedMap sortedMap = new TreeMap();
      JsonDataLoader.load(resourceManager, (ResourceFinder)FINDER, this.registries.getOps(JsonOps.INSTANCE), Recipe.CODEC, sortedMap);
      List list = new ArrayList(sortedMap.size());
      sortedMap.forEach((id, recipe) -> {
         RegistryKey registryKey = RegistryKey.of(RegistryKeys.RECIPE, id);
         RecipeEntry recipeEntry = new RecipeEntry(registryKey, recipe);
         list.add(recipeEntry);
      });
      return PreparedRecipes.of(list);
   }

   protected void apply(PreparedRecipes preparedRecipes, ResourceManager resourceManager, Profiler profiler) {
      this.preparedRecipes = preparedRecipes;
      LOGGER.info("Loaded {} recipes", preparedRecipes.recipes().size());
   }

   public void initialize(FeatureSet features) {
      List list = new ArrayList();
      List list2 = SOLE_INGREDIENT_GETTERS.entrySet().stream().map((entry) -> {
         return new PropertySetBuilder((RegistryKey)entry.getKey(), (SoleIngredientGetter)entry.getValue());
      }).toList();
      this.preparedRecipes.recipes().forEach((recipe) -> {
         Recipe recipe2 = recipe.value();
         if (!recipe2.isIgnoredInRecipeBook() && recipe2.getIngredientPlacement().hasNoPlacement()) {
            LOGGER.warn("Recipe {} can't be placed due to empty ingredients and will be ignored", recipe.id().getValue());
         } else {
            list2.forEach((builder) -> {
               builder.accept(recipe2);
            });
            if (recipe2 instanceof StonecuttingRecipe) {
               StonecuttingRecipe stonecuttingRecipe = (StonecuttingRecipe)recipe2;
               if (isEnabled(features, stonecuttingRecipe.ingredient()) && stonecuttingRecipe.createResultDisplay().isEnabled(features)) {
                  list.add(new CuttingRecipeDisplay.GroupEntry(stonecuttingRecipe.ingredient(), new CuttingRecipeDisplay(stonecuttingRecipe.createResultDisplay(), Optional.of(recipe))));
               }
            }

         }
      });
      this.propertySets = (Map)list2.stream().collect(Collectors.toUnmodifiableMap((builder) -> {
         return builder.propertySetKey;
      }, (builder) -> {
         return builder.build(features);
      }));
      this.stonecutterRecipes = new CuttingRecipeDisplay.Grouping(list);
      this.recipes = collectServerRecipes(this.preparedRecipes.recipes(), features);
      this.recipesByKey = (Map)this.recipes.stream().collect(Collectors.groupingBy((recipe) -> {
         return recipe.parent.id();
      }, IdentityHashMap::new, Collectors.toList()));
   }

   static List filterIngredients(FeatureSet features, List ingredients) {
      ingredients.removeIf((ingredient) -> {
         return !isEnabled(features, ingredient);
      });
      return ingredients;
   }

   private static boolean isEnabled(FeatureSet features, Ingredient ingredient) {
      return ingredient.getMatchingItems().allMatch((entry) -> {
         return ((Item)entry.value()).isEnabled(features);
      });
   }

   public Optional getFirstMatch(RecipeType type, RecipeInput input, World world, @Nullable RegistryKey recipe) {
      RecipeEntry recipeEntry = recipe != null ? this.get(type, recipe) : null;
      return this.getFirstMatch(type, input, world, recipeEntry);
   }

   public Optional getFirstMatch(RecipeType type, RecipeInput input, World world, @Nullable RecipeEntry recipe) {
      return recipe != null && recipe.value().matches(input, world) ? Optional.of(recipe) : this.getFirstMatch(type, input, world);
   }

   public Optional getFirstMatch(RecipeType type, RecipeInput input, World world) {
      return this.preparedRecipes.find(type, input, world).findFirst();
   }

   public Optional get(RegistryKey key) {
      return Optional.ofNullable(this.preparedRecipes.get(key));
   }

   @Nullable
   private RecipeEntry get(RecipeType type, RegistryKey key) {
      RecipeEntry recipeEntry = this.preparedRecipes.get(key);
      return recipeEntry != null && recipeEntry.value().getType().equals(type) ? recipeEntry : null;
   }

   public Map getPropertySets() {
      return this.propertySets;
   }

   public CuttingRecipeDisplay.Grouping getStonecutterRecipeForSync() {
      return this.stonecutterRecipes;
   }

   public RecipePropertySet getPropertySet(RegistryKey key) {
      return (RecipePropertySet)this.propertySets.getOrDefault(key, RecipePropertySet.EMPTY);
   }

   public CuttingRecipeDisplay.Grouping getStonecutterRecipes() {
      return this.stonecutterRecipes;
   }

   public Collection values() {
      return this.preparedRecipes.recipes();
   }

   @Nullable
   public ServerRecipe get(NetworkRecipeId id) {
      int i = id.index();
      return i >= 0 && i < this.recipes.size() ? (ServerRecipe)this.recipes.get(i) : null;
   }

   public void forEachRecipeDisplay(RegistryKey key, Consumer action) {
      List list = (List)this.recipesByKey.get(key);
      if (list != null) {
         list.forEach((recipe) -> {
            action.accept(recipe.display);
         });
      }

   }

   @VisibleForTesting
   protected static RecipeEntry deserialize(RegistryKey key, JsonObject json, RegistryWrapper.WrapperLookup registries) {
      Recipe recipe = (Recipe)Recipe.CODEC.parse(registries.getOps(JsonOps.INSTANCE), json).getOrThrow(JsonParseException::new);
      return new RecipeEntry(key, recipe);
   }

   public static MatchGetter createCachedMatchGetter(final RecipeType type) {
      return new MatchGetter() {
         @Nullable
         private RegistryKey id;

         public Optional getFirstMatch(RecipeInput input, ServerWorld world) {
            ServerRecipeManager serverRecipeManager = world.getRecipeManager();
            Optional optional = serverRecipeManager.getFirstMatch(type, input, world, (RegistryKey)this.id);
            if (optional.isPresent()) {
               RecipeEntry recipeEntry = (RecipeEntry)optional.get();
               this.id = recipeEntry.id();
               return Optional.of(recipeEntry);
            } else {
               return Optional.empty();
            }
         }
      };
   }

   private static List collectServerRecipes(Iterable recipes, FeatureSet enabledFeatures) {
      List list = new ArrayList();
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      Iterator var4 = recipes.iterator();

      while(var4.hasNext()) {
         RecipeEntry recipeEntry = (RecipeEntry)var4.next();
         Recipe recipe = recipeEntry.value();
         OptionalInt optionalInt;
         if (recipe.getGroup().isEmpty()) {
            optionalInt = OptionalInt.empty();
         } else {
            optionalInt = OptionalInt.of(object2IntMap.computeIfAbsent(recipe.getGroup(), (group) -> {
               return object2IntMap.size();
            }));
         }

         Optional optional;
         if (recipe.isIgnoredInRecipeBook()) {
            optional = Optional.empty();
         } else {
            optional = Optional.of(recipe.getIngredientPlacement().getIngredients());
         }

         Iterator var9 = recipe.getDisplays().iterator();

         while(var9.hasNext()) {
            RecipeDisplay recipeDisplay = (RecipeDisplay)var9.next();
            if (recipeDisplay.isEnabled(enabledFeatures)) {
               int i = list.size();
               NetworkRecipeId networkRecipeId = new NetworkRecipeId(i);
               RecipeDisplayEntry recipeDisplayEntry = new RecipeDisplayEntry(networkRecipeId, recipeDisplay, optionalInt, recipe.getRecipeBookCategory(), optional);
               list.add(new ServerRecipe(recipeDisplayEntry, recipeEntry));
            }
         }
      }

      return list;
   }

   private static SoleIngredientGetter cookingIngredientGetter(RecipeType expectedType) {
      return (recipe) -> {
         Optional var10000;
         if (recipe.getType() == expectedType && recipe instanceof SingleStackRecipe singleStackRecipe) {
            var10000 = Optional.of(singleStackRecipe.ingredient());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      };
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager manager, final Profiler profiler) {
      return this.prepare(manager, profiler);
   }

   static {
      SOLE_INGREDIENT_GETTERS = Map.of(RecipePropertySet.SMITHING_ADDITION, (recipe) -> {
         Optional var10000;
         if (recipe instanceof SmithingRecipe smithingRecipe) {
            var10000 = smithingRecipe.addition();
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.SMITHING_BASE, (recipe) -> {
         Optional var10000;
         if (recipe instanceof SmithingRecipe smithingRecipe) {
            var10000 = Optional.of(smithingRecipe.base());
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.SMITHING_TEMPLATE, (recipe) -> {
         Optional var10000;
         if (recipe instanceof SmithingRecipe smithingRecipe) {
            var10000 = smithingRecipe.template();
         } else {
            var10000 = Optional.empty();
         }

         return var10000;
      }, RecipePropertySet.FURNACE_INPUT, cookingIngredientGetter(RecipeType.SMELTING), RecipePropertySet.BLAST_FURNACE_INPUT, cookingIngredientGetter(RecipeType.BLASTING), RecipePropertySet.SMOKER_INPUT, cookingIngredientGetter(RecipeType.SMOKING), RecipePropertySet.CAMPFIRE_INPUT, cookingIngredientGetter(RecipeType.CAMPFIRE_COOKING));
      FINDER = ResourceFinder.json(RegistryKeys.RECIPE);
   }

   public static record ServerRecipe(RecipeDisplayEntry display, RecipeEntry parent) {
      final RecipeDisplayEntry display;
      final RecipeEntry parent;

      public ServerRecipe(RecipeDisplayEntry recipeDisplayEntry, RecipeEntry recipeEntry) {
         this.display = recipeDisplayEntry;
         this.parent = recipeEntry;
      }

      public RecipeDisplayEntry display() {
         return this.display;
      }

      public RecipeEntry parent() {
         return this.parent;
      }
   }

   @FunctionalInterface
   public interface SoleIngredientGetter {
      Optional apply(Recipe recipe);
   }

   public static class PropertySetBuilder implements Consumer {
      final RegistryKey propertySetKey;
      private final SoleIngredientGetter ingredientGetter;
      private final List ingredients = new ArrayList();

      protected PropertySetBuilder(RegistryKey propertySetKey, SoleIngredientGetter ingredientGetter) {
         this.propertySetKey = propertySetKey;
         this.ingredientGetter = ingredientGetter;
      }

      public void accept(Recipe recipe) {
         Optional var10000 = this.ingredientGetter.apply(recipe);
         List var10001 = this.ingredients;
         Objects.requireNonNull(var10001);
         var10000.ifPresent(var10001::add);
      }

      public RecipePropertySet build(FeatureSet enabledFeatures) {
         return RecipePropertySet.of(ServerRecipeManager.filterIngredients(enabledFeatures, this.ingredients));
      }

      // $FF: synthetic method
      public void accept(final Object recipe) {
         this.accept((Recipe)recipe);
      }
   }

   public interface MatchGetter {
      Optional getFirstMatch(RecipeInput input, ServerWorld world);
   }
}
