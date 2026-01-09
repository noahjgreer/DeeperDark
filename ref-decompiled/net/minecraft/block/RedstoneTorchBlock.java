package net.minecraft.block;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class RedstoneTorchBlock extends AbstractTorchBlock {
   public static final MapCodec CODEC = createCodec(RedstoneTorchBlock::new);
   public static final BooleanProperty LIT;
   private static final Map BURNOUT_MAP;
   public static final int field_31227 = 60;
   public static final int field_31228 = 8;
   public static final int field_31229 = 160;
   private static final int SCHEDULED_TICK_DELAY = 2;

   public MapCodec getCodec() {
      return CODEC;
   }

   public RedstoneTorchBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LIT, true));
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      this.update(world, pos, state);
   }

   private void update(World world, BlockPos pos, BlockState state) {
      WireOrientation wireOrientation = this.getEmissionOrientation(world, state);
      Direction[] var5 = Direction.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         world.updateNeighborsAlways(pos.offset(direction), this, OrientationHelper.withFrontNullable(wireOrientation, direction));
      }

   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      if (!moved) {
         this.update(world, pos, state);
      }

   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(LIT) && Direction.UP != direction ? 15 : 0;
   }

   protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
      return world.isEmittingRedstonePower(pos.down(), Direction.DOWN);
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      boolean bl = this.shouldUnpower(world, pos, state);
      List list = (List)BURNOUT_MAP.get(world);

      while(list != null && !list.isEmpty() && world.getTime() - ((BurnoutEntry)list.get(0)).time > 60L) {
         list.remove(0);
      }

      if ((Boolean)state.get(LIT)) {
         if (bl) {
            world.setBlockState(pos, (BlockState)state.with(LIT, false), 3);
            if (isBurnedOut(world, pos, true)) {
               world.syncWorldEvent(1502, pos, 0);
               world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), 160);
            }
         }
      } else if (!bl && !isBurnedOut(world, pos, false)) {
         world.setBlockState(pos, (BlockState)state.with(LIT, true), 3);
      }

   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if ((Boolean)state.get(LIT) == this.shouldUnpower(world, pos, state) && !world.getBlockTickScheduler().isTicking(pos, this)) {
         world.scheduleBlockTick(pos, this, 2);
      }

   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return direction == Direction.DOWN ? state.getWeakRedstonePower(world, pos, direction) : 0;
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if ((Boolean)state.get(LIT)) {
         double d = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
         double e = (double)pos.getY() + 0.7 + (random.nextDouble() - 0.5) * 0.2;
         double f = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
         world.addParticleClient(DustParticleEffect.DEFAULT, d, e, f, 0.0, 0.0, 0.0);
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(LIT);
   }

   private static boolean isBurnedOut(World world, BlockPos pos, boolean addNew) {
      List list = (List)BURNOUT_MAP.computeIfAbsent(world, (worldx) -> {
         return Lists.newArrayList();
      });
      if (addNew) {
         list.add(new BurnoutEntry(pos.toImmutable(), world.getTime()));
      }

      int i = 0;
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         BurnoutEntry burnoutEntry = (BurnoutEntry)var5.next();
         if (burnoutEntry.pos.equals(pos)) {
            ++i;
            if (i >= 8) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   protected WireOrientation getEmissionOrientation(World world, BlockState state) {
      return OrientationHelper.getEmissionOrientation(world, (Direction)null, Direction.UP);
   }

   static {
      LIT = Properties.LIT;
      BURNOUT_MAP = new WeakHashMap();
   }

   public static class BurnoutEntry {
      final BlockPos pos;
      final long time;

      public BurnoutEntry(BlockPos pos, long time) {
         this.pos = pos;
         this.time = time;
      }
   }
}
