package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;

public class RandomBlockStateMatchRuleTest extends RuleTest {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockState.CODEC.fieldOf("block_state").forGetter((ruleTest) -> {
         return ruleTest.blockState;
      }), Codec.FLOAT.fieldOf("probability").forGetter((ruleTest) -> {
         return ruleTest.probability;
      })).apply(instance, RandomBlockStateMatchRuleTest::new);
   });
   private final BlockState blockState;
   private final float probability;

   public RandomBlockStateMatchRuleTest(BlockState blockState, float probability) {
      this.blockState = blockState;
      this.probability = probability;
   }

   public boolean test(BlockState state, Random random) {
      return state == this.blockState && random.nextFloat() < this.probability;
   }

   protected RuleTestType getType() {
      return RuleTestType.RANDOM_BLOCKSTATE_MATCH;
   }
}
