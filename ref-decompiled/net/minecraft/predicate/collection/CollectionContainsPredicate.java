package net.minecraft.predicate.collection;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionContainsPredicate extends Predicate {
   List getPredicates();

   static Codec createCodec(Codec predicateCodec) {
      return predicateCodec.listOf().xmap(CollectionContainsPredicate::create, CollectionContainsPredicate::getPredicates);
   }

   @SafeVarargs
   static CollectionContainsPredicate create(Predicate... predicates) {
      return create(List.of(predicates));
   }

   static CollectionContainsPredicate create(List predicates) {
      Object var10000;
      switch (predicates.size()) {
         case 0:
            var10000 = new Empty();
            break;
         case 1:
            var10000 = new Single((Predicate)predicates.getFirst());
            break;
         default:
            var10000 = new Multiple(predicates);
      }

      return (CollectionContainsPredicate)var10000;
   }

   public static class Empty implements CollectionContainsPredicate {
      public boolean test(Iterable iterable) {
         return true;
      }

      public List getPredicates() {
         return List.of();
      }

      // $FF: synthetic method
      public boolean test(final Object collection) {
         return this.test((Iterable)collection);
      }
   }

   public static record Single(Predicate test) implements CollectionContainsPredicate {
      public Single(Predicate predicate) {
         this.test = predicate;
      }

      public boolean test(Iterable iterable) {
         Iterator var2 = iterable.iterator();

         Object object;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            object = var2.next();
         } while(!this.test.test(object));

         return true;
      }

      public List getPredicates() {
         return List.of(this.test);
      }

      public Predicate test() {
         return this.test;
      }

      // $FF: synthetic method
      public boolean test(final Object collection) {
         return this.test((Iterable)collection);
      }
   }

   public static record Multiple(List tests) implements CollectionContainsPredicate {
      public Multiple(List list) {
         this.tests = list;
      }

      public boolean test(Iterable iterable) {
         List list = new ArrayList(this.tests);
         Iterator var3 = iterable.iterator();

         do {
            if (!var3.hasNext()) {
               return false;
            }

            Object object = var3.next();
            list.removeIf((predicate) -> {
               return predicate.test(object);
            });
         } while(!list.isEmpty());

         return true;
      }

      public List getPredicates() {
         return this.tests;
      }

      public List tests() {
         return this.tests;
      }

      // $FF: synthetic method
      public boolean test(final Object collection) {
         return this.test((Iterable)collection);
      }
   }
}
