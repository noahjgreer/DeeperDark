package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class NetherrackBlock extends Block implements Fertilizable {
   public static final MapCodec CODEC = createCodec(NetherrackBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public NetherrackBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      if (!world.getBlockState(pos.up()).isTransparent()) {
         return false;
      } else {
         Iterator var4 = BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1)).iterator();

         BlockPos blockPos;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            blockPos = (BlockPos)var4.next();
         } while(!world.getBlockState(blockPos).isIn(BlockTags.NYLIUM));

         return true;
      }
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      boolean bl = false;
      boolean bl2 = false;
      Iterator var7 = BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1)).iterator();

      while(var7.hasNext()) {
         BlockPos blockPos = (BlockPos)var7.next();
         BlockState blockState = world.getBlockState(blockPos);
         if (blockState.isOf(Blocks.WARPED_NYLIUM)) {
            bl2 = true;
         }

         if (blockState.isOf(Blocks.CRIMSON_NYLIUM)) {
            bl = true;
         }

         if (bl2 && bl) {
            break;
         }
      }

      if (bl2 && bl) {
         world.setBlockState(pos, random.nextBoolean() ? Blocks.WARPED_NYLIUM.getDefaultState() : Blocks.CRIMSON_NYLIUM.getDefaultState(), 3);
      } else if (bl2) {
         world.setBlockState(pos, Blocks.WARPED_NYLIUM.getDefaultState(), 3);
      } else if (bl) {
         world.setBlockState(pos, Blocks.CRIMSON_NYLIUM.getDefaultState(), 3);
      }

   }

   public Fertilizable.FertilizableType getFertilizableType() {
      return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
   }
}
