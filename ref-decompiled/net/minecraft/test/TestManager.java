package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class TestManager {
   public static final TestManager INSTANCE = new TestManager();
   private final Collection tests = Lists.newCopyOnWriteArrayList();
   @Nullable
   private TestRunContext runContext;
   private State state;
   private volatile boolean shouldTick;

   private TestManager() {
      this.state = TestManager.State.IDLE;
      this.shouldTick = false;
   }

   public void start(GameTestState test) {
      this.tests.add(test);
   }

   public void clear() {
      if (this.state != TestManager.State.IDLE) {
         this.state = TestManager.State.HALTING;
      } else {
         this.tests.clear();
         if (this.runContext != null) {
            this.runContext.clear();
            this.runContext = null;
         }

      }
   }

   public void setRunContext(TestRunContext runContext) {
      if (this.runContext != null) {
         Util.logErrorOrPause("The runner was already set in GameTestTicker");
      }

      this.runContext = runContext;
   }

   public void startTicking() {
      this.shouldTick = true;
   }

   public void tick() {
      if (this.runContext != null && this.shouldTick) {
         this.state = TestManager.State.RUNNING;
         this.tests.forEach((test) -> {
            test.tick(this.runContext);
         });
         this.tests.removeIf(GameTestState::isCompleted);
         State state = this.state;
         this.state = TestManager.State.IDLE;
         if (state == TestManager.State.HALTING) {
            this.clear();
         }

      }
   }

   static enum State {
      IDLE,
      RUNNING,
      HALTING;

      // $FF: synthetic method
      private static State[] method_68078() {
         return new State[]{IDLE, RUNNING, HALTING};
      }
   }
}
