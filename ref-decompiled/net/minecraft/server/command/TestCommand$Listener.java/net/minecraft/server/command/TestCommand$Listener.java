/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestRunContext;
import net.minecraft.test.TestSet;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public record TestCommand.Listener(ServerCommandSource source, TestSet tests) implements TestListener
{
    @Override
    public void onStarted(GameTestState test) {
    }

    @Override
    public void onPassed(GameTestState test, TestRunContext context) {
        this.onFinished();
    }

    @Override
    public void onFailed(GameTestState test, TestRunContext context) {
        this.onFinished();
    }

    @Override
    public void onRetry(GameTestState lastState, GameTestState nextState, TestRunContext context) {
        this.tests.add(nextState);
    }

    private void onFinished() {
        if (this.tests.isDone()) {
            this.source.sendFeedback(() -> Text.translatable("commands.test.summary", this.tests.getTestCount()).formatted(Formatting.WHITE), true);
            if (this.tests.failed()) {
                this.source.sendError(Text.translatable("commands.test.summary.failed", this.tests.getFailedRequiredTestCount()));
            } else {
                this.source.sendFeedback(() -> Text.translatable("commands.test.summary.all_required_passed").formatted(Formatting.GREEN), true);
            }
            if (this.tests.hasFailedOptionalTests()) {
                this.source.sendMessage(Text.translatable("commands.test.summary.optional_failed", this.tests.getFailedOptionalTestCount()));
            }
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{TestCommand.Listener.class, "source;tracker", "source", "tests"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TestCommand.Listener.class, "source;tracker", "source", "tests"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TestCommand.Listener.class, "source;tracker", "source", "tests"}, this, object);
    }
}
