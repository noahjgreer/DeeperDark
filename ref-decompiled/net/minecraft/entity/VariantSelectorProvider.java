package net.minecraft.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public interface VariantSelectorProvider {
   List getSelectors();

   static Stream select(Stream entries, Function providerGetter, Object context) {
      List list = new ArrayList();
      entries.forEach((entry) -> {
         VariantSelectorProvider variantSelectorProvider = (VariantSelectorProvider)providerGetter.apply(entry);
         Iterator var4 = variantSelectorProvider.getSelectors().iterator();

         while(var4.hasNext()) {
            Selector selector = (Selector)var4.next();
            list.add(new UnwrappedSelector(entry, selector.priority(), (SelectorCondition)DataFixUtils.orElseGet(selector.condition(), SelectorCondition::alwaysTrue)));
         }

      });
      list.sort(VariantSelectorProvider.UnwrappedSelector.PRIORITY_COMPARATOR);
      Iterator iterator = list.iterator();
      int i = Integer.MIN_VALUE;

      while(iterator.hasNext()) {
         UnwrappedSelector unwrappedSelector = (UnwrappedSelector)iterator.next();
         if (unwrappedSelector.priority < i) {
            iterator.remove();
         } else if (unwrappedSelector.condition.test(context)) {
            i = unwrappedSelector.priority;
         } else {
            iterator.remove();
         }
      }

      return list.stream().map(UnwrappedSelector::entry);
   }

   static Optional select(Stream entries, Function providerGetter, Random random, Object context) {
      List list = select(entries, providerGetter, context).toList();
      return Util.getRandomOrEmpty(list, random);
   }

   static List createSingle(SelectorCondition condition, int priority) {
      return List.of(new Selector(condition, priority));
   }

   static List createFallback(int priority) {
      return List.of(new Selector(Optional.empty(), priority));
   }

   public static record UnwrappedSelector(Object entry, int priority, SelectorCondition condition) {
      final int priority;
      final SelectorCondition condition;
      public static final Comparator PRIORITY_COMPARATOR = Comparator.comparingInt(UnwrappedSelector::priority).reversed();

      public UnwrappedSelector(Object object, int i, SelectorCondition selectorCondition) {
         this.entry = object;
         this.priority = i;
         this.condition = selectorCondition;
      }

      public Object entry() {
         return this.entry;
      }

      public int priority() {
         return this.priority;
      }

      public SelectorCondition condition() {
         return this.condition;
      }
   }

   @FunctionalInterface
   public interface SelectorCondition extends Predicate {
      static SelectorCondition alwaysTrue() {
         return (context) -> {
            return true;
         };
      }
   }

   public static record Selector(Optional condition, int priority) {
      public Selector(SelectorCondition condition, int priority) {
         this(Optional.of(condition), priority);
      }

      public Selector(int priority) {
         this(Optional.empty(), priority);
      }

      public Selector(Optional optional, int i) {
         this.condition = optional;
         this.priority = i;
      }

      public static Codec createCodec(Codec conditionCodec) {
         return RecordCodecBuilder.create((instance) -> {
            return instance.group(conditionCodec.optionalFieldOf("condition").forGetter(Selector::condition), Codec.INT.fieldOf("priority").forGetter(Selector::priority)).apply(instance, Selector::new);
         });
      }

      public Optional condition() {
         return this.condition;
      }

      public int priority() {
         return this.priority;
      }
   }
}
