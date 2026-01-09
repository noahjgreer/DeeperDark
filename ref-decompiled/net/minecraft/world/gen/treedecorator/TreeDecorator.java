package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;

public abstract class TreeDecorator {
   public static final Codec TYPE_CODEC;

   protected abstract TreeDecoratorType getType();

   public abstract void generate(Generator generator);

   static {
      TYPE_CODEC = Registries.TREE_DECORATOR_TYPE.getCodec().dispatch(TreeDecorator::getType, TreeDecoratorType::getCodec);
   }

   public static final class Generator {
      private final TestableWorld world;
      private final BiConsumer replacer;
      private final Random random;
      private final ObjectArrayList logPositions;
      private final ObjectArrayList leavesPositions;
      private final ObjectArrayList rootPositions;

      public Generator(TestableWorld world, BiConsumer replacer, Random random, Set logPositions, Set leavesPositions, Set rootPositions) {
         this.world = world;
         this.replacer = replacer;
         this.random = random;
         this.rootPositions = new ObjectArrayList(rootPositions);
         this.logPositions = new ObjectArrayList(logPositions);
         this.leavesPositions = new ObjectArrayList(leavesPositions);
         this.logPositions.sort(Comparator.comparingInt(Vec3i::getY));
         this.leavesPositions.sort(Comparator.comparingInt(Vec3i::getY));
         this.rootPositions.sort(Comparator.comparingInt(Vec3i::getY));
      }

      public void replaceWithVine(BlockPos pos, BooleanProperty faceProperty) {
         this.replace(pos, (BlockState)Blocks.VINE.getDefaultState().with(faceProperty, true));
      }

      public void replace(BlockPos pos, BlockState state) {
         this.replacer.accept(pos, state);
      }

      public boolean isAir(BlockPos pos) {
         return this.world.testBlockState(pos, AbstractBlock.AbstractBlockState::isAir);
      }

      public boolean matches(BlockPos pos, Predicate statePredicate) {
         return this.world.testBlockState(pos, statePredicate);
      }

      public TestableWorld getWorld() {
         return this.world;
      }

      public Random getRandom() {
         return this.random;
      }

      public ObjectArrayList getLogPositions() {
         return this.logPositions;
      }

      public ObjectArrayList getLeavesPositions() {
         return this.leavesPositions;
      }

      public ObjectArrayList getRootPositions() {
         return this.rootPositions;
      }
   }
}
