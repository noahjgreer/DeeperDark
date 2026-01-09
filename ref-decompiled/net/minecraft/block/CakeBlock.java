package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

public class CakeBlock extends Block {
   public static final MapCodec CODEC = createCodec(CakeBlock::new);
   public static final int MAX_BITES = 6;
   public static final IntProperty BITES;
   public static final int DEFAULT_COMPARATOR_OUTPUT;
   private static final VoxelShape[] SHAPES_BY_BITES;

   public MapCodec getCodec() {
      return CODEC;
   }

   public CakeBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(BITES, 0));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPES_BY_BITES[(Integer)state.get(BITES)];
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      Item item = stack.getItem();
      if (stack.isIn(ItemTags.CANDLES) && (Integer)state.get(BITES) == 0) {
         Block var10 = Block.getBlockFromItem(item);
         if (var10 instanceof CandleBlock) {
            CandleBlock candleBlock = (CandleBlock)var10;
            stack.decrementUnlessCreative(1, player);
            world.playSound((Entity)null, pos, SoundEvents.BLOCK_CAKE_ADD_CANDLE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.setBlockState(pos, CandleCakeBlock.getCandleCakeFromCandle(candleBlock));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ActionResult.SUCCESS;
         }
      }

      return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (world.isClient) {
         if (tryEat(world, pos, state, player).isAccepted()) {
            return ActionResult.SUCCESS;
         }

         if (player.getStackInHand(Hand.MAIN_HAND).isEmpty()) {
            return ActionResult.CONSUME;
         }
      }

      return tryEat(world, pos, state, player);
   }

   protected static ActionResult tryEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!player.canConsume(false)) {
         return ActionResult.PASS;
      } else {
         player.incrementStat(Stats.EAT_CAKE_SLICE);
         player.getHungerManager().add(2, 0.1F);
         int i = (Integer)state.get(BITES);
         world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.EAT, (BlockPos)pos);
         if (i < 6) {
            world.setBlockState(pos, (BlockState)state.with(BITES, i + 1), 3);
         } else {
            world.removeBlock(pos, false);
            world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.BLOCK_DESTROY, (BlockPos)pos);
         }

         return ActionResult.SUCCESS;
      }
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return world.getBlockState(pos.down()).isSolid();
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(BITES);
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return getComparatorOutput((Integer)state.get(BITES));
   }

   public static int getComparatorOutput(int bites) {
      return (7 - bites) * 2;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      BITES = Properties.BITES;
      DEFAULT_COMPARATOR_OUTPUT = getComparatorOutput(0);
      SHAPES_BY_BITES = Block.createShapeArray(6, (bites) -> {
         return Block.createCuboidShape((double)(1 + bites * 2), 0.0, 1.0, 15.0, 8.0, 15.0);
      });
   }
}
