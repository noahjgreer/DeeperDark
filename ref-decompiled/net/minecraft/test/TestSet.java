package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TestSet {
   private static final char NOT_STARTED = ' ';
   private static final char RUNNING = '_';
   private static final char PASS = '+';
   private static final char OPTIONAL_FAIL = 'x';
   private static final char REQUIRED_FAIL = 'X';
   private final Collection tests = Lists.newArrayList();
   private final Collection listeners = Lists.newArrayList();

   public TestSet() {
   }

   public TestSet(Collection tests) {
      this.tests.addAll(tests);
   }

   public void add(GameTestState test) {
      this.tests.add(test);
      Collection var10000 = this.listeners;
      Objects.requireNonNull(test);
      var10000.forEach(test::addListener);
   }

   public void addListener(TestListener listener) {
      this.listeners.add(listener);
      this.tests.forEach((test) -> {
         test.addListener(listener);
      });
   }

   public void addListener(final Consumer onFailed) {
      this.addListener(new TestListener(this) {
         public void onStarted(GameTestState test) {
         }

         public void onPassed(GameTestState test, TestRunContext context) {
         }

         public void onFailed(GameTestState test, TestRunContext context) {
            onFailed.accept(test);
         }

         public void onRetry(GameTestState lastState, GameTestState nextState, TestRunContext context) {
         }
      });
   }

   public int getFailedRequiredTestCount() {
      return (int)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isRequired).count();
   }

   public int getFailedOptionalTestCount() {
      return (int)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isOptional).count();
   }

   public int getCompletedTestCount() {
      return (int)this.tests.stream().filter(GameTestState::isCompleted).count();
   }

   public boolean failed() {
      return this.getFailedRequiredTestCount() > 0;
   }

   public boolean hasFailedOptionalTests() {
      return this.getFailedOptionalTestCount() > 0;
   }

   public Collection getRequiredTests() {
      return (Collection)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isRequired).collect(Collectors.toList());
   }

   public Collection getOptionalTests() {
      return (Collection)this.tests.stream().filter(GameTestState::isFailed).filter(GameTestState::isOptional).collect(Collectors.toList());
   }

   public int getTestCount() {
      return this.tests.size();
   }

   public boolean isDone() {
      return this.getCompletedTestCount() == this.getTestCount();
   }

   public String getResultString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append('[');
      this.tests.forEach((test) -> {
         if (!test.isStarted()) {
            stringBuffer.append(' ');
         } else if (test.isPassed()) {
            stringBuffer.append('+');
         } else if (test.isFailed()) {
            stringBuffer.append((char)(test.isRequired() ? 'X' : 'x'));
         } else {
            stringBuffer.append('_');
         }

      });
      stringBuffer.append(']');
      return stringBuffer.toString();
   }

   public String toString() {
      return this.getResultString();
   }

   public void remove(GameTestState state) {
      this.tests.remove(state);
   }
}
