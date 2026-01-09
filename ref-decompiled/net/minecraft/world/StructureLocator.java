package net.minecraft.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.scanner.NbtScanQuery;
import net.minecraft.nbt.scanner.SelectiveNbtCollector;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.storage.NbtScannable;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class StructureLocator {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int START_NOT_PRESENT_REFERENCE = -1;
   private final NbtScannable chunkIoWorker;
   private final DynamicRegistryManager registryManager;
   private final StructureTemplateManager structureTemplateManager;
   private final RegistryKey worldKey;
   private final ChunkGenerator chunkGenerator;
   private final NoiseConfig noiseConfig;
   private final HeightLimitView world;
   private final BiomeSource biomeSource;
   private final long seed;
   private final DataFixer dataFixer;
   private final Long2ObjectMap cachedStructuresByChunkPos = new Long2ObjectOpenHashMap();
   private final Map generationPossibilityByStructure = new HashMap();

   public StructureLocator(NbtScannable chunkIoWorker, DynamicRegistryManager registryManager, StructureTemplateManager structureTemplateManager, RegistryKey worldKey, ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, HeightLimitView world, BiomeSource biomeSource, long seed, DataFixer dataFixer) {
      this.chunkIoWorker = chunkIoWorker;
      this.registryManager = registryManager;
      this.structureTemplateManager = structureTemplateManager;
      this.worldKey = worldKey;
      this.chunkGenerator = chunkGenerator;
      this.noiseConfig = noiseConfig;
      this.world = world;
      this.biomeSource = biomeSource;
      this.seed = seed;
      this.dataFixer = dataFixer;
   }

   public StructurePresence getStructurePresence(ChunkPos pos, Structure type, StructurePlacement placement, boolean skipReferencedStructures) {
      long l = pos.toLong();
      Object2IntMap object2IntMap = (Object2IntMap)this.cachedStructuresByChunkPos.get(l);
      if (object2IntMap != null) {
         return this.getStructurePresence(object2IntMap, type, skipReferencedStructures);
      } else {
         StructurePresence structurePresence = this.getStructurePresence(pos, type, skipReferencedStructures, l);
         if (structurePresence != null) {
            return structurePresence;
         } else if (!placement.applyFrequencyReduction(pos.x, pos.z, this.seed)) {
            return StructurePresence.START_NOT_PRESENT;
         } else {
            boolean bl = ((Long2BooleanMap)this.generationPossibilityByStructure.computeIfAbsent(type, (structure2) -> {
               return new Long2BooleanOpenHashMap();
            })).computeIfAbsent(l, (chunkPos) -> {
               return this.isGenerationPossible(pos, type);
            });
            return !bl ? StructurePresence.START_NOT_PRESENT : StructurePresence.CHUNK_LOAD_NEEDED;
         }
      }
   }

   private boolean isGenerationPossible(ChunkPos pos, Structure structure) {
      DynamicRegistryManager var10003 = this.registryManager;
      ChunkGenerator var10004 = this.chunkGenerator;
      BiomeSource var10005 = this.biomeSource;
      NoiseConfig var10006 = this.noiseConfig;
      StructureTemplateManager var10007 = this.structureTemplateManager;
      long var10008 = this.seed;
      HeightLimitView var10010 = this.world;
      RegistryEntryList var10011 = structure.getValidBiomes();
      Objects.requireNonNull(var10011);
      return structure.getValidStructurePosition(new Structure.Context(var10003, var10004, var10005, var10006, var10007, var10008, pos, var10010, var10011::contains)).isPresent();
   }

   @Nullable
   private StructurePresence getStructurePresence(ChunkPos pos, Structure structure, boolean skipReferencedStructures, long posLong) {
      SelectiveNbtCollector selectiveNbtCollector = new SelectiveNbtCollector(new NbtScanQuery[]{new NbtScanQuery(NbtInt.TYPE, "DataVersion"), new NbtScanQuery("Level", "Structures", NbtCompound.TYPE, "Starts"), new NbtScanQuery("structures", NbtCompound.TYPE, "starts")});

      try {
         this.chunkIoWorker.scanChunk(pos, selectiveNbtCollector).join();
      } catch (Exception var13) {
         LOGGER.warn("Failed to read chunk {}", pos, var13);
         return StructurePresence.CHUNK_LOAD_NEEDED;
      }

      NbtElement nbtElement = selectiveNbtCollector.getRoot();
      if (!(nbtElement instanceof NbtCompound nbtCompound)) {
         return null;
      } else {
         int i = VersionedChunkStorage.getDataVersion(nbtCompound);
         if (i <= 1493) {
            return StructurePresence.CHUNK_LOAD_NEEDED;
         } else {
            VersionedChunkStorage.saveContextToNbt(nbtCompound, this.worldKey, this.chunkGenerator.getCodecKey());

            NbtCompound nbtCompound2;
            try {
               nbtCompound2 = DataFixTypes.CHUNK.update(this.dataFixer, nbtCompound, i);
            } catch (Exception var12) {
               LOGGER.warn("Failed to partially datafix chunk {}", pos, var12);
               return StructurePresence.CHUNK_LOAD_NEEDED;
            }

            Object2IntMap object2IntMap = this.collectStructuresAndReferences(nbtCompound2);
            if (object2IntMap == null) {
               return null;
            } else {
               this.cache(posLong, object2IntMap);
               return this.getStructurePresence(object2IntMap, structure, skipReferencedStructures);
            }
         }
      }
   }

   @Nullable
   private Object2IntMap collectStructuresAndReferences(NbtCompound nbt) {
      Optional optional = nbt.getCompound("structures").flatMap((nbtCompoundx) -> {
         return nbtCompoundx.getCompound("starts");
      });
      if (optional.isEmpty()) {
         return null;
      } else {
         NbtCompound nbtCompound = (NbtCompound)optional.get();
         if (nbtCompound.isEmpty()) {
            return Object2IntMaps.emptyMap();
         } else {
            Object2IntMap object2IntMap = new Object2IntOpenHashMap();
            Registry registry = this.registryManager.getOrThrow(RegistryKeys.STRUCTURE);
            nbtCompound.forEach((string, nbtElement) -> {
               Identifier identifier = Identifier.tryParse(string);
               if (identifier != null) {
                  Structure structure = (Structure)registry.get(identifier);
                  if (structure != null) {
                     nbtElement.asCompound().ifPresent((nbtCompound) -> {
                        String string = nbtCompound.getString("id", "");
                        if (!"INVALID".equals(string)) {
                           int i = nbtCompound.getInt("references", 0);
                           object2IntMap.put(structure, i);
                        }

                     });
                  }
               }
            });
            return object2IntMap;
         }
      }
   }

   private static Object2IntMap createMapIfEmpty(Object2IntMap map) {
      return map.isEmpty() ? Object2IntMaps.emptyMap() : map;
   }

   private StructurePresence getStructurePresence(Object2IntMap referencesByStructure, Structure structure, boolean skipReferencedStructures) {
      int i = referencesByStructure.getOrDefault(structure, -1);
      return i == -1 || skipReferencedStructures && i != 0 ? StructurePresence.START_NOT_PRESENT : StructurePresence.START_PRESENT;
   }

   public void cache(ChunkPos pos, Map structureStarts) {
      long l = pos.toLong();
      Object2IntMap object2IntMap = new Object2IntOpenHashMap();
      structureStarts.forEach((start, structureStart) -> {
         if (structureStart.hasChildren()) {
            object2IntMap.put(start, structureStart.getReferences());
         }

      });
      this.cache(l, object2IntMap);
   }

   private void cache(long pos, Object2IntMap referencesByStructure) {
      this.cachedStructuresByChunkPos.put(pos, createMapIfEmpty(referencesByStructure));
      this.generationPossibilityByStructure.values().forEach((generationPossibilityByChunkPos) -> {
         generationPossibilityByChunkPos.remove(pos);
      });
   }

   public void incrementReferences(ChunkPos pos, Structure structure) {
      this.cachedStructuresByChunkPos.compute(pos.toLong(), (posx, referencesByStructure) -> {
         if (referencesByStructure == null || ((Object2IntMap)referencesByStructure).isEmpty()) {
            referencesByStructure = new Object2IntOpenHashMap();
         }

         ((Object2IntMap)referencesByStructure).computeInt(structure, (feature, references) -> {
            return references == null ? 1 : references + 1;
         });
         return (Object2IntMap)referencesByStructure;
      });
   }
}
