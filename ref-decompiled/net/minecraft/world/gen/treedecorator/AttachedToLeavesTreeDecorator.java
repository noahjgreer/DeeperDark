package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class AttachedToLeavesTreeDecorator extends TreeDecorator {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((treeDecorator) -> {
         return treeDecorator.probability;
      }), Codec.intRange(0, 16).fieldOf("exclusion_radius_xz").forGetter((treeDecorator) -> {
         return treeDecorator.exclusionRadiusXZ;
      }), Codec.intRange(0, 16).fieldOf("exclusion_radius_y").forGetter((treeDecorator) -> {
         return treeDecorator.exclusionRadiusY;
      }), BlockStateProvider.TYPE_CODEC.fieldOf("block_provider").forGetter((treeDecorator) -> {
         return treeDecorator.blockProvider;
      }), Codec.intRange(1, 16).fieldOf("required_empty_blocks").forGetter((treeDecorator) -> {
         return treeDecorator.requiredEmptyBlocks;
      }), Codecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter((treeDecorator) -> {
         return treeDecorator.directions;
      })).apply(instance, AttachedToLeavesTreeDecorator::new);
   });
   protected final float probability;
   protected final int exclusionRadiusXZ;
   protected final int exclusionRadiusY;
   protected final BlockStateProvider blockProvider;
   protected final int requiredEmptyBlocks;
   protected final List directions;

   public AttachedToLeavesTreeDecorator(float probability, int exclusionRadiusXZ, int exclusionRadiusY, BlockStateProvider blockProvider, int requiredEmptyBlocks, List directions) {
      this.probability = probability;
      this.exclusionRadiusXZ = exclusionRadiusXZ;
      this.exclusionRadiusY = exclusionRadiusY;
      this.blockProvider = blockProvider;
      this.requiredEmptyBlocks = requiredEmptyBlocks;
      this.directions = directions;
   }

   public void generate(TreeDecorator.Generator generator) {
      Set set = new HashSet();
      Random random = generator.getRandom();
      Iterator var4 = Util.copyShuffled(generator.getLeavesPositions(), random).iterator();

      while(true) {
         BlockPos blockPos;
         Direction direction;
         BlockPos blockPos2;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  blockPos = (BlockPos)var4.next();
                  direction = (Direction)Util.getRandom(this.directions, random);
                  blockPos2 = blockPos.offset(direction);
               } while(set.contains(blockPos2));
            } while(!(random.nextFloat() < this.probability));
         } while(!this.meetsRequiredEmptyBlocks(generator, blockPos, direction));

         BlockPos blockPos3 = blockPos2.add(-this.exclusionRadiusXZ, -this.exclusionRadiusY, -this.exclusionRadiusXZ);
         BlockPos blockPos4 = blockPos2.add(this.exclusionRadiusXZ, this.exclusionRadiusY, this.exclusionRadiusXZ);
         Iterator var10 = BlockPos.iterate(blockPos3, blockPos4).iterator();

         while(var10.hasNext()) {
            BlockPos blockPos5 = (BlockPos)var10.next();
            set.add(blockPos5.toImmutable());
         }

         generator.replace(blockPos2, this.blockProvider.get(random, blockPos2));
      }
   }

   private boolean meetsRequiredEmptyBlocks(TreeDecorator.Generator generator, BlockPos pos, Direction direction) {
      for(int i = 1; i <= this.requiredEmptyBlocks; ++i) {
         BlockPos blockPos = pos.offset(direction, i);
         if (!generator.isAir(blockPos)) {
            return false;
         }
      }

      return true;
   }

   protected TreeDecoratorType getType() {
      return TreeDecoratorType.ATTACHED_TO_LEAVES;
   }
}
