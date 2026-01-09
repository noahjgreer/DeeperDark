package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.StructureLocator;
import net.minecraft.world.StructurePresence;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

public class StructureAccessor {
   private final WorldAccess world;
   private final GeneratorOptions options;
   private final StructureLocator locator;

   public StructureAccessor(WorldAccess world, GeneratorOptions options, StructureLocator locator) {
      this.world = world;
      this.options = options;
      this.locator = locator;
   }

   public StructureAccessor forRegion(ChunkRegion region) {
      if (region.toServerWorld() != this.world) {
         String var10002 = String.valueOf(region.toServerWorld());
         throw new IllegalStateException("Using invalid structure manager (source level: " + var10002 + ", region: " + String.valueOf(region));
      } else {
         return new StructureAccessor(region, this.options, this.locator);
      }
   }

   public List getStructureStarts(ChunkPos pos, Predicate predicate) {
      Map map = this.world.getChunk(pos.x, pos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences();
      ImmutableList.Builder builder = ImmutableList.builder();
      Iterator var5 = map.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry entry = (Map.Entry)var5.next();
         Structure structure = (Structure)entry.getKey();
         if (predicate.test(structure)) {
            LongSet var10002 = (LongSet)entry.getValue();
            Objects.requireNonNull(builder);
            this.acceptStructureStarts(structure, var10002, builder::add);
         }
      }

      return builder.build();
   }

   public List getStructureStarts(ChunkSectionPos sectionPos, Structure structure) {
      LongSet longSet = this.world.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(structure);
      ImmutableList.Builder builder = ImmutableList.builder();
      Objects.requireNonNull(builder);
      this.acceptStructureStarts(structure, longSet, builder::add);
      return builder.build();
   }

   public void acceptStructureStarts(Structure structure, LongSet structureStartPositions, Consumer consumer) {
      LongIterator var4 = structureStartPositions.iterator();

      while(var4.hasNext()) {
         long l = (Long)var4.next();
         ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(new ChunkPos(l), this.world.getBottomSectionCoord());
         StructureStart structureStart = this.getStructureStart(chunkSectionPos, structure, this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS));
         if (structureStart != null && structureStart.hasChildren()) {
            consumer.accept(structureStart);
         }
      }

   }

   @Nullable
   public StructureStart getStructureStart(ChunkSectionPos pos, Structure structure, StructureHolder holder) {
      return holder.getStructureStart(structure);
   }

   public void setStructureStart(ChunkSectionPos pos, Structure structure, StructureStart structureStart, StructureHolder holder) {
      holder.setStructureStart(structure, structureStart);
   }

   public void addStructureReference(ChunkSectionPos pos, Structure structure, long reference, StructureHolder holder) {
      holder.addStructureReference(structure, reference);
   }

   public boolean shouldGenerateStructures() {
      return this.options.shouldGenerateStructures();
   }

   public StructureStart getStructureAt(BlockPos pos, Structure structure) {
      Iterator var3 = this.getStructureStarts(ChunkSectionPos.from(pos), structure).iterator();

      StructureStart structureStart;
      do {
         if (!var3.hasNext()) {
            return StructureStart.DEFAULT;
         }

         structureStart = (StructureStart)var3.next();
      } while(!structureStart.getBoundingBox().contains(pos));

      return structureStart;
   }

   public StructureStart getStructureContaining(BlockPos pos, TagKey tag) {
      return this.getStructureContaining(pos, (structure) -> {
         return structure.isIn(tag);
      });
   }

   public StructureStart getStructureContaining(BlockPos pos, RegistryEntryList structures) {
      Objects.requireNonNull(structures);
      return this.getStructureContaining(pos, structures::contains);
   }

   public StructureStart getStructureContaining(BlockPos pos, Predicate predicate) {
      Registry registry = this.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
      Iterator var4 = this.getStructureStarts(new ChunkPos(pos), (structure) -> {
         Optional var10000 = registry.getEntry(registry.getRawId(structure));
         Objects.requireNonNull(predicate);
         return (Boolean)var10000.map(predicate::test).orElse(false);
      }).iterator();

      StructureStart structureStart;
      do {
         if (!var4.hasNext()) {
            return StructureStart.DEFAULT;
         }

         structureStart = (StructureStart)var4.next();
      } while(!this.structureContains(pos, structureStart));

      return structureStart;
   }

   public StructureStart getStructureContaining(BlockPos pos, Structure structure) {
      Iterator var3 = this.getStructureStarts(ChunkSectionPos.from(pos), structure).iterator();

      StructureStart structureStart;
      do {
         if (!var3.hasNext()) {
            return StructureStart.DEFAULT;
         }

         structureStart = (StructureStart)var3.next();
      } while(!this.structureContains(pos, structureStart));

      return structureStart;
   }

   public boolean structureContains(BlockPos pos, StructureStart structureStart) {
      Iterator var3 = structureStart.getChildren().iterator();

      StructurePiece structurePiece;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         structurePiece = (StructurePiece)var3.next();
      } while(!structurePiece.getBoundingBox().contains(pos));

      return true;
   }

   public boolean hasStructureReferences(BlockPos pos) {
      ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
      return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).hasStructureReferences();
   }

   public Map getStructureReferences(BlockPos pos) {
      ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
      return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences();
   }

   public StructurePresence getStructurePresence(ChunkPos chunkPos, Structure structure, StructurePlacement placement, boolean skipReferencedStructures) {
      return this.locator.getStructurePresence(chunkPos, structure, placement, skipReferencedStructures);
   }

   public void incrementReferences(StructureStart structureStart) {
      structureStart.incrementReferences();
      this.locator.incrementReferences(structureStart.getPos(), structureStart.getStructure());
   }

   public DynamicRegistryManager getRegistryManager() {
      return this.world.getRegistryManager();
   }
}
