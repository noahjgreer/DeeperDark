package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class DesertWellFeature extends Feature {
   private static final BlockStatePredicate CAN_GENERATE;
   private final BlockState sand;
   private final BlockState slab;
   private final BlockState wall;
   private final BlockState fluidInside;

   public DesertWellFeature(Codec codec) {
      super(codec);
      this.sand = Blocks.SAND.getDefaultState();
      this.slab = Blocks.SANDSTONE_SLAB.getDefaultState();
      this.wall = Blocks.SANDSTONE.getDefaultState();
      this.fluidInside = Blocks.WATER.getDefaultState();
   }

   public boolean generate(FeatureContext context) {
      StructureWorldAccess structureWorldAccess = context.getWorld();
      BlockPos blockPos = context.getOrigin();

      for(blockPos = blockPos.up(); structureWorldAccess.isAir(blockPos) && blockPos.getY() > structureWorldAccess.getBottomY() + 2; blockPos = blockPos.down()) {
      }

      if (!CAN_GENERATE.test(structureWorldAccess.getBlockState(blockPos))) {
         return false;
      } else {
         int i;
         int j;
         for(i = -2; i <= 2; ++i) {
            for(j = -2; j <= 2; ++j) {
               if (structureWorldAccess.isAir(blockPos.add(i, -1, j)) && structureWorldAccess.isAir(blockPos.add(i, -2, j))) {
                  return false;
               }
            }
         }

         int k;
         for(i = -2; i <= 0; ++i) {
            for(j = -2; j <= 2; ++j) {
               for(k = -2; k <= 2; ++k) {
                  structureWorldAccess.setBlockState(blockPos.add(j, i, k), this.wall, 2);
               }
            }
         }

         structureWorldAccess.setBlockState(blockPos, this.fluidInside, 2);
         Iterator var8 = Direction.Type.HORIZONTAL.iterator();

         while(var8.hasNext()) {
            Direction direction = (Direction)var8.next();
            structureWorldAccess.setBlockState(blockPos.offset(direction), this.fluidInside, 2);
         }

         BlockPos blockPos2 = blockPos.down();
         structureWorldAccess.setBlockState(blockPos2, this.sand, 2);
         Iterator var11 = Direction.Type.HORIZONTAL.iterator();

         while(var11.hasNext()) {
            Direction direction2 = (Direction)var11.next();
            structureWorldAccess.setBlockState(blockPos2.offset(direction2), this.sand, 2);
         }

         for(j = -2; j <= 2; ++j) {
            for(k = -2; k <= 2; ++k) {
               if (j == -2 || j == 2 || k == -2 || k == 2) {
                  structureWorldAccess.setBlockState(blockPos.add(j, 1, k), this.wall, 2);
               }
            }
         }

         structureWorldAccess.setBlockState(blockPos.add(2, 1, 0), this.slab, 2);
         structureWorldAccess.setBlockState(blockPos.add(-2, 1, 0), this.slab, 2);
         structureWorldAccess.setBlockState(blockPos.add(0, 1, 2), this.slab, 2);
         structureWorldAccess.setBlockState(blockPos.add(0, 1, -2), this.slab, 2);

         for(j = -1; j <= 1; ++j) {
            for(k = -1; k <= 1; ++k) {
               if (j == 0 && k == 0) {
                  structureWorldAccess.setBlockState(blockPos.add(j, 4, k), this.wall, 2);
               } else {
                  structureWorldAccess.setBlockState(blockPos.add(j, 4, k), this.slab, 2);
               }
            }
         }

         for(j = 1; j <= 3; ++j) {
            structureWorldAccess.setBlockState(blockPos.add(-1, j, -1), this.wall, 2);
            structureWorldAccess.setBlockState(blockPos.add(-1, j, 1), this.wall, 2);
            structureWorldAccess.setBlockState(blockPos.add(1, j, -1), this.wall, 2);
            structureWorldAccess.setBlockState(blockPos.add(1, j, 1), this.wall, 2);
         }

         List list = List.of(blockPos, blockPos.east(), blockPos.south(), blockPos.west(), blockPos.north());
         Random random = context.getRandom();
         generateSuspiciousSand(structureWorldAccess, ((BlockPos)Util.getRandom(list, random)).down(1));
         generateSuspiciousSand(structureWorldAccess, ((BlockPos)Util.getRandom(list, random)).down(2));
         return true;
      }
   }

   private static void generateSuspiciousSand(StructureWorldAccess world, BlockPos pos) {
      world.setBlockState(pos, Blocks.SUSPICIOUS_SAND.getDefaultState(), 3);
      world.getBlockEntity(pos, BlockEntityType.BRUSHABLE_BLOCK).ifPresent((blockEntity) -> {
         blockEntity.setLootTable(LootTables.DESERT_WELL_ARCHAEOLOGY, pos.asLong());
      });
   }

   static {
      CAN_GENERATE = BlockStatePredicate.forBlock(Blocks.SAND);
   }
}
