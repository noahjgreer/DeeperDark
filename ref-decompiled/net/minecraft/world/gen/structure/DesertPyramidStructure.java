package net.minecraft.world.gen.structure;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.util.Util;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DesertPyramidStructure extends BasicTempleStructure {
   public static final MapCodec CODEC = createCodec(DesertPyramidStructure::new);

   public DesertPyramidStructure(Structure.Config config) {
      super(DesertTempleGenerator::new, 21, 21, config);
   }

   public void postPlace(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos, StructurePiecesList pieces) {
      Set set = SortedArraySet.create(Vec3i::compareTo);
      Iterator var9 = pieces.pieces().iterator();

      while(var9.hasNext()) {
         StructurePiece structurePiece = (StructurePiece)var9.next();
         if (structurePiece instanceof DesertTempleGenerator desertTempleGenerator) {
            set.addAll(desertTempleGenerator.getPotentialSuspiciousSandPositions());
            placeSuspiciousSand(box, world, desertTempleGenerator.getBasementMarkerPos());
         }
      }

      ObjectArrayList objectArrayList = new ObjectArrayList(set.stream().toList());
      Random random2 = Random.create(world.getSeed()).nextSplitter().split(pieces.getBoundingBox().getCenter());
      Util.shuffle((List)objectArrayList, random2);
      int i = Math.min(set.size(), random2.nextBetweenExclusive(5, 8));
      ObjectListIterator var12 = objectArrayList.iterator();

      while(var12.hasNext()) {
         BlockPos blockPos = (BlockPos)var12.next();
         if (i > 0) {
            --i;
            placeSuspiciousSand(box, world, blockPos);
         } else if (box.contains(blockPos)) {
            world.setBlockState(blockPos, Blocks.SAND.getDefaultState(), 2);
         }
      }

   }

   private static void placeSuspiciousSand(BlockBox box, StructureWorldAccess world, BlockPos pos) {
      if (box.contains(pos)) {
         world.setBlockState(pos, Blocks.SUSPICIOUS_SAND.getDefaultState(), 2);
         world.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK).ifPresent((blockEntity) -> {
            blockEntity.setLootTable(LootTables.DESERT_PYRAMID_ARCHAEOLOGY, pos.asLong());
         });
      }

   }

   public StructureType getType() {
      return StructureType.DESERT_PYRAMID;
   }
}
