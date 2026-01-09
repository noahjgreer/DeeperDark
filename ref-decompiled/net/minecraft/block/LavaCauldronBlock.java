package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.CollisionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class LavaCauldronBlock extends AbstractCauldronBlock {
   public static final MapCodec CODEC = createCodec(LavaCauldronBlock::new);
   private static final VoxelShape LAVA_SHAPE = Block.createColumnShape(12.0, 4.0, 15.0);
   private static final VoxelShape INSIDE_COLLISION_SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public LavaCauldronBlock(AbstractBlock.Settings settings) {
      super(settings, CauldronBehavior.LAVA_CAULDRON_BEHAVIOR);
   }

   protected double getFluidHeight(BlockState state) {
      return 0.9375;
   }

   public boolean isFull(BlockState state) {
      return true;
   }

   protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
      return INSIDE_COLLISION_SHAPE;
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      handler.addEvent(CollisionEvent.LAVA_IGNITE);
      handler.addPostCallback(CollisionEvent.LAVA_IGNITE, Entity::setOnFireFromLava);
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return 3;
   }

   static {
      INSIDE_COLLISION_SHAPE = VoxelShapes.union(AbstractCauldronBlock.OUTLINE_SHAPE, LAVA_SHAPE);
   }
}
