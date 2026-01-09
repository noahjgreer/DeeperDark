package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.entity.LootContextPredicateValidator;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;

public class ItemCriterion extends AbstractCriterion {
   public Codec getConditionsCodec() {
      return ItemCriterion.Conditions.CODEC;
   }

   public void trigger(ServerPlayerEntity player, BlockPos pos, ItemStack stack) {
      ServerWorld serverWorld = player.getWorld();
      BlockState blockState = serverWorld.getBlockState(pos);
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(serverWorld)).add(LootContextParameters.ORIGIN, pos.toCenterPos()).add(LootContextParameters.THIS_ENTITY, player).add(LootContextParameters.BLOCK_STATE, blockState).add(LootContextParameters.TOOL, stack).build(LootContextTypes.ADVANCEMENT_LOCATION);
      LootContext lootContext = (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
      this.trigger(player, (conditions) -> {
         return conditions.test(lootContext);
      });
   }

   public static record Conditions(Optional player, Optional location) implements AbstractCriterion.Conditions {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player), LootContextPredicate.CODEC.optionalFieldOf("location").forGetter(Conditions::location)).apply(instance, Conditions::new);
      });

      public Conditions(Optional playerPredicate, Optional location) {
         this.player = playerPredicate;
         this.location = location;
      }

      public static AdvancementCriterion createPlacedBlock(Block block) {
         LootContextPredicate lootContextPredicate = LootContextPredicate.create(BlockStatePropertyLootCondition.builder(block).build());
         return Criteria.PLACED_BLOCK.create(new Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
      }

      public static AdvancementCriterion createPlacedBlock(LootCondition.Builder... locationConditions) {
         LootContextPredicate lootContextPredicate = LootContextPredicate.create((LootCondition[])Arrays.stream(locationConditions).map(LootCondition.Builder::build).toArray((i) -> {
            return new LootCondition[i];
         }));
         return Criteria.PLACED_BLOCK.create(new Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
      }

      public static AdvancementCriterion createPlacedWithState(Block block, Property property, String value) {
         StatePredicate.Builder builder = StatePredicate.Builder.create().exactMatch(property, value);
         LootContextPredicate lootContextPredicate = LootContextPredicate.create(BlockStatePropertyLootCondition.builder(block).properties(builder).build());
         return Criteria.PLACED_BLOCK.create(new Conditions(Optional.empty(), Optional.of(lootContextPredicate)));
      }

      public static AdvancementCriterion createPlacedWithState(Block block, Property property, boolean value) {
         return createPlacedWithState(block, property, String.valueOf(value));
      }

      public static AdvancementCriterion createPlacedWithState(Block block, Property property, int value) {
         return createPlacedWithState(block, property, String.valueOf(value));
      }

      public static AdvancementCriterion createPlacedWithState(Block block, Property property, Comparable value) {
         return createPlacedWithState(block, property, ((StringIdentifiable)value).asString());
      }

      private static Conditions create(LocationPredicate.Builder location, ItemPredicate.Builder item) {
         LootContextPredicate lootContextPredicate = LootContextPredicate.create(LocationCheckLootCondition.builder(location).build(), MatchToolLootCondition.builder(item).build());
         return new Conditions(Optional.empty(), Optional.of(lootContextPredicate));
      }

      public static AdvancementCriterion createItemUsedOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
         return Criteria.ITEM_USED_ON_BLOCK.create(create(location, item));
      }

      public static AdvancementCriterion createAllayDropItemOnBlock(LocationPredicate.Builder location, ItemPredicate.Builder item) {
         return Criteria.ALLAY_DROP_ITEM_ON_BLOCK.create(create(location, item));
      }

      public boolean test(LootContext location) {
         return this.location.isEmpty() || ((LootContextPredicate)this.location.get()).test(location);
      }

      public void validate(LootContextPredicateValidator validator) {
         AbstractCriterion.Conditions.super.validate(validator);
         this.location.ifPresent((location) -> {
            validator.validate(location, LootContextTypes.ADVANCEMENT_LOCATION, "location");
         });
      }

      public Optional player() {
         return this.player;
      }

      public Optional location() {
         return this.location;
      }
   }
}
