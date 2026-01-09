package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
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
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class CampfireBlock extends BlockWithEntity implements Waterloggable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.fieldOf("spawn_particles").forGetter((block) -> {
         return block.emitsParticles;
      }), Codec.intRange(0, 1000).fieldOf("fire_damage").forGetter((block) -> {
         return block.fireDamage;
      }), createSettingsCodec()).apply(instance, CampfireBlock::new);
   });
   public static final BooleanProperty LIT;
   public static final BooleanProperty SIGNAL_FIRE;
   public static final BooleanProperty WATERLOGGED;
   public static final EnumProperty FACING;
   private static final VoxelShape SHAPE;
   private static final VoxelShape SMOKEY_SHAPE;
   private static final int field_31049 = 5;
   private final boolean emitsParticles;
   private final int fireDamage;

   public MapCodec getCodec() {
      return CODEC;
   }

   public CampfireBlock(boolean emitsParticles, int fireDamage, AbstractBlock.Settings settings) {
      super(settings);
      this.emitsParticles = emitsParticles;
      this.fireDamage = fireDamage;
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LIT, true)).with(SIGNAL_FIRE, false)).with(WATERLOGGED, false)).with(FACING, Direction.NORTH));
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof CampfireBlockEntity campfireBlockEntity) {
         ItemStack itemStack = player.getStackInHand(hand);
         if (world.getRecipeManager().getPropertySet(RecipePropertySet.CAMPFIRE_INPUT).canUse(itemStack)) {
            if (world instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)world;
               if (campfireBlockEntity.addItem(serverWorld, player, itemStack)) {
                  player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
                  return ActionResult.SUCCESS_SERVER;
               }
            }

            return ActionResult.CONSUME;
         }
      }

      return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if ((Boolean)state.get(LIT) && entity instanceof LivingEntity) {
         entity.serverDamage(world.getDamageSources().campfire(), (float)this.fireDamage);
      }

      super.onEntityCollision(state, world, pos, entity, handler);
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      WorldAccess worldAccess = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      boolean bl = worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER;
      return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, bl)).with(SIGNAL_FIRE, this.isSignalFireBaseBlock(worldAccess.getBlockState(blockPos.down())))).with(LIT, !bl)).with(FACING, ctx.getHorizontalPlayerFacing());
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return direction == Direction.DOWN ? (BlockState)state.with(SIGNAL_FIRE, this.isSignalFireBaseBlock(neighborState)) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   private boolean isSignalFireBaseBlock(BlockState state) {
      return state.isOf(Blocks.HAY_BLOCK);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if ((Boolean)state.get(LIT)) {
         if (random.nextInt(10) == 0) {
            world.playSoundClient((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
         }

         if (this.emitsParticles && random.nextInt(5) == 0) {
            for(int i = 0; i < random.nextInt(1) + 1; ++i) {
               world.addParticleClient(ParticleTypes.LAVA, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)(random.nextFloat() / 2.0F), 5.0E-5, (double)(random.nextFloat() / 2.0F));
            }
         }

      }
   }

   public static void extinguish(@Nullable Entity entity, WorldAccess world, BlockPos pos, BlockState state) {
      if (world.isClient()) {
         for(int i = 0; i < 20; ++i) {
            spawnSmokeParticle((World)world, pos, (Boolean)state.get(SIGNAL_FIRE), true);
         }
      }

      world.emitGameEvent((Entity)entity, (RegistryEntry)GameEvent.BLOCK_CHANGE, (BlockPos)pos);
   }

   public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
      if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
         boolean bl = (Boolean)state.get(LIT);
         if (bl) {
            if (!world.isClient()) {
               world.playSound((Entity)null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            extinguish((Entity)null, world, pos, state);
         }

         world.setBlockState(pos, (BlockState)((BlockState)state.with(WATERLOGGED, true)).with(LIT, false), 3);
         world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
         return true;
      } else {
         return false;
      }
   }

   protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
      BlockPos blockPos = hit.getBlockPos();
      if (world instanceof ServerWorld serverWorld) {
         if (projectile.isOnFire() && projectile.canModifyAt(serverWorld, blockPos) && !(Boolean)state.get(LIT) && !(Boolean)state.get(WATERLOGGED)) {
            world.setBlockState(blockPos, (BlockState)state.with(Properties.LIT, true), 11);
         }
      }

   }

   public static void spawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke) {
      Random random = world.getRandom();
      SimpleParticleType simpleParticleType = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
      world.addImportantParticleClient(simpleParticleType, true, (double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
      if (lotsOfSmoke) {
         world.addParticleClient(ParticleTypes.SMOKE, (double)pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4, (double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
      }

   }

   public static boolean isLitCampfireInRange(World world, BlockPos pos) {
      for(int i = 1; i <= 5; ++i) {
         BlockPos blockPos = pos.down(i);
         BlockState blockState = world.getBlockState(blockPos);
         if (isLitCampfire(blockState)) {
            return true;
         }

         boolean bl = VoxelShapes.matchesAnywhere(SMOKEY_SHAPE, blockState.getCollisionShape(world, pos, ShapeContext.absent()), BooleanBiFunction.AND);
         if (bl) {
            BlockState blockState2 = world.getBlockState(blockPos.down());
            return isLitCampfire(blockState2);
         }
      }

      return false;
   }

   public static boolean isLitCampfire(BlockState state) {
      return state.contains(LIT) && state.isIn(BlockTags.CAMPFIRES) && (Boolean)state.get(LIT);
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new CampfireBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      if (world instanceof ServerWorld serverWorld) {
         if ((Boolean)state.get(LIT)) {
            ServerRecipeManager.MatchGetter matchGetter = ServerRecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING);
            return validateTicker(type, BlockEntityType.CAMPFIRE, (worldx, pos, statex, blockEntity) -> {
               CampfireBlockEntity.litServerTick(serverWorld, pos, statex, blockEntity, matchGetter);
            });
         } else {
            return validateTicker(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::unlitServerTick);
         }
      } else {
         return (Boolean)state.get(LIT) ? validateTicker(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::clientTick) : null;
      }
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   public static boolean canBeLit(BlockState state) {
      return state.isIn(BlockTags.CAMPFIRES, (statex) -> {
         return statex.contains(WATERLOGGED) && statex.contains(LIT);
      }) && !(Boolean)state.get(WATERLOGGED) && !(Boolean)state.get(LIT);
   }

   static {
      LIT = Properties.LIT;
      SIGNAL_FIRE = Properties.SIGNAL_FIRE;
      WATERLOGGED = Properties.WATERLOGGED;
      FACING = Properties.HORIZONTAL_FACING;
      SHAPE = Block.createColumnShape(16.0, 0.0, 7.0);
      SMOKEY_SHAPE = Block.createColumnShape(4.0, 0.0, 16.0);
   }
}
