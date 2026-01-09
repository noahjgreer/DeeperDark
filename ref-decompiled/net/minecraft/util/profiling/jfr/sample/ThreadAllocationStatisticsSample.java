package net.minecraft.util.profiling.jfr.sample;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

public record ThreadAllocationStatisticsSample(Instant time, String threadName, long allocated) {
   private static final String UNKNOWN = "unknown";

   public ThreadAllocationStatisticsSample(Instant instant, String string, long l) {
      this.time = instant;
      this.threadName = string;
      this.allocated = l;
   }

   public static ThreadAllocationStatisticsSample fromEvent(RecordedEvent event) {
      RecordedThread recordedThread = event.getThread("thread");
      String string = recordedThread == null ? "unknown" : (String)MoreObjects.firstNonNull(recordedThread.getJavaName(), "unknown");
      return new ThreadAllocationStatisticsSample(event.getStartTime(), string, event.getLong("allocated"));
   }

   public static AllocationMap toAllocationMap(List samples) {
      Map map = new TreeMap();
      Map map2 = (Map)samples.stream().collect(Collectors.groupingBy((sample) -> {
         return sample.threadName;
      }));
      map2.forEach((threadName, groupedSamples) -> {
         if (groupedSamples.size() >= 2) {
            ThreadAllocationStatisticsSample threadAllocationStatisticsSample = (ThreadAllocationStatisticsSample)groupedSamples.get(0);
            ThreadAllocationStatisticsSample threadAllocationStatisticsSample2 = (ThreadAllocationStatisticsSample)groupedSamples.get(groupedSamples.size() - 1);
            long l = Duration.between(threadAllocationStatisticsSample.time, threadAllocationStatisticsSample2.time).getSeconds();
            long m = threadAllocationStatisticsSample2.allocated - threadAllocationStatisticsSample.allocated;
            map.put(threadName, (double)m / (double)l);
         }
      });
      return new AllocationMap(map);
   }

   public Instant time() {
      return this.time;
   }

   public String threadName() {
      return this.threadName;
   }

   public long allocated() {
      return this.allocated;
   }

   public static record AllocationMap(Map allocations) {
      public AllocationMap(Map map) {
         this.allocations = map;
      }

      public Map allocations() {
         return this.allocations;
      }
   }
}
