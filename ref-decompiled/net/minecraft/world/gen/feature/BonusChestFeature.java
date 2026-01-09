package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.loot.LootTables;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BonusChestFeature extends Feature {
   public BonusChestFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      Random random = context.getRandom();
      StructureWorldAccess structureWorldAccess = context.getWorld();
      ChunkPos chunkPos = new ChunkPos(context.getOrigin());
      IntArrayList intArrayList = Util.shuffle(IntStream.rangeClosed(chunkPos.getStartX(), chunkPos.getEndX()), random);
      IntArrayList intArrayList2 = Util.shuffle(IntStream.rangeClosed(chunkPos.getStartZ(), chunkPos.getEndZ()), random);
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      IntListIterator var8 = intArrayList.iterator();

      while(var8.hasNext()) {
         Integer integer = (Integer)var8.next();
         IntListIterator var10 = intArrayList2.iterator();

         while(var10.hasNext()) {
            Integer integer2 = (Integer)var10.next();
            mutable.set(integer, 0, integer2);
            BlockPos blockPos = structureWorldAccess.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable);
            if (structureWorldAccess.isAir(blockPos) || structureWorldAccess.getBlockState(blockPos).getCollisionShape(structureWorldAccess, blockPos).isEmpty()) {
               structureWorldAccess.setBlockState(blockPos, Blocks.CHEST.getDefaultState(), 2);
               LootableInventory.setLootTable(structureWorldAccess, random, blockPos, LootTables.SPAWN_BONUS_CHEST);
               BlockState blockState = Blocks.TORCH.getDefaultState();
               Iterator var14 = Direction.Type.HORIZONTAL.iterator();

               while(var14.hasNext()) {
                  Direction direction = (Direction)var14.next();
                  BlockPos blockPos2 = blockPos.offset(direction);
                  if (blockState.canPlaceAt(structureWorldAccess, blockPos2)) {
                     structureWorldAccess.setBlockState(blockPos2, blockState, 2);
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
