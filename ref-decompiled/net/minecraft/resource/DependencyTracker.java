package net.minecraft.resource;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DependencyTracker {
   private final Map underlying = new HashMap();

   public DependencyTracker add(Object key, Dependencies value) {
      this.underlying.put(key, value);
      return this;
   }

   private void traverse(Multimap parentChild, Set visited, Object rootKey, BiConsumer callback) {
      if (visited.add(rootKey)) {
         parentChild.get(rootKey).forEach((child) -> {
            this.traverse(parentChild, visited, child, callback);
         });
         Dependencies dependencies = (Dependencies)this.underlying.get(rootKey);
         if (dependencies != null) {
            callback.accept(rootKey, dependencies);
         }

      }
   }

   private static boolean containsReverseDependency(Multimap dependencies, Object key, Object dependency) {
      Collection collection = dependencies.get(dependency);
      return collection.contains(key) ? true : collection.stream().anyMatch((subdependency) -> {
         return containsReverseDependency(dependencies, key, subdependency);
      });
   }

   private static void addDependency(Multimap dependencies, Object key, Object dependency) {
      if (!containsReverseDependency(dependencies, key, dependency)) {
         dependencies.put(key, dependency);
      }

   }

   public void traverse(BiConsumer callback) {
      Multimap multimap = HashMultimap.create();
      this.underlying.forEach((key, value) -> {
         value.forDependencies((dependency) -> {
            addDependency(multimap, key, dependency);
         });
      });
      this.underlying.forEach((key, value) -> {
         value.forOptionalDependencies((dependency) -> {
            addDependency(multimap, key, dependency);
         });
      });
      Set set = new HashSet();
      this.underlying.keySet().forEach((key) -> {
         this.traverse(multimap, set, key, callback);
      });
   }

   public interface Dependencies {
      void forDependencies(Consumer callback);

      void forOptionalDependencies(Consumer callback);
   }
}
