package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class SlideDownBlockCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return SlideDownBlockCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, BlockState state) {
      this.trigger(player, (conditions) -> {
         return conditions.test(state);
      });
   }

   public static record Conditions(Optional player, Optional block, Optional state) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(Conditions::block), StatePredicate.CODEC.optionalFieldOf("state").forGetter(Conditions::state)).apply(instance, Conditions::new);
      }).validate(Conditions::validate);

      public Conditions(Optional playerPredicate, Optional optional, Optional state) {
         this.player = playerPredicate;
         this.block = optional;
         this.state = state;
      }

      private static DataResult validate(Conditions conditions) {
         return (DataResult)conditions.block.flatMap((block) -> {
            return conditions.state.flatMap((state) -> {
               return state.findMissing(((Block)block.value()).getStateManager());
            }).map((property) -> {
               return DataResult.error(() -> {
                  String var10000 = String.valueOf(block);
                  return "Block" + var10000 + " has no property " + property;
               });
            });
         }).orElseGet(() -> {
            return DataResult.success(conditions);
         });
      }

      public static AdvancementCriterion create(Block block) {
         return Criteria.SLIDE_DOWN_BLOCK.create(new Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.empty()));
      }

      public boolean test(BlockState state) {
         if (this.block.isPresent() && !state.isOf((RegistryEntry)this.block.get())) {
            return false;
         } else {
            return !this.state.isPresent() || ((StatePredicate)this.state.get()).test(state);
         }
      }

      public Optional player() {
         return this.player;
      }

      public Optional block() {
         return this.block;
      }

      public Optional state() {
         return this.state;
      }
   }
}
