package net.minecraft.predicate.collection;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.predicate.NumberRange;

public record CollectionPredicate(Optional contains, Optional counts, Optional size) implements Predicate {
   public CollectionPredicate(Optional optional, Optional optional2, Optional optional3) {
      this.contains = optional;
      this.counts = optional2;
      this.size = optional3;
   }

   public static Codec createCodec(Codec predicateCodec) {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(CollectionContainsPredicate.createCodec(predicateCodec).optionalFieldOf("contains").forGetter(CollectionPredicate::contains), CollectionCountsPredicate.createCodec(predicateCodec).optionalFieldOf("count").forGetter(CollectionPredicate::counts), NumberRange.IntRange.CODEC.optionalFieldOf("size").forGetter(CollectionPredicate::size)).apply(instance, CollectionPredicate::new);
      });
   }

   public boolean test(Iterable iterable) {
      if (this.contains.isPresent() && !((CollectionContainsPredicate)this.contains.get()).test(iterable)) {
         return false;
      } else if (this.counts.isPresent() && !((CollectionCountsPredicate)this.counts.get()).test(iterable)) {
         return false;
      } else {
         return !this.size.isPresent() || ((NumberRange.IntRange)this.size.get()).test(Iterables.size(iterable));
      }
   }

   public Optional contains() {
      return this.contains;
   }

   public Optional counts() {
      return this.counts;
   }

   public Optional size() {
      return this.size;
   }

   // $FF: synthetic method
   public boolean test(final Object collection) {
      return this.test((Iterable)collection);
   }
}
