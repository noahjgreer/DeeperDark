package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.App;
import java.util.Optional;

public final class MemoryQueryResult {
   private final Brain brain;
   private final MemoryModuleType memory;
   private final App value;

   public MemoryQueryResult(Brain brain, MemoryModuleType memory, App value) {
      this.brain = brain;
      this.memory = memory;
      this.value = value;
   }

   public App getValue() {
      return this.value;
   }

   public void remember(Object value) {
      this.brain.remember(this.memory, Optional.of(value));
   }

   public void remember(Optional value) {
      this.brain.remember(this.memory, value);
   }

   public void remember(Object value, long expiry) {
      this.brain.remember(this.memory, value, expiry);
   }

   public void forget() {
      this.brain.forget(this.memory);
   }
}
