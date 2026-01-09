package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HayBlock extends PillarBlock {
   public static final MapCodec CODEC = createCodec(HayBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public HayBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(AXIS, Direction.Axis.Y));
   }

   public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
      entity.handleFallDamage(fallDistance, 0.2F, world.getDamageSources().fall());
   }
}
