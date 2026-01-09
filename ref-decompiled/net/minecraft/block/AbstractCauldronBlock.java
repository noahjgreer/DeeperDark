package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractCauldronBlock extends Block {
   protected static final int field_30988 = 4;
   private static final VoxelShape RAYCAST_SHAPE = Block.createColumnShape(12.0, 4.0, 16.0);
   protected static final VoxelShape OUTLINE_SHAPE = (VoxelShape)Util.make(() -> {
      int i = true;
      int j = true;
      int k = true;
      return VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(Block.createColumnShape(16.0, 8.0, 0.0, 3.0), Block.createColumnShape(8.0, 16.0, 0.0, 3.0), Block.createColumnShape(12.0, 0.0, 3.0), RAYCAST_SHAPE), BooleanBiFunction.ONLY_FIRST);
   });
   protected final CauldronBehavior.CauldronBehaviorMap behaviorMap;

   protected abstract MapCodec getCodec();

   public AbstractCauldronBlock(AbstractBlock.Settings settings, CauldronBehavior.CauldronBehaviorMap behaviorMap) {
      super(settings);
      this.behaviorMap = behaviorMap;
   }

   protected double getFluidHeight(BlockState state) {
      return 0.0;
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      CauldronBehavior cauldronBehavior = (CauldronBehavior)this.behaviorMap.map().get(stack.getItem());
      return cauldronBehavior.interact(state, world, pos, player, hand, stack);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return OUTLINE_SHAPE;
   }

   protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
      return RAYCAST_SHAPE;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   public abstract boolean isFull(BlockState state);

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      BlockPos blockPos = PointedDripstoneBlock.getDripPos(world, pos);
      if (blockPos != null) {
         Fluid fluid = PointedDripstoneBlock.getDripFluid(world, blockPos);
         if (fluid != Fluids.EMPTY && this.canBeFilledByDripstone(fluid)) {
            this.fillFromDripstone(state, world, pos, fluid);
         }

      }
   }

   protected boolean canBeFilledByDripstone(Fluid fluid) {
      return false;
   }

   protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
   }
}
