package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;

public abstract class FacingBlock extends Block {
   public static final EnumProperty FACING;

   protected FacingBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   protected abstract MapCodec getCodec();

   static {
      FACING = Properties.FACING;
   }
}
