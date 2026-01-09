package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public class TintedGlassBlock extends TransparentBlock {
   public static final MapCodec CODEC = createCodec(TintedGlassBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public TintedGlassBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected boolean isTransparent(BlockState state) {
      return false;
   }

   protected int getOpacity(BlockState state) {
      return 15;
   }
}
