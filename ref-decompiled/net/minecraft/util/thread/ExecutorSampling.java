package net.minecraft.util.thread;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import net.minecraft.util.profiler.Sampler;
import org.jetbrains.annotations.Nullable;

public class ExecutorSampling {
   public static final ExecutorSampling INSTANCE = new ExecutorSampling();
   private final WeakHashMap activeExecutors = new WeakHashMap();

   private ExecutorSampling() {
   }

   public void add(SampleableExecutor executor) {
      this.activeExecutors.put(executor, (Object)null);
   }

   public List createSamplers() {
      Map map = (Map)this.activeExecutors.keySet().stream().flatMap((executor) -> {
         return executor.createSamplers().stream();
      }).collect(Collectors.groupingBy(Sampler::getName));
      return mergeSimilarSamplers(map);
   }

   private static List mergeSimilarSamplers(Map samplers) {
      return (List)samplers.entrySet().stream().map((entry) -> {
         String string = (String)entry.getKey();
         List list = (List)entry.getValue();
         return (Sampler)(list.size() > 1 ? new MergedSampler(string, list) : (Sampler)list.get(0));
      }).collect(Collectors.toList());
   }

   static class MergedSampler extends Sampler {
      private final List delegates;

      MergedSampler(String id, List delegates) {
         super(id, ((Sampler)delegates.get(0)).getType(), () -> {
            return averageRetrievers(delegates);
         }, () -> {
            start(delegates);
         }, combineDeviationCheckers(delegates));
         this.delegates = delegates;
      }

      private static Sampler.DeviationChecker combineDeviationCheckers(List delegates) {
         return (value) -> {
            return delegates.stream().anyMatch((sampler) -> {
               return sampler.deviationChecker != null ? sampler.deviationChecker.check(value) : false;
            });
         };
      }

      private static void start(List samplers) {
         Iterator var1 = samplers.iterator();

         while(var1.hasNext()) {
            Sampler sampler = (Sampler)var1.next();
            sampler.start();
         }

      }

      private static double averageRetrievers(List samplers) {
         double d = 0.0;

         Sampler sampler;
         for(Iterator var3 = samplers.iterator(); var3.hasNext(); d += sampler.getRetriever().getAsDouble()) {
            sampler = (Sampler)var3.next();
         }

         return d / (double)samplers.size();
      }

      public boolean equals(@Nullable Object object) {
         if (this == object) {
            return true;
         } else if (object != null && this.getClass() == object.getClass()) {
            if (!super.equals(object)) {
               return false;
            } else {
               MergedSampler mergedSampler = (MergedSampler)object;
               return this.delegates.equals(mergedSampler.delegates);
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{super.hashCode(), this.delegates});
      }
   }
}
