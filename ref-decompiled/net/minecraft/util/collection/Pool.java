package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public final class Pool {
   private static final int FLATTENED_CONTENT_THRESHOLD = 64;
   private final int totalWeight;
   private final List entries;
   @Nullable
   private final Content content;

   Pool(List entries) {
      this.entries = List.copyOf(entries);
      this.totalWeight = Weighting.getWeightSum(entries, Weighted::weight);
      if (this.totalWeight == 0) {
         this.content = null;
      } else if (this.totalWeight < 64) {
         this.content = new FlattenedContent(this.entries, this.totalWeight);
      } else {
         this.content = new WrappedContent(this.entries);
      }

   }

   public static Pool empty() {
      return new Pool(List.of());
   }

   public static Pool of(Object entry) {
      return new Pool(List.of(new Weighted(entry, 1)));
   }

   @SafeVarargs
   public static Pool of(Weighted... entries) {
      return new Pool(List.of(entries));
   }

   public static Pool of(List entries) {
      return new Pool(entries);
   }

   public static Builder builder() {
      return new Builder();
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public Pool transform(Function function) {
      return new Pool(Lists.transform(this.entries, (entry) -> {
         return entry.transform(function);
      }));
   }

   public Optional getOrEmpty(Random random) {
      if (this.content == null) {
         return Optional.empty();
      } else {
         int i = random.nextInt(this.totalWeight);
         return Optional.of(this.content.get(i));
      }
   }

   public Object get(Random random) {
      if (this.content == null) {
         throw new IllegalStateException("Weighted list has no elements");
      } else {
         int i = random.nextInt(this.totalWeight);
         return this.content.get(i);
      }
   }

   public List getEntries() {
      return this.entries;
   }

   public static Codec createCodec(Codec entryCodec) {
      return Weighted.createCodec(entryCodec).listOf().xmap(Pool::of, Pool::getEntries);
   }

   public static Codec createCodec(MapCodec entryCodec) {
      return Weighted.createCodec(entryCodec).listOf().xmap(Pool::of, Pool::getEntries);
   }

   public static Codec createNonEmptyCodec(Codec entryCodec) {
      return Codecs.nonEmptyList(Weighted.createCodec(entryCodec).listOf()).xmap(Pool::of, Pool::getEntries);
   }

   public static Codec createNonEmptyCodec(MapCodec entryCodec) {
      return Codecs.nonEmptyList(Weighted.createCodec(entryCodec).listOf()).xmap(Pool::of, Pool::getEntries);
   }

   public boolean contains(Object value) {
      Iterator var2 = this.entries.iterator();

      Weighted weighted;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         weighted = (Weighted)var2.next();
      } while(!weighted.value().equals(value));

      return true;
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Pool)) {
         return false;
      } else {
         Pool pool = (Pool)o;
         return this.totalWeight == pool.totalWeight && Objects.equals(this.entries, pool.entries);
      }
   }

   public int hashCode() {
      int i = this.totalWeight;
      i = 31 * i + this.entries.hashCode();
      return i;
   }

   private interface Content {
      Object get(int i);
   }

   private static class FlattenedContent implements Content {
      private final Object[] entries;

      FlattenedContent(List entries, int totalWeight) {
         this.entries = new Object[totalWeight];
         int i = 0;

         int j;
         for(Iterator var4 = entries.iterator(); var4.hasNext(); i += j) {
            Weighted weighted = (Weighted)var4.next();
            j = weighted.weight();
            Arrays.fill(this.entries, i, i + j, weighted.value());
         }

      }

      public Object get(int i) {
         return this.entries[i];
      }
   }

   private static class WrappedContent implements Content {
      private final Weighted[] entries;

      WrappedContent(List entries) {
         this.entries = (Weighted[])entries.toArray((i) -> {
            return new Weighted[i];
         });
      }

      public Object get(int i) {
         Weighted[] var2 = this.entries;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Weighted weighted = var2[var4];
            i -= weighted.weight();
            if (i < 0) {
               return weighted.value();
            }
         }

         throw new IllegalStateException("" + i + " exceeded total weight");
      }
   }

   public static class Builder {
      private final ImmutableList.Builder entries = ImmutableList.builder();

      public Builder add(Object object) {
         return this.add(object, 1);
      }

      public Builder add(Object object, int weight) {
         this.entries.add(new Weighted(object, weight));
         return this;
      }

      public Pool build() {
         return new Pool(this.entries.build());
      }
   }
}
