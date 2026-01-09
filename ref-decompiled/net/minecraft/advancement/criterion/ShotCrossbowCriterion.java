package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.network.ServerPlayerEntity;

public class ShotCrossbowCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return ShotCrossbowCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, ItemStack stack) {
      this.trigger(player, (conditions) -> {
         return conditions.matches(stack);
      });
   }

   public static record Conditions(Optional player, Optional item) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional item) {
         this.player = playerPredicate;
         this.item = item;
      }

      public static AdvancementCriterion create(Optional item) {
         return Criteria.SHOT_CROSSBOW.create(new Conditions(Optional.empty(), item));
      }

      public static AdvancementCriterion create(RegistryEntryLookup itemRegistry, ItemConvertible item) {
         return Criteria.SHOT_CROSSBOW.create(new Conditions(Optional.empty(), Optional.of(ItemPredicate.Builder.create().items(itemRegistry, item).build())));
      }

      public boolean matches(ItemStack stack) {
         return this.item.isEmpty() || ((ItemPredicate)this.item.get()).test(stack);
      }

      public Optional player() {
         return this.player;
      }

      public Optional item() {
         return this.item;
      }
   }
}
