package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;

public class BlockStateMatchRuleTest extends RuleTest {
   public static final MapCodec CODEC;
   private final BlockState blockState;

   public BlockStateMatchRuleTest(BlockState blockState) {
      this.blockState = blockState;
   }

   public boolean test(BlockState state, Random random) {
      return state == this.blockState;
   }

   protected RuleTestType getType() {
      return RuleTestType.BLOCKSTATE_MATCH;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchRuleTest::new, (ruleTest) -> {
         return ruleTest.blockState;
      });
   }
}
