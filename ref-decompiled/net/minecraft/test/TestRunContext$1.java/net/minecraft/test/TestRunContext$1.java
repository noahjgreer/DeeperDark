/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongArraySet
 */
package net.minecraft.test;

import it.unimi.dsi.fastutil.longs.LongArraySet;
import net.minecraft.test.GameTestBatch;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestListener;
import net.minecraft.test.TestManager;
import net.minecraft.test.TestRunContext;
import net.minecraft.test.TestSet;
import net.minecraft.util.math.ChunkPos;

class TestRunContext.1
implements TestListener {
    final /* synthetic */ TestSet field_48482;
    final /* synthetic */ GameTestBatch field_56191;
    final /* synthetic */ int field_48483;

    TestRunContext.1() {
        this.field_48482 = testSet;
        this.field_56191 = gameTestBatch;
        this.field_48483 = i;
    }

    private void onFinished(GameTestState state) {
        state.getTestInstanceBlockEntity().clearBarriers();
        if (this.field_48482.isDone()) {
            TestRunContext.this.batchListeners.forEach(listener -> listener.onFinished(this.field_56191));
            LongArraySet longSet = new LongArraySet(TestRunContext.this.world.getForcedChunks());
            longSet.forEach(chunkPos -> TestRunContext.this.world.setChunkForced(ChunkPos.getPackedX(chunkPos), ChunkPos.getPackedZ(chunkPos), false));
            TestRunContext.this.runBatch(this.field_48483 + 1);
        }
    }

    @Override
    public void onStarted(GameTestState test) {
    }

    @Override
    public void onPassed(GameTestState test, TestRunContext context) {
        this.onFinished(test);
    }

    @Override
    public void onFailed(GameTestState test, TestRunContext context) {
        if (TestRunContext.this.stopAfterFailure) {
            TestRunContext.this.clearEnvironment();
            LongArraySet longSet = new LongArraySet(TestRunContext.this.world.getForcedChunks());
            longSet.forEach(chunkPos -> TestRunContext.this.world.setChunkForced(ChunkPos.getPackedX(chunkPos), ChunkPos.getPackedZ(chunkPos), false));
            TestManager.INSTANCE.clear();
            test.getTestInstanceBlockEntity().clearBarriers();
        } else {
            this.onFinished(test);
        }
    }

    @Override
    public void onRetry(GameTestState lastState, GameTestState nextState, TestRunContext context) {
    }
}
