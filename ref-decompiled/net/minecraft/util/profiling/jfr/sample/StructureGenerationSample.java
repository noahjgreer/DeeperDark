package net.minecraft.util.profiling.jfr.sample;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.util.math.ChunkPos;

public record StructureGenerationSample(Duration duration, ChunkPos chunkPos, String structureName, String level, boolean success) implements LongRunningSample {
   public StructureGenerationSample(Duration duration, ChunkPos chunkPos, String string, String string2, boolean bl) {
      this.duration = duration;
      this.chunkPos = chunkPos;
      this.structureName = string;
      this.level = string2;
      this.success = bl;
   }

   public static StructureGenerationSample fromEvent(RecordedEvent event) {
      return new StructureGenerationSample(event.getDuration(), new ChunkPos(event.getInt("chunkPosX"), event.getInt("chunkPosX")), event.getString("structure"), event.getString("level"), event.getBoolean("success"));
   }

   public Duration duration() {
      return this.duration;
   }

   public ChunkPos chunkPos() {
      return this.chunkPos;
   }

   public String structureName() {
      return this.structureName;
   }

   public String level() {
      return this.level;
   }

   public boolean success() {
      return this.success;
   }
}
