package net.minecraft.util.profiling.jfr;

import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public record JfrProfile(Instant startTime, Instant endTime, Duration duration, @Nullable Duration worldGenDuration, List serverTickTimeSamples, List cpuLoadSamples, GcHeapSummarySample.Statistics gcHeapSummaryStatistics, ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap, NetworkIoStatistics packetReadStatistics, NetworkIoStatistics packetSentStatistics, NetworkIoStatistics writtenChunks, NetworkIoStatistics readChunks, FileIoSample.Statistics fileWriteStatistics, FileIoSample.Statistics fileReadStatistics, List chunkGenerationSamples, List structureGenerationSamples) {
   public JfrProfile(Instant instant, Instant instant2, Duration duration, @Nullable Duration duration2, List list, List list2, GcHeapSummarySample.Statistics statistics, ThreadAllocationStatisticsSample.AllocationMap allocationMap, NetworkIoStatistics networkIoStatistics, NetworkIoStatistics networkIoStatistics2, NetworkIoStatistics networkIoStatistics3, NetworkIoStatistics networkIoStatistics4, FileIoSample.Statistics statistics2, FileIoSample.Statistics statistics3, List list3, List list4) {
      this.startTime = instant;
      this.endTime = instant2;
      this.duration = duration;
      this.worldGenDuration = duration2;
      this.serverTickTimeSamples = list;
      this.cpuLoadSamples = list2;
      this.gcHeapSummaryStatistics = statistics;
      this.threadAllocationMap = allocationMap;
      this.packetReadStatistics = networkIoStatistics;
      this.packetSentStatistics = networkIoStatistics2;
      this.writtenChunks = networkIoStatistics3;
      this.readChunks = networkIoStatistics4;
      this.fileWriteStatistics = statistics2;
      this.fileReadStatistics = statistics3;
      this.chunkGenerationSamples = list3;
      this.structureGenerationSamples = list4;
   }

   public List getChunkGenerationSampleStatistics() {
      Map map = (Map)this.chunkGenerationSamples.stream().collect(Collectors.groupingBy(ChunkGenerationSample::chunkStatus));
      return map.entrySet().stream().map((entry) -> {
         return Pair.of((ChunkStatus)entry.getKey(), LongRunningSampleStatistics.fromSamples((List)entry.getValue()));
      }).sorted(Comparator.comparing((pair) -> {
         return ((LongRunningSampleStatistics)pair.getSecond()).totalDuration();
      }).reversed()).toList();
   }

   public String toJson() {
      return (new JfrJsonReport()).toString(this);
   }

   public Instant startTime() {
      return this.startTime;
   }

   public Instant endTime() {
      return this.endTime;
   }

   public Duration duration() {
      return this.duration;
   }

   @Nullable
   public Duration worldGenDuration() {
      return this.worldGenDuration;
   }

   public List serverTickTimeSamples() {
      return this.serverTickTimeSamples;
   }

   public List cpuLoadSamples() {
      return this.cpuLoadSamples;
   }

   public GcHeapSummarySample.Statistics gcHeapSummaryStatistics() {
      return this.gcHeapSummaryStatistics;
   }

   public ThreadAllocationStatisticsSample.AllocationMap threadAllocationMap() {
      return this.threadAllocationMap;
   }

   public NetworkIoStatistics packetReadStatistics() {
      return this.packetReadStatistics;
   }

   public NetworkIoStatistics packetSentStatistics() {
      return this.packetSentStatistics;
   }

   public NetworkIoStatistics writtenChunks() {
      return this.writtenChunks;
   }

   public NetworkIoStatistics readChunks() {
      return this.readChunks;
   }

   public FileIoSample.Statistics fileWriteStatistics() {
      return this.fileWriteStatistics;
   }

   public FileIoSample.Statistics fileReadStatistics() {
      return this.fileReadStatistics;
   }

   public List chunkGenerationSamples() {
      return this.chunkGenerationSamples;
   }

   public List structureGenerationSamples() {
      return this.structureGenerationSamples;
   }
}
