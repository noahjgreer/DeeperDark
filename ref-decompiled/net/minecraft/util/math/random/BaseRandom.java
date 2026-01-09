package net.minecraft.util.math.random;

public interface BaseRandom extends Random {
   float FLOAT_MULTIPLIER = 5.9604645E-8F;
   double DOUBLE_MULTIPLIER = 1.1102230246251565E-16;

   int next(int bits);

   default int nextInt() {
      return this.next(32);
   }

   default int nextInt(int bound) {
      if (bound <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else if ((bound & bound - 1) == 0) {
         return (int)((long)bound * (long)this.next(31) >> 31);
      } else {
         int i;
         int j;
         do {
            i = this.next(31);
            j = i % bound;
         } while(i - j + (bound - 1) < 0);

         return j;
      }
   }

   default long nextLong() {
      int i = this.next(32);
      int j = this.next(32);
      long l = (long)i << 32;
      return l + (long)j;
   }

   default boolean nextBoolean() {
      return this.next(1) != 0;
   }

   default float nextFloat() {
      return (float)this.next(24) * 5.9604645E-8F;
   }

   default double nextDouble() {
      int i = this.next(26);
      int j = this.next(27);
      long l = ((long)i << 27) + (long)j;
      return (double)l * 1.1102230246251565E-16;
   }
}
