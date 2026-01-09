package net.minecraft.loot.condition;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public record BlockStatePropertyLootCondition(RegistryEntry block, Optional properties) implements LootCondition {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Registries.BLOCK.getEntryCodec().fieldOf("block").forGetter(BlockStatePropertyLootCondition::block), StatePredicate.CODEC.optionalFieldOf("properties").forGetter(BlockStatePropertyLootCondition::properties)).apply(instance, BlockStatePropertyLootCondition::new);
   }).validate(BlockStatePropertyLootCondition::validateHasProperties);

   public BlockStatePropertyLootCondition(RegistryEntry registryEntry, Optional optional) {
      this.block = registryEntry;
      this.properties = optional;
   }

   private static DataResult validateHasProperties(BlockStatePropertyLootCondition condition) {
      return (DataResult)condition.properties().flatMap((predicate) -> {
         return predicate.findMissing(((Block)condition.block().value()).getStateManager());
      }).map((property) -> {
         return DataResult.error(() -> {
            String var10000 = String.valueOf(condition.block());
            return "Block " + var10000 + " has no property" + property;
         });
      }).orElse(DataResult.success(condition));
   }

   public LootConditionType getType() {
      return LootConditionTypes.BLOCK_STATE_PROPERTY;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.BLOCK_STATE);
   }

   public boolean test(LootContext lootContext) {
      BlockState blockState = (BlockState)lootContext.get(LootContextParameters.BLOCK_STATE);
      return blockState != null && blockState.isOf(this.block) && (this.properties.isEmpty() || ((StatePredicate)this.properties.get()).test(blockState));
   }

   public static Builder builder(Block block) {
      return new Builder(block);
   }

   public RegistryEntry block() {
      return this.block;
   }

   public Optional properties() {
      return this.properties;
   }

   // $FF: synthetic method
   public boolean test(final Object context) {
      return this.test((LootContext)context);
   }

   public static class Builder implements LootCondition.Builder {
      private final RegistryEntry block;
      private Optional propertyValues = Optional.empty();

      public Builder(Block block) {
         this.block = block.getRegistryEntry();
      }

      public Builder properties(StatePredicate.Builder builder) {
         this.propertyValues = builder.build();
         return this;
      }

      public LootCondition build() {
         return new BlockStatePropertyLootCondition(this.block, this.propertyValues);
      }
   }
}
