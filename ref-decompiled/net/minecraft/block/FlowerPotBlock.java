package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

public class FlowerPotBlock extends Block {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Registries.BLOCK.getCodec().fieldOf("potted").forGetter((block) -> {
         return block.content;
      }), createSettingsCodec()).apply(instance, FlowerPotBlock::new);
   });
   private static final Map CONTENT_TO_POTTED = Maps.newHashMap();
   private static final VoxelShape SHAPE = Block.createColumnShape(6.0, 0.0, 6.0);
   private final Block content;

   public MapCodec getCodec() {
      return CODEC;
   }

   public FlowerPotBlock(Block content, AbstractBlock.Settings settings) {
      super(settings);
      this.content = content;
      CONTENT_TO_POTTED.put(content, this);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      Item var10 = stack.getItem();
      Block var10000;
      if (var10 instanceof BlockItem blockItem) {
         var10000 = (Block)CONTENT_TO_POTTED.getOrDefault(blockItem.getBlock(), Blocks.AIR);
      } else {
         var10000 = Blocks.AIR;
      }

      BlockState blockState = var10000.getDefaultState();
      if (blockState.isAir()) {
         return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
      } else if (!this.isEmpty()) {
         return ActionResult.CONSUME;
      } else {
         world.setBlockState(pos, blockState, 3);
         world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
         player.incrementStat(Stats.POT_FLOWER);
         stack.decrementUnlessCreative(1, player);
         return ActionResult.SUCCESS;
      }
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      if (this.isEmpty()) {
         return ActionResult.CONSUME;
      } else {
         ItemStack itemStack = new ItemStack(this.content);
         if (!player.giveItemStack(itemStack)) {
            player.dropItem(itemStack, false);
         }

         world.setBlockState(pos, Blocks.FLOWER_POT.getDefaultState(), 3);
         world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
         return ActionResult.SUCCESS;
      }
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      return this.isEmpty() ? super.getPickStack(world, pos, state, includeData) : new ItemStack(this.content);
   }

   private boolean isEmpty() {
      return this.content == Blocks.AIR;
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   public Block getContent() {
      return this.content;
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   protected boolean hasRandomTicks(BlockState state) {
      return state.isOf(Blocks.POTTED_OPEN_EYEBLOSSOM) || state.isOf(Blocks.POTTED_CLOSED_EYEBLOSSOM);
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (this.hasRandomTicks(state) && world.getDimension().natural()) {
         boolean bl = this.content == Blocks.OPEN_EYEBLOSSOM;
         boolean bl2 = CreakingHeartBlock.isNightAndNatural(world);
         if (bl != bl2) {
            world.setBlockState(pos, this.getToggledState(state), 3);
            EyeblossomBlock.EyeblossomState eyeblossomState = EyeblossomBlock.EyeblossomState.of(bl).getOpposite();
            eyeblossomState.spawnTrailParticle(world, pos, random);
            world.playSound((Entity)null, pos, eyeblossomState.getLongSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

      super.randomTick(state, world, pos, random);
   }

   public BlockState getToggledState(BlockState state) {
      if (state.isOf(Blocks.POTTED_OPEN_EYEBLOSSOM)) {
         return Blocks.POTTED_CLOSED_EYEBLOSSOM.getDefaultState();
      } else {
         return state.isOf(Blocks.POTTED_CLOSED_EYEBLOSSOM) ? Blocks.POTTED_OPEN_EYEBLOSSOM.getDefaultState() : state;
      }
   }
}
