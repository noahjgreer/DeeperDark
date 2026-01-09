package net.minecraft.util.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface LazyIterationConsumer {
   NextIteration accept(Object value);

   static LazyIterationConsumer forConsumer(Consumer consumer) {
      return (value) -> {
         consumer.accept(value);
         return LazyIterationConsumer.NextIteration.CONTINUE;
      };
   }

   public static enum NextIteration {
      CONTINUE,
      ABORT;

      public boolean shouldAbort() {
         return this == ABORT;
      }

      // $FF: synthetic method
      private static NextIteration[] method_47544() {
         return new NextIteration[]{CONTINUE, ABORT};
      }
   }
}
