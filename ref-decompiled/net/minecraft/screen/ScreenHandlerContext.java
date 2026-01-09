package net.minecraft.screen;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ScreenHandlerContext {
   ScreenHandlerContext EMPTY = new ScreenHandlerContext() {
      public Optional get(BiFunction getter) {
         return Optional.empty();
      }
   };

   static ScreenHandlerContext create(final World world, final BlockPos pos) {
      return new ScreenHandlerContext() {
         public Optional get(BiFunction getter) {
            return Optional.of(getter.apply(world, pos));
         }
      };
   }

   Optional get(BiFunction getter);

   default Object get(BiFunction getter, Object defaultValue) {
      return this.get(getter).orElse(defaultValue);
   }

   default void run(BiConsumer function) {
      this.get((world, pos) -> {
         function.accept(world, pos);
         return Optional.empty();
      });
   }
}
