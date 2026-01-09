package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractChestBlock extends BlockWithEntity {
   protected final Supplier entityTypeRetriever;

   protected AbstractChestBlock(AbstractBlock.Settings settings, Supplier entityTypeRetriever) {
      super(settings);
      this.entityTypeRetriever = entityTypeRetriever;
   }

   protected abstract MapCodec getCodec();

   public abstract DoubleBlockProperties.PropertySource getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked);
}
