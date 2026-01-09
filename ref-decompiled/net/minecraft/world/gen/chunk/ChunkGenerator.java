package net.minecraft.world.gen.chunk;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructurePresence;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ReadableContainer;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.PlacedFeatureIndexer;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkGenerator {
   public static final Codec CODEC;
   protected final BiomeSource biomeSource;
   private final Supplier indexedFeaturesListSupplier;
   private final Function generationSettingsGetter;

   public ChunkGenerator(BiomeSource biomeSource) {
      this(biomeSource, (biomeEntry) -> {
         return ((Biome)biomeEntry.value()).getGenerationSettings();
      });
   }

   public ChunkGenerator(BiomeSource biomeSource, Function generationSettingsGetter) {
      this.biomeSource = biomeSource;
      this.generationSettingsGetter = generationSettingsGetter;
      this.indexedFeaturesListSupplier = Suppliers.memoize(() -> {
         return PlacedFeatureIndexer.collectIndexedFeatures(List.copyOf(biomeSource.getBiomes()), (biomeEntry) -> {
            return ((GenerationSettings)generationSettingsGetter.apply(biomeEntry)).getFeatures();
         }, true);
      });
   }

   public void initializeIndexedFeaturesList() {
      this.indexedFeaturesListSupplier.get();
   }

   protected abstract MapCodec getCodec();

   public StructurePlacementCalculator createStructurePlacementCalculator(RegistryWrapper structureSetRegistry, NoiseConfig noiseConfig, long seed) {
      return StructurePlacementCalculator.create(noiseConfig, seed, this.biomeSource, structureSetRegistry);
   }

   public Optional getCodecKey() {
      return Registries.CHUNK_GENERATOR.getKey(this.getCodec());
   }

   public CompletableFuture populateBiomes(NoiseConfig noiseConfig, Blender blender, StructureAccessor structureAccessor, Chunk chunk) {
      return CompletableFuture.supplyAsync(() -> {
         chunk.populateBiomes(this.biomeSource, noiseConfig.getMultiNoiseSampler());
         return chunk;
      }, Util.getMainWorkerExecutor().named("init_biomes"));
   }

   public abstract void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk);

   @Nullable
   public Pair locateStructure(ServerWorld world, RegistryEntryList structures, BlockPos center, int radius, boolean skipReferencedStructures) {
      StructurePlacementCalculator structurePlacementCalculator = world.getChunkManager().getStructurePlacementCalculator();
      Map map = new Object2ObjectArrayMap();
      Iterator var8 = structures.iterator();

      while(var8.hasNext()) {
         RegistryEntry registryEntry = (RegistryEntry)var8.next();
         Iterator var10 = structurePlacementCalculator.getPlacements(registryEntry).iterator();

         while(var10.hasNext()) {
            StructurePlacement structurePlacement = (StructurePlacement)var10.next();
            ((Set)map.computeIfAbsent(structurePlacement, (placement) -> {
               return new ObjectArraySet();
            })).add(registryEntry);
         }
      }

      if (map.isEmpty()) {
         return null;
      } else {
         Pair pair = null;
         double d = Double.MAX_VALUE;
         StructureAccessor structureAccessor = world.getStructureAccessor();
         List list = new ArrayList(map.size());
         Iterator var13 = map.entrySet().iterator();

         while(var13.hasNext()) {
            Map.Entry entry = (Map.Entry)var13.next();
            StructurePlacement structurePlacement2 = (StructurePlacement)entry.getKey();
            if (structurePlacement2 instanceof ConcentricRingsStructurePlacement) {
               ConcentricRingsStructurePlacement concentricRingsStructurePlacement = (ConcentricRingsStructurePlacement)structurePlacement2;
               Pair pair2 = this.locateConcentricRingsStructure((Set)entry.getValue(), world, structureAccessor, center, skipReferencedStructures, concentricRingsStructurePlacement);
               if (pair2 != null) {
                  BlockPos blockPos = (BlockPos)pair2.getFirst();
                  double e = center.getSquaredDistance(blockPos);
                  if (e < d) {
                     d = e;
                     pair = pair2;
                  }
               }
            } else if (structurePlacement2 instanceof RandomSpreadStructurePlacement) {
               list.add(entry);
            }
         }

         if (!list.isEmpty()) {
            int i = ChunkSectionPos.getSectionCoord(center.getX());
            int j = ChunkSectionPos.getSectionCoord(center.getZ());

            for(int k = 0; k <= radius; ++k) {
               boolean bl = false;
               Iterator var30 = list.iterator();

               while(var30.hasNext()) {
                  Map.Entry entry2 = (Map.Entry)var30.next();
                  RandomSpreadStructurePlacement randomSpreadStructurePlacement = (RandomSpreadStructurePlacement)entry2.getKey();
                  Pair pair3 = locateRandomSpreadStructure((Set)entry2.getValue(), world, structureAccessor, i, j, k, skipReferencedStructures, structurePlacementCalculator.getStructureSeed(), randomSpreadStructurePlacement);
                  if (pair3 != null) {
                     bl = true;
                     double f = center.getSquaredDistance((Vec3i)pair3.getFirst());
                     if (f < d) {
                        d = f;
                        pair = pair3;
                     }
                  }
               }

               if (bl) {
                  return pair;
               }
            }
         }

         return pair;
      }
   }

   @Nullable
   private Pair locateConcentricRingsStructure(Set structures, ServerWorld world, StructureAccessor structureAccessor, BlockPos center, boolean skipReferencedStructures, ConcentricRingsStructurePlacement placement) {
      List list = world.getChunkManager().getStructurePlacementCalculator().getPlacementPositions(placement);
      if (list == null) {
         throw new IllegalStateException("Somehow tried to find structures for a placement that doesn't exist");
      } else {
         Pair pair = null;
         double d = Double.MAX_VALUE;
         BlockPos.Mutable mutable = new BlockPos.Mutable();
         Iterator var12 = list.iterator();

         while(var12.hasNext()) {
            ChunkPos chunkPos = (ChunkPos)var12.next();
            mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
            double e = mutable.getSquaredDistance(center);
            boolean bl = pair == null || e < d;
            if (bl) {
               Pair pair2 = locateStructure(structures, world, structureAccessor, skipReferencedStructures, placement, chunkPos);
               if (pair2 != null) {
                  pair = pair2;
                  d = e;
               }
            }
         }

         return pair;
      }
   }

   @Nullable
   private static Pair locateRandomSpreadStructure(Set structures, WorldView world, StructureAccessor structureAccessor, int centerChunkX, int centerChunkZ, int radius, boolean skipReferencedStructures, long seed, RandomSpreadStructurePlacement placement) {
      int i = placement.getSpacing();

      for(int j = -radius; j <= radius; ++j) {
         boolean bl = j == -radius || j == radius;

         for(int k = -radius; k <= radius; ++k) {
            boolean bl2 = k == -radius || k == radius;
            if (bl || bl2) {
               int l = centerChunkX + i * j;
               int m = centerChunkZ + i * k;
               ChunkPos chunkPos = placement.getStartChunk(seed, l, m);
               Pair pair = locateStructure(structures, world, structureAccessor, skipReferencedStructures, placement, chunkPos);
               if (pair != null) {
                  return pair;
               }
            }
         }
      }

      return null;
   }

   @Nullable
   private static Pair locateStructure(Set structures, WorldView world, StructureAccessor structureAccessor, boolean skipReferencedStructures, StructurePlacement placement, ChunkPos pos) {
      Iterator var6 = structures.iterator();

      RegistryEntry registryEntry;
      StructureStart structureStart;
      do {
         do {
            do {
               StructurePresence structurePresence;
               do {
                  if (!var6.hasNext()) {
                     return null;
                  }

                  registryEntry = (RegistryEntry)var6.next();
                  structurePresence = structureAccessor.getStructurePresence(pos, (Structure)registryEntry.value(), placement, skipReferencedStructures);
               } while(structurePresence == StructurePresence.START_NOT_PRESENT);

               if (!skipReferencedStructures && structurePresence == StructurePresence.START_PRESENT) {
                  return Pair.of(placement.getLocatePos(pos), registryEntry);
               }

               Chunk chunk = world.getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_STARTS);
               structureStart = structureAccessor.getStructureStart(ChunkSectionPos.from(chunk), (Structure)registryEntry.value(), chunk);
            } while(structureStart == null);
         } while(!structureStart.hasChildren());
      } while(skipReferencedStructures && !checkNotReferenced(structureAccessor, structureStart));

      return Pair.of(placement.getLocatePos(structureStart.getPos()), registryEntry);
   }

   private static boolean checkNotReferenced(StructureAccessor structureAccessor, StructureStart start) {
      if (start.isNeverReferenced()) {
         structureAccessor.incrementReferences(start);
         return true;
      } else {
         return false;
      }
   }

   public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
      ChunkPos chunkPos = chunk.getPos();
      if (!SharedConstants.isOutsideGenerationArea(chunkPos)) {
         ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunkPos, world.getBottomSectionCoord());
         BlockPos blockPos = chunkSectionPos.getMinPos();
         Registry registry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
         Map map = (Map)registry.stream().collect(Collectors.groupingBy((structureType) -> {
            return structureType.getFeatureGenerationStep().ordinal();
         }));
         List list = (List)this.indexedFeaturesListSupplier.get();
         ChunkRandom chunkRandom = new ChunkRandom(new Xoroshiro128PlusPlusRandom(RandomSeed.getSeed()));
         long l = chunkRandom.setPopulationSeed(world.getSeed(), blockPos.getX(), blockPos.getZ());
         Set set = new ObjectArraySet();
         ChunkPos.stream(chunkSectionPos.toChunkPos(), 1).forEach((pos) -> {
            Chunk chunk = world.getChunk(pos.x, pos.z);
            ChunkSection[] var4 = chunk.getSectionArray();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               ChunkSection chunkSection = var4[var6];
               ReadableContainer var10000 = chunkSection.getBiomeContainer();
               Objects.requireNonNull(set);
               var10000.forEachValue(set::add);
            }

         });
         set.retainAll(this.biomeSource.getBiomes());
         int i = list.size();

         try {
            Registry registry2 = world.getRegistryManager().getOrThrow(RegistryKeys.PLACED_FEATURE);
            int j = Math.max(GenerationStep.Feature.values().length, i);

            for(int k = 0; k < j; ++k) {
               int m = 0;
               CrashReportSection var10000;
               Iterator var20;
               if (structureAccessor.shouldGenerateStructures()) {
                  List list2 = (List)map.getOrDefault(k, Collections.emptyList());

                  for(var20 = list2.iterator(); var20.hasNext(); ++m) {
                     Structure structure = (Structure)var20.next();
                     chunkRandom.setDecoratorSeed(l, m, k);
                     Supplier supplier = () -> {
                        Optional var10000 = registry.getKey(structure).map(Object::toString);
                        Objects.requireNonNull(structure);
                        return (String)var10000.orElseGet(structure::toString);
                     };

                     try {
                        world.setCurrentlyGeneratingStructureName(supplier);
                        structureAccessor.getStructureStarts(chunkSectionPos, structure).forEach((start) -> {
                           start.place(world, structureAccessor, this, chunkRandom, getBlockBoxForChunk(chunk), chunkPos);
                        });
                     } catch (Exception var29) {
                        CrashReport crashReport = CrashReport.create(var29, "Feature placement");
                        var10000 = crashReport.addElement("Feature");
                        Objects.requireNonNull(supplier);
                        var10000.add("Description", supplier::get);
                        throw new CrashException(crashReport);
                     }
                  }
               }

               if (k < i) {
                  IntSet intSet = new IntArraySet();
                  var20 = set.iterator();

                  while(var20.hasNext()) {
                     RegistryEntry registryEntry = (RegistryEntry)var20.next();
                     List list3 = ((GenerationSettings)this.generationSettingsGetter.apply(registryEntry)).getFeatures();
                     if (k < list3.size()) {
                        RegistryEntryList registryEntryList = (RegistryEntryList)list3.get(k);
                        PlacedFeatureIndexer.IndexedFeatures indexedFeatures = (PlacedFeatureIndexer.IndexedFeatures)list.get(k);
                        registryEntryList.stream().map(RegistryEntry::value).forEach((feature) -> {
                           intSet.add(indexedFeatures.indexMapping().applyAsInt(feature));
                        });
                     }
                  }

                  int n = intSet.size();
                  int[] is = intSet.toIntArray();
                  Arrays.sort(is);
                  PlacedFeatureIndexer.IndexedFeatures indexedFeatures2 = (PlacedFeatureIndexer.IndexedFeatures)list.get(k);

                  for(int o = 0; o < n; ++o) {
                     int p = is[o];
                     PlacedFeature placedFeature = (PlacedFeature)indexedFeatures2.features().get(p);
                     Supplier supplier2 = () -> {
                        Optional var10000 = registry2.getKey(placedFeature).map(Object::toString);
                        Objects.requireNonNull(placedFeature);
                        return (String)var10000.orElseGet(placedFeature::toString);
                     };
                     chunkRandom.setDecoratorSeed(l, p, k);

                     try {
                        world.setCurrentlyGeneratingStructureName(supplier2);
                        placedFeature.generate(world, this, chunkRandom, blockPos);
                     } catch (Exception var30) {
                        CrashReport crashReport2 = CrashReport.create(var30, "Feature placement");
                        var10000 = crashReport2.addElement("Feature");
                        Objects.requireNonNull(supplier2);
                        var10000.add("Description", supplier2::get);
                        throw new CrashException(crashReport2);
                     }
                  }
               }
            }

            world.setCurrentlyGeneratingStructureName((Supplier)null);
         } catch (Exception var31) {
            CrashReport crashReport3 = CrashReport.create(var31, "Biome decoration");
            crashReport3.addElement("Generation").add("CenterX", (Object)chunkPos.x).add("CenterZ", (Object)chunkPos.z).add("Decoration Seed", (Object)l);
            throw new CrashException(crashReport3);
         }
      }
   }

   private static BlockBox getBlockBoxForChunk(Chunk chunk) {
      ChunkPos chunkPos = chunk.getPos();
      int i = chunkPos.getStartX();
      int j = chunkPos.getStartZ();
      HeightLimitView heightLimitView = chunk.getHeightLimitView();
      int k = heightLimitView.getBottomY() + 1;
      int l = heightLimitView.getTopYInclusive();
      return new BlockBox(i, k, j, i + 15, l, j + 15);
   }

   public abstract void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk);

   public abstract void populateEntities(ChunkRegion region);

   public int getSpawnHeight(HeightLimitView world) {
      return 64;
   }

   public BiomeSource getBiomeSource() {
      return this.biomeSource;
   }

   public abstract int getWorldHeight();

   public Pool getEntitySpawnList(RegistryEntry biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos) {
      Map map = accessor.getStructureReferences(pos);
      Iterator var6 = map.entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry entry = (Map.Entry)var6.next();
         Structure structure = (Structure)entry.getKey();
         StructureSpawns structureSpawns = (StructureSpawns)structure.getStructureSpawns().get(group);
         if (structureSpawns != null) {
            MutableBoolean mutableBoolean = new MutableBoolean(false);
            Predicate predicate = structureSpawns.boundingBox() == StructureSpawns.BoundingBox.PIECE ? (start) -> {
               return accessor.structureContains(pos, start);
            } : (start) -> {
               return start.getBoundingBox().contains(pos);
            };
            accessor.acceptStructureStarts(structure, (LongSet)entry.getValue(), (start) -> {
               if (mutableBoolean.isFalse() && predicate.test(start)) {
                  mutableBoolean.setTrue();
               }

            });
            if (mutableBoolean.isTrue()) {
               return structureSpawns.spawns();
            }
         }
      }

      return ((Biome)biome.value()).getSpawnSettings().getSpawnEntries(group);
   }

   public void setStructureStarts(DynamicRegistryManager registryManager, StructurePlacementCalculator placementCalculator, StructureAccessor structureAccessor, Chunk chunk, StructureTemplateManager structureTemplateManager, RegistryKey dimension) {
      ChunkPos chunkPos = chunk.getPos();
      ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);
      NoiseConfig noiseConfig = placementCalculator.getNoiseConfig();
      placementCalculator.getStructureSets().forEach((structureSet) -> {
         StructurePlacement structurePlacement = ((StructureSet)structureSet.value()).placement();
         List list = ((StructureSet)structureSet.value()).structures();
         Iterator var13 = list.iterator();

         while(var13.hasNext()) {
            StructureSet.WeightedEntry weightedEntry = (StructureSet.WeightedEntry)var13.next();
            StructureStart structureStart = structureAccessor.getStructureStart(chunkSectionPos, (Structure)weightedEntry.structure().value(), chunk);
            if (structureStart != null && structureStart.hasChildren()) {
               return;
            }
         }

         if (structurePlacement.shouldGenerate(placementCalculator, chunkPos.x, chunkPos.z)) {
            if (list.size() == 1) {
               this.trySetStructureStart((StructureSet.WeightedEntry)list.get(0), structureAccessor, registryManager, noiseConfig, structureTemplateManager, placementCalculator.getStructureSeed(), chunk, chunkPos, chunkSectionPos, dimension);
            } else {
               ArrayList arrayList = new ArrayList(list.size());
               arrayList.addAll(list);
               ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
               chunkRandom.setCarverSeed(placementCalculator.getStructureSeed(), chunkPos.x, chunkPos.z);
               int i = 0;

               StructureSet.WeightedEntry weightedEntry2;
               for(Iterator var16 = arrayList.iterator(); var16.hasNext(); i += weightedEntry2.weight()) {
                  weightedEntry2 = (StructureSet.WeightedEntry)var16.next();
               }

               while(!arrayList.isEmpty()) {
                  int j = chunkRandom.nextInt(i);
                  int k = 0;

                  for(Iterator var18 = arrayList.iterator(); var18.hasNext(); ++k) {
                     StructureSet.WeightedEntry weightedEntry3 = (StructureSet.WeightedEntry)var18.next();
                     j -= weightedEntry3.weight();
                     if (j < 0) {
                        break;
                     }
                  }

                  StructureSet.WeightedEntry weightedEntry4 = (StructureSet.WeightedEntry)arrayList.get(k);
                  if (this.trySetStructureStart(weightedEntry4, structureAccessor, registryManager, noiseConfig, structureTemplateManager, placementCalculator.getStructureSeed(), chunk, chunkPos, chunkSectionPos, dimension)) {
                     return;
                  }

                  arrayList.remove(k);
                  i -= weightedEntry4.weight();
               }

            }
         }
      });
   }

   private boolean trySetStructureStart(StructureSet.WeightedEntry weightedEntry, StructureAccessor structureAccessor, DynamicRegistryManager dynamicRegistryManager, NoiseConfig noiseConfig, StructureTemplateManager structureManager, long seed, Chunk chunk, ChunkPos pos, ChunkSectionPos sectionPos, RegistryKey dimension) {
      Structure structure = (Structure)weightedEntry.structure().value();
      int i = getStructureReferences(structureAccessor, chunk, sectionPos, structure);
      RegistryEntryList registryEntryList = structure.getValidBiomes();
      Objects.requireNonNull(registryEntryList);
      Predicate predicate = registryEntryList::contains;
      StructureStart structureStart = structure.createStructureStart(weightedEntry.structure(), dimension, dynamicRegistryManager, this, this.biomeSource, noiseConfig, structureManager, seed, pos, i, chunk, predicate);
      if (structureStart.hasChildren()) {
         structureAccessor.setStructureStart(sectionPos, structure, structureStart, chunk);
         return true;
      } else {
         return false;
      }
   }

   private static int getStructureReferences(StructureAccessor structureAccessor, Chunk chunk, ChunkSectionPos sectionPos, Structure structure) {
      StructureStart structureStart = structureAccessor.getStructureStart(sectionPos, structure, chunk);
      return structureStart != null ? structureStart.getReferences() : 0;
   }

   public void addStructureReferences(StructureWorldAccess world, StructureAccessor structureAccessor, Chunk chunk) {
      int i = true;
      ChunkPos chunkPos = chunk.getPos();
      int j = chunkPos.x;
      int k = chunkPos.z;
      int l = chunkPos.getStartX();
      int m = chunkPos.getStartZ();
      ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(chunk);

      for(int n = j - 8; n <= j + 8; ++n) {
         for(int o = k - 8; o <= k + 8; ++o) {
            long p = ChunkPos.toLong(n, o);
            Iterator var15 = world.getChunk(n, o).getStructureStarts().values().iterator();

            while(var15.hasNext()) {
               StructureStart structureStart = (StructureStart)var15.next();

               try {
                  if (structureStart.hasChildren() && structureStart.getBoundingBox().intersectsXZ(l, m, l + 15, m + 15)) {
                     structureAccessor.addStructureReference(chunkSectionPos, structureStart.getStructure(), p, chunk);
                     DebugInfoSender.sendStructureStart(world, structureStart);
                  }
               } catch (Exception var21) {
                  CrashReport crashReport = CrashReport.create(var21, "Generating structure reference");
                  CrashReportSection crashReportSection = crashReport.addElement("Structure");
                  Optional optional = world.getRegistryManager().getOptional(RegistryKeys.STRUCTURE);
                  crashReportSection.add("Id", () -> {
                     return (String)optional.map((structureTypeRegistry) -> {
                        return structureTypeRegistry.getId(structureStart.getStructure()).toString();
                     }).orElse("UNKNOWN");
                  });
                  crashReportSection.add("Name", () -> {
                     return Registries.STRUCTURE_TYPE.getId(structureStart.getStructure().getType()).toString();
                  });
                  crashReportSection.add("Class", () -> {
                     return structureStart.getStructure().getClass().getCanonicalName();
                  });
                  throw new CrashException(crashReport);
               }
            }
         }
      }

   }

   public abstract CompletableFuture populateNoise(Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk);

   public abstract int getSeaLevel();

   public abstract int getMinimumY();

   public abstract int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig);

   public abstract VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig);

   public int getHeightOnGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
      return this.getHeight(x, z, heightmap, world, noiseConfig);
   }

   public int getHeightInGround(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
      return this.getHeight(x, z, heightmap, world, noiseConfig) - 1;
   }

   public abstract void appendDebugHudText(List text, NoiseConfig noiseConfig, BlockPos pos);

   /** @deprecated */
   @Deprecated
   public GenerationSettings getGenerationSettings(RegistryEntry biomeEntry) {
      return (GenerationSettings)this.generationSettingsGetter.apply(biomeEntry);
   }

   static {
      CODEC = Registries.CHUNK_GENERATOR.getCodec().dispatchStable(ChunkGenerator::getCodec, Function.identity());
   }
}
