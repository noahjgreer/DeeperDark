package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

public class CopyComponentsLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(CopyComponentsLootFunction.Source.CODEC.fieldOf("source").forGetter((function) -> {
         return function.source;
      }), ComponentType.CODEC.listOf().optionalFieldOf("include").forGetter((function) -> {
         return function.include;
      }), ComponentType.CODEC.listOf().optionalFieldOf("exclude").forGetter((function) -> {
         return function.exclude;
      }))).apply(instance, CopyComponentsLootFunction::new);
   });
   private final Source source;
   private final Optional include;
   private final Optional exclude;
   private final Predicate filter;

   CopyComponentsLootFunction(List conditions, Source source, Optional include, Optional exclude) {
      super(conditions);
      this.source = source;
      this.include = include.map(List::copyOf);
      this.exclude = exclude.map(List::copyOf);
      List list = new ArrayList(2);
      exclude.ifPresent((excludedTypes) -> {
         list.add((type) -> {
            return !excludedTypes.contains(type);
         });
      });
      include.ifPresent((includedTypes) -> {
         Objects.requireNonNull(includedTypes);
         list.add(includedTypes::contains);
      });
      this.filter = Util.allOf(list);
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.COPY_COMPONENTS;
   }

   public Set getAllowedParameters() {
      return this.source.getRequiredParameters();
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      ComponentMap componentMap = this.source.getComponents(context);
      stack.applyComponentsFrom(componentMap.filtered(this.filter));
      return stack;
   }

   public static Builder builder(Source source) {
      return new Builder(source);
   }

   public static enum Source implements StringIdentifiable {
      BLOCK_ENTITY("block_entity");

      public static final Codec CODEC = StringIdentifiable.createBasicCodec(Source::values);
      private final String id;

      private Source(final String id) {
         this.id = id;
      }

      public ComponentMap getComponents(LootContext context) {
         switch (this.ordinal()) {
            case 0:
               BlockEntity blockEntity = (BlockEntity)context.get(LootContextParameters.BLOCK_ENTITY);
               return blockEntity != null ? blockEntity.createComponentMap() : ComponentMap.EMPTY;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      public Set getRequiredParameters() {
         switch (this.ordinal()) {
            case 0:
               return Set.of(LootContextParameters.BLOCK_ENTITY);
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      public String asString() {
         return this.id;
      }

      // $FF: synthetic method
      private static Source[] method_57645() {
         return new Source[]{BLOCK_ENTITY};
      }
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private final Source source;
      private Optional include = Optional.empty();
      private Optional exclude = Optional.empty();

      Builder(Source source) {
         this.source = source;
      }

      public Builder include(ComponentType type) {
         if (this.include.isEmpty()) {
            this.include = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.include.get()).add(type);
         return this;
      }

      public Builder exclude(ComponentType type) {
         if (this.exclude.isEmpty()) {
            this.exclude = Optional.of(ImmutableList.builder());
         }

         ((ImmutableList.Builder)this.exclude.get()).add(type);
         return this;
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public LootFunction build() {
         return new CopyComponentsLootFunction(this.getConditions(), this.source, this.include.map(ImmutableList.Builder::build), this.exclude.map(ImmutableList.Builder::build));
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
