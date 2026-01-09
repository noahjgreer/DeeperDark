package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class BeehiveBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(BeehiveBlock::new);
   public static final EnumProperty FACING;
   public static final IntProperty HONEY_LEVEL;
   public static final int FULL_HONEY_LEVEL = 5;
   private static final int DROPPED_HONEYCOMB_COUNT = 3;

   public MapCodec getCodec() {
      return CODEC;
   }

   public BeehiveBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(HONEY_LEVEL, 0)).with(FACING, Direction.NORTH));
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return (Integer)state.get(HONEY_LEVEL);
   }

   public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
      super.afterBreak(world, player, pos, state, blockEntity, tool);
      if (!world.isClient && blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
         if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_BEE_SPAWNS_WHEN_MINING)) {
            beehiveBlockEntity.angerBees(player, state, BeehiveBlockEntity.BeeState.EMERGENCY);
            ItemScatterer.onStateReplaced(state, world, pos);
            this.angerNearbyBees(world, pos);
         }

         Criteria.BEE_NEST_DESTROYED.trigger((ServerPlayerEntity)player, state, tool, beehiveBlockEntity.getBeeCount());
      }

   }

   protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer stackMerger) {
      super.onExploded(state, world, pos, explosion, stackMerger);
      this.angerNearbyBees(world, pos);
   }

   private void angerNearbyBees(World world, BlockPos pos) {
      Box box = (new Box(pos)).expand(8.0, 6.0, 8.0);
      List list = world.getNonSpectatingEntities(BeeEntity.class, box);
      if (!list.isEmpty()) {
         List list2 = world.getNonSpectatingEntities(PlayerEntity.class, box);
         if (list2.isEmpty()) {
            return;
         }

         Iterator var6 = list.iterator();

         while(var6.hasNext()) {
            BeeEntity beeEntity = (BeeEntity)var6.next();
            if (beeEntity.getTarget() == null) {
               PlayerEntity playerEntity = (PlayerEntity)Util.getRandom(list2, world.random);
               beeEntity.setTarget(playerEntity);
            }
         }
      }

   }

   public static void dropHoneycomb(World world, BlockPos pos) {
      dropStack(world, pos, new ItemStack(Items.HONEYCOMB, 3));
   }

   protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      int i = (Integer)state.get(HONEY_LEVEL);
      boolean bl = false;
      if (i >= 5) {
         Item item = stack.getItem();
         if (stack.isOf(Items.SHEARS)) {
            world.playSound(player, player.getX(), player.getY(), player.getZ(), (SoundEvent)SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);
            dropHoneycomb(world, pos);
            stack.damage(1, player, (EquipmentSlot)LivingEntity.getSlotForHand(hand));
            bl = true;
            world.emitGameEvent(player, GameEvent.SHEAR, pos);
         } else if (stack.isOf(Items.GLASS_BOTTLE)) {
            stack.decrement(1);
            world.playSound(player, player.getX(), player.getY(), player.getZ(), (SoundEvent)SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (stack.isEmpty()) {
               player.setStackInHand(hand, new ItemStack(Items.HONEY_BOTTLE));
            } else if (!player.getInventory().insertStack(new ItemStack(Items.HONEY_BOTTLE))) {
               player.dropItem(new ItemStack(Items.HONEY_BOTTLE), false);
            }

            bl = true;
            world.emitGameEvent(player, GameEvent.FLUID_PICKUP, pos);
         }

         if (!world.isClient() && bl) {
            player.incrementStat(Stats.USED.getOrCreateStat(item));
         }
      }

      if (bl) {
         if (!CampfireBlock.isLitCampfireInRange(world, pos)) {
            if (this.hasBees(world, pos)) {
               this.angerNearbyBees(world, pos);
            }

            this.takeHoney(world, state, pos, player, BeehiveBlockEntity.BeeState.EMERGENCY);
         } else {
            this.takeHoney(world, state, pos);
         }

         return ActionResult.SUCCESS;
      } else {
         return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
      }
   }

   private boolean hasBees(World world, BlockPos pos) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
         return !beehiveBlockEntity.hasNoBees();
      } else {
         return false;
      }
   }

   public void takeHoney(World world, BlockState state, BlockPos pos, @Nullable PlayerEntity player, BeehiveBlockEntity.BeeState beeState) {
      this.takeHoney(world, state, pos);
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
         beehiveBlockEntity.angerBees(player, state, beeState);
      }

   }

   public void takeHoney(World world, BlockState state, BlockPos pos) {
      world.setBlockState(pos, (BlockState)state.with(HONEY_LEVEL, 0), 3);
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if ((Integer)state.get(HONEY_LEVEL) >= 5) {
         for(int i = 0; i < random.nextInt(1) + 1; ++i) {
            this.spawnHoneyParticles(world, pos, state);
         }
      }

   }

   private void spawnHoneyParticles(World world, BlockPos pos, BlockState state) {
      if (state.getFluidState().isEmpty() && !(world.random.nextFloat() < 0.3F)) {
         VoxelShape voxelShape = state.getCollisionShape(world, pos);
         double d = voxelShape.getMax(Direction.Axis.Y);
         if (d >= 1.0 && !state.isIn(BlockTags.IMPERMEABLE)) {
            double e = voxelShape.getMin(Direction.Axis.Y);
            if (e > 0.0) {
               this.addHoneyParticle(world, pos, voxelShape, (double)pos.getY() + e - 0.05);
            } else {
               BlockPos blockPos = pos.down();
               BlockState blockState = world.getBlockState(blockPos);
               VoxelShape voxelShape2 = blockState.getCollisionShape(world, blockPos);
               double f = voxelShape2.getMax(Direction.Axis.Y);
               if ((f < 1.0 || !blockState.isFullCube(world, blockPos)) && blockState.getFluidState().isEmpty()) {
                  this.addHoneyParticle(world, pos, voxelShape, (double)pos.getY() - 0.05);
               }
            }
         }

      }
   }

   private void addHoneyParticle(World world, BlockPos pos, VoxelShape shape, double height) {
      this.addHoneyParticle(world, (double)pos.getX() + shape.getMin(Direction.Axis.X), (double)pos.getX() + shape.getMax(Direction.Axis.X), (double)pos.getZ() + shape.getMin(Direction.Axis.Z), (double)pos.getZ() + shape.getMax(Direction.Axis.Z), height);
   }

   private void addHoneyParticle(World world, double minX, double maxX, double minZ, double maxZ, double height) {
      world.addParticleClient(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(world.random.nextDouble(), minX, maxX), height, MathHelper.lerp(world.random.nextDouble(), minZ, maxZ), 0.0, 0.0, 0.0);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(HONEY_LEVEL, FACING);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new BeehiveBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return world.isClient ? null : validateTicker(type, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
   }

   public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (world instanceof ServerWorld serverWorld) {
         if (player.shouldSkipBlockDrops() && serverWorld.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BeehiveBlockEntity) {
               BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
               int i = (Integer)state.get(HONEY_LEVEL);
               boolean bl = !beehiveBlockEntity.hasNoBees();
               if (bl || i > 0) {
                  ItemStack itemStack = new ItemStack(this);
                  itemStack.applyComponentsFrom(beehiveBlockEntity.createComponentMap());
                  itemStack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(HONEY_LEVEL, (Comparable)i));
                  ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemStack);
                  itemEntity.setToDefaultPickupDelay();
                  world.spawnEntity(itemEntity);
               }
            }
         }
      }

      return super.onBreak(world, pos, state, player);
   }

   protected List getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
      Entity entity = (Entity)builder.getOptional(LootContextParameters.THIS_ENTITY);
      if (entity instanceof TntEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TntMinecartEntity) {
         BlockEntity blockEntity = (BlockEntity)builder.getOptional(LootContextParameters.BLOCK_ENTITY);
         if (blockEntity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            beehiveBlockEntity.angerBees((PlayerEntity)null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
         }
      }

      return super.getDroppedStacks(state, builder);
   }

   protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
      ItemStack itemStack = super.getPickStack(world, pos, state, includeData);
      if (includeData) {
         itemStack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(HONEY_LEVEL, (Comparable)((Integer)state.get(HONEY_LEVEL))));
      }

      return itemStack;
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (world.getBlockState(neighborPos).getBlock() instanceof FireBlock) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            beehiveBlockEntity.angerBees((PlayerEntity)null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
         }
      }

      return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   public BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   public BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      HONEY_LEVEL = Properties.HONEY_LEVEL;
   }
}
