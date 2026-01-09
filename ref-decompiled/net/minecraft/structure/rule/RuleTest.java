package net.minecraft.structure.rule;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

public abstract class RuleTest {
   public static final Codec TYPE_CODEC;

   public abstract boolean test(BlockState state, Random random);

   protected abstract RuleTestType getType();

   static {
      TYPE_CODEC = Registries.RULE_TEST.getCodec().dispatch("predicate_type", RuleTest::getType, RuleTestType::codec);
   }
}
