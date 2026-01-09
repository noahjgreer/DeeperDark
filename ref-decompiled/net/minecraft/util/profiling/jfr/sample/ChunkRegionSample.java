package net.minecraft.util.profiling.jfr.sample;

import jdk.jfr.consumer.RecordedEvent;

public record ChunkRegionSample(String level, String dimension, int x, int z) {
   public ChunkRegionSample(String string, String string2, int i, int j) {
      this.level = string;
      this.dimension = string2;
      this.x = i;
      this.z = j;
   }

   public static ChunkRegionSample fromEvent(RecordedEvent event) {
      return new ChunkRegionSample(event.getString("level"), event.getString("dimension"), event.getInt("chunkPosX"), event.getInt("chunkPosZ"));
   }

   public String level() {
      return this.level;
   }

   public String dimension() {
      return this.dimension;
   }

   public int x() {
      return this.x;
   }

   public int z() {
      return this.z;
   }
}
