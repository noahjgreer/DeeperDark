package net.minecraft.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class DoubleBlockProperties {
   public static PropertySource toPropertySource(BlockEntityType blockEntityType, Function typeMapper, Function directionMapper, Property facingProperty, BlockState state, WorldAccess world, BlockPos pos, BiPredicate fallbackTester) {
      BlockEntity blockEntity = blockEntityType.get(world, pos);
      if (blockEntity == null) {
         return PropertyRetriever::getFallback;
      } else if (fallbackTester.test(world, pos)) {
         return PropertyRetriever::getFallback;
      } else {
         Type type = (Type)typeMapper.apply(state);
         boolean bl = type == DoubleBlockProperties.Type.SINGLE;
         boolean bl2 = type == DoubleBlockProperties.Type.FIRST;
         if (bl) {
            return new PropertySource.Single(blockEntity);
         } else {
            BlockPos blockPos = pos.offset((Direction)directionMapper.apply(state));
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(state.getBlock())) {
               Type type2 = (Type)typeMapper.apply(blockState);
               if (type2 != DoubleBlockProperties.Type.SINGLE && type != type2 && blockState.get(facingProperty) == state.get(facingProperty)) {
                  if (fallbackTester.test(world, blockPos)) {
                     return PropertyRetriever::getFallback;
                  }

                  BlockEntity blockEntity2 = blockEntityType.get(world, blockPos);
                  if (blockEntity2 != null) {
                     BlockEntity blockEntity3 = bl2 ? blockEntity : blockEntity2;
                     BlockEntity blockEntity4 = bl2 ? blockEntity2 : blockEntity;
                     return new PropertySource.Pair(blockEntity3, blockEntity4);
                  }
               }
            }

            return new PropertySource.Single(blockEntity);
         }
      }
   }

   public interface PropertySource {
      Object apply(PropertyRetriever retriever);

      public static final class Single implements PropertySource {
         private final Object single;

         public Single(Object single) {
            this.single = single;
         }

         public Object apply(PropertyRetriever propertyRetriever) {
            return propertyRetriever.getFrom(this.single);
         }
      }

      public static final class Pair implements PropertySource {
         private final Object first;
         private final Object second;

         public Pair(Object first, Object second) {
            this.first = first;
            this.second = second;
         }

         public Object apply(PropertyRetriever propertyRetriever) {
            return propertyRetriever.getFromBoth(this.first, this.second);
         }
      }
   }

   public static enum Type {
      SINGLE,
      FIRST,
      SECOND;

      // $FF: synthetic method
      private static Type[] method_36705() {
         return new Type[]{SINGLE, FIRST, SECOND};
      }
   }

   public interface PropertyRetriever {
      Object getFromBoth(Object first, Object second);

      Object getFrom(Object single);

      Object getFallback();
   }
}
