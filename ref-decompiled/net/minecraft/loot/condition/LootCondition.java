package net.minecraft.loot.condition;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;

public interface LootCondition extends LootContextAware, Predicate {
   Codec BASE_CODEC = Registries.LOOT_CONDITION_TYPE.getCodec().dispatch("condition", LootCondition::getType, LootConditionType::codec);
   Codec CODEC = Codec.lazyInitialized(() -> {
      return Codec.withAlternative(BASE_CODEC, AllOfLootCondition.INLINE_CODEC);
   });
   Codec ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PREDICATE, CODEC);

   LootConditionType getType();

   @FunctionalInterface
   public interface Builder {
      LootCondition build();

      default Builder invert() {
         return InvertedLootCondition.builder(this);
      }

      default AnyOfLootCondition.Builder or(Builder condition) {
         return AnyOfLootCondition.builder(this, condition);
      }

      default AllOfLootCondition.Builder and(Builder condition) {
         return AllOfLootCondition.builder(this, condition);
      }
   }
}
