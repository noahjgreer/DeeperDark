package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.math.Quantiles;
import org.jetbrains.annotations.Nullable;

public record LongRunningSampleStatistics(LongRunningSample fastestSample, LongRunningSample slowestSample, @Nullable LongRunningSample secondSlowestSample, int count, Map quantiles, Duration totalDuration) {
   public LongRunningSampleStatistics(LongRunningSample longRunningSample, LongRunningSample longRunningSample2, @Nullable LongRunningSample longRunningSample3, int i, Map map, Duration duration) {
      this.fastestSample = longRunningSample;
      this.slowestSample = longRunningSample2;
      this.secondSlowestSample = longRunningSample3;
      this.count = i;
      this.quantiles = map;
      this.totalDuration = duration;
   }

   public static LongRunningSampleStatistics fromSamples(List samples) {
      if (samples.isEmpty()) {
         throw new IllegalArgumentException("No values");
      } else {
         List list = samples.stream().sorted(Comparator.comparing(LongRunningSample::duration)).toList();
         Duration duration = (Duration)list.stream().map(LongRunningSample::duration).reduce(Duration::plus).orElse(Duration.ZERO);
         LongRunningSample longRunningSample = (LongRunningSample)list.get(0);
         LongRunningSample longRunningSample2 = (LongRunningSample)list.get(list.size() - 1);
         LongRunningSample longRunningSample3 = list.size() > 1 ? (LongRunningSample)list.get(list.size() - 2) : null;
         int i = list.size();
         Map map = Quantiles.create(list.stream().mapToLong((sample) -> {
            return sample.duration().toNanos();
         }).toArray());
         return new LongRunningSampleStatistics(longRunningSample, longRunningSample2, longRunningSample3, i, map, duration);
      }
   }

   public LongRunningSample fastestSample() {
      return this.fastestSample;
   }

   public LongRunningSample slowestSample() {
      return this.slowestSample;
   }

   @Nullable
   public LongRunningSample secondSlowestSample() {
      return this.secondSlowestSample;
   }

   public int count() {
      return this.count;
   }

   public Map quantiles() {
      return this.quantiles;
   }

   public Duration totalDuration() {
      return this.totalDuration;
   }
}
