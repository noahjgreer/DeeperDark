package net.minecraft.client.render.model.json;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public record SimpleMultipartModelSelector(Map tests) implements MultipartModelCondition {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec CODEC;

   public SimpleMultipartModelSelector(Map map) {
      this.tests = map;
   }

   public Predicate instantiate(StateManager stateManager) {
      List list = new ArrayList(this.tests.size());
      this.tests.forEach((property, terms) -> {
         list.add(init(stateManager, property, terms));
      });
      return Util.allOf(list);
   }

   private static Predicate init(StateManager stateManager, String property, Terms terms) {
      Property property2 = stateManager.getProperty(property);
      if (property2 == null) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", property, stateManager.getOwner()));
      } else {
         return terms.instantiate(stateManager.getOwner(), property2);
      }
   }

   public Map tests() {
      return this.tests;
   }

   static {
      CODEC = Codecs.nonEmptyMap(Codec.unboundedMap(Codec.STRING, SimpleMultipartModelSelector.Terms.VALUE_CODEC)).xmap(SimpleMultipartModelSelector::new, SimpleMultipartModelSelector::tests);
   }

   @Environment(EnvType.CLIENT)
   public static record Terms(List entries) {
      private static final char DELIMITER = '|';
      private static final Joiner JOINER = Joiner.on('|');
      private static final Splitter SPLITTER = Splitter.on('|');
      private static final Codec CODEC;
      public static final Codec VALUE_CODEC;

      public Terms(List entries) {
         if (entries.isEmpty()) {
            throw new IllegalArgumentException("Empty value for property");
         } else {
            this.entries = entries;
         }
      }

      public static DataResult tryParse(String terms) {
         List list = SPLITTER.splitToStream(terms).map(Term::parse).toList();
         if (list.isEmpty()) {
            return DataResult.error(() -> {
               return "Empty value for property";
            });
         } else {
            Iterator var2 = list.iterator();

            Term term;
            do {
               if (!var2.hasNext()) {
                  return DataResult.success(new Terms(list));
               }

               term = (Term)var2.next();
            } while(!term.value.isEmpty());

            return DataResult.error(() -> {
               return "Empty term in value '" + terms + "'";
            });
         }
      }

      public String toString() {
         return JOINER.join(this.entries);
      }

      public Predicate instantiate(Object object, Property property) {
         Predicate predicate = Util.anyOf(Lists.transform(this.entries, (term) -> {
            return this.instantiate(object, property, term);
         }));
         List list = new ArrayList(property.getValues());
         int i = list.size();
         list.removeIf(predicate.negate());
         int j = list.size();
         if (j == 0) {
            SimpleMultipartModelSelector.LOGGER.warn("Condition {} for property {} on {} is always false", new Object[]{this, property.getName(), object});
            return (state) -> {
               return false;
            };
         } else {
            int k = i - j;
            if (k == 0) {
               SimpleMultipartModelSelector.LOGGER.warn("Condition {} for property {} on {} is always true", new Object[]{this, property.getName(), object});
               return (state) -> {
                  return true;
               };
            } else {
               boolean bl;
               ArrayList list2;
               if (j <= k) {
                  bl = false;
                  list2 = list;
               } else {
                  bl = true;
                  List list3 = new ArrayList(property.getValues());
                  list3.removeIf(predicate);
                  list2 = list3;
               }

               if (list2.size() == 1) {
                  Comparable comparable = (Comparable)list2.getFirst();
                  return (state) -> {
                     Comparable comparable2 = state.get(property);
                     return comparable.equals(comparable2) ^ bl;
                  };
               } else {
                  return (state) -> {
                     Comparable comparable = state.get(property);
                     return list2.contains(comparable) ^ bl;
                  };
               }
            }
         }
      }

      private Comparable parseValue(Object object, Property property, String value) {
         Optional optional = property.parse(value);
         if (optional.isEmpty()) {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", value, property, object, this));
         } else {
            return (Comparable)optional.get();
         }
      }

      private Predicate instantiate(Object object, Property property, Term term) {
         Comparable comparable = this.parseValue(object, property, term.value);
         return term.negated ? (value) -> {
            return !value.equals(comparable);
         } : (value) -> {
            return value.equals(comparable);
         };
      }

      public List entries() {
         return this.entries;
      }

      static {
         CODEC = Codec.either(Codec.INT, Codec.BOOL).flatComapMap((either) -> {
            return (String)either.map(String::valueOf, String::valueOf);
         }, (string) -> {
            return DataResult.error(() -> {
               return "This codec can't be used for encoding";
            });
         });
         VALUE_CODEC = Codec.withAlternative(Codec.STRING, CODEC).comapFlatMap(Terms::tryParse, Terms::toString);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Term(String value, boolean negated) {
      final String value;
      final boolean negated;
      private static final String NEGATED_PREFIX = "!";

      public Term(String value, boolean negated) {
         if (value.isEmpty()) {
            throw new IllegalArgumentException("Empty term");
         } else {
            this.value = value;
            this.negated = negated;
         }
      }

      public static Term parse(String value) {
         return value.startsWith("!") ? new Term(value.substring(1), true) : new Term(value, false);
      }

      public String toString() {
         return this.negated ? "!" + this.value : this.value;
      }

      public String value() {
         return this.value;
      }

      public boolean negated() {
         return this.negated;
      }
   }
}
