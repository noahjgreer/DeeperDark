package net.minecraft.world.gen.feature.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.TopologicalSorts;
import net.minecraft.util.Util;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class PlacedFeatureIndexer {
   public static List collectIndexedFeatures(List biomes, Function biomesToPlacedFeaturesList, boolean listInvolvedBiomesOnFailure) {
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      MutableInt mutableInt = new MutableInt(0);
      Comparator comparator = Comparator.comparingInt(IndexedFeature::step).thenComparingInt(IndexedFeature::featureIndex);
      Map map = new TreeMap(comparator);
      int i = 0;
      Iterator var8 = biomes.iterator();

      ArrayList list;
      int j;

      record IndexedFeature(int featureIndex, int step, PlacedFeature feature) {
         IndexedFeature(int i, int j, PlacedFeature placedFeature) {
            this.featureIndex = i;
            this.step = j;
            this.feature = placedFeature;
         }

         public int featureIndex() {
            return this.featureIndex;
         }

         public int step() {
            return this.step;
         }

         public PlacedFeature feature() {
            return this.feature;
         }
      }

      while(var8.hasNext()) {
         Object object = var8.next();
         list = Lists.newArrayList();
         List list2 = (List)biomesToPlacedFeaturesList.apply(object);
         i = Math.max(i, list2.size());

         for(j = 0; j < list2.size(); ++j) {
            Iterator var13 = ((RegistryEntryList)list2.get(j)).iterator();

            while(var13.hasNext()) {
               RegistryEntry registryEntry = (RegistryEntry)var13.next();
               PlacedFeature placedFeature = (PlacedFeature)registryEntry.value();
               list.add(new IndexedFeature(object2IntMap.computeIfAbsent(placedFeature, (feature) -> {
                  return mutableInt.getAndIncrement();
               }), j, placedFeature));
            }
         }

         for(j = 0; j < list.size(); ++j) {
            Set set = (Set)map.computeIfAbsent((IndexedFeature)list.get(j), (feature) -> {
               return new TreeSet(comparator);
            });
            if (j < list.size() - 1) {
               set.add((IndexedFeature)list.get(j + 1));
            }
         }
      }

      Set set2 = new TreeSet(comparator);
      Set set3 = new TreeSet(comparator);
      list = Lists.newArrayList();
      Iterator var21 = map.keySet().iterator();

      while(var21.hasNext()) {
         IndexedFeature indexedFeature = (IndexedFeature)var21.next();
         if (!set3.isEmpty()) {
            throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
         }

         if (!set2.contains(indexedFeature)) {
            Objects.requireNonNull(list);
            if (TopologicalSorts.sort(map, set2, set3, list::add, indexedFeature)) {
               if (!listInvolvedBiomesOnFailure) {
                  throw new IllegalStateException("Feature order cycle found");
               }

               List list3 = new ArrayList(biomes);

               int k;
               do {
                  k = list3.size();
                  ListIterator listIterator = list3.listIterator();

                  while(listIterator.hasNext()) {
                     Object object2 = listIterator.next();
                     listIterator.remove();

                     try {
                        collectIndexedFeatures(list3, biomesToPlacedFeaturesList, false);
                     } catch (IllegalStateException var18) {
                        continue;
                     }

                     listIterator.add(object2);
                  }
               } while(k != list3.size());

               throw new IllegalStateException("Feature order cycle found, involved sources: " + String.valueOf(list3));
            }
         }
      }

      Collections.reverse(list);
      ImmutableList.Builder builder = ImmutableList.builder();

      for(j = 0; j < i; ++j) {
         List list4 = (List)list.stream().filter((feature) -> {
            return feature.step() == j;
         }).map(IndexedFeature::feature).collect(Collectors.toList());
         builder.add(new IndexedFeatures(list4));
      }

      return builder.build();
   }

   public static record IndexedFeatures(List features, ToIntFunction indexMapping) {
      IndexedFeatures(List features) {
         this(features, Util.lastIdentityIndexGetter(features));
      }

      public IndexedFeatures(List list, ToIntFunction toIntFunction) {
         this.features = list;
         this.indexMapping = toIntFunction;
      }

      public List features() {
         return this.features;
      }

      public ToIntFunction indexMapping() {
         return this.indexMapping;
      }
   }
}
