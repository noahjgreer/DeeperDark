package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class WallSkullBlock extends AbstractSkullBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getSkullType), createSettingsCodec()).apply(instance, WallSkullBlock::new);
   });
   public static final EnumProperty FACING;
   private static final Map SHAPES_BY_DIRECTION;

   public MapCodec getCodec() {
      return CODEC;
   }

   public WallSkullBlock(SkullBlock.SkullType skullType, AbstractBlock.Settings settings) {
      super(skullType, settings);
      this.setDefaultState((BlockState)this.getDefaultState().with(FACING, Direction.NORTH));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_DIRECTION.get(state.get(FACING));
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = super.getPlacementState(ctx);
      BlockView blockView = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      Direction[] directions = ctx.getPlacementDirections();
      Direction[] var6 = directions;
      int var7 = directions.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction direction = var6[var8];
         if (direction.getAxis().isHorizontal()) {
            Direction direction2 = direction.getOpposite();
            blockState = (BlockState)blockState.with(FACING, direction2);
            if (!blockView.getBlockState(blockPos.offset(direction)).canReplace(ctx)) {
               return blockState;
            }
         }
      }

      return null;
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      super.appendProperties(builder);
      builder.add(FACING);
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(8.0, 8.0, 16.0));
   }
}
