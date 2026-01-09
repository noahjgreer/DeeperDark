package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class UsedEnderEyeCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return UsedEnderEyeCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, BlockPos strongholdPos) {
      double d = player.getX() - (double)strongholdPos.getX();
      double e = player.getZ() - (double)strongholdPos.getZ();
      double f = d * d + e * e;
      this.trigger(player, (conditions) -> {
         return conditions.matches(f);
      });
   }

   public static record Conditions(Optional player, NumberRange.DoubleRange distance) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), NumberRange.DoubleRange.CODEC.optionalFieldOf("distance", NumberRange.DoubleRange.ANY).forGetter(Conditions::distance)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, NumberRange.DoubleRange distance) {
         this.player = playerPredicate;
         this.distance = distance;
      }

      public boolean matches(double distance) {
         return this.distance.testSqrt(distance);
      }

      public Optional player() {
         return this.player;
      }

      public NumberRange.DoubleRange distance() {
         return this.distance;
      }
   }
}
