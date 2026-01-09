package net.minecraft.test;

public record TestAttemptConfig(int numberOfTries, boolean haltOnFailure) {
   private static final TestAttemptConfig ONCE = new TestAttemptConfig(1, true);

   public TestAttemptConfig(int i, boolean bl) {
      this.numberOfTries = i;
      this.haltOnFailure = bl;
   }

   public static TestAttemptConfig once() {
      return ONCE;
   }

   public boolean isDisabled() {
      return this.numberOfTries < 1;
   }

   public boolean shouldTestAgain(int attempt, int successes) {
      boolean bl = attempt != successes;
      boolean bl2 = this.isDisabled() || attempt < this.numberOfTries;
      return bl2 && (!bl || !this.haltOnFailure);
   }

   public boolean needsMultipleAttempts() {
      return this.numberOfTries != 1;
   }

   public int numberOfTries() {
      return this.numberOfTries;
   }

   public boolean haltOnFailure() {
      return this.haltOnFailure;
   }
}
