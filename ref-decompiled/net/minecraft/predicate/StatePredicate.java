package net.minecraft.predicate;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public record StatePredicate(List conditions) {
   private static final Codec CONDITION_LIST_CODEC;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public StatePredicate(List conditions) {
      this.conditions = conditions;
   }

   public boolean test(StateManager stateManager, State container) {
      Iterator var3 = this.conditions.iterator();

      Condition condition;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         condition = (Condition)var3.next();
      } while(condition.test(stateManager, container));

      return false;
   }

   public boolean test(BlockState state) {
      return this.test(state.getBlock().getStateManager(), state);
   }

   public boolean test(FluidState state) {
      return this.test(state.getFluid().getStateManager(), state);
   }

   public Optional findMissing(StateManager stateManager) {
      Iterator var2 = this.conditions.iterator();

      Optional optional;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         Condition condition = (Condition)var2.next();
         optional = condition.reportMissing(stateManager);
      } while(!optional.isPresent());

      return optional;
   }

   public List conditions() {
      return this.conditions;
   }

   static {
      CONDITION_LIST_CODEC = Codec.unboundedMap(Codec.STRING, StatePredicate.ValueMatcher.CODEC).xmap((states) -> {
         return states.entrySet().stream().map((state) -> {
            return new Condition((String)state.getKey(), (ValueMatcher)state.getValue());
         }).toList();
      }, (conditions) -> {
         return (Map)conditions.stream().collect(Collectors.toMap(Condition::key, Condition::valueMatcher));
      });
      CODEC = CONDITION_LIST_CODEC.xmap(StatePredicate::new, StatePredicate::conditions);
      PACKET_CODEC = StatePredicate.Condition.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(StatePredicate::new, StatePredicate::conditions);
   }

   static record Condition(String key, ValueMatcher valueMatcher) {
      public static final PacketCodec PACKET_CODEC;

      Condition(String key, ValueMatcher valueMatcher) {
         this.key = key;
         this.valueMatcher = valueMatcher;
      }

      public boolean test(StateManager stateManager, State state) {
         Property property = stateManager.getProperty(this.key);
         return property != null && this.valueMatcher.test(state, property);
      }

      public Optional reportMissing(StateManager factory) {
         Property property = factory.getProperty(this.key);
         return property != null ? Optional.empty() : Optional.of(this.key);
      }

      public String key() {
         return this.key;
      }

      public ValueMatcher valueMatcher() {
         return this.valueMatcher;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, Condition::key, StatePredicate.ValueMatcher.PACKET_CODEC, Condition::valueMatcher, Condition::new);
      }
   }

   private interface ValueMatcher {
      Codec CODEC = Codec.either(StatePredicate.ExactValueMatcher.CODEC, StatePredicate.RangedValueMatcher.CODEC).xmap(Either::unwrap, (valueMatcher) -> {
         if (valueMatcher instanceof ExactValueMatcher exactValueMatcher) {
            return Either.left(exactValueMatcher);
         } else if (valueMatcher instanceof RangedValueMatcher rangedValueMatcher) {
            return Either.right(rangedValueMatcher);
         } else {
            throw new UnsupportedOperationException();
         }
      });
      PacketCodec PACKET_CODEC = PacketCodecs.either(StatePredicate.ExactValueMatcher.PACKET_CODEC, StatePredicate.RangedValueMatcher.PACKET_CODEC).xmap(Either::unwrap, (valueMatcher) -> {
         if (valueMatcher instanceof ExactValueMatcher exactValueMatcher) {
            return Either.left(exactValueMatcher);
         } else if (valueMatcher instanceof RangedValueMatcher rangedValueMatcher) {
            return Either.right(rangedValueMatcher);
         } else {
            throw new UnsupportedOperationException();
         }
      });

      boolean test(State state, Property property);
   }

   public static class Builder {
      private final ImmutableList.Builder conditions = ImmutableList.builder();

      private Builder() {
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder exactMatch(Property property, String valueName) {
         this.conditions.add(new Condition(property.getName(), new ExactValueMatcher(valueName)));
         return this;
      }

      public Builder exactMatch(Property property, int value) {
         return this.exactMatch(property, Integer.toString(value));
      }

      public Builder exactMatch(Property property, boolean value) {
         return this.exactMatch(property, Boolean.toString(value));
      }

      public Builder exactMatch(Property property, Comparable value) {
         return this.exactMatch(property, ((StringIdentifiable)value).asString());
      }

      public Optional build() {
         return Optional.of(new StatePredicate(this.conditions.build()));
      }
   }

   private static record RangedValueMatcher(Optional min, Optional max) implements ValueMatcher {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.STRING.optionalFieldOf("min").forGetter(RangedValueMatcher::min), Codec.STRING.optionalFieldOf("max").forGetter(RangedValueMatcher::max)).apply(instance, RangedValueMatcher::new);
      });
      public static final PacketCodec PACKET_CODEC;

      private RangedValueMatcher(Optional optional, Optional optional2) {
         this.min = optional;
         this.max = optional2;
      }

      public boolean test(State state, Property property) {
         Comparable comparable = state.get(property);
         Optional optional;
         if (this.min.isPresent()) {
            optional = property.parse((String)this.min.get());
            if (optional.isEmpty() || comparable.compareTo((Comparable)optional.get()) < 0) {
               return false;
            }
         }

         if (this.max.isPresent()) {
            optional = property.parse((String)this.max.get());
            if (optional.isEmpty() || comparable.compareTo((Comparable)optional.get()) > 0) {
               return false;
            }
         }

         return true;
      }

      public Optional min() {
         return this.min;
      }

      public Optional max() {
         return this.max;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(PacketCodecs.STRING), RangedValueMatcher::min, PacketCodecs.optional(PacketCodecs.STRING), RangedValueMatcher::max, RangedValueMatcher::new);
      }
   }

   private static record ExactValueMatcher(String value) implements ValueMatcher {
      public static final Codec CODEC;
      public static final PacketCodec PACKET_CODEC;

      ExactValueMatcher(String key) {
         this.value = key;
      }

      public boolean test(State state, Property property) {
         Comparable comparable = state.get(property);
         Optional optional = property.parse(this.value);
         return optional.isPresent() && comparable.compareTo((Comparable)optional.get()) == 0;
      }

      public String value() {
         return this.value;
      }

      static {
         CODEC = Codec.STRING.xmap(ExactValueMatcher::new, ExactValueMatcher::value);
         PACKET_CODEC = PacketCodecs.STRING.xmap(ExactValueMatcher::new, ExactValueMatcher::value);
      }
   }
}
