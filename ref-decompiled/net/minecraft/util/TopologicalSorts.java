package net.minecraft.util;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class TopologicalSorts {
   private TopologicalSorts() {
   }

   public static boolean sort(Map successors, Set visited, Set visiting, Consumer reversedOrderConsumer, Object now) {
      if (visited.contains(now)) {
         return false;
      } else if (visiting.contains(now)) {
         return true;
      } else {
         visiting.add(now);
         Iterator var5 = ((Set)successors.getOrDefault(now, ImmutableSet.of())).iterator();

         Object object;
         do {
            if (!var5.hasNext()) {
               visiting.remove(now);
               visited.add(now);
               reversedOrderConsumer.accept(now);
               return false;
            }

            object = var5.next();
         } while(!sort(successors, visited, visiting, reversedOrderConsumer, object));

         return true;
      }
   }
}
