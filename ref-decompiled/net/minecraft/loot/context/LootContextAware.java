package net.minecraft.loot.context;

import java.util.Set;
import net.minecraft.loot.LootTableReporter;

public interface LootContextAware {
   default Set getAllowedParameters() {
      return Set.of();
   }

   default void validate(LootTableReporter reporter) {
      reporter.validateContext(this);
   }
}
