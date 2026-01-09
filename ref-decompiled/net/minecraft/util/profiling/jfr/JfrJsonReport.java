package net.minecraft.util.profiling.jfr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.LongSerializationPolicy;
import com.mojang.datafixers.util.Pair;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quantiles;
import net.minecraft.util.profiling.jfr.sample.ChunkGenerationSample;
import net.minecraft.util.profiling.jfr.sample.ChunkRegionSample;
import net.minecraft.util.profiling.jfr.sample.CpuLoadSample;
import net.minecraft.util.profiling.jfr.sample.FileIoSample;
import net.minecraft.util.profiling.jfr.sample.GcHeapSummarySample;
import net.minecraft.util.profiling.jfr.sample.LongRunningSampleStatistics;
import net.minecraft.util.profiling.jfr.sample.NetworkIoStatistics;
import net.minecraft.util.profiling.jfr.sample.PacketSample;
import net.minecraft.util.profiling.jfr.sample.StructureGenerationSample;
import net.minecraft.util.profiling.jfr.sample.ThreadAllocationStatisticsSample;
import net.minecraft.world.chunk.ChunkStatus;

public class JfrJsonReport {
   private static final String BYTES_PER_SECOND = "bytesPerSecond";
   private static final String COUNT = "count";
   private static final String DURATION_NANOS_TOTAL = "durationNanosTotal";
   private static final String TOTAL_BYTES = "totalBytes";
   private static final String COUNT_PER_SECOND = "countPerSecond";
   final Gson gson;

   public JfrJsonReport() {
      this.gson = (new GsonBuilder()).setPrettyPrinting().setLongSerializationPolicy(LongSerializationPolicy.DEFAULT).create();
   }

   private static void addPacketData(PacketSample packet, JsonObject json) {
      json.addProperty("protocolId", packet.protocolId());
      json.addProperty("packetId", packet.packetId());
   }

   private static void addChunkData(ChunkRegionSample chunk, JsonObject json) {
      json.addProperty("level", chunk.level());
      json.addProperty("dimension", chunk.dimension());
      json.addProperty("x", chunk.x());
      json.addProperty("z", chunk.z());
   }

   public String toString(JfrProfile profile) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("startedEpoch", profile.startTime().toEpochMilli());
      jsonObject.addProperty("endedEpoch", profile.endTime().toEpochMilli());
      jsonObject.addProperty("durationMs", profile.duration().toMillis());
      Duration duration = profile.worldGenDuration();
      if (duration != null) {
         jsonObject.addProperty("worldGenDurationMs", duration.toMillis());
      }

