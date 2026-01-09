package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.DyeColor;

public class StainedGlassBlock extends TransparentBlock implements Stainable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassBlock::getColor), createSettingsCodec()).apply(instance, StainedGlassBlock::new);
   });
   private final DyeColor color;

   public MapCodec getCodec() {
      return CODEC;
   }

   public StainedGlassBlock(DyeColor color, AbstractBlock.Settings settings) {
      super(settings);
      this.color = color;
   }

   public DyeColor getColor() {
      return this.color;
   }
}
