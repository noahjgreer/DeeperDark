package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class CocoaTreeDecorator extends TreeDecorator {
   public static final MapCodec CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CocoaTreeDecorator::new, (decorator) -> {
      return decorator.probability;
   });
   private final float probability;

   public CocoaTreeDecorator(float probability) {
      this.probability = probability;
   }

   protected TreeDecoratorType getType() {
      return TreeDecoratorType.COCOA;
   }

   public void generate(TreeDecorator.Generator generator) {
      Random random = generator.getRandom();
      if (!(random.nextFloat() >= this.probability)) {
         List list = generator.getLogPositions();
         if (!list.isEmpty()) {
            int i = ((BlockPos)list.getFirst()).getY();
            list.stream().filter((pos) -> {
               return pos.getY() - i <= 2;
            }).forEach((pos) -> {
               Iterator var3 = Direction.Type.HORIZONTAL.iterator();

               while(var3.hasNext()) {
                  Direction direction = (Direction)var3.next();
                  if (random.nextFloat() <= 0.25F) {
                     Direction direction2 = direction.getOpposite();
                     BlockPos blockPos = pos.add(direction2.getOffsetX(), 0, direction2.getOffsetZ());
                     if (generator.isAir(blockPos)) {
                        generator.replace(blockPos, (BlockState)((BlockState)Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, random.nextInt(3))).with(CocoaBlock.FACING, direction));
                     }
                  }
               }

            });
         }
      }
   }
}
