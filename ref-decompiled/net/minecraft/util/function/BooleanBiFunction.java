package net.minecraft.util.function;

public interface BooleanBiFunction {
   BooleanBiFunction FALSE = (a, b) -> {
      return false;
   };
   BooleanBiFunction NOT_OR = (a, b) -> {
      return !a && !b;
   };
   BooleanBiFunction ONLY_SECOND = (a, b) -> {
      return b && !a;
   };
   BooleanBiFunction NOT_FIRST = (a, b) -> {
      return !a;
   };
   BooleanBiFunction ONLY_FIRST = (a, b) -> {
      return a && !b;
   };
   BooleanBiFunction NOT_SECOND = (a, b) -> {
      return !b;
   };
   BooleanBiFunction NOT_SAME = (a, b) -> {
      return a != b;
   };
   BooleanBiFunction NOT_AND = (a, b) -> {
      return !a || !b;
   };
   BooleanBiFunction AND = (a, b) -> {
      return a && b;
   };
   BooleanBiFunction SAME = (a, b) -> {
      return a == b;
   };
   BooleanBiFunction SECOND = (a, b) -> {
      return b;
   };
   BooleanBiFunction CAUSES = (a, b) -> {
      return !a || b;
   };
   BooleanBiFunction FIRST = (a, b) -> {
      return a;
   };
   BooleanBiFunction CAUSED_BY = (a, b) -> {
      return a || !b;
   };
   BooleanBiFunction OR = (a, b) -> {
      return a || b;
   };
   BooleanBiFunction TRUE = (a, b) -> {
      return true;
   };

   boolean apply(boolean a, boolean b);
}
