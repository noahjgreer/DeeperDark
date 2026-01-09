package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.Sherds;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
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
import org.jetbrains.annotations.Nullable;

public class DecoratedPotBlock extends BlockWithEntity implements Waterloggable {
   public static final MapCodec CODEC = createCodec(DecoratedPotBlock::new);
   public static final Identifier SHERDS_DYNAMIC_DROP_ID = Identifier.ofVanilla("sherds");
   public static final EnumProperty FACING;
   public static final BooleanProperty CRACKED;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;

   public MapCodec getCodec() {
      return CODEC;
   }

   public DecoratedPotBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(WATERLOGGED, false)).with(CRACKED, false));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
      return (BlockState)((BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing())).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)).with(CRACKED, false);
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      BlockEntity var9 = world.getBlockEntity(pos);
      if (var9 instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
         if (world.isClient) {
            return ActionResult.SUCCESS;
         } else {
            ItemStack itemStack = decoratedPotBlockEntity.getStack();
            if (!stack.isEmpty() && (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount())) {
               decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleType.POSITIVE);
               player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
               ItemStack itemStack2 = stack.splitUnlessCreative(1, player);
               float f;
               if (decoratedPotBlockEntity.isEmpty()) {
                  decoratedPotBlockEntity.setStack(itemStack2);
                  f = (float)itemStack2.getCount() / (float)itemStack2.getMaxCount();
               } else {
                  itemStack.increment(1);
                  f = (float)itemStack.getCount() / (float)itemStack.getMaxCount();
               }

               world.playSound((Entity)null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0F, 0.7F + 0.5F * f);
               if (world instanceof ServerWorld) {
                  ServerWorld serverWorld = (ServerWorld)world;
                  serverWorld.spawnParticles(ParticleTypes.DUST_PLUME, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0);
               }

               decoratedPotBlockEntity.markDirty();
               world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
               return ActionResult.SUCCESS;
            } else {
               return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
         }
      } else {
         return ActionResult.PASS;
      }
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      BlockEntity var7 = world.getBlockEntity(pos);
      if (var7 instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
         decoratedPotBlockEntity.wobble(DecoratedPotBlockEntity.WobbleType.NEGATIVE);
         world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
         return ActionResult.SUCCESS;
      } else {
         return ActionResult.PASS;
      }
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, WATERLOGGED, CRACKED);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new DecoratedPotBlockEntity(pos, state);
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      ItemScatterer.onStateReplaced(state, world, pos);
   }

   protected List getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
      BlockEntity blockEntity = (BlockEntity)builder.getOptional(LootContextParameters.BLOCK_ENTITY);
      if (blockEntity instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
         builder.addDynamicDrop(SHERDS_DYNAMIC_DROP_ID, (lootConsumer) -> {
            Iterator var2 = decoratedPotBlockEntity.getSherds().toList().iterator();

            while(var2.hasNext()) {
               Item item = (Item)var2.next();
               lootConsumer.accept(item.getDefaultStack());
            }

         });
      }

      return super.getDroppedStacks(state, builder);
   }

   public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      ItemStack itemStack = player.getMainHandStack();
      BlockState blockState = state;
      if (itemStack.isIn(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasAnyEnchantmentsIn(itemStack, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING)) {
         blockState = (BlockState)state.with(CRACKED, true);
         world.setBlockState(pos, blockState, 260);
      }

      return super.onBreak(world, pos, blockState, player);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected BlockSoundGroup getSoundGroup(BlockState state) {
      return (Boolean)state.get(CRACKED) ? BlockSoundGroup.DECORATED_POT_SHATTER : BlockSoundGroup.DECORATED_POT;
   }

   protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
      BlockPos blockPos = hit.getBlockPos();
      if (world instanceof ServerWorld serverWorld) {
         if (projectile.canModifyAt(serverWorld, blockPos) && projectile.canBreakBlocks(serverWorld)) {
            world.setBlockState(blockPos, (BlockState)state.with(CRACKED, true), 260);
            world.breakBlock(blockPos, true, projectile);
         }
      }

   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      BlockEntity var6 = world.getBlockEntity(pos);
      if (var6 instanceof DecoratedPotBlockEntity decoratedPotBlockEntity) {
         Sherds sherds = decoratedPotBlockEntity.getSherds();
         return DecoratedPotBlockEntity.getStackWith(sherds);
      } else {
         return super.getPickStack(world, pos, state, includeData);
      }
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   static {
      FACING = Properties.HORIZONTAL_FACING;
      CRACKED = Properties.CRACKED;
      WATERLOGGED = Properties.WATERLOGGED;
      SHAPE = Block.createColumnShape(14.0, 0.0, 16.0);
   }
}
