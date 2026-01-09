package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WallWitherSkullBlock extends WallSkullBlock {
   public static final MapCodec CODEC = createCodec(WallWitherSkullBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public WallWitherSkullBlock(AbstractBlock.Settings settings) {
      super(SkullBlock.Type.WITHER_SKELETON, settings);
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
      WitherSkullBlock.onPlaced(world, pos);
   }
}
