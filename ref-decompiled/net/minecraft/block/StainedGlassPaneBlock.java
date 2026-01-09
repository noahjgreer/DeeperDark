package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.DyeColor;

public class StainedGlassPaneBlock extends PaneBlock implements Stainable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassPaneBlock::getColor), createSettingsCodec()).apply(instance, StainedGlassPaneBlock::new);
   });
   private final DyeColor color;

   public MapCodec getCodec() {
      return CODEC;
   }

   public StainedGlassPaneBlock(DyeColor color, AbstractBlock.Settings settings) {
      super(settings);
      this.color = color;
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
   }

   public DyeColor getColor() {
      return this.color;
   }
}
