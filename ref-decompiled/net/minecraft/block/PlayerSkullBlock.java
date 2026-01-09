package net.minecraft.block;

import com.mojang.serialization.MapCodec;

public class PlayerSkullBlock extends SkullBlock {
   public static final MapCodec CODEC = createCodec(PlayerSkullBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public PlayerSkullBlock(AbstractBlock.Settings settings) {
      super(SkullBlock.Type.PLAYER, settings);
   }
}