      jsonObject.add("heap", this.collectHeapSection(profile.gcHeapSummaryStatistics()));
      jsonObject.add("cpuPercent", this.collectCpuPercentSection(profile.cpuLoadSamples()));
      jsonObject.add("network", this.collectNetworkSection(profile));
      jsonObject.add("fileIO", this.collectFileIoSection(profile));
      jsonObject.add("serverTick", this.collectServerTickSection(profile.serverTickTimeSamples()));
      jsonObject.add("threadAllocation", this.collectThreadAllocationSection(profile.threadAllocationMap()));
      jsonObject.add("chunkGen", this.collectChunkGenSection(profile.getChunkGenerationSampleStatistics()));
      jsonObject.add("structureGen", this.collectStructureGenSection(profile.structureGenerationSamples()));
      return this.gson.toJson(jsonObject);
   }

   private JsonElement collectHeapSection(GcHeapSummarySample.Statistics statistics) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("allocationRateBytesPerSecond", statistics.allocatedBytesPerSecond());
      jsonObject.addProperty("gcCount", statistics.count());
      jsonObject.addProperty("gcOverHeadPercent", statistics.getGcDurationRatio());
      jsonObject.addProperty("gcTotalDurationMs", statistics.gcDuration().toMillis());
      return jsonObject;
   }

   private JsonElement collectStructureGenSection(List structureGenerationSamples) {
      JsonObject jsonObject = new JsonObject();
      LongRunningSampleStatistics longRunningSampleStatistics = LongRunningSampleStatistics.fromSamples(structureGenerationSamples);
      JsonArray jsonArray = new JsonArray();
      jsonObject.add("structure", jsonArray);
      ((Map)structureGenerationSamples.stream().collect(Collectors.groupingBy(StructureGenerationSample::structureName))).forEach((name, samples) -> {
         JsonObject jsonObject2 = new JsonObject();
         jsonArray.add(jsonObject2);
         jsonObject2.addProperty("name", name);
         LongRunningSampleStatistics longRunningSampleStatistics2 = LongRunningSampleStatistics.fromSamples(samples);
         jsonObject2.addProperty("count", longRunningSampleStatistics2.count());
         jsonObject2.addProperty("durationNanosTotal", longRunningSampleStatistics2.totalDuration().toNanos());
         jsonObject2.addProperty("durationNanosAvg", longRunningSampleStatistics2.totalDuration().toNanos() / (long)longRunningSampleStatistics2.count());
         JsonObject jsonObject3 = (JsonObject)Util.make(new JsonObject(), (jsonObject2x) -> {
            jsonObject2.add("durationNanosPercentiles", jsonObject2x);
         });
         longRunningSampleStatistics2.quantiles().forEach((integer, double_) -> {
            jsonObject3.addProperty("p" + integer, double_);
         });
         Function function = (sample) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("durationNanos", sample.duration().toNanos());
            jsonObject.addProperty("chunkPosX", sample.chunkPos().x);
            jsonObject.addProperty("chunkPosZ", sample.chunkPos().z);
            jsonObject.addProperty("structureName", sample.structureName());
            jsonObject.addProperty("level", sample.level());
            jsonObject.addProperty("success", sample.success());
            return jsonObject;
         };
         jsonObject.add("fastest", (JsonElement)function.apply((StructureGenerationSample)longRunningSampleStatistics.fastestSample()));
         jsonObject.add("slowest", (JsonElement)function.apply((StructureGenerationSample)longRunningSampleStatistics.slowestSample()));
         jsonObject.add("secondSlowest", (JsonElement)(longRunningSampleStatistics.secondSlowestSample() != null ? (JsonElement)function.apply((StructureGenerationSample)longRunningSampleStatistics.secondSlowestSample()) : JsonNull.INSTANCE));
      });
      return jsonObject;
   }

   private JsonElement collectChunkGenSection(List statistics) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("durationNanosTotal", statistics.stream().mapToDouble((pairx) -> {
         return (double)((LongRunningSampleStatistics)pairx.getSecond()).totalDuration().toNanos();
      }).sum());
      JsonArray jsonArray = (JsonArray)Util.make(new JsonArray(), (json) -> {
         jsonObject.add("status", json);
      });
      Iterator var4 = statistics.iterator();

      while(var4.hasNext()) {
         Pair pair = (Pair)var4.next();
         LongRunningSampleStatistics longRunningSampleStatistics = (LongRunningSampleStatistics)pair.getSecond();
         JsonObject var10000 = new JsonObject();
         Objects.requireNonNull(jsonArray);
         JsonObject jsonObject2 = (JsonObject)Util.make(var10000, jsonArray::add);
         jsonObject2.addProperty("state", ((ChunkStatus)pair.getFirst()).toString());
         jsonObject2.addProperty("count", longRunningSampleStatistics.count());
         jsonObject2.addProperty("durationNanosTotal", longRunningSampleStatistics.totalDuration().toNanos());
         jsonObject2.addProperty("durationNanosAvg", longRunningSampleStatistics.totalDuration().toNanos() / (long)longRunningSampleStatistics.count());
         JsonObject jsonObject3 = (JsonObject)Util.make(new JsonObject(), (json) -> {
            jsonObject2.add("durationNanosPercentiles", json);
         });
         longRunningSampleStatistics.quantiles().forEach((quantile, value) -> {
            jsonObject3.addProperty("p" + quantile, value);
         });
         Function function = (sample) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("durationNanos", sample.duration().toNanos());
            jsonObject.addProperty("level", sample.worldKey());
            jsonObject.addProperty("chunkPosX", sample.chunkPos().x);
            jsonObject.addProperty("chunkPosZ", sample.chunkPos().z);
            jsonObject.addProperty("worldPosX", sample.centerPos().x());
            jsonObject.addProperty("worldPosZ", sample.centerPos().z());
            return jsonObject;
         };
         jsonObject2.add("fastest", (JsonElement)function.apply((ChunkGenerationSample)longRunningSampleStatistics.fastestSample()));
         jsonObject2.add("slowest", (JsonElement)function.apply((ChunkGenerationSample)longRunningSampleStatistics.slowestSample()));
         jsonObject2.add("secondSlowest", (JsonElement)(longRunningSampleStatistics.secondSlowestSample() != null ? (JsonElement)function.apply((ChunkGenerationSample)longRunningSampleStatistics.secondSlowestSample()) : JsonNull.INSTANCE));
      }

      return jsonObject;
   }

   private JsonElement collectThreadAllocationSection(ThreadAllocationStatisticsSample.AllocationMap statistics) {
      JsonArray jsonArray = new JsonArray();
      statistics.allocations().forEach((threadName, allocation) -> {
         jsonArray.add((JsonElement)Util.make(new JsonObject(), (json) -> {
            json.addProperty("thread", threadName);
            json.addProperty("bytesPerSecond", allocation);
         }));
      });
      return jsonArray;
   }

   private JsonElement collectServerTickSection(List samples) {
      if (samples.isEmpty()) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonObject = new JsonObject();
         double[] ds = samples.stream().mapToDouble((sample) -> {
            return (double)sample.averageTickMs().toNanos() / 1000000.0;
         }).toArray();
         DoubleSummaryStatistics doubleSummaryStatistics = DoubleStream.of(ds).summaryStatistics();
         jsonObject.addProperty("minMs", doubleSummaryStatistics.getMin());
         jsonObject.addProperty("averageMs", doubleSummaryStatistics.getAverage());
         jsonObject.addProperty("maxMs", doubleSummaryStatistics.getMax());
         Map map = Quantiles.create(ds);
         map.forEach((quantile, value) -> {
            jsonObject.addProperty("p" + quantile, value);
         });
         return jsonObject;
      }
   }

   private JsonElement collectFileIoSection(JfrProfile profile) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("write", this.collectFileIoSection(profile.fileWriteStatistics()));
      jsonObject.add("read", this.collectFileIoSection(profile.fileReadStatistics()));
      jsonObject.add("chunksRead", this.collectPacketSection(profile.readChunks(), JfrJsonReport::addChunkData));
      jsonObject.add("chunksWritten", this.collectPacketSection(profile.writtenChunks(), JfrJsonReport::addChunkData));
      return jsonObject;
   }

   private JsonElement collectFileIoSection(FileIoSample.Statistics statistics) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("totalBytes", statistics.totalBytes());
      jsonObject.addProperty("count", statistics.count());
      jsonObject.addProperty("bytesPerSecond", statistics.bytesPerSecond());
      jsonObject.addProperty("countPerSecond", statistics.countPerSecond());
      JsonArray jsonArray = new JsonArray();
      jsonObject.add("topContributors", jsonArray);
      statistics.topContributors().forEach((pair) -> {
         JsonObject jsonObject = new JsonObject();
         jsonArray.add(jsonObject);
         jsonObject.addProperty("path", (String)pair.getFirst());
         jsonObject.addProperty("totalBytes", (Number)pair.getSecond());
      });
      return jsonObject;
   }

   private JsonElement collectNetworkSection(JfrProfile profile) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("sent", this.collectPacketSection(profile.packetSentStatistics(), JfrJsonReport::addPacketData));
      jsonObject.add("received", this.collectPacketSection(profile.packetReadStatistics(), JfrJsonReport::addPacketData));
      return jsonObject;
   }

   private JsonElement collectPacketSection(NetworkIoStatistics statistics, BiConsumer callback) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("totalBytes", statistics.getTotalSize());
      jsonObject.addProperty("count", statistics.getTotalCount());
      jsonObject.addProperty("bytesPerSecond", statistics.getBytesPerSecond());
      jsonObject.addProperty("countPerSecond", statistics.getCountPerSecond());
      JsonArray jsonArray = new JsonArray();
      jsonObject.add("topContributors", jsonArray);
      statistics.getTopContributors().forEach((topContributor) -> {
         JsonObject jsonObject = new JsonObject();
         jsonArray.add(jsonObject);
         Object object = topContributor.getFirst();
         NetworkIoStatistics.PacketStatistics packetStatistics = (NetworkIoStatistics.PacketStatistics)topContributor.getSecond();
         callback.accept(object, jsonObject);
         jsonObject.addProperty("totalBytes", packetStatistics.totalSize());
         jsonObject.addProperty("count", packetStatistics.totalCount());
         jsonObject.addProperty("averageSize", packetStatistics.getAverageSize());
      });
      return jsonObject;
   }

   private JsonElement collectCpuPercentSection(List samples) {
      JsonObject jsonObject = new JsonObject();
      BiFunction biFunction = (samplesx, valueGetter) -> {
         JsonObject jsonObject = new JsonObject();
         DoubleSummaryStatistics doubleSummaryStatistics = samplesx.stream().mapToDouble(valueGetter).summaryStatistics();
         jsonObject.addProperty("min", doubleSummaryStatistics.getMin());
         jsonObject.addProperty("average", doubleSummaryStatistics.getAverage());
         jsonObject.addProperty("max", doubleSummaryStatistics.getMax());
         return jsonObject;
      };
      jsonObject.add("jvm", (JsonElement)biFunction.apply(samples, CpuLoadSample::jvm));
      jsonObject.add("userJvm", (JsonElement)biFunction.apply(samples, CpuLoadSample::userJvm));
      jsonObject.add("system", (JsonElement)biFunction.apply(samples, CpuLoadSample::system));
      return jsonObject;
   }
}
