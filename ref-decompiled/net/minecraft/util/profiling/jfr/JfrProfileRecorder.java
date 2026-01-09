package net.minecraft.util.profiling.jfr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.ChunkRegionSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.util.profiling.jfr.sample.PacketSample;
import net.minecraft.util.profiling.jfr.sample.ServerTickTimeSample;
import net.minecraft.util.profiling.jfr.sample.StructureGenerationSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import org.jetbrains.annotations.Nullable;

public class JfrProfileRecorder {
   private Instant startTime;
   private Instant endTime;
   private final List chunkGenerationSamples;
   private final List structureGenerationSamples;
   private final List cpuLoadSamples;
   private final Map receivedPacketsToCounter;
   private final Map sentPacketsToCounter;
   private final Map chunkRegionReadCounter;
   private final Map chunkRegionWriteCounter;
   private final List fileWriteSamples;
   private final List fileReadSamples;
   private int gcCount;
   private Duration gcDuration;
   private final List gcHeapSummarySamples;
   private final List threadAllocationStatisticsSamples;
   private final List serverTickTimeSamples;
   @Nullable
   private Duration worldGenDuration;

   private JfrProfileRecorder(Stream events) {
      this.startTime = Instant.EPOCH;
      this.endTime = Instant.EPOCH;
      this.chunkGenerationSamples = Lists.newArrayList();
      this.structureGenerationSamples = Lists.newArrayList();
      this.cpuLoadSamples = Lists.newArrayList();
      this.receivedPacketsToCounter = Maps.newHashMap();
      this.sentPacketsToCounter = Maps.newHashMap();
      this.chunkRegionReadCounter = Maps.newHashMap();
      this.chunkRegionWriteCounter = Maps.newHashMap();
      this.fileWriteSamples = Lists.newArrayList();
      this.fileReadSamples = Lists.newArrayList();
      this.gcDuration = Duration.ZERO;
      this.gcHeapSummarySamples = Lists.newArrayList();
      this.threadAllocationStatisticsSamples = Lists.newArrayList();
      this.serverTickTimeSamples = Lists.newArrayList();
      this.worldGenDuration = null;
      this.handleEvents(events);
   }

