package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TargetBlock extends Block {
   public static final MapCodec CODEC = createCodec(TargetBlock::new);
   private static final IntProperty POWER;
   private static final int RECOVERABLE_POWER_DELAY = 20;
   private static final int REGULAR_POWER_DELAY = 8;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TargetBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWER, 0));
   }

   protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
      int i = trigger(world, state, hit, projectile);
      Entity entity = projectile.getOwner();
      if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
         serverPlayerEntity.incrementStat(Stats.TARGET_HIT);
         Criteria.TARGET_HIT.trigger(serverPlayerEntity, projectile, hit.getPos(), i);
      }

   }

   private static int trigger(WorldAccess world, BlockState state, BlockHitResult hitResult, Entity entity) {
      int i = calculatePower(hitResult, hitResult.getPos());
      int j = entity instanceof PersistentProjectileEntity ? 20 : 8;
      if (!world.getBlockTickScheduler().isQueued(hitResult.getBlockPos(), state.getBlock())) {
         setPower(world, state, i, hitResult.getBlockPos(), j);
      }

      return i;
   }

   private static int calculatePower(BlockHitResult hitResult, Vec3d pos) {
      Direction direction = hitResult.getSide();
      double d = Math.abs(MathHelper.fractionalPart(pos.x) - 0.5);
      double e = Math.abs(MathHelper.fractionalPart(pos.y) - 0.5);
      double f = Math.abs(MathHelper.fractionalPart(pos.z) - 0.5);
      Direction.Axis axis = direction.getAxis();
      double g;
      if (axis == Direction.Axis.Y) {
         g = Math.max(d, f);
      } else if (axis == Direction.Axis.Z) {
         g = Math.max(d, e);
      } else {
         g = Math.max(e, f);
      }

      return Math.max(1, MathHelper.ceil(15.0 * MathHelper.clamp((0.5 - g) / 0.5, 0.0, 1.0)));
   }

   private static void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
      world.setBlockState(pos, (BlockState)state.with(POWER, power), 3);
      world.scheduleBlockTick(pos, state.getBlock(), delay);
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Integer)state.get(POWER) != 0) {
         world.setBlockState(pos, (BlockState)state.with(POWER, 0), 3);
      }

   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Integer)state.get(POWER);
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(POWER);
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!world.isClient() && !state.isOf(oldState.getBlock())) {
         if ((Integer)state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
            world.setBlockState(pos, (BlockState)state.with(POWER, 0), 18);
         }

      }
   }

   static {
      POWER = Properties.POWER;
   }
}
