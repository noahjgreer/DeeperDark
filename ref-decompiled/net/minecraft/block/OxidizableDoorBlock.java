package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class OxidizableDoorBlock extends DoorBlock implements Oxidizable {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(DoorBlock::getBlockSetType), Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableDoorBlock::getDegradationLevel), createSettingsCodec()).apply(instance, OxidizableDoorBlock::new);
   });
   private final Oxidizable.OxidationLevel oxidationLevel;

   public MapCodec getCodec() {
      return CODEC;
   }

   public OxidizableDoorBlock(BlockSetType type, Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
      super(type, settings);
      this.oxidationLevel = oxidationLevel;
   }

   protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
         this.tickDegradation(state, world, pos, random);
      }

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
