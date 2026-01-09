package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TravelCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return TravelCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, Vec3d startPos) {
      Vec3d vec3d = player.getPos();
      this.trigger(player, (conditions) -> {
         return conditions.matches(player.getWorld(), startPos, vec3d);
      });
   }

   public static record Conditions(Optional player, Optional startPosition, Optional distance) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), LocationPredicate.CODEC.optionalFieldOf("start_position").forGetter(Conditions::startPosition), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(Conditions::distance)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional startPos, Optional distance) {
         this.player = playerPredicate;
         this.startPosition = startPos;
         this.distance = distance;
      }

      public static AdvancementCriterion fallFromHeight(EntityPredicate.Builder entity, DistancePredicate distance, LocationPredicate.Builder startPos) {
         return Criteria.FALL_FROM_HEIGHT.create(new Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity)), Optional.of(startPos.build()), Optional.of(distance)));
      }

      public static AdvancementCriterion rideEntityInLava(EntityPredicate.Builder entity, DistancePredicate distance) {
         return Criteria.RIDE_ENTITY_IN_LAVA.create(new Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(entity)), Optional.empty(), Optional.of(distance)));
      }

      public static AdvancementCriterion netherTravel(DistancePredicate distance) {
         return Criteria.NETHER_TRAVEL.create(new Conditions(Optional.empty(), Optional.empty(), Optional.of(distance)));
      }

      public boolean matches(ServerWorld world, Vec3d pos, Vec3d endPos) {
         if (this.startPosition.isPresent() && !((LocationPredicate)this.startPosition.get()).test(world, pos.x, pos.y, pos.z)) {
            return false;
         } else {
            return !this.distance.isPresent() || ((DistancePredicate)this.distance.get()).test(pos.x, pos.y, pos.z, endPos.x, endPos.y, endPos.z);
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
   }
}
