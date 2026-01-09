package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class FallAfterExplosionCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return FallAfterExplosionCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Vec3d startPosition, @Nullable Entity cause) {
      Vec3d vec3d = player.getPos();
      LootContext lootContext = cause != null ? EntityPredicate.createAdvancementEntityLootContext(player, cause) : null;
      this.trigger(player, (conditions) -> {
         return conditions.matches(player.getWorld(), startPosition, vec3d, lootContext);
      });
   }

   public static record Conditions(Optional player, Optional startPosition, Optional distance, Optional cause) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), LocationPredicate.CODEC.optionalFieldOf("start_position").forGetter(Conditions::startPosition), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(Conditions::distance), EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("cause").forGetter(Conditions::cause)).apply(instance, Conditions::new);
      });

      public Conditions(Optional optional, Optional optional2, Optional optional3, Optional optional4) {
         this.player = optional;
         this.startPosition = optional2;
         this.distance = optional3;
         this.cause = optional4;
      }

      public static AdvancementCriterion create(DistancePredicate distance, EntityPredicate.Builder cause) {
         return Criteria.FALL_AFTER_EXPLOSION.create(new Conditions(Optional.empty(), Optional.empty(), Optional.of(distance), Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(cause))));
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         validator.validateEntityPredicate(this.cause(), "cause");
      }

      public boolean matches(ServerWorld world, Vec3d startPosition, Vec3d endPosition, @Nullable LootContext cause) {
         if (this.startPosition.isPresent() && !((LocationPredicate)this.startPosition.get()).test(world, startPosition.x, startPosition.y, startPosition.z)) {
            return false;
         } else if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).test(startPosition.x, startPosition.y, startPosition.z, endPosition.x, endPosition.y, endPosition.z)) {
            return false;
         } else {
            return !this.cause.isPresent() || cause != null && ((LootContextPredicate)this.cause.get()).test(cause);
         }
      }

      public Optional player() {
         return this.player;
      }

      public Optional startPosition() {
         return this.startPosition;
      }

      public Optional distance() {
         return this.distance;
      }

      public Optional cause() {
         return this.cause;
      }
   }
}
