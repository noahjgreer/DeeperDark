package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.network.packet.s2c.custom.DebugRedstoneUpdateOrderCustomPayload;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class ExperimentalRedstoneController extends RedstoneController {
   private final Deque powerIncreaseQueue = new ArrayDeque();
   private final Deque powerDecreaseQueue = new ArrayDeque();
   private final Object2IntMap wireOrientationsAndPowers = new Object2IntLinkedOpenHashMap();

   public ExperimentalRedstoneController(RedstoneWireBlock redstoneWireBlock) {
      super(redstoneWireBlock);
   }

   public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
      WireOrientation wireOrientation = tweakOrientation(world, orientation);
      this.propagatePowerUpdates(world, pos, wireOrientation);
      ObjectIterator objectIterator = this.wireOrientationsAndPowers.object2IntEntrySet().iterator();

      for(boolean bl = true; objectIterator.hasNext(); bl = false) {
         Object2IntMap.Entry entry = (Object2IntMap.Entry)objectIterator.next();
         BlockPos blockPos = (BlockPos)entry.getKey();
         int i = entry.getIntValue();
         int j = unpackPower(i);
         BlockState blockState = world.getBlockState(blockPos);
         if (blockState.isOf(this.wire) && !((Integer)blockState.get(RedstoneWireBlock.POWER)).equals(j)) {
            int k = 2;
            if (!blockAdded || !bl) {
               k |= 128;
            }

            world.setBlockState(blockPos, (BlockState)blockState.with(RedstoneWireBlock.POWER, j), k);
         } else {
            objectIterator.remove();
         }
      }

      this.update(world);
   }

   private void update(World world) {
      this.wireOrientationsAndPowers.forEach((pos, orientationAndPower) -> {
         WireOrientation wireOrientation = unpackOrientation(orientationAndPower);
         BlockState blockState = world.getBlockState(pos);
         Iterator var6 = wireOrientation.getDirectionsByPriority().iterator();

         while(true) {
            Direction direction;
            BlockPos blockPos;
            BlockState blockState2;
            WireOrientation wireOrientation2;
            do {
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  direction = (Direction)var6.next();
               } while(!canProvidePowerTo(blockState, direction));

               blockPos = pos.offset(direction);
               blockState2 = world.getBlockState(blockPos);
               wireOrientation2 = wireOrientation.withFrontIfNotUp(direction);
               world.updateNeighbor(blockState2, blockPos, this.wire, wireOrientation2, false);
            } while(!blockState2.isSolidBlock(world, blockPos));

            Iterator var11 = wireOrientation2.getDirectionsByPriority().iterator();

            while(var11.hasNext()) {
               Direction direction2 = (Direction)var11.next();
               if (direction2 != direction.getOpposite()) {
                  world.updateNeighbor(blockPos.offset(direction2), this.wire, wireOrientation2.withFrontIfNotUp(direction2));
               }
            }
         }
      });
   }

   private static boolean canProvidePowerTo(BlockState wireState, Direction direction) {
      EnumProperty enumProperty = (EnumProperty)RedstoneWireBlock.DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction);
      if (enumProperty == null) {
         return direction == Direction.DOWN;
      } else {
         return ((WireConnection)wireState.get(enumProperty)).isConnected();
      }
   }

   private static WireOrientation tweakOrientation(World world, @Nullable WireOrientation orientation) {
      WireOrientation wireOrientation;
      if (orientation != null) {
         wireOrientation = orientation;
      } else {
         wireOrientation = WireOrientation.random(world.random);
      }

      return wireOrientation.withUp(Direction.UP).withSideBias(WireOrientation.SideBias.LEFT);
   }

   private void propagatePowerUpdates(World world, BlockPos pos, WireOrientation orientation) {
      BlockState blockState = world.getBlockState(pos);
      if (blockState.isOf(this.wire)) {
         this.updatePowerAt(pos, (Integer)blockState.get(RedstoneWireBlock.POWER), orientation);
         this.powerIncreaseQueue.add(pos);
      } else {
         this.spreadPowerUpdateToNeighbors(world, pos, 0, orientation, true);
      }

      BlockPos blockPos;
      int i;
      WireOrientation wireOrientation;
      int j;
      int k;
      int l;
      int m;
      int n;
      for(; !this.powerIncreaseQueue.isEmpty(); this.spreadPowerUpdateToNeighbors(world, blockPos, n, wireOrientation, j > m)) {
         blockPos = (BlockPos)this.powerIncreaseQueue.removeFirst();
         i = this.wireOrientationsAndPowers.getInt(blockPos);
         wireOrientation = unpackOrientation(i);
         j = unpackPower(i);
         k = this.getStrongPowerAt(world, blockPos);
         l = this.calculateWirePowerAt(world, blockPos);
         m = Math.max(k, l);
         if (m < j) {
            if (k > 0 && !this.powerDecreaseQueue.contains(blockPos)) {
               this.powerDecreaseQueue.add(blockPos);
            }

            n = 0;
         } else {
            n = m;
         }

         if (n != j) {
            this.updatePowerAt(blockPos, n, wireOrientation);
         }
      }

      WireOrientation wireOrientation2;
      for(; !this.powerDecreaseQueue.isEmpty(); this.spreadPowerUpdateToNeighbors(world, blockPos, l, wireOrientation2, false)) {
         blockPos = (BlockPos)this.powerDecreaseQueue.removeFirst();
         i = this.wireOrientationsAndPowers.getInt(blockPos);
         int o = unpackPower(i);
         j = this.getStrongPowerAt(world, blockPos);
         k = this.calculateWirePowerAt(world, blockPos);
         l = Math.max(j, k);
         wireOrientation2 = unpackOrientation(i);
         if (l > o) {
            this.updatePowerAt(blockPos, l, wireOrientation2);
         } else if (l < o) {
            throw new IllegalStateException("Turning off wire while trying to turn it on. Should not happen.");
         }
      }

   }

   private static int packOrientationAndPower(WireOrientation orientation, int power) {
      return orientation.ordinal() << 4 | power;
   }

   private static WireOrientation unpackOrientation(int packed) {
      return WireOrientation.fromOrdinal(packed >> 4);
   }

   private static int unpackPower(int packed) {
      return packed & 15;
   }

   private void updatePowerAt(BlockPos pos, int power, WireOrientation defaultOrientation) {
      this.wireOrientationsAndPowers.compute(pos, (pos2, orientationAndPower) -> {
         return orientationAndPower == null ? packOrientationAndPower(defaultOrientation, power) : packOrientationAndPower(unpackOrientation(orientationAndPower), power);
      });
   }

   private void spreadPowerUpdateToNeighbors(World world, BlockPos pos, int power, WireOrientation orientation, boolean canIncreasePower) {
      Iterator var6 = orientation.getHorizontalDirections().iterator();

      Direction direction;
      BlockPos blockPos;
      while(var6.hasNext()) {
         direction = (Direction)var6.next();
         blockPos = pos.offset(direction);
         this.spreadPowerUpdateTo(world, blockPos, power, orientation.withFront(direction), canIncreasePower);
      }

      var6 = orientation.getVerticalDirections().iterator();

      label35:
      while(var6.hasNext()) {
         direction = (Direction)var6.next();
         blockPos = pos.offset(direction);
         boolean bl = world.getBlockState(blockPos).isSolidBlock(world, blockPos);
         Iterator var10 = orientation.getHorizontalDirections().iterator();

         while(true) {
            while(true) {
               if (!var10.hasNext()) {
                  continue label35;
               }

               Direction direction2 = (Direction)var10.next();
               BlockPos blockPos2 = pos.offset(direction2);
               BlockPos blockPos3;
               if (direction == Direction.UP && !bl) {
                  blockPos3 = blockPos.offset(direction2);
                  this.spreadPowerUpdateTo(world, blockPos3, power, orientation.withFront(direction2), canIncreasePower);
               } else if (direction == Direction.DOWN && !world.getBlockState(blockPos2).isSolidBlock(world, blockPos2)) {
                  blockPos3 = blockPos.offset(direction2);
                  this.spreadPowerUpdateTo(world, blockPos3, power, orientation.withFront(direction2), canIncreasePower);
               }
            }
         }
      }

   }

   private void spreadPowerUpdateTo(World world, BlockPos neighborPos, int power, WireOrientation orientation, boolean canIncreasePower) {
      BlockState blockState = world.getBlockState(neighborPos);
      if (blockState.isOf(this.wire)) {
         int i = this.getWirePowerAt(neighborPos, blockState);
         if (i < power - 1 && !this.powerDecreaseQueue.contains(neighborPos)) {
            this.powerDecreaseQueue.add(neighborPos);
            this.updatePowerAt(neighborPos, i, orientation);
         }

         if (canIncreasePower && i > power && !this.powerIncreaseQueue.contains(neighborPos)) {
            this.powerIncreaseQueue.add(neighborPos);
            this.updatePowerAt(neighborPos, i, orientation);
         }
      }

   }

   protected int getWirePowerAt(BlockPos world, BlockState pos) {
      int i = this.wireOrientationsAndPowers.getOrDefault(world, -1);
      return i != -1 ? unpackPower(i) : super.getWirePowerAt(world, pos);
   }

   // $FF: synthetic method
   private static void method_61837(List list, BlockPos blockPos, Integer integer) {
      WireOrientation wireOrientation = unpackOrientation(integer);
      list.add(new DebugRedstoneUpdateOrderCustomPayload.Wire(blockPos, wireOrientation));
   }
}
