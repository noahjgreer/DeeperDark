package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapSummarySample(Instant time, long heapUsed, SummaryType summaryType) {
   public GcHeapSummarySample(Instant instant, long l, SummaryType summaryType) {
      this.time = instant;
      this.heapUsed = l;
      this.summaryType = summaryType;
   }

   public static GcHeapSummarySample fromEvent(RecordedEvent event) {
      return new GcHeapSummarySample(event.getStartTime(), event.getLong("heapUsed"), event.getString("when").equalsIgnoreCase("before gc") ? GcHeapSummarySample.SummaryType.BEFORE_GC : GcHeapSummarySample.SummaryType.AFTER_GC);
   }

   public static Statistics toStatistics(Duration duration, List samples, Duration gcDuration, int count) {
      return new Statistics(duration, gcDuration, count, getAllocatedBytesPerSecond(samples));
   }

   private static double getAllocatedBytesPerSecond(List samples) {
      long l = 0L;
      Map map = (Map)samples.stream().collect(Collectors.groupingBy((gcHeapSummarySamplex) -> {
         return gcHeapSummarySamplex.summaryType;
      }));
      List list = (List)map.get(GcHeapSummarySample.SummaryType.BEFORE_GC);
      List list2 = (List)map.get(GcHeapSummarySample.SummaryType.AFTER_GC);

      for(int i = 1; i < list.size(); ++i) {
         GcHeapSummarySample gcHeapSummarySample = (GcHeapSummarySample)list.get(i);
         GcHeapSummarySample gcHeapSummarySample2 = (GcHeapSummarySample)list2.get(i - 1);
         l += gcHeapSummarySample.heapUsed - gcHeapSummarySample2.heapUsed;
      }

      Duration duration = Duration.between(((GcHeapSummarySample)samples.get(1)).time, ((GcHeapSummarySample)samples.get(samples.size() - 1)).time);
      return (double)l / (double)duration.getSeconds();
   }

   public Instant time() {
      return this.time;
   }

   public long heapUsed() {
      return this.heapUsed;
   }

   public SummaryType summaryType() {
      return this.summaryType;
   }

   static enum SummaryType {
      BEFORE_GC,
      AFTER_GC;

      // $FF: synthetic method
      private static SummaryType[] method_38044() {
         return new SummaryType[]{BEFORE_GC, AFTER_GC};
      }
   }

   public static record Statistics(Duration duration, Duration gcDuration, int count, double allocatedBytesPerSecond) {
      public Statistics(Duration duration, Duration duration2, int i, double d) {
         this.duration = duration;
         this.gcDuration = duration2;
         this.count = i;
         this.allocatedBytesPerSecond = d;
      }

      public float getGcDurationRatio() {
         return (float)this.gcDuration.toMillis() / (float)this.duration.toMillis();
      }

      public Duration duration() {
         return this.duration;
      }

      public Duration gcDuration() {
         return this.gcDuration;
      }

      public int count() {
         return this.count;
      }

      public double allocatedBytesPerSecond() {
         return this.allocatedBytesPerSecond;
      }
   }
}
