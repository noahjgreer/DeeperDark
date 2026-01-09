package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class DaylightDetectorBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(DaylightDetectorBlock::new);
   public static final IntProperty POWER;
   public static final BooleanProperty INVERTED;
   private static final VoxelShape SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public DaylightDetectorBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWER, 0)).with(INVERTED, false));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return true;
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Integer)state.get(POWER);
   }

   private static void updateState(BlockState state, World world, BlockPos pos) {
      int i = world.getLightLevel(LightType.SKY, pos) - world.getAmbientDarkness();
      float f = world.getSkyAngleRadians(1.0F);
      boolean bl = (Boolean)state.get(INVERTED);
      if (bl) {
         i = 15 - i;
      } else if (i > 0) {
         float g = f < 3.1415927F ? 0.0F : 6.2831855F;
         f += (g - f) * 0.2F;
         i = Math.round((float)i * MathHelper.cos(f));
      }

      i = MathHelper.clamp(i, 0, 15);
      if ((Integer)state.get(POWER) != i) {
         world.setBlockState(pos, (BlockState)state.with(POWER, i), 3);
      }

   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (!player.canModifyBlocks()) {
         return super.onUse(state, world, pos, player, hit);
      } else {
         if (!world.isClient) {
            BlockState blockState = (BlockState)state.cycle(INVERTED);
            world.setBlockState(pos, blockState, 2);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, blockState));
            updateState(blockState, world, pos);
         }

         return ActionResult.SUCCESS;
      }
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new DaylightDetectorBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return !world.isClient && world.getDimension().hasSkyLight() ? validateTicker(type, BlockEntityType.DAYLIGHT_DETECTOR, DaylightDetectorBlock::tick) : null;
   }

   private static void tick(World world, BlockPos pos, BlockState state, DaylightDetectorBlockEntity blockEntity) {
      if (world.getTime() % 20L == 0L) {
         updateState(state, world, pos);
      }

   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(POWER, INVERTED);
   }

   static {
      POWER = Properties.POWER;
      INVERTED = Properties.INVERTED;
      SHAPE = Block.createColumnShape(16.0, 0.0, 6.0);
   }
}
