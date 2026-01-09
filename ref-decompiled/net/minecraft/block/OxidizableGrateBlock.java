package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableGrateBlock extends GrateBlock implements Oxidizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableGrateBlock::getDegradationLevel), createSettingsCodec()).apply(instance, OxidizableGrateBlock::new);
   });
   private final Oxidizable.OxidationLevel oxidationLevel;

   protected MapCodec getCodec() {
      return CODEC;
   }

   public OxidizableGrateBlock(Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
      super(settings);
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
