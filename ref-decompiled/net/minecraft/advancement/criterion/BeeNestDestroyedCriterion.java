package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

public class BeeNestDestroyedCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return BeeNestDestroyedCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, BlockState state, ItemStack stack, int beeCount) {
      this.trigger(player, (conditions) -> {
         return conditions.test(state, stack, beeCount);
      });
   }

   public static record Conditions(Optional player, Optional block, Optional item, NumberRange.IntRange beesInside) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), Registries.BLOCK.getEntryCodec().optionalFieldOf("block").forGetter(Conditions::block), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item), NumberRange.IntRange.CODEC.optionalFieldOf("num_bees_inside", NumberRange.IntRange.ANY).forGetter(Conditions::beesInside)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional optional, Optional item, NumberRange.IntRange beeCount) {
         this.player = playerPredicate;
         this.block = optional;
         this.item = item;
         this.beesInside = beeCount;
      }

      public static AdvancementCriterion create(Block block, ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange beeCountRange) {
         return Criteria.BEE_NEST_DESTROYED.create(new Conditions(Optional.empty(), Optional.of(block.getRegistryEntry()), Optional.of(itemPredicateBuilder.build()), beeCountRange));
      }

      public boolean test(BlockState state, ItemStack stack, int count) {
         if (this.block.isPresent() && !state.isOf((RegistryEntry)this.block.get())) {
            return false;
         } else {
            return this.item.isPresent() && !((ItemPredicate)this.item.get()).test(stack) ? false : this.beesInside.test(count);
         }
      }

      public Optional player() {
         return this.player;
      }

      public Optional block() {
         return this.block;
      }

      public Optional item() {
         return this.item;
      }

      public NumberRange.IntRange beesInside() {
         return this.beesInside;
      }
   }
}
