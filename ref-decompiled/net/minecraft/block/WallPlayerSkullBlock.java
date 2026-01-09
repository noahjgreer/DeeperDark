package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public class WallPlayerSkullBlock extends WallSkullBlock {
   public static final MapCodec CODEC = createCodec(WallPlayerSkullBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public WallPlayerSkullBlock(AbstractBlock.Settings settings) {
      super(SkullBlock.Type.PLAYER, settings);
   }
}
