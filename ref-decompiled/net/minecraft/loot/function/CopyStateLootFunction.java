package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public class CopyStateLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(Registries.BLOCK.getEntryCodec().fieldOf("block").forGetter((function) -> {
         return function.block;
      }), Codec.STRING.listOf().fieldOf("properties").forGetter((function) -> {
         return function.properties.stream().map(Property::getName).toList();
      }))).apply(instance, CopyStateLootFunction::new);
   });
   private final RegistryEntry block;
   private final Set properties;

   CopyStateLootFunction(List conditions, RegistryEntry block, Set properties) {
      super(conditions);
      this.block = block;
      this.properties = properties;
   }

   private CopyStateLootFunction(List conditions, RegistryEntry block, List properties) {
      Stream var10003 = properties.stream();
      StateManager var10004 = ((Block)block.value()).getStateManager();
      Objects.requireNonNull(var10004);
      this(conditions, block, (Set)var10003.map(var10004::getProperty).filter(Objects::nonNull).collect(Collectors.toSet()));
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.COPY_STATE;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.BLOCK_STATE);
   }

   protected ItemStack process(ItemStack stack, LootContext context) {
      BlockState blockState = (BlockState)context.get(LootContextParameters.BLOCK_STATE);
      if (blockState != null) {
         stack.apply(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT, (component) -> {
            Iterator var3 = this.properties.iterator();

            while(var3.hasNext()) {
               Property property = (Property)var3.next();
               if (blockState.contains(property)) {
                  component = component.with(property, blockState);
               }
            }

            return component;
         });
      }

      return stack;
   }

   public static Builder builder(Block block) {
      return new Builder(block);
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final RegistryEntry block;
      private final ImmutableSet.Builder properties = ImmutableSet.builder();

      Builder(Block block) {
         this.block = block.getRegistryEntry();
      }

      public Builder addProperty(Property property) {
         if (!((Block)this.block.value()).getStateManager().getProperties().contains(property)) {
            String var10002 = String.valueOf(property);
            throw new IllegalStateException("Property " + var10002 + " is not present on block " + String.valueOf(this.block));
         } else {
            this.properties.add(property);
            return this;
         }
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public LootFunction build() {
         return new CopyStateLootFunction(this.getConditions(), this.block, this.properties.build());
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
