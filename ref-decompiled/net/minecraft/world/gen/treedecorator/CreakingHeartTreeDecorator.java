package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.block.enums.CreakingHeartState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class CreakingHeartTreeDecorator extends TreeDecorator {
   public static final MapCodec CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(CreakingHeartTreeDecorator::new, (treeDecorator) -> {
      return treeDecorator.probability;
   });
   private final float probability;

   public CreakingHeartTreeDecorator(float probability) {
      this.probability = probability;
   }

   protected TreeDecoratorType getType() {
      return TreeDecoratorType.CREAKING_HEART;
   }

   public void generate(TreeDecorator.Generator generator) {
      Random random = generator.getRandom();
      List list = generator.getLogPositions();
      if (!list.isEmpty()) {
         if (!(random.nextFloat() >= this.probability)) {
            List list2 = new ArrayList(list);
            Util.shuffle((List)list2, random);
            Optional optional = list2.stream().filter((pos) -> {
               Direction[] var2 = Direction.values();
               int var3 = var2.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  Direction direction = var2[var4];
                  if (!generator.matches(pos.offset(direction), (state) -> {
                     return state.isIn(BlockTags.LOGS);
                  })) {
                     return false;
                  }
               }

               return true;
            }).findFirst();
            if (!optional.isEmpty()) {
               generator.replace((BlockPos)optional.get(), (BlockState)((BlockState)Blocks.CREAKING_HEART.getDefaultState().with(CreakingHeartBlock.ACTIVE, CreakingHeartState.DORMANT)).with(CreakingHeartBlock.NATURAL, true));
            }
         }
      }
   }
}
