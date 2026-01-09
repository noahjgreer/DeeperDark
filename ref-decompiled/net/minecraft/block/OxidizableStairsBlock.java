package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableStairsBlock extends StairsBlock implements Oxidizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(Degradable::getDegradationLevel), BlockState.CODEC.fieldOf("base_state").forGetter((oxidizableStairsBlock) -> {
         return oxidizableStairsBlock.baseBlockState;
      }), createSettingsCodec()).apply(instance, OxidizableStairsBlock::new);
   });
   private final Oxidizable.OxidationLevel oxidationLevel;

   public MapCodec getCodec() {
      return CODEC;
   }

   public OxidizableStairsBlock(Oxidizable.OxidationLevel oxidationLevel, BlockState baseBlockState, AbstractBlock.Settings settings) {
      super(baseBlockState, settings);
      this.oxidationLevel = oxidationLevel;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      this.tickDegradation(state, world, pos, random);
   }

   protected boolean hasRandomTicks(BlockState state) {
      return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
   }

   public Oxidizable.OxidationLevel getDegradationLevel() {
      return this.oxidationLevel;
   }

   // $FF: synthetic method
   public Enum getDegradationLevel() {
      return this.getDegradationLevel();
   }
}
