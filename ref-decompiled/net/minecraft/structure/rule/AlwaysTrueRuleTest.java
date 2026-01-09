package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;

public class AlwaysTrueRuleTest extends RuleTest {
   public static final MapCodec CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final AlwaysTrueRuleTest INSTANCE = new AlwaysTrueRuleTest();

   private AlwaysTrueRuleTest() {
   }

   public boolean test(BlockState state, Random random) {
      return true;
   }

   protected RuleTestType getType() {
      return RuleTestType.ALWAYS_TRUE;
   }
}
