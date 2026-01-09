package net.minecraft.loot.operator;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class BoundedIntUnaryOperator {
   private static final Codec OPERATOR_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(LootNumberProviderTypes.CODEC.optionalFieldOf("min").forGetter((operator) -> {
         return Optional.ofNullable(operator.min);
      }), LootNumberProviderTypes.CODEC.optionalFieldOf("max").forGetter((operator) -> {
         return Optional.ofNullable(operator.max);
      })).apply(instance, BoundedIntUnaryOperator::new);
   });
   public static final Codec CODEC;
   @Nullable
   private final LootNumberProvider min;
   @Nullable
   private final LootNumberProvider max;
   private final Applier applier;
   private final Tester tester;

   public Set getRequiredParameters() {
      ImmutableSet.Builder builder = ImmutableSet.builder();
      if (this.min != null) {
         builder.addAll(this.min.getAllowedParameters());
      }

      if (this.max != null) {
         builder.addAll(this.max.getAllowedParameters());
      }

      return builder.build();
   }

   private BoundedIntUnaryOperator(Optional min, Optional max) {
      this((LootNumberProvider)min.orElse((Object)null), (LootNumberProvider)max.orElse((Object)null));
   }

   private BoundedIntUnaryOperator(@Nullable LootNumberProvider min, @Nullable LootNumberProvider max) {
      this.min = min;
      this.max = max;
      if (min == null) {
         if (max == null) {
            this.applier = (context, value) -> {
               return value;
            };
            this.tester = (context, value) -> {
               return true;
            };
         } else {
            this.applier = (context, value) -> {
               return Math.min(max.nextInt(context), value);
            };
            this.tester = (context, value) -> {
               return value <= max.nextInt(context);
            };
         }
      } else if (max == null) {
         this.applier = (context, value) -> {
            return Math.max(min.nextInt(context), value);
         };
         this.tester = (context, value) -> {
            return value >= min.nextInt(context);
         };
      } else {
         this.applier = (context, value) -> {
            return MathHelper.clamp(value, min.nextInt(context), max.nextInt(context));
         };
         this.tester = (context, value) -> {
            return value >= min.nextInt(context) && value <= max.nextInt(context);
         };
      }

   }

   public static BoundedIntUnaryOperator create(int value) {
      ConstantLootNumberProvider constantLootNumberProvider = ConstantLootNumberProvider.create((float)value);
      return new BoundedIntUnaryOperator(Optional.of(constantLootNumberProvider), Optional.of(constantLootNumberProvider));
   }

   public static BoundedIntUnaryOperator create(int min, int max) {
      return new BoundedIntUnaryOperator(Optional.of(ConstantLootNumberProvider.create((float)min)), Optional.of(ConstantLootNumberProvider.create((float)max)));
   }

   public static BoundedIntUnaryOperator createMin(int min) {
      return new BoundedIntUnaryOperator(Optional.of(ConstantLootNumberProvider.create((float)min)), Optional.empty());
   }

   public static BoundedIntUnaryOperator createMax(int max) {
      return new BoundedIntUnaryOperator(Optional.empty(), Optional.of(ConstantLootNumberProvider.create((float)max)));
   }

   public int apply(LootContext context, int value) {
      return this.applier.apply(context, value);
   }

   public boolean test(LootContext context, int value) {
      return this.tester.test(context, value);
   }

   private OptionalInt getConstantValue() {
      if (Objects.equals(this.min, this.max)) {
         LootNumberProvider var2 = this.min;
         if (var2 instanceof ConstantLootNumberProvider) {
            ConstantLootNumberProvider constantLootNumberProvider = (ConstantLootNumberProvider)var2;
            if (Math.floor((double)constantLootNumberProvider.value()) == (double)constantLootNumberProvider.value()) {
               return OptionalInt.of((int)constantLootNumberProvider.value());
            }
         }
      }

      return OptionalInt.empty();
   }

   static {
      CODEC = Codec.either(Codec.INT, OPERATOR_CODEC).xmap((either) -> {
         return (BoundedIntUnaryOperator)either.map(BoundedIntUnaryOperator::create, Function.identity());
      }, (operator) -> {
         OptionalInt optionalInt = operator.getConstantValue();
         return optionalInt.isPresent() ? Either.left(optionalInt.getAsInt()) : Either.right(operator);
      });
   }

   @FunctionalInterface
   private interface Applier {
      int apply(LootContext context, int value);
   }

   @FunctionalInterface
   private interface Tester {
      boolean test(LootContext context, int value);
   }
}
