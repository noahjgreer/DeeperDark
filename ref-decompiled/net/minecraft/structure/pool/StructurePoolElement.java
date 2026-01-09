package net.minecraft.structure.pool;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

public abstract class StructurePoolElement {
   public static final Codec CODEC;
   private static final RegistryEntry EMPTY_PROCESSORS;
   @Nullable
   private volatile StructurePool.Projection projection;

   protected static RecordCodecBuilder projectionGetter() {
      return StructurePool.Projection.CODEC.fieldOf("projection").forGetter(StructurePoolElement::getProjection);
   }

   protected StructurePoolElement(StructurePool.Projection projection) {
      this.projection = projection;
   }

   public abstract Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation);

   public abstract List getStructureBlockInfos(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random);

   public abstract BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation);

   public abstract boolean generate(StructureTemplateManager structureTemplateManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos pivot, BlockRotation rotation, BlockBox box, Random random, StructureLiquidSettings liquidSettings, boolean keepJigsaws);

   public abstract StructurePoolElementType getType();

   public void method_16756(WorldAccess world, StructureTemplate.StructureBlockInfo structureBlockInfo, BlockPos pos, BlockRotation rotation, Random random, BlockBox box) {
   }

   public StructurePoolElement setProjection(StructurePool.Projection projection) {
      this.projection = projection;
      return this;
   }

   public StructurePool.Projection getProjection() {
      StructurePool.Projection projection = this.projection;
      if (projection == null) {
         throw new IllegalStateException();
      } else {
         return projection;
      }
   }

   public int getGroundLevelDelta() {
      return 1;
   }

   public static Function ofEmpty() {
      return (projection) -> {
         return EmptyPoolElement.INSTANCE;
      };
   }

   public static Function ofLegacySingle(String id) {
      return (projection) -> {
         return new LegacySinglePoolElement(Either.left(Identifier.of(id)), EMPTY_PROCESSORS, projection, Optional.empty());
      };
   }

   public static Function ofProcessedLegacySingle(String id, RegistryEntry processorListEntry) {
      return (projection) -> {
         return new LegacySinglePoolElement(Either.left(Identifier.of(id)), processorListEntry, projection, Optional.empty());
      };
   }

   public static Function ofSingle(String id) {
      return (projection) -> {
         return new SinglePoolElement(Either.left(Identifier.of(id)), EMPTY_PROCESSORS, projection, Optional.empty());
      };
   }

   public static Function ofProcessedSingle(String id, RegistryEntry processorListEntry) {
      return (projection) -> {
         return new SinglePoolElement(Either.left(Identifier.of(id)), processorListEntry, projection, Optional.empty());
      };
   }

   public static Function ofSingle(String id, StructureLiquidSettings liquidSettings) {
      return (projection) -> {
         return new SinglePoolElement(Either.left(Identifier.of(id)), EMPTY_PROCESSORS, projection, Optional.of(liquidSettings));
      };
   }

   public static Function ofProcessedSingle(String id, RegistryEntry processorListEntry, StructureLiquidSettings liquidSettings) {
      return (projection) -> {
         return new SinglePoolElement(Either.left(Identifier.of(id)), processorListEntry, projection, Optional.of(liquidSettings));
      };
   }

   public static Function ofFeature(RegistryEntry placedFeatureEntry) {
      return (projection) -> {
         return new FeaturePoolElement(placedFeatureEntry, projection);
      };
   }

   public static Function ofList(List elementGetters) {
      return (projection) -> {
         return new ListPoolElement((List)elementGetters.stream().map((elementGetter) -> {
            return (StructurePoolElement)elementGetter.apply(projection);
         }).collect(Collectors.toList()), projection);
      };
   }

   static {
      CODEC = Registries.STRUCTURE_POOL_ELEMENT.getCodec().dispatch("element_type", StructurePoolElement::getType, StructurePoolElementType::codec);
      EMPTY_PROCESSORS = RegistryEntry.of(new StructureProcessorList(List.of()));
   }
}
