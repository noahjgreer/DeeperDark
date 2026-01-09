package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class ChunkStatus {
   public static final int field_35470 = 8;
   private static final EnumSet WORLD_GEN_HEIGHTMAP_TYPES;
   public static final EnumSet NORMAL_HEIGHTMAP_TYPES;
   public static final ChunkStatus EMPTY;
   public static final ChunkStatus STRUCTURE_STARTS;
   public static final ChunkStatus STRUCTURE_REFERENCES;
   public static final ChunkStatus BIOMES;
   public static final ChunkStatus NOISE;
   public static final ChunkStatus SURFACE;
   public static final ChunkStatus CARVERS;
   public static final ChunkStatus FEATURES;
   public static final ChunkStatus INITIALIZE_LIGHT;
   public static final ChunkStatus LIGHT;
   public static final ChunkStatus SPAWN;
   public static final ChunkStatus FULL;
   public static final Codec CODEC;
   private final int index;
   private final ChunkStatus previous;
   private final ChunkType chunkType;
   private final EnumSet heightMapTypes;

   private static ChunkStatus register(String id, @Nullable ChunkStatus previous, EnumSet heightMapTypes, ChunkType chunkType) {
      return (ChunkStatus)Registry.register(Registries.CHUNK_STATUS, (String)id, new ChunkStatus(previous, heightMapTypes, chunkType));
   }

   public static List createOrderedList() {
      List list = Lists.newArrayList();

      ChunkStatus chunkStatus;
      for(chunkStatus = FULL; chunkStatus.getPrevious() != chunkStatus; chunkStatus = chunkStatus.getPrevious()) {
         list.add(chunkStatus);
      }

      list.add(chunkStatus);
      Collections.reverse(list);
      return list;
   }

   @VisibleForTesting
   protected ChunkStatus(@Nullable ChunkStatus previous, EnumSet heightMapTypes, ChunkType chunkType) {
      this.previous = previous == null ? this : previous;
      this.chunkType = chunkType;
      this.heightMapTypes = heightMapTypes;
      this.index = previous == null ? 0 : previous.getIndex() + 1;
   }

   public int getIndex() {
      return this.index;
   }

   public ChunkStatus getPrevious() {
      return this.previous;
   }

   public ChunkType getChunkType() {
      return this.chunkType;
   }

   public static ChunkStatus byId(String id) {
      return (ChunkStatus)Registries.CHUNK_STATUS.get(Identifier.tryParse(id));
   }

   public EnumSet getHeightmapTypes() {
      return this.heightMapTypes;
   }

   public boolean isAtLeast(ChunkStatus other) {
      return this.getIndex() >= other.getIndex();
   }

   public boolean isLaterThan(ChunkStatus other) {
      return this.getIndex() > other.getIndex();
   }

   public boolean isAtMost(ChunkStatus other) {
      return this.getIndex() <= other.getIndex();
   }

   public boolean isEarlierThan(ChunkStatus other) {
      return this.getIndex() < other.getIndex();
   }

   public static ChunkStatus max(ChunkStatus a, ChunkStatus b) {
      return a.isLaterThan(b) ? a : b;
   }

   public String toString() {
      return this.getId();
   }

   public String getId() {
      return Registries.CHUNK_STATUS.getId(this).toString();
   }

   static {
      WORLD_GEN_HEIGHTMAP_TYPES = EnumSet.of(Heightmap.Type.OCEAN_FLOOR_WG, Heightmap.Type.WORLD_SURFACE_WG);
      NORMAL_HEIGHTMAP_TYPES = EnumSet.of(Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE, Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      EMPTY = register("empty", (ChunkStatus)null, WORLD_GEN_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      STRUCTURE_STARTS = register("structure_starts", EMPTY, WORLD_GEN_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      STRUCTURE_REFERENCES = register("structure_references", STRUCTURE_STARTS, WORLD_GEN_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      BIOMES = register("biomes", STRUCTURE_REFERENCES, WORLD_GEN_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      NOISE = register("noise", BIOMES, WORLD_GEN_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      SURFACE = register("surface", NOISE, WORLD_GEN_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      CARVERS = register("carvers", SURFACE, NORMAL_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      FEATURES = register("features", CARVERS, NORMAL_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      INITIALIZE_LIGHT = register("initialize_light", FEATURES, NORMAL_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      LIGHT = register("light", INITIALIZE_LIGHT, NORMAL_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      SPAWN = register("spawn", LIGHT, NORMAL_HEIGHTMAP_TYPES, ChunkType.PROTOCHUNK);
      FULL = register("full", SPAWN, NORMAL_HEIGHTMAP_TYPES, ChunkType.LEVELCHUNK);
      CODEC = Registries.CHUNK_STATUS.getCodec();
   }
}
