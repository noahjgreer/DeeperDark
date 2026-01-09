package net.minecraft.structure.rule;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface RuleTestType {
   RuleTestType ALWAYS_TRUE = register("always_true", AlwaysTrueRuleTest.CODEC);
   RuleTestType BLOCK_MATCH = register("block_match", BlockMatchRuleTest.CODEC);
   RuleTestType BLOCKSTATE_MATCH = register("blockstate_match", BlockStateMatchRuleTest.CODEC);
   RuleTestType TAG_MATCH = register("tag_match", TagMatchRuleTest.CODEC);
   RuleTestType RANDOM_BLOCK_MATCH = register("random_block_match", RandomBlockMatchRuleTest.CODEC);
   RuleTestType RANDOM_BLOCKSTATE_MATCH = register("random_blockstate_match", RandomBlockStateMatchRuleTest.CODEC);

   MapCodec codec();

   static RuleTestType register(String id, MapCodec codec) {
      return (RuleTestType)Registry.register(Registries.RULE_TEST, (String)id, () -> {
         return codec;
      });
   }
}
