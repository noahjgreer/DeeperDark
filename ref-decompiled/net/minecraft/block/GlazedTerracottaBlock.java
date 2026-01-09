package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;

public class GlazedTerracottaBlock extends HorizontalFacingBlock {
   public static final MapCodec CODEC = createCodec(GlazedTerracottaBlock::new);

   public MapCodec getCodec() {
      return CODEC;
   }

   public GlazedTerracottaBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
   }
}
