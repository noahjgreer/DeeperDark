package net.minecraft.test;

import com.google.common.base.MoreObjects;
import java.util.Optional;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.exception.ExceptionUtils;

class StructureTestListener implements TestListener {
   private int attempt = 0;
   private int successes = 0;

   public StructureTestListener() {
   }

   public void onStarted(GameTestState test) {
      ++this.attempt;
   }

   private void retry(GameTestState state, TestRunContext context, boolean lastPassed) {
      TestAttemptConfig testAttemptConfig = state.getTestAttemptConfig();
      String string = String.format("[Run: %4d, Ok: %4d, Fail: %4d", this.attempt, this.successes, this.attempt - this.successes);
      if (!testAttemptConfig.isDisabled()) {
         string = string + String.format(", Left: %4d", testAttemptConfig.numberOfTries() - this.attempt);
      }

      string = string + "]";
      String var10000 = String.valueOf(state.getId());
      String string2 = var10000 + " " + (lastPassed ? "passed" : "failed") + "! " + state.getElapsedMilliseconds() + "ms";
      String string3 = String.format("%-53s%s", string, string2);
      if (lastPassed) {
         passTest(state, string3);
      } else {
         sendMessageToAllPlayers(state.getWorld(), Formatting.RED, string3);
      }

      if (testAttemptConfig.shouldTestAgain(this.attempt, this.successes)) {
         context.retry(state);
      }

   }

   public void onPassed(GameTestState test, TestRunContext context) {
      ++this.successes;
      if (test.getTestAttemptConfig().needsMultipleAttempts()) {
         this.retry(test, context, true);
      } else {
         String var10001;
         if (!test.isFlaky()) {
            var10001 = String.valueOf(test.getId());
            passTest(test, var10001 + " passed! (" + test.getElapsedMilliseconds() + "ms)");
         } else {
            if (this.successes >= test.getRequiredSuccesses()) {
               var10001 = String.valueOf(test);
               passTest(test, var10001 + " passed " + this.successes + " times of " + this.attempt + " attempts.");
            } else {
               ServerWorld var10000 = test.getWorld();
               Formatting var3 = Formatting.GREEN;
               String var10002 = String.valueOf(test);
               sendMessageToAllPlayers(var10000, var3, "Flaky test " + var10002 + " succeeded, attempt: " + this.attempt + " successes: " + this.successes);
               context.retry(test);
            }

         }
      }
   }

   public void onFailed(GameTestState test, TestRunContext context) {
      if (!test.isFlaky()) {
         failTest(test, test.getThrowable());
         if (test.getTestAttemptConfig().needsMultipleAttempts()) {
            this.retry(test, context, false);
         }

      } else {
         TestInstance testInstance = test.getInstance();
         String var10000 = String.valueOf(test);
         String string = "Flaky test " + var10000 + " failed, attempt: " + this.attempt + "/" + testInstance.getMaxAttempts();
         if (testInstance.getRequiredSuccesses() > 1) {
            string = string + ", successes: " + this.successes + " (" + testInstance.getRequiredSuccesses() + " required)";
         }

         sendMessageToAllPlayers(test.getWorld(), Formatting.YELLOW, string);
         if (test.getMaxAttempts() - this.attempt + this.successes >= test.getRequiredSuccesses()) {
            context.retry(test);
         } else {
            failTest(test, new NotEnoughSuccessesError(this.attempt, this.successes, test));
         }

      }
   }

   public void onRetry(GameTestState lastState, GameTestState nextState, TestRunContext context) {
      nextState.addListener(this);
   }

   public static void passTest(GameTestState test, String output) {
      getTestInstanceBlockEntity(test).ifPresent((testInstanceBlockEntity) -> {
         testInstanceBlockEntity.setFinished();
      });
      finishPassedTest(test, output);
   }

   private static void finishPassedTest(GameTestState test, String output) {
      sendMessageToAllPlayers(test.getWorld(), Formatting.GREEN, output);
      TestFailureLogger.passTest(test);
   }

   protected static void failTest(GameTestState test, Throwable output) {
      Object text;
      if (output instanceof GameTestException gameTestException) {
         text = gameTestException.getText();
      } else {
         text = Text.literal(Util.getInnermostMessage(output));
      }

      getTestInstanceBlockEntity(test).ifPresent((testInstanceBlockEntity) -> {
         testInstanceBlockEntity.setErrorMessage(text);
      });
      finishFailedTest(test, output);
   }

   protected static void finishFailedTest(GameTestState test, Throwable output) {
      String var10000 = output.getMessage();
      String string = var10000 + (output.getCause() == null ? "" : " cause: " + Util.getInnermostMessage(output.getCause()));
      var10000 = test.isRequired() ? "" : "(optional) ";
      String string2 = var10000 + String.valueOf(test.getId()) + " failed! " + string;
      sendMessageToAllPlayers(test.getWorld(), test.isRequired() ? Formatting.RED : Formatting.YELLOW, string2);
      Throwable throwable = (Throwable)MoreObjects.firstNonNull(ExceptionUtils.getRootCause(output), output);
      if (throwable instanceof PositionedException positionedException) {
         addGameTestMarker(test.getWorld(), positionedException.getPos(), positionedException.getDebugMessage());
      }

      TestFailureLogger.failTest(test);
   }

   private static Optional getTestInstanceBlockEntity(GameTestState state) {
      ServerWorld serverWorld = state.getWorld();
      Optional optional = Optional.ofNullable(state.getPos());
      Optional optional2 = optional.flatMap((pos) -> {
         return serverWorld.getBlockEntity(pos, BlockEntityType.TEST_INSTANCE_BLOCK);
      });
      return optional2;
   }

   protected static void sendMessageToAllPlayers(ServerWorld world, Formatting formatting, String message) {
      world.getPlayers((player) -> {
         return true;
      }).forEach((player) -> {
         player.sendMessage(Text.literal(message).formatted(formatting));
      });
   }

   private static void addGameTestMarker(ServerWorld world, BlockPos pos, String message) {
      DebugInfoSender.addGameTestMarker(world, pos, message, -2130771968, Integer.MAX_VALUE);
   }
}
