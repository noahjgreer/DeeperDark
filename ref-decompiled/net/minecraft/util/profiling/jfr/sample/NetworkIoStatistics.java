package net.minecraft.util.profiling.jfr.sample;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;

public final class NetworkIoStatistics {
   private final PacketStatistics combinedStatistics;
   private final List topContributors;
   private final Duration duration;

   public NetworkIoStatistics(Duration duration, List packetsToStatistics) {
      this.duration = duration;
      this.combinedStatistics = (PacketStatistics)packetsToStatistics.stream().map(Pair::getSecond).reduce(new PacketStatistics(0L, 0L), PacketStatistics::add);
      this.topContributors = packetsToStatistics.stream().sorted(Comparator.comparing(Pair::getSecond, NetworkIoStatistics.PacketStatistics.COMPARATOR)).limit(10L).toList();
   }

   public double getCountPerSecond() {
      return (double)this.combinedStatistics.totalCount / (double)this.duration.getSeconds();
   }

   public double getBytesPerSecond() {
      return (double)this.combinedStatistics.totalSize / (double)this.duration.getSeconds();
   }

   public long getTotalCount() {
      return this.combinedStatistics.totalCount;
   }

   public long getTotalSize() {
      return this.combinedStatistics.totalSize;
   }

   public List getTopContributors() {
      return this.topContributors;
   }

   public static record PacketStatistics(long totalCount, long totalSize) {
      final long totalCount;
      final long totalSize;
      static final Comparator COMPARATOR = Comparator.comparing(PacketStatistics::totalSize).thenComparing(PacketStatistics::totalCount).reversed();

      public PacketStatistics(long l, long m) {
         this.totalCount = l;
         this.totalSize = m;
      }

      PacketStatistics add(PacketStatistics statistics) {
         return new PacketStatistics(this.totalCount + statistics.totalCount, this.totalSize + statistics.totalSize);
      }

      public float getAverageSize() {
         return (float)this.totalSize / (float)this.totalCount;
      }

      public long totalCount() {
         return this.totalCount;
      }

      public long totalSize() {
         return this.totalSize;
      }
   }
}
