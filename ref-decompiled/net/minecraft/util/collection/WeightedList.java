package net.minecraft.util.collection;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.math.random.Random;

public class WeightedList implements Iterable {
   protected final List entries;
   private final Random random = Random.create();

   public WeightedList() {
      this.entries = Lists.newArrayList();
   }

   private WeightedList(List list) {
      this.entries = Lists.newArrayList(list);
   }

   public static Codec createCodec(Codec codec) {
      return WeightedList.Entry.createCodec(codec).listOf().xmap(WeightedList::new, (weightedList) -> {
         return weightedList.entries;
      });
   }

   public WeightedList add(Object data, int weight) {
      this.entries.add(new Entry(data, weight));
      return this;
   }

   public WeightedList shuffle() {
      this.entries.forEach((entry) -> {
         entry.setShuffledOrder(this.random.nextFloat());
      });
      this.entries.sort(Comparator.comparingDouble(Entry::getShuffledOrder));
      return this;
   }

   public Stream stream() {
      return this.entries.stream().map(Entry::getElement);
   }

   public Iterator iterator() {
      return Iterators.transform(this.entries.iterator(), Entry::getElement);
   }

   public String toString() {
      return "ShufflingList[" + String.valueOf(this.entries) + "]";
   }

   public static class Entry {
      final Object data;
      final int weight;
      private double shuffledOrder;

      Entry(Object data, int weight) {
         this.weight = weight;
         this.data = data;
      }

      private double getShuffledOrder() {
         return this.shuffledOrder;
      }

      void setShuffledOrder(float random) {
         this.shuffledOrder = -Math.pow((double)random, (double)(1.0F / (float)this.weight));
      }

      public Object getElement() {
         return this.data;
      }

      public int getWeight() {
         return this.weight;
      }

      public String toString() {
         int var10000 = this.weight;
         return "" + var10000 + ":" + String.valueOf(this.data);
      }

      public static Codec createCodec(final Codec codec) {
         return new Codec() {
            public DataResult decode(DynamicOps ops, Object data) {
               Dynamic dynamic = new Dynamic(ops, data);
               OptionalDynamic var10000 = dynamic.get("data");
               Codec var10001 = codec;
               Objects.requireNonNull(var10001);
               return var10000.flatMap(var10001::parse).map((datax) -> {
                  return new Entry(datax, dynamic.get("weight").asInt(1));
               }).map((entry) -> {
                  return Pair.of(entry, ops.empty());
               });
            }

            public DataResult encode(Entry entry, DynamicOps dynamicOps, Object object) {
               return dynamicOps.mapBuilder().add("weight", dynamicOps.createInt(entry.weight)).add("data", codec.encodeStart(dynamicOps, entry.data)).build(object);
            }

            // $FF: synthetic method
            public DataResult encode(final Object entries, final DynamicOps ops, final Object data) {
               return this.encode((Entry)entries, ops, data);
            }
         };
      }
   }
}
