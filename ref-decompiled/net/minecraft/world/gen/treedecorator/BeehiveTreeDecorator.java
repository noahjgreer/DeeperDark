package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class BeehiveTreeDecorator extends TreeDecorator {
   public static final MapCodec CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(BeehiveTreeDecorator::new, (decorator) -> {
      return decorator.probability;
   });
   private static final Direction BEE_NEST_FACE;
   private static final Direction[] GENERATE_DIRECTIONS;
   private final float probability;

   public BeehiveTreeDecorator(float probability) {
      this.probability = probability;
   }

   protected TreeDecoratorType getType() {
      return TreeDecoratorType.BEEHIVE;
   }

   public void generate(TreeDecorator.Generator generator) {
      List list = generator.getLeavesPositions();
      List list2 = generator.getLogPositions();
      if (!list2.isEmpty()) {
         Random random = generator.getRandom();
         if (!(random.nextFloat() >= this.probability)) {
            int i = !list.isEmpty() ? Math.max(((BlockPos)list.getFirst()).getY() - 1, ((BlockPos)list2.getFirst()).getY() + 1) : Math.min(((BlockPos)list2.getFirst()).getY() + 1 + random.nextInt(3), ((BlockPos)list2.getLast()).getY());
            List list3 = (List)list2.stream().filter((pos) -> {
               return pos.getY() == i;
            }).flatMap((pos) -> {
               Stream var10000 = Stream.of(GENERATE_DIRECTIONS);
               Objects.requireNonNull(pos);
               return var10000.map(pos::offset);
            }).collect(Collectors.toList());
            if (!list3.isEmpty()) {
               Util.shuffle(list3, random);
               Optional optional = list3.stream().filter((pos) -> {
                  return generator.isAir(pos) && generator.isAir(pos.offset(BEE_NEST_FACE));
               }).findFirst();
               if (!optional.isEmpty()) {
                  generator.replace((BlockPos)optional.get(), (BlockState)Blocks.BEE_NEST.getDefaultState().with(BeehiveBlock.FACING, BEE_NEST_FACE));
                  generator.getWorld().getBlockEntity((BlockPos)optional.get(), BlockEntityType.BEEHIVE).ifPresent((blockEntity) -> {
                     int i = 2 + random.nextInt(2);

                     for(int j = 0; j < i; ++j) {
                        blockEntity.addBee(BeehiveBlockEntity.BeeData.create(random.nextInt(599)));
                     }

                  });
               }
            }
         }
      }
   }

   static {
      BEE_NEST_FACE = Direction.SOUTH;
      GENERATE_DIRECTIONS = (Direction[])Direction.Type.HORIZONTAL.stream().filter((direction) -> {
         return direction != BEE_NEST_FACE.getOpposite();
      }).toArray((i) -> {
         return new Direction[i];
      });
   }
}