   public static JfrProfile readProfile(Path path) {
      try {
         final RecordingFile recordingFile = new RecordingFile(path);

         JfrProfile var4;
         try {
            Iterator iterator = new Iterator() {
               public boolean hasNext() {
                  return recordingFile.hasMoreEvents();
               }

               public RecordedEvent next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     try {
                        return recordingFile.readEvent();
                     } catch (IOException var2) {
                        throw new UncheckedIOException(var2);
                     }
                  }
               }

               // $FF: synthetic method
               public Object next() {
                  return this.next();
               }
            };
            Stream stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 1297), false);
            var4 = (new JfrProfileRecorder(stream)).createProfile();
         } catch (Throwable var6) {
            try {
               recordingFile.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         recordingFile.close();
         return var4;
      } catch (IOException var7) {
         throw new UncheckedIOException(var7);
      }
   }

   private JfrProfile createProfile() {
      Duration duration = Duration.between(this.startTime, this.endTime);
      return new JfrProfile(this.startTime, this.endTime, duration, this.worldGenDuration, this.serverTickTimeSamples, this.cpuLoadSamples, GcHeapSummarySample.toStatistics(duration, this.gcHeapSummarySamples, this.gcDuration, this.gcCount), ThreadAllocationStatisticsSample.toAllocationMap(this.threadAllocationStatisticsSamples), createNetworkIoStatistics(duration, this.receivedPacketsToCounter), createNetworkIoStatistics(duration, this.sentPacketsToCounter), createNetworkIoStatistics(duration, this.chunkRegionWriteCounter), createNetworkIoStatistics(duration, this.chunkRegionReadCounter), FileIoSample.toStatistics(duration, this.fileWriteSamples), FileIoSample.toStatistics(duration, this.fileReadSamples), this.chunkGenerationSamples, this.structureGenerationSamples);
   }

   private void handleEvents(Stream events) {
      events.forEach((event) -> {
         if (event.getEndTime().isAfter(this.endTime) || this.endTime.equals(Instant.EPOCH)) {
            this.endTime = event.getEndTime();
         }

         if (event.getStartTime().isBefore(this.startTime) || this.startTime.equals(Instant.EPOCH)) {
            this.startTime = event.getStartTime();
         }

         switch (event.getEventType().getName()) {
            case "minecraft.ChunkGeneration":
               this.chunkGenerationSamples.add(ChunkGenerationSample.fromEvent(event));
               break;
            case "minecraft.StructureGeneration":
               this.structureGenerationSamples.add(StructureGenerationSample.fromEvent(event));
               break;
            case "minecraft.LoadWorld":
               this.worldGenDuration = event.getDuration();
               break;
            case "minecraft.ServerTickTime":
               this.serverTickTimeSamples.add(ServerTickTimeSample.fromEvent(event));
               break;
            case "minecraft.PacketReceived":
               this.addPacket(event, event.getInt("bytes"), this.receivedPacketsToCounter);
               break;
            case "minecraft.PacketSent":
               this.addPacket(event, event.getInt("bytes"), this.sentPacketsToCounter);
               break;
            case "minecraft.ChunkRegionRead":
               this.addChunkRegionEvent(event, event.getInt("bytes"), this.chunkRegionReadCounter);
               break;
            case "minecraft.ChunkRegionWrite":
               this.addChunkRegionEvent(event, event.getInt("bytes"), this.chunkRegionWriteCounter);
               break;
            case "jdk.ThreadAllocationStatistics":
               this.threadAllocationStatisticsSamples.add(ThreadAllocationStatisticsSample.fromEvent(event));
               break;
            case "jdk.GCHeapSummary":
               this.gcHeapSummarySamples.add(GcHeapSummarySample.fromEvent(event));
               break;
            case "jdk.CPULoad":
               this.cpuLoadSamples.add(CpuLoadSample.fromEvent(event));
               break;
            case "jdk.FileWrite":
               this.addFileIoSample(event, this.fileWriteSamples, "bytesWritten");
               break;
            case "jdk.FileRead":
               this.addFileIoSample(event, this.fileReadSamples, "bytesRead");
               break;
            case "jdk.GarbageCollection":
               ++this.gcCount;
               this.gcDuration = this.gcDuration.plus(event.getDuration());
         }

      });
   }

   private void addPacket(RecordedEvent event, int bytes, Map packetsToCounter) {
      ((Counter)packetsToCounter.computeIfAbsent(PacketSample.fromEvent(event), (packet) -> {
         return new Counter();
      })).add(bytes);
   }

   private void addChunkRegionEvent(RecordedEvent event, int bytes, Map chunksToCounter) {
      ((Counter)chunksToCounter.computeIfAbsent(ChunkRegionSample.fromEvent(event), (chunk) -> {
         return new Counter();
      })).add(bytes);
   }

   private void addFileIoSample(RecordedEvent event, List samples, String bytesKey) {
      samples.add(new FileIoSample(event.getDuration(), event.getString("path"), event.getLong(bytesKey)));
   }

   private static NetworkIoStatistics createNetworkIoStatistics(Duration duration, Map packetsToCounter) {
      List list = packetsToCounter.entrySet().stream().map((entry) -> {
         return Pair.of(entry.getKey(), ((Counter)entry.getValue()).toStatistics());
      }).toList();
      return new NetworkIoStatistics(duration, list);
   }

   public static final class Counter {
      private long totalCount;
      private long totalBytes;

      public void add(int bytes) {
         this.totalBytes += (long)bytes;
         ++this.totalCount;
      }

      public NetworkIoStatistics.PacketStatistics toStatistics() {
         return new NetworkIoStatistics.PacketStatistics(this.totalCount, this.totalBytes);
      }
   }
}
