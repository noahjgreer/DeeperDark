package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

public class RandomBlockMatchRuleTest extends RuleTest {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Registries.BLOCK.getCodec().fieldOf("block").forGetter((ruleTest) -> {
         return ruleTest.block;
      }), Codec.FLOAT.fieldOf("probability").forGetter((ruleTest) -> {
         return ruleTest.probability;
      })).apply(instance, RandomBlockMatchRuleTest::new);
   });
   private final Block block;
   private final float probability;

   public RandomBlockMatchRuleTest(Block block, float probability) {
      this.block = block;
      this.probability = probability;
   }

   public boolean test(BlockState state, Random random) {
      return state.isOf(this.block) && random.nextFloat() < this.probability;
   }

   protected RuleTestType getType() {
      return RuleTestType.RANDOM_BLOCK_MATCH;
   }
}
