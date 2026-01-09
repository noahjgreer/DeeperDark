package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class TrunkVineTreeDecorator extends TreeDecorator {
   public static final MapCodec CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final TrunkVineTreeDecorator INSTANCE = new TrunkVineTreeDecorator();

   protected TreeDecoratorType getType() {
      return TreeDecoratorType.TRUNK_VINE;
   }

   public void generate(TreeDecorator.Generator generator) {
      Random random = generator.getRandom();
      generator.getLogPositions().forEach((pos) -> {
         BlockPos blockPos;
         if (random.nextInt(3) > 0) {
            blockPos = pos.west();
            if (generator.isAir(blockPos)) {
               generator.replaceWithVine(blockPos, VineBlock.EAST);
            }
         }

         if (random.nextInt(3) > 0) {
            blockPos = pos.east();
            if (generator.isAir(blockPos)) {
               generator.replaceWithVine(blockPos, VineBlock.WEST);
            }
         }

         if (random.nextInt(3) > 0) {
            blockPos = pos.north();
            if (generator.isAir(blockPos)) {
               generator.replaceWithVine(blockPos, VineBlock.SOUTH);
            }
         }

         if (random.nextInt(3) > 0) {
            blockPos = pos.south();
            if (generator.isAir(blockPos)) {
               generator.replaceWithVine(blockPos, VineBlock.NORTH);
            }
         }

      });
   }
}
