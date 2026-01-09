package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Unit;
import org.jetbrains.annotations.Nullable;

public interface MemoryQuery {
   MemoryModuleType memory();

   MemoryModuleState getState();

   @Nullable
   MemoryQueryResult toQueryResult(Brain brain, java.util.Optional value);

   public static record Absent(MemoryModuleType memory) implements MemoryQuery {
      public Absent(MemoryModuleType memoryModuleType) {
         this.memory = memoryModuleType;
      }

      public MemoryModuleState getState() {
         return MemoryModuleState.VALUE_ABSENT;
      }

      public MemoryQueryResult toQueryResult(Brain brain, java.util.Optional value) {
         return value.isPresent() ? null : new MemoryQueryResult(brain, this.memory, Const.create(Unit.INSTANCE));
      }

      public MemoryModuleType memory() {
         return this.memory;
      }
   }

   public static record Value(MemoryModuleType memory) implements MemoryQuery {
      public Value(MemoryModuleType memoryModuleType) {
         this.memory = memoryModuleType;
      }

      public MemoryModuleState getState() {
         return MemoryModuleState.VALUE_PRESENT;
      }

      public MemoryQueryResult toQueryResult(Brain brain, java.util.Optional value) {
         return value.isEmpty() ? null : new MemoryQueryResult(brain, this.memory, IdF.create(value.get()));
      }

      public MemoryModuleType memory() {
         return this.memory;
      }
   }

   public static record Optional(MemoryModuleType memory) implements MemoryQuery {
      public Optional(MemoryModuleType memoryModuleType) {
         this.memory = memoryModuleType;
      }

      public MemoryModuleState getState() {
         return MemoryModuleState.REGISTERED;
      }

      public MemoryQueryResult toQueryResult(Brain brain, java.util.Optional value) {
         return new MemoryQueryResult(brain, this.memory, OptionalBox.create(value));
      }

      public MemoryModuleType memory() {
         return this.memory;
      }
   }
}
