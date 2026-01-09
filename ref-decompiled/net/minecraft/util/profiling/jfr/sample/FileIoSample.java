package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public record FileIoSample(Duration duration, @Nullable String path, long bytes) {
   public FileIoSample(Duration duration, @Nullable String string, long l) {
      this.duration = duration;
      this.path = string;
      this.bytes = l;
   }

   public static Statistics toStatistics(Duration duration, List samples) {
      long l = samples.stream().mapToLong((sample) -> {
         return sample.bytes;
      }).sum();
      return new Statistics(l, (double)l / (double)duration.getSeconds(), (long)samples.size(), (double)samples.size() / (double)duration.getSeconds(), (Duration)samples.stream().map(FileIoSample::duration).reduce(Duration.ZERO, Duration::plus), ((Map)samples.stream().filter((sample) -> {
         return sample.path != null;
      }).collect(Collectors.groupingBy((sample) -> {
         return sample.path;
      }, Collectors.summingLong((sample) -> {
         return sample.bytes;
      })))).entrySet().stream().sorted(Entry.comparingByValue().reversed()).map((entry) -> {
         return Pair.of((String)entry.getKey(), (Long)entry.getValue());
      }).limit(10L).toList());
   }

   public Duration duration() {
      return this.duration;
   }

   @Nullable
   public String path() {
      return this.path;
   }

   public long bytes() {
      return this.bytes;
   }

   public static record Statistics(long totalBytes, double bytesPerSecond, long count, double countPerSecond, Duration totalDuration, List topContributors) {
      public Statistics(long l, double d, long m, double e, Duration duration, List list) {
         this.totalBytes = l;
         this.bytesPerSecond = d;
         this.count = m;
         this.countPerSecond = e;
         this.totalDuration = duration;
         this.topContributors = list;
      }

      public long totalBytes() {
         return this.totalBytes;
      }

      public double bytesPerSecond() {
         return this.bytesPerSecond;
      }

      public long count() {
         return this.count;
      }

      public double countPerSecond() {
         return this.countPerSecond;
      }

      public Duration totalDuration() {
         return this.totalDuration;
      }

      public List topContributors() {
         return this.topContributors;
      }
   }
}
