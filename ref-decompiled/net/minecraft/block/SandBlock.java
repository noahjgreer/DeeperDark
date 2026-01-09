package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sound.AmbientDesertBlockSounds;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SandBlock extends ColoredFallingBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ColorCode.CODEC.fieldOf("falling_dust_color").forGetter((block) -> {
         return block.color;
      }), createSettingsCodec()).apply(instance, SandBlock::new);
   });

   public MapCodec getCodec() {
      return CODEC;
   }

   public SandBlock(ColorCode colorCode, AbstractBlock.Settings settings) {
      super(colorCode, settings);
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      AmbientDesertBlockSounds.tryPlaySandSounds(world, pos, random);
   }
}
