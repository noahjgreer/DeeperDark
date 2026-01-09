package net.minecraft.util.function;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.function.Function;

public interface ToFloatFunction {
   ToFloatFunction IDENTITY = fromFloat((value) -> {
      return value;
   });

   float apply(Object x);

   float min();

   float max();

   static ToFloatFunction fromFloat(final Float2FloatFunction delegate) {
      return new ToFloatFunction() {
         public float apply(Float float_) {
            return (Float)delegate.apply(float_);
         }

         public float min() {
            return Float.NEGATIVE_INFINITY;
         }

         public float max() {
            return Float.POSITIVE_INFINITY;
         }
      };
   }

   default ToFloatFunction compose(final Function before) {
      return new ToFloatFunction(this) {
         public float apply(Object x) {
            return ToFloatFunction.this.apply(before.apply(x));
         }

         public float min() {
            return ToFloatFunction.this.min();
         }

         public float max() {
            return ToFloatFunction.this.max();
         }
      };
   }
}
