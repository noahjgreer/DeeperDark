package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Direction;

public class TranslucentBlock extends Block {
   public static final MapCodec CODEC = createCodec(TranslucentBlock::new);

   protected MapCodec getCodec() {
      return CODEC;
   }

   public TranslucentBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
      return stateFrom.isOf(this) ? true : super.isSideInvisible(state, stateFrom, direction);
   }
}
