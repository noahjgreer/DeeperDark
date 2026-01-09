package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.ToIntFunction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class GlowLichenBlock extends MultifaceGrowthBlock implements Fertilizable {
   public static final MapCodec CODEC = createCodec(GlowLichenBlock::new);
   private final MultifaceGrower grower = new MultifaceGrower(this);

   public MapCodec getCodec() {
      return CODEC;
   }

   public GlowLichenBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public static ToIntFunction getLuminanceSupplier(int luminance) {
      return (state) -> {
         return MultifaceBlock.hasAnyDirection(state) ? luminance : 0;
      };
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return Direction.stream().anyMatch((direction) -> {
         return this.grower.canGrow(state, world, pos, direction.getOpposite());
      });
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      this.grower.grow(state, world, pos, random);
   }

   protected boolean isTransparent(BlockState state) {
      return state.getFluidState().isEmpty();
   }

   public MultifaceGrower getGrower() {
      return this.grower;
   }
}
