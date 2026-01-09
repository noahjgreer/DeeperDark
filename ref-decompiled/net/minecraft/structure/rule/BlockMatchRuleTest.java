package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

public class BlockMatchRuleTest extends RuleTest {
   public static final MapCodec CODEC;
   private final Block block;

   public BlockMatchRuleTest(Block block) {
      this.block = block;
   }

   public boolean test(BlockState state, Random random) {
      return state.isOf(this.block);
   }

   protected RuleTestType getType() {
      return RuleTestType.BLOCK_MATCH;
   }

   static {
      CODEC = Registries.BLOCK.getCodec().fieldOf("block").xmap(BlockMatchRuleTest::new, (ruleTest) -> {
         return ruleTest.block;
      });
   }
}
