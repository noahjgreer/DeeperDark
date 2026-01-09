package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecipeCraftedCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return RecipeCraftedCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, RegistryKey recipeKey, List ingredients) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(recipeKey, ingredients);
      });
   }

   public static record Conditions(Optional player, RegistryKey recipeId, List ingredients) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), Recipe.KEY_CODEC.fieldOf("recipe_id").forGetter(Conditions::recipeId), ItemPredicate.CODEC.listOf().optionalFieldOf("ingredients", List.of()).forGetter(Conditions::ingredients)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, RegistryKey registryKey, List ingredients) {
         this.player = playerPredicate;
         this.recipeId = registryKey;
         this.ingredients = ingredients;
      }

      public static AdvancementCriterion create(RegistryKey recipeKey, List ingredients) {
         return Criteria.RECIPE_CRAFTED.create(new Conditions(Optional.empty(), recipeKey, ingredients.stream().map(ItemPredicate.Builder::build).toList()));
      }

      public static AdvancementCriterion create(RegistryKey recipeKey) {
         return Criteria.RECIPE_CRAFTED.create(new Conditions(Optional.empty(), recipeKey, List.of()));
      }

      public static AdvancementCriterion createCrafterRecipeCrafted(RegistryKey recipeKey) {
         return Criteria.CRAFTER_RECIPE_CRAFTED.create(new Conditions(Optional.empty(), recipeKey, List.of()));
      }

      boolean matches(RegistryKey recipeKey, List ingredients) {
         if (recipeKey != this.recipeId) {
            return false;
         } else {
            List list = new ArrayList(ingredients);
            Iterator var4 = this.ingredients.iterator();

            boolean bl;
            do {
               if (!var4.hasNext()) {
                  return true;
               }

               ItemPredicate itemPredicate = (ItemPredicate)var4.next();
               bl = false;
               Iterator iterator = list.iterator();

               while(iterator.hasNext()) {
                  if (itemPredicate.test((ItemStack)iterator.next())) {
                     iterator.remove();
                     bl = true;
                     break;
                  }
               }
            } while(bl);

            return false;
         }
      }

      public Optional player() {
         return this.player;
      }

      public RegistryKey recipeId() {
         return this.recipeId;
      }

      public List ingredients() {
         return this.ingredients;
      }
   }
}
