package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;

public class RecipeUnlockedCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return RecipeUnlockedCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, RecipeEntry recipe) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(recipe);
      });
   }

   public static AdvancementCriterion create(RegistryKey registryKey) {
      return Criteria.RECIPE_UNLOCKED.create(new Conditions(Optional.empty(), registryKey));
   }

   public static record Conditions(Optional player, RegistryKey recipe) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), Recipe.KEY_CODEC.fieldOf("recipe").forGetter(Conditions::recipe)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, RegistryKey registryKey) {
         this.player = playerPredicate;
         this.recipe = registryKey;
      }

      public boolean matches(RecipeEntry recipe) {
         return this.recipe == recipe.id();
      }

      public Optional player() {
         return this.player;
      }

      public RegistryKey recipe() {
         return this.recipe;
      }
   }
}
