/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.test;

import java.util.function.Consumer;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestRunContext;

class TestSet.1
implements TestListener {
    final /* synthetic */ Consumer field_25304;

    TestSet.1() {
        this.field_25304 = consumer;
    }

    @Override
    public void onStarted(GameTestState test) {
    }

    @Override
    public void onPassed(GameTestState test, TestRunContext context) {
    }

    @Override
    public void onFailed(GameTestState test, TestRunContext context) {
        this.field_25304.accept(test);
    }

    @Override
    public void onRetry(GameTestState lastState, GameTestState nextState, TestRunContext context) {
    }
}
