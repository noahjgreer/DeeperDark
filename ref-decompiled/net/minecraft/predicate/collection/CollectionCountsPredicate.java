package net.minecraft.predicate.collection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.predicate.NumberRange;

public interface CollectionCountsPredicate extends Predicate {
   List getEntries();

   static Codec createCodec(Codec predicateCodec) {
      return CollectionCountsPredicate.Entry.createCodec(predicateCodec).listOf().xmap(CollectionCountsPredicate::create, CollectionCountsPredicate::getEntries);
   }

   @SafeVarargs
   static CollectionCountsPredicate create(Entry... entries) {
      return create(List.of(entries));
   }

   static CollectionCountsPredicate create(List entries) {
      Object var10000;
      switch (entries.size()) {
         case 0:
            var10000 = new Empty();
            break;
         case 1:
            var10000 = new Single((Entry)entries.getFirst());
            break;
         default:
            var10000 = new Multiple(entries);
      }

      return (CollectionCountsPredicate)var10000;
   }

   public static record Entry(Predicate test, NumberRange.IntRange count) {
      public Entry(Predicate predicate, NumberRange.IntRange intRange) {
         this.test = predicate;
         this.count = intRange;
      }

      public static Codec createCodec(Codec predicateCodec) {
         return RecordCodecBuilder.create((instance) -> {
            return instance.group(predicateCodec.fieldOf("test").forGetter(Entry::test), NumberRange.IntRange.CODEC.fieldOf("count").forGetter(Entry::count)).apply(instance, Entry::new);
         });
      }

      public boolean test(Iterable collection) {
         int i = 0;
         Iterator var3 = collection.iterator();

         while(var3.hasNext()) {
            Object object = var3.next();
            if (this.test.test(object)) {
               ++i;
            }
         }

         return this.count.test(i);
      }

      public Predicate test() {
         return this.test;
      }

      public NumberRange.IntRange count() {
         return this.count;
      }
   }

   public static class Empty implements CollectionCountsPredicate {
      public boolean test(Iterable iterable) {
         return true;
      }

      public List getEntries() {
         return List.of();
      }

      // $FF: synthetic method
      public boolean test(final Object collection) {
         return this.test((Iterable)collection);
      }
   }

   public static record Single(Entry entry) implements CollectionCountsPredicate {
      public Single(Entry entry) {
         this.entry = entry;
      }

      public boolean test(Iterable iterable) {
         return this.entry.test(iterable);
      }

      public List getEntries() {
         return List.of(this.entry);
      }

      public Entry entry() {
         return this.entry;
      }

      // $FF: synthetic method
      public boolean test(final Object collection) {
         return this.test((Iterable)collection);
      }
   }

   public static record Multiple(List entries) implements CollectionCountsPredicate {
      public Multiple(List list) {
         this.entries = list;
      }

      public boolean test(Iterable iterable) {
         Iterator var2 = this.entries.iterator();

         Entry entry;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            entry = (Entry)var2.next();
         } while(entry.test(iterable));

         return false;
      }

      public List getEntries() {
         return this.entries;
      }

      public List entries() {
         return this.entries;
      }

      // $FF: synthetic method
      public boolean test(final Object collection) {
         return this.test((Iterable)collection);
      }
   }
}
