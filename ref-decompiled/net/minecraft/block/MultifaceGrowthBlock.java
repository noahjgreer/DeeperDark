package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public abstract class MultifaceGrowthBlock extends MultifaceBlock {
   public MultifaceGrowthBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   public abstract MapCodec getCodec();

   public abstract MultifaceGrower getGrower();
}
