package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.DyeColor;

public class DyedCarpetBlock extends CarpetBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DyeColor.CODEC.fieldOf("color").forGetter(DyedCarpetBlock::getDyeColor), createSettingsCodec()).apply(instance, DyedCarpetBlock::new);
   });
   private final DyeColor dyeColor;

   public MapCodec getCodec() {
      return CODEC;
   }

   public DyedCarpetBlock(DyeColor dyeColor, AbstractBlock.Settings settings) {
      super(settings);
      this.dyeColor = dyeColor;
   }

   public DyeColor getDyeColor() {
      return this.dyeColor;
   }
}
