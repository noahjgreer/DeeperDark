package net.minecraft.structure.pool;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.JigsawReplacementStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SinglePoolElement extends StructurePoolElement {
   private static final Comparator JIGSAW_BLOCK_INFO_COMPARATOR = Comparator.comparingInt(StructureTemplate.JigsawBlockInfo::selectionPriority).reversed();
   private static final Codec LOCATION_CODEC;
   public static final MapCodec CODEC;
   protected final Either location;
   protected final RegistryEntry processors;
   protected final Optional overrideLiquidSettings;

   private static DataResult encodeLocation(Either location, DynamicOps ops, Object prefix) {
      Optional optional = location.left();
      return optional.isEmpty() ? DataResult.error(() -> {
         return "Can not serialize a runtime pool element";
      }) : Identifier.CODEC.encode((Identifier)optional.get(), ops, prefix);
   }

   protected static RecordCodecBuilder processorsGetter() {
      return StructureProcessorType.REGISTRY_CODEC.fieldOf("processors").forGetter((pool) -> {
         return pool.processors;
      });
   }

   protected static RecordCodecBuilder overrideLiquidSettingsGetter() {
      return StructureLiquidSettings.codec.optionalFieldOf("override_liquid_settings").forGetter((pool) -> {
         return pool.overrideLiquidSettings;
      });
   }

   protected static RecordCodecBuilder locationGetter() {
      return LOCATION_CODEC.fieldOf("location").forGetter((pool) -> {
         return pool.location;
      });
   }

   protected SinglePoolElement(Either location, RegistryEntry processors, StructurePool.Projection projection, Optional overrideLiquidSettings) {
      super(projection);
      this.location = location;
      this.processors = processors;
      this.overrideLiquidSettings = overrideLiquidSettings;
   }

   public Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation) {
      StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
      return structureTemplate.getRotatedSize(rotation);
   }

   private StructureTemplate getStructure(StructureTemplateManager structureTemplateManager) {
      Either var10000 = this.location;
      Objects.requireNonNull(structureTemplateManager);
      return (StructureTemplate)var10000.map(structureTemplateManager::getTemplateOrBlank, Function.identity());
   }

   public List getDataStructureBlocks(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, boolean mirroredAndRotated) {
      StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
      List list = structureTemplate.getInfosForBlock(pos, (new StructurePlacementData()).setRotation(rotation), Blocks.STRUCTURE_BLOCK, mirroredAndRotated);
      List list2 = Lists.newArrayList();
      Iterator var8 = list.iterator();

      while(var8.hasNext()) {
         StructureTemplate.StructureBlockInfo structureBlockInfo = (StructureTemplate.StructureBlockInfo)var8.next();
         NbtCompound nbtCompound = structureBlockInfo.nbt();
         if (nbtCompound != null) {
            StructureBlockMode structureBlockMode = (StructureBlockMode)nbtCompound.get("mode", StructureBlockMode.CODEC).orElseThrow();
            if (structureBlockMode == StructureBlockMode.DATA) {
               list2.add(structureBlockInfo);
            }
         }
      }

      return list2;
   }

   public List getStructureBlockInfos(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random) {
      List list = this.getStructure(structureTemplateManager).getJigsawInfos(pos, rotation);
      Util.shuffle(list, random);
      sort(list);
      return list;
   }

   @VisibleForTesting
   static void sort(List blocks) {
      blocks.sort(JIGSAW_BLOCK_INFO_COMPARATOR);
   }

   public BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation) {
      StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
      return structureTemplate.calculateBoundingBox((new StructurePlacementData()).setRotation(rotation), pos);
   }

   public boolean generate(StructureTemplateManager structureTemplateManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos pivot, BlockRotation rotation, BlockBox box, Random random, StructureLiquidSettings liquidSettings, boolean keepJigsaws) {
      StructureTemplate structureTemplate = this.getStructure(structureTemplateManager);
      StructurePlacementData structurePlacementData = this.createPlacementData(rotation, box, liquidSettings, keepJigsaws);
      if (!structureTemplate.place(world, pos, pivot, structurePlacementData, random, 18)) {
         return false;
      } else {
         List list = StructureTemplate.process(world, pos, pivot, structurePlacementData, this.getDataStructureBlocks(structureTemplateManager, pos, rotation, false));
         Iterator var15 = list.iterator();

         while(var15.hasNext()) {
            StructureTemplate.StructureBlockInfo structureBlockInfo = (StructureTemplate.StructureBlockInfo)var15.next();
            this.method_16756(world, structureBlockInfo, pos, rotation, random, box);
         }

         return true;
      }
   }

   protected StructurePlacementData createPlacementData(BlockRotation rotation, BlockBox box, StructureLiquidSettings liquidSettings, boolean keepJigsaws) {
      StructurePlacementData structurePlacementData = new StructurePlacementData();
      structurePlacementData.setBoundingBox(box);
      structurePlacementData.setRotation(rotation);
      structurePlacementData.setUpdateNeighbors(true);
      structurePlacementData.setIgnoreEntities(false);
      structurePlacementData.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
      structurePlacementData.setInitializeMobs(true);
      structurePlacementData.setLiquidSettings((StructureLiquidSettings)this.overrideLiquidSettings.orElse(liquidSettings));
      if (!keepJigsaws) {
         structurePlacementData.addProcessor(JigsawReplacementStructureProcessor.INSTANCE);
      }

      List var10000 = ((StructureProcessorList)this.processors.value()).getList();
      Objects.requireNonNull(structurePlacementData);
      var10000.forEach(structurePlacementData::addProcessor);
      ImmutableList var6 = this.getProjection().getProcessors();
      Objects.requireNonNull(structurePlacementData);
      var6.forEach(structurePlacementData::addProcessor);
      return structurePlacementData;
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.SINGLE_POOL_ELEMENT;
   }

   public String toString() {
      return "Single[" + String.valueOf(this.location) + "]";
   }

   @VisibleForTesting
   public Identifier getIdOrThrow() {
      return (Identifier)this.location.orThrow();
   }

   static {
      LOCATION_CODEC = Codec.of(SinglePoolElement::encodeLocation, Identifier.CODEC.map(Either::left));
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(locationGetter(), processorsGetter(), projectionGetter(), overrideLiquidSettingsGetter()).apply(instance, SinglePoolElement::new);
      });
   }
}
