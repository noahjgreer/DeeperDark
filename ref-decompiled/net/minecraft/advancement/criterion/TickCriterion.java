package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.network.ServerPlayerEntity;

public class TickCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return TickCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player) {
      this.trigger(player, (conditions) -> {
         return true;
      });
   }

   public static record Conditions(Optional player) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player)).apply(instance, Conditions::new);
      });

      public Conditions(Optional optional) {
         this.player = optional;
      }

      public static AdvancementCriterion createLocation(LocationPredicate.Builder location) {
         return Criteria.LOCATION.create(new Conditions(Optional.of(EntityPredicate.contextPredicateFromEntityPredicate(EntityPredicate.Builder.create().location(location)))));
      }

      public static AdvancementCriterion createLocation(EntityPredicate.Builder entity) {
         return Criteria.LOCATION.create(new Conditions(Optional.of(EntityPredicate.asLootContextPredicate(entity.build()))));
      }

      public static AdvancementCriterion createLocation(Optional entity) {
         return Criteria.LOCATION.create(new Conditions(EntityPredicate.contextPredicateFromEntityPredicate(entity)));
      }

      public static AdvancementCriterion createSleptInBed() {
         return Criteria.SLEPT_IN_BED.create(new Conditions(Optional.empty()));
      }

      public static AdvancementCriterion createHeroOfTheVillage() {
         return Criteria.HERO_OF_THE_VILLAGE.create(new Conditions(Optional.empty()));
      }

      public static AdvancementCriterion createAvoidVibration() {
         return Criteria.AVOID_VIBRATION.create(new Conditions(Optional.empty()));
      }

      public static AdvancementCriterion createTick() {
         return Criteria.TICK.create(new Conditions(Optional.empty()));
      }

      public static AdvancementCriterion createLocation(RegistryEntryLookup blockRegistry, RegistryEntryLookup itemRegistry, Block steppingOn, Item boots) {
         return createLocation(EntityPredicate.Builder.create().equipment(EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create().items(itemRegistry, boots))).steppingOn(LocationPredicate.Builder.create().block(BlockPredicate.Builder.create().blocks(blockRegistry, steppingOn))));
      }

      public Optional player() {
         return this.player;
      }
   }
}
