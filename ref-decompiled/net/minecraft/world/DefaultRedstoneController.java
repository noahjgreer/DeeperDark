package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

public class DefaultRedstoneController extends RedstoneController {
   public DefaultRedstoneController(RedstoneWireBlock redstoneWireBlock) {
      super(redstoneWireBlock);
   }

   public void update(World world, BlockPos pos, BlockState state, @Nullable WireOrientation orientation, boolean blockAdded) {
      int i = this.calculateTotalPowerAt(world, pos);
      if ((Integer)state.get(RedstoneWireBlock.POWER) != i) {
         if (world.getBlockState(pos) == state) {
            world.setBlockState(pos, (BlockState)state.with(RedstoneWireBlock.POWER, i), 2);
         }

         Set set = Sets.newHashSet();
         set.add(pos);
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction direction = var8[var10];
            set.add(pos.offset(direction));
         }

         Iterator var12 = set.iterator();

         while(var12.hasNext()) {
            BlockPos blockPos = (BlockPos)var12.next();
            world.updateNeighbors(blockPos, this.wire);
         }
      }

   }

   private int calculateTotalPowerAt(World world, BlockPos pos) {
      int i = this.getStrongPowerAt(world, pos);
      return i == 15 ? i : Math.max(i, this.calculateWirePowerAt(world, pos));
   }
}
