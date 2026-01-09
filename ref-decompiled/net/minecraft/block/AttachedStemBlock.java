package net.minecraft.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class AttachedStemBlock extends PlantBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("fruit").forGetter((block) -> {
         return block.gourdBlock;
      }), RegistryKey.createCodec(RegistryKeys.BLOCK).fieldOf("stem").forGetter((block) -> {
         return block.stemBlock;
      }), RegistryKey.createCodec(RegistryKeys.ITEM).fieldOf("seed").forGetter((block) -> {
         return block.pickBlockItem;
      }), createSettingsCodec()).apply(instance, AttachedStemBlock::new);
   });
   public static final EnumProperty FACING;
   private static final Map SHAPES_BY_DIRECTION;
   private final RegistryKey gourdBlock;
   private final RegistryKey stemBlock;
   private final RegistryKey pickBlockItem;

   public MapCodec getCodec() {
      return CODEC;
   }

   public AttachedStemBlock(RegistryKey stemBlock, RegistryKey gourdBlock, RegistryKey pickBlockItem, AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH));
      this.stemBlock = stemBlock;
      this.gourdBlock = gourdBlock;
      this.pickBlockItem = pickBlockItem;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_DIRECTION.get(state.get(FACING));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (!neighborState.matchesKey(this.gourdBlock) && direction == state.get(FACING)) {
         Optional optional = world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK).getOptionalValue(this.stemBlock);
         if (optional.isPresent()) {
            return (BlockState)((Block)optional.get()).getDefaultState().withIfExists(StemBlock.AGE, 7);
         }
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return floor.isOf(Blocks.FARMLAND);
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return new ItemStack((ItemConvertible)DataFixUtils.orElse(world.getRegistryManager().getOrThrow(RegistryKeys.ITEM).getOptionalValue(this.pickBlockItem), this));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING);
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(4.0, 0.0, 10.0, 0.0, 10.0));
   }
}
