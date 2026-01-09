package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtShort;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.storage.StorageKey;
import net.minecraft.world.tick.ChunkTickScheduler;
import net.minecraft.world.tick.SimpleTickScheduler;
import net.minecraft.world.tick.Tick;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public record SerializedChunk(Registry biomeRegistry, ChunkPos chunkPos, int minSectionY, long lastUpdateTime, long inhabitedTime, ChunkStatus chunkStatus, @Nullable BlendingData.Serialized blendingData, @Nullable BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, @Nullable long[] carvingMask, Map heightmaps, Chunk.TickSchedulers packedTicks, ShortList[] postProcessingSections, boolean lightCorrect, List sectionData, List entities, List blockEntities, NbtCompound structureData) {
   private static final Codec CODEC;
   private static final Codec BLOCK_TICKS_CODEC;
   private static final Codec FLUID_TICKS_CODEC;
   private static final Logger LOGGER;
   private static final String UPGRADE_DATA_KEY = "UpgradeData";
   private static final String BLOCK_TICKS = "block_ticks";
   private static final String FLUID_TICKS = "fluid_ticks";
   public static final String X_POS_KEY = "xPos";
   public static final String Z_POS_KEY = "zPos";
   public static final String HEIGHTMAPS_KEY = "Heightmaps";
   public static final String IS_LIGHT_ON_KEY = "isLightOn";
   public static final String SECTIONS_KEY = "sections";
   public static final String BLOCK_LIGHT_KEY = "BlockLight";
   public static final String SKY_LIGHT_KEY = "SkyLight";

   public SerializedChunk(Registry registry, ChunkPos chunkPos, int i, long l, long m, ChunkStatus chunkStatus, @Nullable BlendingData.Serialized serialized, @Nullable BelowZeroRetrogen belowZeroRetrogen, UpgradeData upgradeData, @Nullable long[] ls, Map map, Chunk.TickSchedulers tickSchedulers, ShortList[] shortLists, boolean bl, List list, List list2, List list3, NbtCompound nbtCompound) {
      this.biomeRegistry = registry;
      this.chunkPos = chunkPos;
      this.minSectionY = i;
      this.lastUpdateTime = l;
      this.inhabitedTime = m;
      this.chunkStatus = chunkStatus;
      this.blendingData = serialized;
      this.belowZeroRetrogen = belowZeroRetrogen;
      this.upgradeData = upgradeData;
      this.carvingMask = ls;
      this.heightmaps = map;
      this.packedTicks = tickSchedulers;
      this.postProcessingSections = shortLists;
      this.lightCorrect = bl;
      this.sectionData = list;
      this.entities = list2;
      this.blockEntities = list3;
      this.structureData = nbtCompound;
   }

   @Nullable
   public static SerializedChunk fromNbt(HeightLimitView world, DynamicRegistryManager registryManager, NbtCompound nbt) {
      if (nbt.getString("Status").isEmpty()) {
         return null;
      } else {
         ChunkPos chunkPos = new ChunkPos(nbt.getInt("xPos", 0), nbt.getInt("zPos", 0));
         long l = nbt.getLong("LastUpdate", 0L);
         long m = nbt.getLong("InhabitedTime", 0L);
         ChunkStatus chunkStatus = (ChunkStatus)nbt.get("Status", ChunkStatus.CODEC).orElse(ChunkStatus.EMPTY);
         UpgradeData upgradeData = (UpgradeData)nbt.getCompound("UpgradeData").map((upgradeDatax) -> {
            return new UpgradeData(upgradeDatax, world);
         }).orElse(UpgradeData.NO_UPGRADE_DATA);
         boolean bl = nbt.getBoolean("isLightOn", false);
         BlendingData.Serialized serialized = (BlendingData.Serialized)nbt.get("blending_data", BlendingData.Serialized.CODEC).orElse((Object)null);
         BelowZeroRetrogen belowZeroRetrogen = (BelowZeroRetrogen)nbt.get("below_zero_retrogen", BelowZeroRetrogen.CODEC).orElse((Object)null);
         long[] ls = (long[])nbt.getLongArray("carving_mask").orElse((Object)null);
         Map map = new EnumMap(Heightmap.Type.class);
         nbt.getCompound("Heightmaps").ifPresent((heightmaps) -> {
            Iterator var3 = chunkStatus.getHeightmapTypes().iterator();

            while(var3.hasNext()) {
               Heightmap.Type type = (Heightmap.Type)var3.next();
               heightmaps.getLongArray(type.getId()).ifPresent((heightmapType) -> {
                  map.put(type, heightmapType);
               });
            }

         });
         List list = Tick.filter((List)nbt.get("block_ticks", BLOCK_TICKS_CODEC).orElse(List.of()), chunkPos);
         List list2 = Tick.filter((List)nbt.get("fluid_ticks", FLUID_TICKS_CODEC).orElse(List.of()), chunkPos);
         Chunk.TickSchedulers tickSchedulers = new Chunk.TickSchedulers(list, list2);
         NbtList nbtList = nbt.getListOrEmpty("PostProcessing");
         ShortList[] shortLists = new ShortList[nbtList.size()];

         for(int i = 0; i < nbtList.size(); ++i) {
            NbtList nbtList2 = nbtList.getListOrEmpty(i);
            ShortList shortList = new ShortArrayList(nbtList2.size());

            for(int j = 0; j < nbtList2.size(); ++j) {
               shortList.add(nbtList2.getShort(j, (short)0));
            }

            shortLists[i] = shortList;
         }

         List list3 = nbt.getList("entities").stream().flatMap(NbtList::streamCompounds).toList();
         List list4 = nbt.getList("block_entities").stream().flatMap(NbtList::streamCompounds).toList();
         NbtCompound nbtCompound = nbt.getCompoundOrEmpty("structures");
         NbtList nbtList3 = nbt.getListOrEmpty("sections");
         List list5 = new ArrayList(nbtList3.size());
         Registry registry = registryManager.getOrThrow(RegistryKeys.BIOME);
         Codec codec = createCodec(registry);

         for(int k = 0; k < nbtList3.size(); ++k) {
            Optional optional = nbtList3.getCompound(k);
            if (!optional.isEmpty()) {
               NbtCompound nbtCompound2 = (NbtCompound)optional.get();
               int n = nbtCompound2.getByte("Y", (byte)0);
               ChunkSection chunkSection;
               if (n >= world.getBottomSectionCoord() && n <= world.getTopSectionCoord()) {
                  PalettedContainer palettedContainer = (PalettedContainer)nbtCompound2.getCompound("block_states").map((blockStates) -> {
                     return (PalettedContainer)CODEC.parse(NbtOps.INSTANCE, blockStates).promotePartial((error) -> {
                        logRecoverableError(chunkPos, n, error);
                     }).getOrThrow(ChunkLoadingException::new);
                  }).orElseGet(() -> {
                     return new PalettedContainer(Block.STATE_IDS, Blocks.AIR.getDefaultState(), PalettedContainer.PaletteProvider.BLOCK_STATE);
                  });
                  ReadableContainer readableContainer = (ReadableContainer)nbtCompound2.getCompound("biomes").map((biomes) -> {
                     return (ReadableContainer)codec.parse(NbtOps.INSTANCE, biomes).promotePartial((error) -> {
                        logRecoverableError(chunkPos, n, error);
                     }).getOrThrow(ChunkLoadingException::new);
                  }).orElseGet(() -> {
                     return new PalettedContainer(registry.getIndexedEntries(), registry.getOrThrow(BiomeKeys.PLAINS), PalettedContainer.PaletteProvider.BIOME);
                  });
                  chunkSection = new ChunkSection(palettedContainer, readableContainer);
               } else {
                  chunkSection = null;
               }

               ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)nbtCompound2.getByteArray("BlockLight").map(ChunkNibbleArray::new).orElse((Object)null);
               ChunkNibbleArray chunkNibbleArray2 = (ChunkNibbleArray)nbtCompound2.getByteArray("SkyLight").map(ChunkNibbleArray::new).orElse((Object)null);
               list5.add(new SectionData(n, chunkSection, chunkNibbleArray, chunkNibbleArray2));
            }
         }

         return new SerializedChunk(registry, chunkPos, world.getBottomSectionCoord(), l, m, chunkStatus, serialized, belowZeroRetrogen, upgradeData, ls, map, tickSchedulers, shortLists, bl, list5, list3, list4, nbtCompound);
      }
   }

   public ProtoChunk convert(ServerWorld world, PointOfInterestStorage poiStorage, StorageKey key, ChunkPos expectedPos) {
      if (!Objects.equals(expectedPos, this.chunkPos)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", new Object[]{expectedPos, expectedPos, this.chunkPos});
         world.getServer().onChunkMisplacement(this.chunkPos, expectedPos, key);
      }

      int i = world.countVerticalSections();
      ChunkSection[] chunkSections = new ChunkSection[i];
      boolean bl = world.getDimension().hasSkyLight();
      ChunkManager chunkManager = world.getChunkManager();
      LightingProvider lightingProvider = chunkManager.getLightingProvider();
      Registry registry = world.getRegistryManager().getOrThrow(RegistryKeys.BIOME);
      boolean bl2 = false;
      Iterator var12 = this.sectionData.iterator();

      while(true) {
         SectionData sectionData;
         ChunkSectionPos chunkSectionPos;
         boolean bl3;
         boolean bl4;
         do {
            if (!var12.hasNext()) {
               ChunkType chunkType = this.chunkStatus.getChunkType();
               Object chunk;
               if (chunkType == ChunkType.LEVELCHUNK) {
                  ChunkTickScheduler chunkTickScheduler = new ChunkTickScheduler(this.packedTicks.blocks());
                  ChunkTickScheduler chunkTickScheduler2 = new ChunkTickScheduler(this.packedTicks.fluids());
                  chunk = new WorldChunk(world.toServerWorld(), expectedPos, this.upgradeData, chunkTickScheduler, chunkTickScheduler2, this.inhabitedTime, chunkSections, getEntityLoadingCallback(world, this.entities, this.blockEntities), BlendingData.fromSerialized(this.blendingData));
               } else {
                  SimpleTickScheduler simpleTickScheduler = SimpleTickScheduler.tick(this.packedTicks.blocks());
                  SimpleTickScheduler simpleTickScheduler2 = SimpleTickScheduler.tick(this.packedTicks.fluids());
                  ProtoChunk protoChunk = new ProtoChunk(expectedPos, this.upgradeData, chunkSections, simpleTickScheduler, simpleTickScheduler2, world, registry, BlendingData.fromSerialized(this.blendingData));
                  chunk = protoChunk;
                  protoChunk.setInhabitedTime(this.inhabitedTime);
                  if (this.belowZeroRetrogen != null) {
                     protoChunk.setBelowZeroRetrogen(this.belowZeroRetrogen);
                  }

                  protoChunk.setStatus(this.chunkStatus);
                  if (this.chunkStatus.isAtLeast(ChunkStatus.INITIALIZE_LIGHT)) {
                     protoChunk.setLightingProvider(lightingProvider);
                  }
               }

               ((Chunk)chunk).setLightOn(this.lightCorrect);
               EnumSet enumSet = EnumSet.noneOf(Heightmap.Type.class);
               Iterator var25 = ((Chunk)chunk).getStatus().getHeightmapTypes().iterator();

               while(var25.hasNext()) {
                  Heightmap.Type type = (Heightmap.Type)var25.next();
                  long[] ls = (long[])this.heightmaps.get(type);
                  if (ls != null) {
                     ((Chunk)chunk).setHeightmap(type, ls);
                  } else {
                     enumSet.add(type);
                  }
               }

               Heightmap.populateHeightmaps((Chunk)chunk, enumSet);
               ((Chunk)chunk).setStructureStarts(readStructureStarts(StructureContext.from(world), this.structureData, world.getSeed()));
               ((Chunk)chunk).setStructureReferences(readStructureReferences(world.getRegistryManager(), expectedPos, this.structureData));

               for(int j = 0; j < this.postProcessingSections.length; ++j) {
                  ((Chunk)chunk).markBlocksForPostProcessing(this.postProcessingSections[j], j);
               }

               if (chunkType == ChunkType.LEVELCHUNK) {
                  return new WrapperProtoChunk((WorldChunk)chunk, false);
               }

               ProtoChunk protoChunk2 = (ProtoChunk)chunk;
               Iterator var30 = this.entities.iterator();

               NbtCompound nbtCompound;
               while(var30.hasNext()) {
                  nbtCompound = (NbtCompound)var30.next();
                  protoChunk2.addEntity(nbtCompound);
               }

               var30 = this.blockEntities.iterator();

               while(var30.hasNext()) {
                  nbtCompound = (NbtCompound)var30.next();
                  protoChunk2.addPendingBlockEntityNbt(nbtCompound);
               }

               if (this.carvingMask != null) {
                  protoChunk2.setCarvingMask(new CarvingMask(this.carvingMask, ((Chunk)chunk).getBottomY()));
               }

               return protoChunk2;
            }

            sectionData = (SectionData)var12.next();
            chunkSectionPos = ChunkSectionPos.from(expectedPos, sectionData.y);
            if (sectionData.chunkSection != null) {
               chunkSections[world.sectionCoordToIndex(sectionData.y)] = sectionData.chunkSection;
               poiStorage.initForPalette(chunkSectionPos, sectionData.chunkSection);
            }

            bl3 = sectionData.blockLight != null;
            bl4 = bl && sectionData.skyLight != null;
         } while(!bl3 && !bl4);

         if (!bl2) {
            lightingProvider.setRetainData(expectedPos, true);
            bl2 = true;
         }

         if (bl3) {
            lightingProvider.enqueueSectionData(LightType.BLOCK, chunkSectionPos, sectionData.blockLight);
         }

         if (bl4) {
            lightingProvider.enqueueSectionData(LightType.SKY, chunkSectionPos, sectionData.skyLight);
         }
      }
   }

   private static void logRecoverableError(ChunkPos chunkPos, int y, String message) {
      LOGGER.error("Recoverable errors when loading section [{}, {}, {}]: {}", new Object[]{chunkPos.x, y, chunkPos.z, message});
   }

   private static Codec createCodec(Registry biomeRegistry) {
      return PalettedContainer.createReadableContainerCodec(biomeRegistry.getIndexedEntries(), biomeRegistry.getEntryCodec(), PalettedContainer.PaletteProvider.BIOME, biomeRegistry.getOrThrow(BiomeKeys.PLAINS));
   }

   public static SerializedChunk fromChunk(ServerWorld world, Chunk chunk) {
      if (!chunk.isSerializable()) {
         throw new IllegalArgumentException("Chunk can't be serialized: " + String.valueOf(chunk));
      } else {
         ChunkPos chunkPos = chunk.getPos();
         List list = new ArrayList();
         ChunkSection[] chunkSections = chunk.getSectionArray();
         LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();

         for(int i = lightingProvider.getBottomY(); i < lightingProvider.getTopY(); ++i) {
            int j = chunk.sectionCoordToIndex(i);
            boolean bl = j >= 0 && j < chunkSections.length;
            ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(chunkPos, i));
            ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY).getLightSection(ChunkSectionPos.from(chunkPos, i));
            ChunkNibbleArray chunkNibbleArray3 = chunkNibbleArray != null && !chunkNibbleArray.isUninitialized() ? chunkNibbleArray.copy() : null;
            ChunkNibbleArray chunkNibbleArray4 = chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized() ? chunkNibbleArray2.copy() : null;
            if (bl || chunkNibbleArray3 != null || chunkNibbleArray4 != null) {
               ChunkSection chunkSection = bl ? chunkSections[j].copy() : null;
               list.add(new SectionData(i, chunkSection, chunkNibbleArray3, chunkNibbleArray4));
            }
         }

         List list2 = new ArrayList(chunk.getBlockEntityPositions().size());
         Iterator var15 = chunk.getBlockEntityPositions().iterator();

         while(var15.hasNext()) {
            BlockPos blockPos = (BlockPos)var15.next();
            NbtCompound nbtCompound = chunk.getPackedBlockEntityNbt(blockPos, world.getRegistryManager());
            if (nbtCompound != null) {
               list2.add(nbtCompound);
            }
         }

         List list3 = new ArrayList();
         long[] ls = null;
         if (chunk.getStatus().getChunkType() == ChunkType.PROTOCHUNK) {
            ProtoChunk protoChunk = (ProtoChunk)chunk;
            list3.addAll(protoChunk.getEntities());
            CarvingMask carvingMask = protoChunk.getCarvingMask();
            if (carvingMask != null) {
               ls = carvingMask.getMask();
            }
         }

         Map map = new EnumMap(Heightmap.Type.class);
         Iterator var23 = chunk.getHeightmaps().iterator();

         while(var23.hasNext()) {
            Map.Entry entry = (Map.Entry)var23.next();
            if (chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) {
               long[] ms = ((Heightmap)entry.getValue()).asLongArray();
               map.put((Heightmap.Type)entry.getKey(), (long[])ms.clone());
            }
         }

         Chunk.TickSchedulers tickSchedulers = chunk.getTickSchedulers(world.getTime());
         ShortList[] shortLists = (ShortList[])Arrays.stream(chunk.getPostProcessingLists()).map((postProcessings) -> {
            return postProcessings != null ? new ShortArrayList(postProcessings) : null;
         }).toArray((ix) -> {
            return new ShortList[ix];
         });
         NbtCompound nbtCompound2 = writeStructures(StructureContext.from(world), chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences());
         return new SerializedChunk(world.getRegistryManager().getOrThrow(RegistryKeys.BIOME), chunkPos, chunk.getBottomSectionCoord(), world.getTime(), chunk.getInhabitedTime(), chunk.getStatus(), (BlendingData.Serialized)Nullables.map(chunk.getBlendingData(), BlendingData::toSerialized), chunk.getBelowZeroRetrogen(), chunk.getUpgradeData().copy(), ls, map, tickSchedulers, shortLists, chunk.isLightOn(), list, list3, list2, nbtCompound2);
      }
   }

   public NbtCompound serialize() {
      NbtCompound nbtCompound = NbtHelper.putDataVersion(new NbtCompound());
      nbtCompound.putInt("xPos", this.chunkPos.x);
      nbtCompound.putInt("yPos", this.minSectionY);
      nbtCompound.putInt("zPos", this.chunkPos.z);
      nbtCompound.putLong("LastUpdate", this.lastUpdateTime);
      nbtCompound.putLong("InhabitedTime", this.inhabitedTime);
      nbtCompound.putString("Status", Registries.CHUNK_STATUS.getId(this.chunkStatus).toString());
      nbtCompound.putNullable("blending_data", BlendingData.Serialized.CODEC, this.blendingData);
      nbtCompound.putNullable("below_zero_retrogen", BelowZeroRetrogen.CODEC, this.belowZeroRetrogen);
      if (!this.upgradeData.isDone()) {
         nbtCompound.put("UpgradeData", this.upgradeData.toNbt());
      }

      NbtList nbtList = new NbtList();
      Codec codec = createCodec(this.biomeRegistry);
      Iterator var4 = this.sectionData.iterator();

      while(var4.hasNext()) {
         SectionData sectionData = (SectionData)var4.next();
         NbtCompound nbtCompound2 = new NbtCompound();
         ChunkSection chunkSection = sectionData.chunkSection;
         if (chunkSection != null) {
            nbtCompound2.put("block_states", CODEC, chunkSection.getBlockStateContainer());
            nbtCompound2.put("biomes", codec, chunkSection.getBiomeContainer());
         }

         if (sectionData.blockLight != null) {
            nbtCompound2.putByteArray("BlockLight", sectionData.blockLight.asByteArray());
         }

         if (sectionData.skyLight != null) {
            nbtCompound2.putByteArray("SkyLight", sectionData.skyLight.asByteArray());
         }

         if (!nbtCompound2.isEmpty()) {
            nbtCompound2.putByte("Y", (byte)sectionData.y);
            nbtList.add(nbtCompound2);
         }
      }

      nbtCompound.put("sections", nbtList);
      if (this.lightCorrect) {
         nbtCompound.putBoolean("isLightOn", true);
      }

      NbtList nbtList2 = new NbtList();
      nbtList2.addAll(this.blockEntities);
      nbtCompound.put("block_entities", nbtList2);
      if (this.chunkStatus.getChunkType() == ChunkType.PROTOCHUNK) {
         NbtList nbtList3 = new NbtList();
         nbtList3.addAll(this.entities);
         nbtCompound.put("entities", nbtList3);
         if (this.carvingMask != null) {
            nbtCompound.putLongArray("carving_mask", this.carvingMask);
         }
      }

      serializeTicks(nbtCompound, this.packedTicks);
      nbtCompound.put("PostProcessing", toNbt(this.postProcessingSections));
      NbtCompound nbtCompound3 = new NbtCompound();
      this.heightmaps.forEach((type, values) -> {
         nbtCompound3.put(type.getId(), new NbtLongArray(values));
      });
      nbtCompound.put("Heightmaps", nbtCompound3);
      nbtCompound.put("structures", this.structureData);
      return nbtCompound;
   }

   private static void serializeTicks(NbtCompound nbt, Chunk.TickSchedulers schedulers) {
      nbt.put("block_ticks", BLOCK_TICKS_CODEC, schedulers.blocks());
      nbt.put("fluid_ticks", FLUID_TICKS_CODEC, schedulers.fluids());
   }

   public static ChunkStatus getChunkStatus(@Nullable NbtCompound nbt) {
      return nbt != null ? (ChunkStatus)nbt.get("Status", ChunkStatus.CODEC).orElse(ChunkStatus.EMPTY) : ChunkStatus.EMPTY;
   }

   @Nullable
   private static WorldChunk.EntityLoader getEntityLoadingCallback(ServerWorld world, List entities, List blockEntities) {
      return entities.isEmpty() && blockEntities.isEmpty() ? null : (chunk) -> {
         if (!entities.isEmpty()) {
            ErrorReporter.Logging logging = new ErrorReporter.Logging(chunk.getErrorReporterContext(), LOGGER);

            try {
               world.loadEntities(EntityType.streamFromData(NbtReadView.createList(logging, world.getRegistryManager(), entities), world, SpawnReason.LOAD));
            } catch (Throwable var10) {
               try {
                  logging.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }

               throw var10;
            }

            logging.close();
         }

         Iterator var11 = blockEntities.iterator();

         while(var11.hasNext()) {
            NbtCompound nbtCompound = (NbtCompound)var11.next();
            boolean bl = nbtCompound.getBoolean("keepPacked", false);
            if (bl) {
               chunk.addPendingBlockEntityNbt(nbtCompound);
            } else {
               BlockPos blockPos = BlockEntity.posFromNbt(chunk.getPos(), nbtCompound);
               BlockEntity blockEntity = BlockEntity.createFromNbt(blockPos, chunk.getBlockState(blockPos), nbtCompound, world.getRegistryManager());
               if (blockEntity != null) {
                  chunk.setBlockEntity(blockEntity);
               }
            }
         }

      };
   }

   private static NbtCompound writeStructures(StructureContext context, ChunkPos pos, Map starts, Map references) {
      NbtCompound nbtCompound = new NbtCompound();
      NbtCompound nbtCompound2 = new NbtCompound();
      Registry registry = context.registryManager().getOrThrow(RegistryKeys.STRUCTURE);
      Iterator var7 = starts.entrySet().iterator();

      while(var7.hasNext()) {
         Map.Entry entry = (Map.Entry)var7.next();
         Identifier identifier = registry.getId((Structure)entry.getKey());
         nbtCompound2.put(identifier.toString(), ((StructureStart)entry.getValue()).toNbt(context, pos));
      }

      nbtCompound.put("starts", nbtCompound2);
      NbtCompound nbtCompound3 = new NbtCompound();
      Iterator var12 = references.entrySet().iterator();

      while(var12.hasNext()) {
         Map.Entry entry2 = (Map.Entry)var12.next();
         if (!((LongSet)entry2.getValue()).isEmpty()) {
            Identifier identifier2 = registry.getId((Structure)entry2.getKey());
            nbtCompound3.putLongArray(identifier2.toString(), ((LongSet)entry2.getValue()).toLongArray());
         }
      }

      nbtCompound.put("References", nbtCompound3);
      return nbtCompound;
   }

   private static Map readStructureStarts(StructureContext context, NbtCompound nbt, long worldSeed) {
      Map map = Maps.newHashMap();
      Registry registry = context.registryManager().getOrThrow(RegistryKeys.STRUCTURE);
      NbtCompound nbtCompound = nbt.getCompoundOrEmpty("starts");
      Iterator var7 = nbtCompound.getKeys().iterator();

      while(var7.hasNext()) {
         String string = (String)var7.next();
         Identifier identifier = Identifier.tryParse(string);
         Structure structure = (Structure)registry.get(identifier);
         if (structure == null) {
            LOGGER.error("Unknown structure start: {}", identifier);
         } else {
            StructureStart structureStart = StructureStart.fromNbt(context, nbtCompound.getCompoundOrEmpty(string), worldSeed);
            if (structureStart != null) {
               map.put(structure, structureStart);
            }
         }
      }

      return map;
   }

   private static Map readStructureReferences(DynamicRegistryManager registryManager, ChunkPos pos, NbtCompound nbt) {
      Map map = Maps.newHashMap();
      Registry registry = registryManager.getOrThrow(RegistryKeys.STRUCTURE);
      NbtCompound nbtCompound = nbt.getCompoundOrEmpty("References");
      nbtCompound.forEach((id, chunkPos) -> {
         Identifier identifier = Identifier.tryParse(id);
         Structure structure = (Structure)registry.get(identifier);
         if (structure == null) {
            LOGGER.warn("Found reference to unknown structure '{}' in chunk {}, discarding", identifier, pos);
         } else {
            Optional optional = chunkPos.asLongArray();
            if (!optional.isEmpty()) {
               map.put(structure, new LongOpenHashSet(Arrays.stream((long[])optional.get()).filter((packedPos) -> {
                  ChunkPos chunkPos2 = new ChunkPos(packedPos);
                  if (chunkPos2.getChebyshevDistance(pos) > 8) {
                     LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", new Object[]{identifier, chunkPos2, pos});
                     return false;
                  } else {
                     return true;
                  }
               }).toArray()));
            }
         }
      });
      return map;
   }

   private static NbtList toNbt(ShortList[] lists) {
      NbtList nbtList = new NbtList();
      ShortList[] var2 = lists;
      int var3 = lists.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ShortList shortList = var2[var4];
         NbtList nbtList2 = new NbtList();
         if (shortList != null) {
            for(int i = 0; i < shortList.size(); ++i) {
               nbtList2.add(NbtShort.of(shortList.getShort(i)));
            }
         }

         nbtList.add(nbtList2);
      }

      return nbtList;
   }

   public Registry biomeRegistry() {
      return this.biomeRegistry;
   }

   public ChunkPos chunkPos() {
      return this.chunkPos;
   }

   public int minSectionY() {
      return this.minSectionY;
   }

   public long lastUpdateTime() {
      return this.lastUpdateTime;
   }

   public long inhabitedTime() {
      return this.inhabitedTime;
   }

   public ChunkStatus chunkStatus() {
      return this.chunkStatus;
   }

   @Nullable
   public BlendingData.Serialized blendingData() {
      return this.blendingData;
   }

   @Nullable
   public BelowZeroRetrogen belowZeroRetrogen() {
      return this.belowZeroRetrogen;
   }

   public UpgradeData upgradeData() {
      return this.upgradeData;
   }

   @Nullable
   public long[] carvingMask() {
      return this.carvingMask;
   }

   public Map heightmaps() {
      return this.heightmaps;
   }

   public Chunk.TickSchedulers packedTicks() {
      return this.packedTicks;
   }

   public ShortList[] postProcessingSections() {
      return this.postProcessingSections;
   }

   public boolean lightCorrect() {
      return this.lightCorrect;
   }

   public List sectionData() {
      return this.sectionData;
   }

   public List entities() {
      return this.entities;
   }

   public List blockEntities() {
      return this.blockEntities;
   }

   public NbtCompound structureData() {
      return this.structureData;
   }

   static {
      CODEC = PalettedContainer.createPalettedContainerCodec(Block.STATE_IDS, BlockState.CODEC, PalettedContainer.PaletteProvider.BLOCK_STATE, Blocks.AIR.getDefaultState());
      BLOCK_TICKS_CODEC = Tick.createCodec(Registries.BLOCK.getCodec()).listOf();
      FLUID_TICKS_CODEC = Tick.createCodec(Registries.FLUID.getCodec()).listOf();
      LOGGER = LogUtils.getLogger();
   }

   public static record SectionData(int y, @Nullable ChunkSection chunkSection, @Nullable ChunkNibbleArray blockLight, @Nullable ChunkNibbleArray skyLight) {
      final int y;
      @Nullable
      final ChunkSection chunkSection;
      @Nullable
      final ChunkNibbleArray blockLight;
      @Nullable
      final ChunkNibbleArray skyLight;

      public SectionData(int i, @Nullable ChunkSection chunkSection, @Nullable ChunkNibbleArray chunkNibbleArray, @Nullable ChunkNibbleArray chunkNibbleArray2) {
         this.y = i;
         this.chunkSection = chunkSection;
         this.blockLight = chunkNibbleArray;
         this.skyLight = chunkNibbleArray2;
      }

      public int y() {
         return this.y;
      }

      @Nullable
      public ChunkSection chunkSection() {
         return this.chunkSection;
      }

      @Nullable
      public ChunkNibbleArray blockLight() {
         return this.blockLight;
      }

      @Nullable
      public ChunkNibbleArray skyLight() {
         return this.skyLight;
      }
   }

   public static class ChunkLoadingException extends NbtException {
      public ChunkLoadingException(String string) {
         super(string);
      }
   }
}
