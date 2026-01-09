package net.minecraft.entity.spawn;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.VariantSelectorProvider;

public record SpawnConditionSelectors(List selectors) {
   public static final SpawnConditionSelectors EMPTY = new SpawnConditionSelectors(List.of());
   public static final Codec CODEC;

   public SpawnConditionSelectors(List list) {
      this.selectors = list;
   }

   public static SpawnConditionSelectors createSingle(SpawnCondition condition, int priority) {
      return new SpawnConditionSelectors(VariantSelectorProvider.createSingle(condition, priority));
   }

   public static SpawnConditionSelectors createFallback(int priority) {
      return new SpawnConditionSelectors(VariantSelectorProvider.createFallback(priority));
   }

   public List selectors() {
      return this.selectors;
   }

   static {
      CODEC = VariantSelectorProvider.Selector.createCodec(SpawnCondition.CODEC).listOf().xmap(SpawnConditionSelectors::new, SpawnConditionSelectors::selectors);
   }
}
