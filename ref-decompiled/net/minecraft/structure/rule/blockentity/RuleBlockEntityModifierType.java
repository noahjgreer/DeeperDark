package net.minecraft.structure.rule.blockentity;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface RuleBlockEntityModifierType {
   RuleBlockEntityModifierType CLEAR = register("clear", ClearRuleBlockEntityModifier.CODEC);
   RuleBlockEntityModifierType PASSTHROUGH = register("passthrough", PassthroughRuleBlockEntityModifier.CODEC);
   RuleBlockEntityModifierType APPEND_STATIC = register("append_static", AppendStaticRuleBlockEntityModifier.CODEC);
   RuleBlockEntityModifierType APPEND_LOOT = register("append_loot", AppendLootRuleBlockEntityModifier.CODEC);

   MapCodec codec();

   private static RuleBlockEntityModifierType register(String id, MapCodec codec) {
      return (RuleBlockEntityModifierType)Registry.register(Registries.RULE_BLOCK_ENTITY_MODIFIER, (String)id, () -> {
         return codec;
      });
   }
}
